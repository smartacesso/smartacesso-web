package br.com.startjob.acesso.dataprovider.mapper;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
                PedestreRegraMapper.class,
                PedestreEquipamentoMapper.class,
                DocumentoMapper.class,
                BiometriaMapper.class,
                MensagemEquipamentoMapper.class,
                HistoricoCotaMapper.class,
                ResponsavelMapper.class
        },
        builder = @Builder(disableBuilder = true)
)
public interface PedestreMapper {

    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    @Mapping(target = "departamento", qualifiedByName = "departamentoSemEmpresa")
    @Mapping(target = "centroCusto", qualifiedByName = "centroCustoSemEmpresa")
    @Mapping(target = "cargo", qualifiedByName = "cargoSemEmpresa")
    @Mapping(target = "regras", qualifiedByName = "pedestreRegraSemPedestre")
    @Mapping(target = "responsaveis", qualifiedByName = "pedestreResumo")
    Pedestre toDomain(final PedestreEntity entity, @Context CycleAvoidingMappingContext context);

    @Named("pedestreResumo")
    @Mapping(target = "foto", ignore = true)
    @Mapping(target = "cliente", qualifiedByName = "clienteResumo")
    @Mapping(target = "endereco", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "departamento", ignore = true)
    @Mapping(target = "centroCusto", ignore = true)
    @Mapping(target = "cargo", ignore = true)
    @Mapping(target = "empresaVisitadaRef", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "regras", ignore = true)
    @Mapping(target = "equipamentos", ignore = true)
    @Mapping(target = "documentos", ignore = true)
    @Mapping(target = "biometrias", ignore = true)
    @Mapping(target = "mensagensPersonalizadas", ignore = true)
    @Mapping(target = "cotas", ignore = true)
    @Mapping(target = "responsaveis", ignore = true)
    @Mapping(target = "responsaveisEntidade", ignore = true)
    Pedestre toDomainResumo(final PedestreEntity entity, @Context CycleAvoidingMappingContext context);

    PedestreEntity toEntity(final Pedestre domain, @Context CycleAvoidingMappingContext context);
}
