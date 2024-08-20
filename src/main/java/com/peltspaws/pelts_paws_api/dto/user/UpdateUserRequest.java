package com.peltspaws.pelts_paws_api.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
    private String username;
}
