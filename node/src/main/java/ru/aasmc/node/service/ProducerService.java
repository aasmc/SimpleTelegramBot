package ru.aasmc.node.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Interface describing a service that produces messages for ANSWER_MESSAGE rabbit message queue.
 */
public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
}
