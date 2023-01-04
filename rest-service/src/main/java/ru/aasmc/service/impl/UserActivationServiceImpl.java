package ru.aasmc.service.impl;

import org.springframework.stereotype.Service;
import ru.aasmc.dao.AppUserDAO;
import ru.aasmc.service.UserActivationService;
import ru.aasmc.utils.CryptoTool;

@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    public UserActivationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var optionalUser = appUserDAO.findById(userId);
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            user.setIsActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
}
