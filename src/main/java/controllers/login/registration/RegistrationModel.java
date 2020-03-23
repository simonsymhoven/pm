package controllers.login.registration;

import lombok.Data;

@Data
public class RegistrationModel {
    private String forename;
    private String surname;
    private String username;
    private String password;
    private String hash;
    private byte[] image;
}
