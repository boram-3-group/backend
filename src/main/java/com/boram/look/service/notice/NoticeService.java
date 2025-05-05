package com.boram.look.service.notice;

import com.boram.look.api.dto.notice.NoticeDto;
import com.boram.look.domain.notice.Notice;
import com.boram.look.domain.notice.repository.NoticeRepository;
import com.boram.look.domain.user.entity.User;
import com.boram.look.global.security.authentication.PrincipalDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public Long insertNotice(
            PrincipalDetails principalDetails,
            NoticeDto.Save dto
    ) {
        User loginUser = principalDetails.getUser();
        Notice notice = dto.toEntity(loginUser);
        Notice entity = noticeRepository.save(notice);
        return entity.getId();
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    @Transactional
    public NoticeDto.Get updateNotice(Long noticeId, NoticeDto.Save dto) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new);
        notice.update(dto);
        Notice savedEntity = noticeRepository.save(notice);
        return savedEntity.toDto();
    }


    @Transactional(readOnly = true)
    public NoticeDto.Get getNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new);
        return notice.toDto();
    }


    @Transactional(readOnly = true)
    public Page<NoticeDto.Get> getAllNotice(Pageable pageable) {
        Page<Notice> notices = noticeRepository.findAll(pageable);
        return notices.map(Notice::toDto);
    }
}
