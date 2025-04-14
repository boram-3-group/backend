package com.boram.look.service.user;

import com.boram.look.api.dto.UserDto;
import com.boram.look.domain.user.entity.User;
import com.boram.look.domain.user.entity.UserRole;
import com.boram.look.domain.user.repository.UserRepository;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.global.security.oauth.OAuth2Response;
import com.boram.look.service.user.helper.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    public void updateUserProfile(String userId, UserDto.Save dto) {
        User user = UserServiceHelper.findUser(UUID.fromString(userId), userRepository);
        user.update(dto);
    }

    @Transactional
    public void updateUserPassword(String userId, String password) {
        User user = UserServiceHelper.findUser(UUID.fromString(userId), userRepository);
        String encodedPassword = passwordEncoder.encode(password);
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(String userId) {
        User deleteUser = UserServiceHelper.findUser(UUID.fromString(userId), userRepository);
        userRepository.delete(deleteUser);
    }

    @Transactional(readOnly = true)
    public UserDto.Profile getUserProfile(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(ResourceNotFoundException::new);
        return user.toDto();
    }

    @Transactional
    public User findOrCreateUser(OAuth2Response oAuth2Response) {
        String username = oAuth2Response.registrationId().getRegistrationId() + "_" + oAuth2Response.id();
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    // 신규 사용자 생성 로직
                    User joinUser = User.builder()
                            .username(username)
                            .role(UserRole.USER)
                            .build();
                    return userRepository.save(joinUser);
                });
    }

}
