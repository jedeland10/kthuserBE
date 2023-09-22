package com.ivanandjohan.socialnetworkuserbe.Authentication;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtResponse implements Serializable {
    private final String jwtToken;
}
