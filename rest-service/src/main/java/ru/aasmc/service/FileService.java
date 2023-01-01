package ru.aasmc.service;

import org.springframework.core.io.FileSystemResource;
import ru.aasmc.entity.AppDocument;
import ru.aasmc.entity.AppPhoto;
import ru.aasmc.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);

    /**
     * Converts binary content to FileSystemResource, which is needed to
     * pass response in HTTP response.
     */
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
