package ru.aasmc.service;

import ru.aasmc.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);

    String setEmail(AppUser appUser, String email);
}
