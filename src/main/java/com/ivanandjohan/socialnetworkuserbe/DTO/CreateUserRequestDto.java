package com.ivanandjohan.socialnetworkuserbe.DTO;


import com.ivanandjohan.socialnetworkuserbe.Model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreateUserRequestDto implements Serializable {
    private final String username;

    private final String firstName;

    private final String lastName;

    private final String password;


    public static User convertToUser(CreateUserRequestDto u) {
        User user = new User();
        user.setUsername(u.getUsername());
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());
        user.setPassword(u.getPassword());
        return user;
    }
}
