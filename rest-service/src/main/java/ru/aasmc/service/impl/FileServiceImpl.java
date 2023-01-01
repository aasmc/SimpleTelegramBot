package ru.aasmc.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ru.aasmc.dao.AppDocumentDAO;
import ru.aasmc.dao.AppPhotoDAO;
import ru.aasmc.entity.AppDocument;
import ru.aasmc.entity.AppPhoto;
import ru.aasmc.entity.BinaryContent;
import ru.aasmc.service.FileService;

import java.io.File;
import java.io.IOException;

@Service
@Log4j
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public AppDocument getDocument(String id) {
        // TODO add decoding of hashed-string
        var docId = Long.parseLong(id);
        return appDocumentDAO.findById(docId).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String id) {
        // TODO add decoding of hashed-string
        var photoId = Long.parseLong(id);
        return appPhotoDAO.findById(photoId).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            // TODO add random file-name generation
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit(); // enqueue temp file for deletion on program exit
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
