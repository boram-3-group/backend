package com.boram.look.domain.notice;

import com.boram.look.api.dto.FileDto;
import com.boram.look.api.dto.notice.NoticeImageDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Table(name = "notice_image")
public class NoticeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder.Default
    @OneToMany(mappedBy = "noticeImage", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<NoticeImageFile> imageFiles = new ArrayList<>();

    public void withNoticeImages(List<NoticeImageFile> imageFiles) {
        this.imageFiles = imageFiles;
    }

    public NoticeImageDto.Get toDto(List<FileDto> files) {
        return NoticeImageDto.Get.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .images(files)
                .build();
    }
}
