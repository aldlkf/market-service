package com.marketplace.repository;

import com.marketplace.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public User save(User user){
        users.add(user);
        return user;
    }

    public Optional<User> findById(Long id){
        for(User user : users){
            if(user.getId().equals(id)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public List<User> findAll(){
        return users;
    }

}
