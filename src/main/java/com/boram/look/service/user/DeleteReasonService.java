package com.boram.look.service.user;

import com.boram.look.api.dto.DeleteReasonDto;
import com.boram.look.domain.user.entity.DeleteReason;
import com.boram.look.domain.user.repository.DeleteReasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteReasonService {

    private final DeleteReasonRepository deleteReasonRepository;

    @Transactional(readOnly = true)
    public List<DeleteReasonDto.Get> findAllDeleteReason() {
        List<DeleteReason> reasons = deleteReasonRepository.findAll();
        return reasons.stream().map(DeleteReason::toDto).toList();
    }

}
