package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.acessosistema.AcessoSistema;
import br.com.startjob.acesso.dataprovider.entity.AcessoSistemaEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AcessoSistemaMapper {

    AcessoSistema toDomain(final AcessoSistemaEntity entity, @Context CycleAvoidingMappingContext context);

    AcessoSistemaEntity toEntity(final AcessoSistema domain, @Context CycleAvoidingMappingContext context);
}
