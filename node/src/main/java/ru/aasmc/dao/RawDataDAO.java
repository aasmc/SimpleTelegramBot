package ru.aasmc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aasmc.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
