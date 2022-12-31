package ru.aasmc.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Interface describing a service that consumes messages from update Rabbit message queues
 * (DOC_MESSAGE_UPDATE, PHOTO_MESSAGE_UPDATE, PHOTO_MESSAGE_UPDATE).
 */
public interface ConsumerService {
    void consumeTextMessageUpdate(Update update);
    void consumeDocMessageUpdate(Update update);
    void consumePhotoMessageUpdate(Update update);
}
