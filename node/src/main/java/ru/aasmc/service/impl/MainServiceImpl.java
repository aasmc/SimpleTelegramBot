package ru.aasmc.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.aasmc.dao.AppUserDAO;
import ru.aasmc.entity.AppUser;
import ru.aasmc.entity.RawData;
import ru.aasmc.dao.RawDataDAO;
import ru.aasmc.entity.UserState;
import ru.aasmc.service.MainService;
import ru.aasmc.service.ProducerService;
import ru.aasmc.service.enums.ServiceCommands;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(
            RawDataDAO rawDataDAO,
            ProducerService producerService,
            AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if (ServiceCommands.CANCEL.equals(text)) {
            output = processCancel(appUser);
        } else if (UserState.BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (UserState.WAIT_FOR_EMAIL_STATE.equals(userState)) {
            // TODO add processing of the email
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
        // TODO add logic to save document
        var answer = "Document saved successfully! Use link to download: http://test.ru/get-doc/777";
        sendAnswer(answer, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        // TODO add logic to save photo
        var answer = "Photo saved successfully! Use link to download: http://test.ru/get-doc/777";
        sendAnswer(answer, chatId);
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
        if (ServiceCommands.REGISTRATION.equals(cmd)) {
            // TODO add registration
            return "Temporarily unavailable";
        } else if (ServiceCommands.HELP.equals(cmd)) {
            return help();
        } else if (ServiceCommands.START.equals(cmd)) {
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
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    // TODO change default value after adding registration logic
                    .isActive(true)
                    .state(UserState.BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
