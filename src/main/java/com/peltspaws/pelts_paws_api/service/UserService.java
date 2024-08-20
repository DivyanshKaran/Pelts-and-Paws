package com.peltspaws.pelts_paws_api.service;

import com.peltspaws.pelts_paws_api.dto.user.UpdateUserRequest;
import com.peltspaws.pelts_paws_api.dto.user.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);
    UserResponse updateCurrentUser(String email, UpdateUserRequest request);
}
