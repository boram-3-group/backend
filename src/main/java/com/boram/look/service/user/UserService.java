package com.boram.look.service.user;

import com.boram.look.api.dto.UserDto;
import com.boram.look.domain.user.entity.User;
import com.boram.look.domain.user.repository.UserRepository;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.service.user.helper.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void joinUser(UserDto.Save dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = dto.toEntity(encodedPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserProfile(Long userId, UserDto.Save dto) {
        User user = UserServiceHelper.findUser(userId, userRepository);
        user.update(dto);
    }

    @Transactional
    public void updateUserPassword(Long userId, String password) {
        User user = UserServiceHelper.findUser(userId, userRepository);
        String encodedPassword = passwordEncoder.encode(password);
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User deleteUser = UserServiceHelper.findUser(userId, userRepository);
        userRepository.delete(deleteUser);
    }

    @Transactional(readOnly = true)
    public UserDto.Profile getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);
        return user.toDto();
    }
}
