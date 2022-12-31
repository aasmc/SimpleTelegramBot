package ru.aasmc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aasmc.entity.AppUser;
public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
