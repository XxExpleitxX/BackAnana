package com.example.backAnana.Services;

import com.example.backAnana.Entities.User;

public interface UserService extends BaseService<User, Long> {

    User authenticate(String username, String password);

}
