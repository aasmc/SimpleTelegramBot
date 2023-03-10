package ru.aasmc.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.aasmc.dao.AppUserDAO;
import ru.aasmc.dao.RawDataDAO;
import ru.aasmc.entity.AppDocument;
import ru.aasmc.entity.AppPhoto;
import ru.aasmc.entity.AppUser;
import ru.aasmc.entity.RawData;
import ru.aasmc.entity.enums.UserState;
import ru.aasmc.exceptions.UploadFileException;
import ru.aasmc.service.AppUserService;
import ru.aasmc.service.FileService;
import ru.aasmc.service.MainService;
import ru.aasmc.service.ProducerService;
import ru.aasmc.service.enums.LinkType;
import ru.aasmc.service.enums.ServiceCommands;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    public MainServiceImpl(
            RawDataDAO rawDataDAO,
            ProducerService producerService,
            AppUserDAO appUserDAO,
            FileService fileService,
            AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommands.fromValue(text);
        if (ServiceCommands.CANCEL.equals(serviceCommand)) {
            output = processCancel(appUser);
        } else if (UserState.BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (UserState.WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown state: " + userState);
            output = "Unknown error! Please, input /cancel and try again.";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            var answer = "Document saved successfully! " +
                    "Use link to download: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "Unfortunately, the file wasn't uploaded. Try again later";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Photo saved successfully! " +
                    "Use link to download: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "Unfortunately your photo couldn't be loaded. Try again later.";
            sendAnswer(error, chatId);
        }
    }


    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Register or activate your account to load content.";
            sendAnswer(error, chatId);
            return true;
        } else if (!UserState.BASIC_STATE.equals(userState)) {
            var error = "Cancel current command with the help of /cancel to load files.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommands.fromValue(cmd);
        if (ServiceCommands.REGISTRATION.equals(serviceCommand)) {
            return appUserService.registerUser(appUser);
        } else if (ServiceCommands.HELP.equals(serviceCommand)) {
            return help();
        } else if (ServiceCommands.START.equals(serviceCommand)) {
            return "Hi! To get a list of available commands, input /help";
        } else {
            return "Unknown command! To get a list of available commands, input /help";
        }
    }

    private String help() {
        return "List of available commands:\n" +
                "/cancel - cancel current command\n" +
                "/registration - register new user.";
    }

    private String processCancel(AppUser appUser) {
        appUser.setState(UserState.BASIC_STATE);
        appUserDAO.save(appUser);
        return "Command cancelled";
    }

    private AppUser findOrSaveAppUser(Update update) {
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var optionalAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        return optionalAppUser.orElseGet(() -> {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(UserState.BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        });
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
