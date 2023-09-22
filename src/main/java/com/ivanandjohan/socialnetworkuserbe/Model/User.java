package com.ivanandjohan.socialnetworkuserbe.Model;

import org.springframework.data.annotation.Id;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private List<String> postsId = new ArrayList<>();

    private List<String> friends = new ArrayList<>();

    private List<String> friendRequests = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (id == null) return false;
        if (!(o instanceof User user)) return false;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
