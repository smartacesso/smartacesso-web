package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EnderecoMapper {

    Endereco toDomain(final EnderecoEntity entity, @Context CycleAvoidingMappingContext context);

    EnderecoEntity toEntity(final Endereco domain, @Context CycleAvoidingMappingContext context);
}
