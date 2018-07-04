package ch.admin.seco.jobroom.service;

public class UserInfoNotFoundException extends Exception {

    public UserInfoNotFoundException(String identification) {
        super("User not found having: " + identification);
    }
}
