package ru.aasmc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.aasmc.service.UserActivationService;

@RestController
@RequestMapping("/user")
public class ActivationController {
    private final UserActivationService userActivationService;

    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = userActivationService.activation(id);
        // TODO replace with more fine-grained exception handling, e.g. if user
        // sent wrong id, there should be a 404 error, instead of an internal server error.
        if (res) {
            return ResponseEntity.ok().body("Registration has been successfully completed!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
