package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.dataprovider.entity.EmpresaEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ClienteMapper.class, DepartamentoMapper.class, CentroCustoMapper.class, CargoMapper.class, EnderecoMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface EmpresaMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    @Mapping(target = "departamentos", qualifiedByName = "departamentoSemEmpresa")
    @Mapping(target = "centros", qualifiedByName = "centroCustoSemEmpresa")
    @Mapping(target = "cargos", qualifiedByName = "cargoSemEmpresa")
    Empresa toDomain(final EmpresaEntity entity, @Context CycleAvoidingMappingContext context);

    EmpresaEntity toEntity(final Empresa domain, @Context CycleAvoidingMappingContext context);
}
