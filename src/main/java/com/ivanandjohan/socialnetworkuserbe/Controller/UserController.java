package com.ivanandjohan.socialnetworkuserbe.Controller;

import com.ivanandjohan.socialnetworkuserbe.Authentication.JwtAuthenticationController;
import com.ivanandjohan.socialnetworkuserbe.DTO.CreateUserRequestDto;
import com.ivanandjohan.socialnetworkuserbe.DTO.FriendDto;
import com.ivanandjohan.socialnetworkuserbe.DTO.UserResponseDto;
import com.ivanandjohan.socialnetworkuserbe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    private final JwtAuthenticationController authenticationController;

    @Autowired
    public UserController(UserService userService, JwtAuthenticationController authenticationController) {
        this.userService = userService;
        this.authenticationController = authenticationController;
    }

    @GetMapping("/") // for testing
    public ResponseEntity<?> getAllUsers() {

        var users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token,
                                     @PathVariable String id) {

        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }


        var user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDto user) {

        var createdUser = userService.createUser(user);

        return ResponseEntity.ok(createdUser);
    }


    @GetMapping("/search/{searchString}")
    public ResponseEntity<?> searchUsers(@RequestHeader("Authorization") String token,
                                         @PathVariable String searchString) {
        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        return ResponseEntity.ok(userService.searchUsers(searchString));
    }


    @PostMapping("/friends")
    public ResponseEntity<?> addFriend(@RequestHeader("Authorization") String token,
                                       @RequestBody FriendDto friend) {

        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        UserResponseDto userFromToken = userService.getUserById(response.getBody());

        try {
            userService.sendFriendRequest(userFromToken.getId(), friend.getFriendId());
            return ResponseEntity.ok(null);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/friends/remove")
    public ResponseEntity<?> removeFriend(@RequestHeader("Authorization") String token,
                                          @RequestBody FriendDto friend) {
        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        UserResponseDto userFromToken = userService.getUserById(response.getBody());

        try {

            userService.removeFriend(userFromToken.getId(), friend.getFriendId());
            return ResponseEntity.ok(null);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/friends/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestHeader("Authorization") String token,
                                                 @RequestBody FriendDto friend) {
        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        UserResponseDto userFromToken = userService.getUserById(response.getBody());

        try {
            userService.acceptFriendRequest(userFromToken.getId(), friend.getFriendId());

            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/friends/deny")
    public ResponseEntity<?> denyFriendRequest(@RequestHeader("Authorization") String token,
                                                 @RequestBody FriendDto friend) {
        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        UserResponseDto userFromToken = userService.getUserById(response.getBody());


        try {
            userService.denyFriendRequest(userFromToken.getId(), friend.getFriendId());

            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(@RequestHeader("Authorization") String token) {

        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        UserResponseDto userFromToken = userService.getUserById(response.getBody());

        try {
            var friends = userService.getFriends(userFromToken.getId());
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/friendsIds")
    public ResponseEntity<?> getFriendsIds(@RequestHeader("Authorization") String token) {

        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        UserResponseDto userFromToken = userService.getUserById(response.getBody());

        try {
            var friends = userService.getFriendsIds(userFromToken.getId());
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/friends/requests")
    public ResponseEntity<?> getFriendRequests(@RequestHeader("Authorization") String token) {

        var response = authenticationController.validateToken(token);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        UserResponseDto userFromToken = userService.getUserById(response.getBody());

        try {
            var friendRequests = userService.getFriendRequests(userFromToken.getId());
            return ResponseEntity.ok(friendRequests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
