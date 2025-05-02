package com.boram.look.service.user;

import com.boram.look.api.dto.user.UserDto;
import com.boram.look.domain.auth.PasswordResetCode;
import com.boram.look.domain.auth.repository.PasswordResetCodeRepository;
import com.boram.look.domain.user.entity.*;
import com.boram.look.domain.user.repository.DeleteReasonRepository;
import com.boram.look.domain.user.repository.UserDeleteHistoryRepository;
import com.boram.look.domain.user.repository.UserRepository;
import com.boram.look.global.ex.DuplicateEmailUseException;
import com.boram.look.global.ex.EmailAndUsernameNotEqualException;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.global.security.oauth.OAuth2Response;
import com.boram.look.service.user.helper.UserServiceHelper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final DeleteReasonRepository deleteReasonRepository;
    private final UserDeleteHistoryRepository deleteHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetCodeRepository resetCodeRepository;

    @Transactional
    public String joinUser(UserDto.Save dto) {
        Optional<User> existUser = userRepository.findByUsername(dto.getUsername());
        existUser.ifPresent(user -> new DuplicateKeyException("이미 사용 중인 아이디입니다."));

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Agreed agreed = Agreed.builder()
                .agreedToMarketing(dto.getAgreedToMarketing())
                .agreedToPrivacy(dto.getAgreedToPrivacy())
                .agreedToTerms(dto.getAgreedToTerms())
                .build();
        User user = dto.toEntity(encodedPassword, agreed);
        return userRepository.save(user).getId().toString();
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
    public void resetUserPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(String userId, Long reasonId) {
        User deleteUser = UserServiceHelper.findUser(UUID.fromString(userId), userRepository);
        DeleteReason reason = deleteReasonRepository.findById(reasonId).orElseThrow(EntityNotFoundException::new);
        UserDeleteHistory history = UserDeleteHistory.builder()
                .deleteReason(reason)
                .userId(deleteUser.getId())
                .username(deleteUser.getUsername())
                .build();
        deleteHistoryRepository.save(history);
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

    @Transactional(readOnly = true)
    public Boolean canUseUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public String findUsername(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return user.getUsername();
    }

    public String getUserEmail(UserDto.PasswordResetEmail dto) {
        User user = userRepository.findByUsername(dto.username()).orElseThrow(EntityNotFoundException::new);
        if (!Objects.equals(dto.email(), user.getEmail())) {
            throw new EmailAndUsernameNotEqualException("이메일, 유저네임 불일치");
        }
        return user.getEmail();
    }

    public void canUseEmail(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new DuplicateEmailUseException("중복"));
    }
}
