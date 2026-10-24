package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                ClienteMapper.class,
                EnderecoMapper.class,
                EmpresaMapper.class,
                DepartamentoMapper.class,
                CentroCustoMapper.class,
                CargoMapper.class,
                UsuarioMapper.class,
                PedestreRegraMapper.class
        },
        builder = @Builder(disableBuilder = true)
)
public interface PedestreMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    @Mapping(target = "departamento", qualifiedByName = "departamentoSemEmpresa")
    @Mapping(target = "centroCusto", qualifiedByName = "centroCustoSemEmpresa")
    @Mapping(target = "cargo", qualifiedByName = "cargoSemEmpresa")
    @Mapping(target = "regras", qualifiedByName = "pedestreRegraSemPedestre")
    Pedestre toDomain(final PedestreEntity entity, @Context CycleAvoidingMappingContext context);

    PedestreEntity toEntity(final Pedestre domain, @Context CycleAvoidingMappingContext context);
}
