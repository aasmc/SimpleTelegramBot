package ru.aasmc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aasmc.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
