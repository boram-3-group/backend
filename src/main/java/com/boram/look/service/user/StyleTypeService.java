package com.boram.look.service.user;

import com.boram.look.api.dto.StyleTypeDto;
import com.boram.look.domain.Action;
import com.boram.look.domain.user.entity.StyleType;
import com.boram.look.domain.user.repository.StyleTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StyleTypeService {
    private final StyleTypeRepository repository;


    @Transactional
    public void doEdit(List<StyleTypeDto.Edit> dto) {
        Map<Action, List<StyleTypeDto.Edit>> grouped =
                dto.stream().collect(Collectors.groupingBy(StyleTypeDto.Edit::getAction));

        List<StyleTypeDto.Edit> creates = grouped.getOrDefault(Action.CREATE, List.of());
        creates.forEach(command ->
                repository.save(new StyleType(command.getContent()))
        );

        List<StyleTypeDto.Edit> updates = grouped.getOrDefault(Action.UPDATE, List.of());
        updates.forEach(command -> {
            StyleType existing = repository.findById(command.getId())
                    .orElseThrow(EntityNotFoundException::new);
            existing.update(command.getContent());
        });

        List<StyleTypeDto.Edit> deletes = grouped.getOrDefault(Action.DELETE, List.of());
        deletes.forEach(command -> repository.deleteById(command.getId()));

        log.info("""
                            create request count: {}
                            update request count: {}
                            delete request count: {}
                        """,
                creates.size(),
                updates.size(),
                deletes.size()
        );
    }


    @Transactional(readOnly = true)
    public Page<StyleTypeDto.Get> getStyleTypes(Pageable pageable) {
        Page<StyleType> pages = repository.findAll(pageable);
        return pages.map(StyleType::toDto);
    }
}
