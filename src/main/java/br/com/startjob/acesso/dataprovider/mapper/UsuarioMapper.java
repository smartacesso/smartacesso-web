package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.dataprovider.entity.UsuarioEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UsuarioMapper {

    Usuario toDomain(final UsuarioEntity entity, @Context CycleAvoidingMappingContext context);

    UsuarioEntity toEntity(final Usuario domain, @Context CycleAvoidingMappingContext context);
}
