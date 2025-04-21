package com.boram.look.domain.user.entity;

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
}
