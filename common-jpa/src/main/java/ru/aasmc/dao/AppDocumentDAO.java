package ru.aasmc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aasmc.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
