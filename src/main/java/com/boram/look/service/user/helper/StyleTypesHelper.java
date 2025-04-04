package com.boram.look.service.user.helper;

import com.boram.look.domain.user.entity.StyleType;
import com.boram.look.domain.user.repository.StyleTypeRepository;

import java.util.List;
import java.util.Set;

public class StyleTypesHelper {
    public static Set<StyleType> findStyleTypes(
            List<Integer> styleIds,
            StyleTypeRepository repository
    ) {
        return repository.findByIdIn(styleIds);
    }
}
