package com.ivanandjohan.socialnetworkuserbe.DTO;

import com.ivanandjohan.socialnetworkuserbe.Model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto implements Serializable {
    private String id;

    private String username;

    private String firstName;

    private String lastName;


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof UserResponseDto otherUser))
            return false;

        return id.equals(otherUser.id) &&
               username.equals(otherUser.username) &&
               firstName.equals(otherUser.firstName) &&
               lastName.equals(otherUser.lastName);
    }

    public static UserResponseDto from(User u) {
        return new UserResponseDto(
                u.getId(),
                u.getUsername(),
                u.getFirstName(),
                u.getLastName()
        );
    }
}
