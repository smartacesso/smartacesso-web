package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
import br.com.startjob.acesso.dataprovider.entity.WebPerfilPermissaoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface WebPerfilPermissaoMapper {

    WebPerfilPermissao toDomain(final WebPerfilPermissaoEntity entity, @Context CycleAvoidingMappingContext context);

    WebPerfilPermissaoEntity toEntity(final WebPerfilPermissao domain, @Context CycleAvoidingMappingContext context);
}
