package ch.admin.seco.jobroom.service;

public class UserInfoNotFoundException extends Exception {

    UserInfoNotFoundException(String identification) {
        super("User not found having: " + identification);
    }
}
