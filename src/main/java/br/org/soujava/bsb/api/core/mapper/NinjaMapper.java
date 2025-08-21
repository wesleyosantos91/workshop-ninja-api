package br.org.soujava.bsb.api.core.mapper;


import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

@Mapper(nullValuePropertyMappingStrategy = IGNORE,
        nullValueCheckStrategy = ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@Component
public interface NinjaMapper {

    NinjaMapper MAPPER = Mappers.getMapper(NinjaMapper.class);

    NinjaEntity toEntity(NinjaQueryRequest request);

    NinjaEntity toEntity(NinjaRequest request);

    NinjaEntity toEntity(NinjaRequest request, @MappingTarget NinjaEntity entity);

    NinjaResponse toResponse(NinjaEntity entity);

    default List<NinjaResponse> toListResponse(List<NinjaEntity> entities) {
        final List<NinjaResponse> list = new ArrayList<>();
        entities.forEach(e -> list.add(toResponse(e)));
        return list;
    }

    default Page<NinjaResponse> toPageResponse(Page<NinjaEntity> pages) {
        final List<NinjaResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());

    }
}
