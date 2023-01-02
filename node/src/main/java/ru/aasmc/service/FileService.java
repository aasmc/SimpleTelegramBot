package ru.aasmc.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.aasmc.entity.AppDocument;
import ru.aasmc.entity.AppPhoto;
import ru.aasmc.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);
}
