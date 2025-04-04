package com.boram.look.domain.user.entity;

import com.boram.look.api.dto.UserDto;
import com.boram.look.domain.AuditingFields;
import com.boram.look.domain.user.constants.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Set;
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
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;   // 각 영상 프로젝트를 구분하는 UUID

    @Column(name = "username", unique = true, length = 50)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "body_type_id", nullable = false)
    private ThermoSensitivity thermoSensitivity;

    @ManyToMany
    @JoinTable(
            name = "user_style",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "style_type_id")
    )
    private Set<StyleType> styleTypes;


    public void update(UserDto.Save dto, ThermoSensitivity sensitivity, Set<StyleType> styleTypes) {
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.thermoSensitivity = sensitivity;
        this.styleTypes = styleTypes;
        this.gender = dto.getGender();
    }

    public UserDto.Profile toDto() {
        return UserDto.Profile.builder()
                .id(this.id.toString())
                .username(this.username)
                .build();
    }
}
