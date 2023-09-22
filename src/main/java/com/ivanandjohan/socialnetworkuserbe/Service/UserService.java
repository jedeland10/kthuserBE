package com.ivanandjohan.socialnetworkuserbe.Service;


import com.ivanandjohan.socialnetworkuserbe.DTO.CreateUserRequestDto;
import com.ivanandjohan.socialnetworkuserbe.DTO.UserResponseDto;
import com.ivanandjohan.socialnetworkuserbe.Model.User;
import com.ivanandjohan.socialnetworkuserbe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserResponseDto createUser(CreateUserRequestDto user) {
        if (userRepository.findUserByUsername(user.getUsername()).isPresent())
            throw new IllegalArgumentException("Username already taken");

        User createdUser = userRepository.save(CreateUserRequestDto.convertToUser(user));

        return UserResponseDto.from(createdUser);
    }


    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponseDto::from).toList();
    }


    public UserResponseDto getUserById(String id) {
        User u = userRepository.findById(id).orElseThrow();

        return UserResponseDto.from(u);
    }


    public UserResponseDto getUserByUsername(String username) {
        User u = userRepository.findUserByUsername(username).orElseThrow();

        return UserResponseDto.from(u);
    }


    public void sendFriendRequest(String userId, String friendId) {
        if (userId.equals(friendId))
            throw new IllegalArgumentException("UserId and FriendId are the same");

        User u = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        if (u.getFriends().contains(friend))
            throw new IllegalArgumentException("User: "+userId+" and user: "+friendId+" are already friends");

        friend.getFriendRequests().add(u.getId());

        userRepository.save(friend);
    }


    public void acceptFriendRequest(String userId, String friendId) {
        if (userId.equals(friendId))
            throw new IllegalArgumentException("UserId and FriendId are the same");

        User u = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();
        if (!u.getFriendRequests().contains(friend.getId()))
            throw new IllegalArgumentException("User: "+userId+" has no pending friend request from user: "+ friendId);

        u.getFriendRequests().remove(friend.getId());
        u.getFriends().add(friend.getId());
        friend.getFriends().add(u.getId());
        userRepository.save(u);
        userRepository.save(friend);
    }



    public List<UserResponseDto> searchUsers(String searchUser) {
        return userRepository.findUsersByFirstNameStartingWithOrLastNameStartingWith(searchUser, searchUser)
                .stream().map(UserResponseDto::from).toList();
    }



    public void denyFriendRequest(String userId, String friendId) {
        if (userId.equals(friendId))
            throw new IllegalArgumentException("UserId and FriendId are the same");

        User u = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        if (!u.getFriendRequests().remove(friend.getId()))
            throw new IllegalArgumentException("User: "+friendId+" has no pending friend request to user: "+ userId);

        userRepository.save(u);
    }


    public List<UserResponseDto> getFriends(String userId) {
        User u = userRepository.findById(userId).orElseThrow();

        return u.getFriends().stream().map(id -> UserResponseDto.from(userRepository.findById(id).orElseThrow())).toList();
    }

    public List<String> getFriendsIds(String userId) {
        User u = userRepository.findById(userId).orElseThrow();

        return u.getFriends();
    }


    public List<UserResponseDto> getFriendRequests(String userId) {
        User u = userRepository.findById(userId).orElseThrow();

        return u.getFriendRequests().stream().map(id -> UserResponseDto.from(userRepository.findById(id).orElseThrow())).toList();
    }


    public void removeFriend(String userId, String friendId) {
        if (userId.equals(friendId))
            throw new IllegalArgumentException("UserId and FriendId are the same");

        User u = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        if (!u.getFriends().contains(friend.getId()) || !friend.getFriends().contains(u.getId()))
            throw new IllegalArgumentException("User: "+userId+" and user: "+friendId+" are not friends");

        u.getFriends().remove(friend.getId());
        friend.getFriends().remove(u.getId());
        userRepository.save(u);
        userRepository.save(friend);
    }
}
