package com.ivanandjohan.socialnetworkuserbe.Authentication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.ivanandjohan.socialnetworkuserbe.DTO.UserResponseDto;
import com.ivanandjohan.socialnetworkuserbe.Repository.UserRepository;
import com.ivanandjohan.socialnetworkuserbe.Service.UserService;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenUtil {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    private final String secret = "3ed6f079d644d176eebb3b2c40045857e9937e8324ced80d6f" +
            "bcceb5e57c7824b1326d64b4fa163c7175d46ae06f4cc35d237be3c9d0f4e0c54480931d466a77";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;

    private final UserRepository userRepository;

    @Autowired
    public JwtTokenUtil(UserService userService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    //retrieve subject from jwt token
    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public UserResponseDto getUserFromToken(String token) {
        try {
            return objectMapper.readValue(getSubjectFromToken(token), UserResponseDto.class);
        } catch (JacksonException e) {
            //e.printStackTrace();
            //System.out.println(token);
            return null;
        }
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {


        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        if (token.startsWith("Bearer "))
            token = token.substring(7);

        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .parseClaimsJws(token)
                .getBody();
    }


    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    //generate token for user
    public String generateToken(UserResponseDto user) throws JsonProcessingException {
        Map<String, Object> claims = new HashMap<>();

        String userJSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        return doGenerateToken(claims, userJSON);
    }


    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        try {
            return Jwts.builder().setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            return "";
        }
    }


    public UserResponseDto validateToken(String token)
            throws IllegalArgumentException, NoSuchElementException, JsonProcessingException {

        UserResponseDto userFromToken = null;
        String jwtToken = null;

        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
        } else {
            if (token == null)
                throw new IllegalArgumentException("Token is null. \ntoken: " + token);

            if (!token.startsWith("Bearer"))
                throw new IllegalArgumentException("Token does not begin with 'Bearer'. \ntoken: "+token);
        }

/*        if(socketRequestToken != null) {
            jwtToken = socketRequestToken;
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        }*/

        if(jwtToken != null) {
            var userJSON = getSubjectFromToken(jwtToken);
            userFromToken = objectMapper.readValue(userJSON, UserResponseDto.class);
        } else {
            throw new IllegalArgumentException("Token is null. \ntoken: " + token);
        }


        // Once we get the token validate it.
        if (userFromToken != null) {
            var userFromDb = userService.getUserById(userFromToken.getId());

            if (userFromToken.equals(userFromDb)) {
                return userFromDb;
            }
            else {
                throw new NoSuchElementException("Could not find user from token with username: " + userFromToken.getUsername());
            }

        } else {
            throw new IllegalArgumentException("Could not convert token body to user");
        }


    }

    public UserResponseDto authenticate(JwtRequest request) throws AuthenticationException {

        final var optionalUserFromDb = userRepository.findUserByUsername(request.getUsername());
        if (optionalUserFromDb.isEmpty()) {
            throw new BadCredentialsException("Username: '" + request.getUsername() + "' not found");
        }

        var userFromDb = optionalUserFromDb.get();

        if (!userFromDb.getPassword().equals(request.getPassword())) {
            throw new BadCredentialsException("Wrong password for username: " + request.getUsername());
        }

        return UserResponseDto.from(userFromDb);
    }
}
