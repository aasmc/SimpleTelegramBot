package ru.aasmc.service;

import ru.aasmc.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
