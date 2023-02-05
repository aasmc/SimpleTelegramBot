package ru.aasmc.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.aasmc.controller.UpdateProcessor;
import ru.aasmc.service.AnswerConsumer;
import ru.aasmc.RabbitQueue;

@Service
@Log4j
public class AnswerConsumerServiceImpl implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;

    public AnswerConsumerServiceImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    /**
     * Consumes messages from ANSWER_MESSAGE rabbit mq and sends them
     * to UpdateController, which sends them to the TelegramBot.
     */
    @Override
    @RabbitListener(queues = RabbitQueue.ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
