package com.ivanandjohan.socialnetworkuserbe.Authentication;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {
    private final String username;
    private final String password;

}
