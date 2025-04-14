package com.boram.look.service.user.helper;

import com.boram.look.domain.user.entity.User;
import com.boram.look.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class UserServiceHelper {
    public static User findUser(
            UUID userId,
            UserRepository repository
    ) {
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
    }
}
