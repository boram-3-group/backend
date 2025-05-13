package com.boram.look.domain.user.entity;

import com.boram.look.api.dto.user.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Agreed {
    private Boolean agreedToTerms;
    private Boolean agreedToPrivacy;
    private Boolean agreedToMarketing;
    private Boolean agreedToLocation;

    public void updateAgreed(UserDto.Save dto) {
        this.agreedToLocation = dto.getAgreedToLocation();
        this.agreedToPrivacy = dto.getAgreedToPrivacy();
        this.agreedToMarketing = dto.getAgreedToMarketing();
        this.agreedToTerms = dto.getAgreedToTerms();
    }
}
