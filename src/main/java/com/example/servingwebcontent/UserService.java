package com.example.servingwebcontent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Transactional
    public User registerNewUserAccount(UserDTO userDto) {
        User newUser = new User();
        newUser.setUsername(userDto.getFirstName());
        newUser.setPassword("{noop}"+userDto.getPassword());
        return repository.save(newUser);
    }

}

