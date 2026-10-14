package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.dataprovider.entity.RegraEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {EmpresaMapper.class, ClienteMapper.class, HorarioMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface RegraMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    @Mapping(target = "horarios", qualifiedByName = "horarioSemRegra")
    Regra toDomain(final RegraEntity entity, @Context CycleAvoidingMappingContext context);

    RegraEntity toEntity(final Regra domain, @Context CycleAvoidingMappingContext context);
}
