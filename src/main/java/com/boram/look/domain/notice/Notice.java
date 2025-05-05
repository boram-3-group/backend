package com.boram.look.domain.notice;

import com.boram.look.api.dto.notice.NoticeDto;
import com.boram.look.domain.AuditingFields;
import com.boram.look.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Table(name = "notice")
public class Notice extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writer;

    public NoticeDto.Get toDto() {
        return NoticeDto.Get.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .writerProfile(this.writer.toDto())
                .createdAt(this.getCreatedAt())
                .build();
    }

    public void update(NoticeDto.Save dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }
}
