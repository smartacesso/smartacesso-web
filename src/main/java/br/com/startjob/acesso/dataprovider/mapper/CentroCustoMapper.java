package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.dataprovider.entity.CentroCustoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CentroCustoMapper {

    CentroCusto toDomain(final CentroCustoEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("centroCustoSemEmpresa")
    @Mapping(target = "empresa", ignore = true)
    CentroCusto toDomainSemEmpresa(final CentroCustoEntity entity, @Context CycleAvoidingMappingContext context);

    CentroCustoEntity toEntity(final CentroCusto domain, @Context CycleAvoidingMappingContext context);
}
