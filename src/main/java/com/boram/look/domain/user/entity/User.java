package com.boram.look.domain.user.entity;

import com.boram.look.api.dto.UserDto;
import com.boram.look.domain.AuditingFields;
import com.boram.look.domain.user.constants.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

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
    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    public void update(UserDto.Save dto) {
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.gender = dto.getGender();
    }

    public UserDto.Profile toDto() {
        return UserDto.Profile.builder()
                .id(this.id.toString())
                .username(this.username)
                .gender(this.gender)
                .build();
    }
}
