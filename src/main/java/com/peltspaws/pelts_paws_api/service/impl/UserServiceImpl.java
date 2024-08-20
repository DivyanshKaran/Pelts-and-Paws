package com.peltspaws.pelts_paws_api.service.impl;

import com.peltspaws.pelts_paws_api.dto.user.UpdateUserRequest;
import com.peltspaws.pelts_paws_api.dto.user.UserResponse;
import com.peltspaws.pelts_paws_api.entity.User;
import com.peltspaws.pelts_paws_api.exception.BadRequestException;
import com.peltspaws.pelts_paws_api.exception.ResourceNotFoundException;
import com.peltspaws.pelts_paws_api.repository.UserRepository;
import com.peltspaws.pelts_paws_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = findByEmail(email);
        return toDto(user);
    }

    @Override
    public UserResponse updateCurrentUser(String email, UpdateUserRequest request) {
        User user = findByEmail(email);

        if (StringUtils.hasText(request.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())
                    && !request.getUsername().equals(user.getUsername())) {
                throw new BadRequestException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }

        return toDto(userRepository.save(user));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private UserResponse toDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
