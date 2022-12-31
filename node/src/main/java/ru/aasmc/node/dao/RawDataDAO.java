package ru.aasmc.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aasmc.node.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
