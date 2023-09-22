package com.ivanandjohan.socialnetworkuserbe.Repository;



import com.ivanandjohan.socialnetworkuserbe.Model.User;

import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByUsername(String username);

    List<User> findUsersByFirstNameStartingWithOrLastNameStartingWith(String searchString, String searchString2);

}
