package com.boram.look.domain.user.entity;

import com.boram.look.api.dto.UserDto;
import com.boram.look.domain.AuditingFields;
import com.boram.look.domain.user.constants.Gender;
import com.boram.look.global.security.oauth.OAuth2RegistrationId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@ToString
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends AuditingFields {
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", unique = true, length = 50)
    private String username;
    @Column(name = "nickname", length = 7)
    private String nickname;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_id", length = 20)
    private OAuth2RegistrationId registrationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Embedded
    private Agreed agreed;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    public List<UserRole> getRoles() {
        return List.of(role);
    }


    public void update(UserDto.Save dto) {
        this.username = dto.getUsername();
        this.gender = dto.getGender();
        this.nickname = dto.getNickname();
        this.birthDate = dto.getBirthDate();
    }

    public UserDto.Profile toDto() {
        return UserDto.Profile.builder()
                .id(this.id.toString())
                .username(this.username)
                .gender(this.gender)
                .nickname(this.nickname)
                .birthDate(this.birthDate)
                .build();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
