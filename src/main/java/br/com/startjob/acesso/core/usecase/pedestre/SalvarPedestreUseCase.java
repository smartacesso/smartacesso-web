package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.equipamento.Equipamento;
import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.core.domain.pedestre.*;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.*;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SalvarPedestreUseCase {

    public static final String NOME_REGRA_PADRAO_VISITANTE = "ACESSO_UNICO_VISITANTE";
    private static final int FOTO_CROP_MAX_LADO_PX = 640;

    private final PedestreGateway pedestreGateway;
    private final EmpresaGateway empresaGateway;
    private final RegraGateway regraGateway;
    private final EquipamentoGateway equipamentoGateway;
    private final ResponsavelGateway responsavelGateway;
    private final ParametroGateway parametroGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;
    private final PasswordGateway passwordGateway;

    @Transactional
    public Pedestre execute(Pedestre pedestre) {
        return execute(pedestre, false);
    }

    @Transactional
    public Pedestre execute(Pedestre pedestre, boolean removerFoto) {
        if (pedestre == null) {
            throw new BusinessException("Dados do pedestre não informados.");
        }

        if (pedestre.getTipo() == null) {
            pedestre.setTipo(TipoPedestre.PEDESTRE);
        } else if (TipoPedestre.AMBOS.equals(pedestre.getTipo())) {
            throw new BusinessException("Tipo de pedestre inválido.");
        }

        if (pedestre.getStatus() == null) {
            pedestre.setStatus(Status.ATIVO);
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }
        Long idClienteLogado = usuarioLogado.getCliente().getId();

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (pedestre.isVisitante()) {
            if (!permissoes.contains(WebPermissao.VISITANTE_EDITAR.getCodigo())) {
                throw new BusinessException("Você não possui permissão para salvar visitantes.");
            }
        } else {
            if (!permissoes.contains(WebPermissao.PEDESTRE_EDITAR.getCodigo())) {
                throw new BusinessException("Você não possui permissão para salvar pedestres.");
            }
        }

        if (pedestre.getNome() == null || pedestre.getNome().isBlank()) {
            throw new BusinessException("Nome é obrigatório.");
        }
        pedestre.setNome(pedestre.getNome().trim());

        Map<String, String> parametrosCliente = carregarParametrosCliente(idClienteLogado);

        Pedestre pedestreExistente = null;
        if (pedestre.getId() != null) {
            pedestreExistente = pedestreGateway.findById(pedestre.getId())
                    .orElseThrow(() -> new BusinessException("Pedestre não encontrado."));

            if (Boolean.TRUE.equals(pedestreExistente.getRemovido())) {
                throw new BusinessException("Pedestre não encontrado.");
            }

            if (pedestreExistente.getCliente() == null
                    || !pedestreExistente.getCliente().getId().equals(idClienteLogado)) {
                throw new BusinessException("O pedestre não pertence ao seu cliente.");
            }

            pedestre.setCliente(pedestreExistente.getCliente());
            pedestre.setDataCriacao(pedestreExistente.getDataCriacao());
            pedestre.setVersao(pedestreExistente.getVersao());
            pedestre.setUniqueId(pedestreExistente.getUniqueId());
            pedestre.setRemovido(false);
            if (pedestre.getQrCodeParaAcesso() == null) {
                pedestre.setQrCodeParaAcesso(pedestreExistente.getQrCodeParaAcesso());
            }
            if (pedestre.getTipoQRCode() == null) {
                pedestre.setTipoQRCode(pedestreExistente.getTipoQRCode());
            }
            if (pedestre.getAcessoLivre() == null) {
                pedestre.setAcessoLivre(pedestreExistente.getAcessoLivre());
            }
            if (pedestre.getJustificativa() == null) {
                pedestre.setJustificativa(pedestreExistente.getJustificativa());
            }
            if (pedestre.getDataInicioPeriodoAgendamento() == null) {
                pedestre.setDataInicioPeriodoAgendamento(pedestreExistente.getDataInicioPeriodoAgendamento());
            }
            if (pedestre.getDataFimPeriodoAgendamento() == null) {
                pedestre.setDataFimPeriodoAgendamento(pedestreExistente.getDataFimPeriodoAgendamento());
            }
            if (pedestre.getAgendamentoLiberado() == null) {
                pedestre.setAgendamentoLiberado(pedestreExistente.getAgendamentoLiberado());
            }
            if (pedestre.getQtdAcessoAntesSinc() == null) {
                pedestre.setQtdAcessoAntesSinc(pedestreExistente.getQtdAcessoAntesSinc());
            }
        } else {
            pedestre.setCliente(usuarioLogado.getCliente());
            pedestre.setRemovido(false);
        }

        pedestre.setUsuario(usuarioLogado);

        normalizarDocumentosEContatos(pedestre);
        tratarFoto(pedestre, pedestreExistente, removerFoto);
        tratarMatriculaSequencial(pedestre, idClienteLogado, parametrosCliente);
        tratarCartaoAcesso(pedestre, idClienteLogado, parametrosCliente);
        tratarAssociacoesPorTipo(pedestre, idClienteLogado, parametrosCliente);
        tratarEndereco(pedestre, pedestreExistente);
        tratarResponsaveis(pedestre, idClienteLogado);
        validarCamposObrigatoriosDinamicos(pedestre, parametrosCliente);
        validarCpfEUnicidades(pedestre, idClienteLogado, parametrosCliente);
        tratarRegrasAcesso(pedestre, usuarioLogado);
        tratarEquipamentos(pedestre, idClienteLogado);
        tratarDocumentos(pedestre, pedestreExistente);
        tratarBiometrias(pedestre, pedestreExistente, parametrosCliente);
        tratarMensagensPersonalizadas(pedestre);
        tratarCotas(pedestre, usuarioLogado);
        tratarAcessoApp(pedestre, pedestreExistente);

        Pedestre salvo = pedestreGateway.save(pedestre);
        log.info("Pedestre/Visitante id={} tipo={} salvo com sucesso.", salvo.getId(), salvo.getTipo());
        return salvo;
    }

    private Map<String, String> carregarParametrosCliente(Long idCliente) {
        List<Parametro> lista = parametroGateway.findAllByCliente(idCliente);
        Map<String, String> mapa = new HashMap<>();
        if (lista != null) {
            for (Parametro p : lista) {
                if (p != null && p.getNome() != null && p.getValor() != null) {
                    String nomeNormalizado = ParametroCatalogo.normalizarNomeLegado(p.getNome());
                    if (!ParametroCatalogo.isDepreciado(nomeNormalizado)) {
                        mapa.putIfAbsent(nomeNormalizado, p.getValor());
                    }
                }
            }
        }
        return mapa;
    }

    private String obterValorParametro(Map<String, String> parametrosCliente, ParametroCatalogo catalogo) {
        String valorBruto = parametrosCliente.getOrDefault(
                catalogo.getNomePersistido(),
                catalogo.getValorPadraoPersistido()
        );
        return catalogo.normalizarValorPersistidoLeitura(valorBruto);
    }

    private boolean obterBooleanParametro(Map<String, String> parametrosCliente, ParametroCatalogo catalogo) {
        String valorBruto = parametrosCliente.getOrDefault(
                catalogo.getNomePersistido(),
                catalogo.getValorPadraoPersistido()
        );
        return Boolean.TRUE.equals(catalogo.converterPersistidoParaTipado(valorBruto));
    }

    private int obterIntParametro(Map<String, String> parametrosCliente, ParametroCatalogo catalogo, int padrao) {
        String valorBruto = parametrosCliente.getOrDefault(
                catalogo.getNomePersistido(),
                catalogo.getValorPadraoPersistido()
        );
        Object convertido = catalogo.converterPersistidoParaTipado(valorBruto);
        if (convertido instanceof Integer i) {
            return i;
        }
        return padrao;
    }

    @SuppressWarnings("unchecked")
    private List<String> obterListaStringParametro(Map<String, String> parametrosCliente, ParametroCatalogo catalogo) {
        String valorBruto = parametrosCliente.getOrDefault(
                catalogo.getNomePersistido(),
                catalogo.getValorPadraoPersistido()
        );
        Object convertido = catalogo.converterPersistidoParaTipado(valorBruto);
        if (convertido instanceof List<?> lista) {
            return (List<String>) lista;
        }
        return List.of();
    }

    private void normalizarDocumentosEContatos(Pedestre pedestre) {
        if (pedestre.getCpf() != null) {
            String cpfLimpo = pedestre.getCpf().replaceAll("\\D", "");
            pedestre.setCpf(cpfLimpo.isEmpty() ? null : cpfLimpo);
        }
        if (pedestre.getRg() != null) {
            String rgTrim = pedestre.getRg().trim();
            pedestre.setRg(rgTrim.isEmpty() ? null : rgTrim);
        }
        if (pedestre.getEmail() != null) {
            String emailTrim = pedestre.getEmail().trim();
            pedestre.setEmail(emailTrim.isEmpty() ? null : emailTrim);
        }
        if (pedestre.getMatricula() != null) {
            String matTrim = pedestre.getMatricula().trim();
            pedestre.setMatricula(matTrim.isEmpty() ? null : matTrim);
        }
        if (pedestre.getSempreLiberado() == null) {
            pedestre.setSempreLiberado(false);
        }
        if (pedestre.getHabilitarTeclado() == null) {
            pedestre.setHabilitarTeclado(false);
        }
        if (pedestre.getCadastroFacialObrigatorio() == null) {
            pedestre.setCadastroFacialObrigatorio(false);
        }
        if (pedestre.getEnviaSmsAoPassarNaCatraca() == null) {
            pedestre.setEnviaSmsAoPassarNaCatraca(false);
        }
    }

    private void tratarFoto(Pedestre pedestre, Pedestre pedestreExistente, boolean removerFoto) {
        if (removerFoto) {
            pedestre.setFoto(null);
            pedestre.setDataAlteracaoFoto(LocalDateTime.now());
            return;
        }

        if (pedestre.getFoto() != null && pedestre.getFoto().length > 0) {
            pedestre.setFoto(comprimirFotoJpeg(pedestre.getFoto(), FOTO_CROP_MAX_LADO_PX));
            pedestre.setDataAlteracaoFoto(LocalDateTime.now());
        } else if (pedestreExistente != null) {
            pedestre.setFoto(pedestreExistente.getFoto());
            pedestre.setDataAlteracaoFoto(pedestreExistente.getDataAlteracaoFoto());
        }
    }

    private void tratarMatriculaSequencial(
            Pedestre pedestre,
            Long idCliente,
            Map<String, String> parametrosCliente
    ) {
        boolean matriculaSequencial = obterBooleanParametro(parametrosCliente, ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL);
        if (matriculaSequencial && (pedestre.getMatricula() == null || pedestre.getMatricula().isBlank())) {
            String novaMatricula = gerarProximaMatriculaSequencial(idCliente);
            pedestre.setMatricula(novaMatricula);
        }
    }

    private String gerarProximaMatriculaSequencial(Long idCliente) {
        Optional<String> ultimaOpt = pedestreGateway.findUltimaMatriculaByCliente(idCliente);
        if (ultimaOpt.isPresent()) {
            String ultima = ultimaOpt.get().replace(".", "").trim();
            try {
                long proximo = Long.parseLong(ultima) + 1L;
                return String.valueOf(proximo);
            } catch (NumberFormatException ignored) {
                // fallback para "1"
            }
        }
        return "1";
    }

    private void tratarCartaoAcesso(
            Pedestre pedestre,
            Long idCliente,
            Map<String, String> parametrosCliente
    ) {
        String cartaoAtual = pedestre.getCodigoCartaoAcesso();
        if (isCartaoVazioOuSoZeros(cartaoAtual)) {
            boolean preencherComMatricula = obterBooleanParametro(parametrosCliente, ParametroCatalogo.PREENCHER_CARTAO_COM_MATRICULA);
            boolean preencherAuto = obterBooleanParametro(parametrosCliente, ParametroCatalogo.PREENCHER_CARTAO_AUTO);

            if (preencherComMatricula && pedestre.getMatricula() != null && !pedestre.getMatricula().isBlank()) {
                pedestre.setCodigoCartaoAcesso(normalizarCartao(pedestre.getMatricula()));
            } else if (preencherAuto) {
                int qtdeDigitos = obterIntParametro(parametrosCliente, ParametroCatalogo.ESCOLHER_QTDE_DIGITOS_CARTAO, 10);
                pedestre.setCodigoCartaoAcesso(gerarCartaoUnicoPorTimestamp(idCliente, pedestre.getId(), qtdeDigitos));
            } else {
                pedestre.setCodigoCartaoAcesso(null);
            }
        } else {
            pedestre.setCodigoCartaoAcesso(normalizarCartao(cartaoAtual));
        }
    }

    private String gerarCartaoUnicoPorTimestamp(Long idCliente, Long idPedestre, int qtdeDigitos) {
        int size = (qtdeDigitos > 0 && qtdeDigitos <= 16) ? qtdeDigitos : 10;
        Random rnd = new Random();
        long modulo = 1L;
        for (int i = 0; i < size; i++) {
            modulo *= 10L;
        }

        for (int tentativa = 0; tentativa < 30; tentativa++) {
            long millis = System.currentTimeMillis();
            long valor = Math.abs((millis + rnd.nextInt(1000) + tentativa) % modulo);
            String codigo = normalizarCartao(String.format("%0" + size + "d", valor));
            if (codigo == null || codigo.isEmpty()) {
                continue;
            }
            if (!pedestreGateway.existsAtivoByCartaoNormalizadoAndCliente(codigo, idCliente, idPedestre)) {
                return codigo;
            }
        }

        String fallback = String.valueOf(System.currentTimeMillis()) + (rnd.nextInt(90) + 10);
        fallback = fallback.replaceAll("\\D", "");
        if (fallback.length() > size) {
            fallback = fallback.substring(fallback.length() - size);
        }
        return normalizarCartao(fallback);
    }

    public static String normalizarCartao(String raw) {
        if (raw == null) {
            return null;
        }
        String t = raw.trim();
        if (t.isEmpty()) {
            return null;
        }
        String stripped = t.replaceFirst("^0+", "");
        return stripped.isEmpty() ? null : stripped;
    }

    private static boolean isCartaoVazioOuSoZeros(String cartaoAtual) {
        if (cartaoAtual == null || cartaoAtual.trim().isEmpty()) {
            return true;
        }
        String digits = cartaoAtual.replaceAll("\\D", "");
        return digits.isEmpty() || digits.replace("0", "").isEmpty();
    }

    private void tratarAssociacoesPorTipo(
            Pedestre pedestre,
            Long idClienteLogado,
            Map<String, String> parametrosCliente
    ) {
        if (pedestre.isVisitante()) {
            pedestre.setEmpresa(null);
            pedestre.setDepartamento(null);
            pedestre.setCentroCusto(null);
            pedestre.setCargo(null);

            if (pedestre.getEmpresaVisitadaRef() != null && pedestre.getEmpresaVisitadaRef().getId() != null) {
                Empresa empVisitada = carregarEmpresaValidaDoCliente(
                        pedestre.getEmpresaVisitadaRef().getId(), idClienteLogado);
                pedestre.setEmpresaVisitadaRef(empVisitada);
                if (pedestre.getEmpresaVisitada() == null || pedestre.getEmpresaVisitada().isBlank()) {
                    pedestre.setEmpresaVisitada(empVisitada.getNome());
                } else {
                    pedestre.setEmpresaVisitada(pedestre.getEmpresaVisitada().trim());
                }
            } else if (pedestre.getEmpresaVisitada() != null && !pedestre.getEmpresaVisitada().isBlank()) {
                pedestre.setEmpresaVisitada(pedestre.getEmpresaVisitada().trim());
                pedestre.setEmpresaVisitadaRef(null);
            } else {
                pedestre.setEmpresaVisitada(null);
                pedestre.setEmpresaVisitadaRef(null);
            }

            if (pedestre.getUuidLocal() == null || pedestre.getUuidLocal().isBlank()) {
                String localPadraoVisitante = obterValorParametro(parametrosCliente, ParametroCatalogo.LOCAL_PADRAO_VISITANTE);
                if (localPadraoVisitante != null && !localPadraoVisitante.isBlank()) {
                    pedestre.setUuidLocal(localPadraoVisitante.trim());
                }
            }
        } else {
            pedestre.setEmpresaVisitada(null);
            pedestre.setEmpresaVisitadaRef(null);

            if (pedestre.getEmpresa() != null && pedestre.getEmpresa().getId() != null) {
                Empresa empresa = carregarEmpresaValidaDoCliente(pedestre.getEmpresa().getId(), idClienteLogado);
                pedestre.setEmpresa(empresa);

                if (pedestre.getDepartamento() != null && pedestre.getDepartamento().getId() != null) {
                    Long idDepto = pedestre.getDepartamento().getId();
                    Departamento depto = (empresa.getDepartamentos() == null ? List.<Departamento>of() : empresa.getDepartamentos())
                            .stream()
                            .filter(d -> idDepto.equals(d.getId()) && (d.getRemovido() == null || !d.getRemovido()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException("Departamento não encontrado na empresa selecionada."));
                    pedestre.setDepartamento(depto);
                } else {
                    pedestre.setDepartamento(null);
                }

                if (pedestre.getCentroCusto() != null && pedestre.getCentroCusto().getId() != null) {
                    Long idCentro = pedestre.getCentroCusto().getId();
                    CentroCusto centro = (empresa.getCentros() == null ? List.<CentroCusto>of() : empresa.getCentros())
                            .stream()
                            .filter(c -> idCentro.equals(c.getId()) && (c.getRemovido() == null || !c.getRemovido()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException("Centro de custo não encontrado na empresa selecionada."));
                    pedestre.setCentroCusto(centro);
                } else {
                    pedestre.setCentroCusto(null);
                }

                if (pedestre.getCargo() != null && pedestre.getCargo().getId() != null) {
                    Long idCargo = pedestre.getCargo().getId();
                    Cargo cargo = (empresa.getCargos() == null ? List.<Cargo>of() : empresa.getCargos())
                            .stream()
                            .filter(ca -> idCargo.equals(ca.getId()) && (ca.getRemovido() == null || !ca.getRemovido()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException("Cargo não encontrado na empresa selecionada."));
                    pedestre.setCargo(cargo);
                } else {
                    pedestre.setCargo(null);
                }
            } else {
                pedestre.setEmpresa(null);
                pedestre.setDepartamento(null);
                pedestre.setCentroCusto(null);
                pedestre.setCargo(null);
            }

            if (pedestre.getUuidLocal() == null || pedestre.getUuidLocal().isBlank()) {
                String localPadraoPedestre = obterValorParametro(parametrosCliente, ParametroCatalogo.LOCAL_PADRAO_PEDESTRE);
                if (localPadraoPedestre != null && !localPadraoPedestre.isBlank()) {
                    pedestre.setUuidLocal(localPadraoPedestre.trim());
                }
            }
        }
    }

    private Empresa carregarEmpresaValidaDoCliente(Long idEmpresa, Long idClienteLogado) {
        Empresa empresa = empresaGateway.findById(idEmpresa)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada."));

        if (Boolean.TRUE.equals(empresa.getRemovido())) {
            throw new BusinessException("Empresa não encontrada.");
        }

        if (empresa.getCliente() == null || !empresa.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("A empresa não pertence ao seu cliente.");
        }
        return empresa;
    }

    private void tratarEndereco(Pedestre pedestre, Pedestre pedestreExistente) {
        if (isEnderecoEmpty(pedestre.getEndereco())) {
            pedestre.setEndereco(null);
        } else if (pedestreExistente != null
                && pedestreExistente.getEndereco() != null
                && pedestre.getEndereco().getId() == null) {
            pedestre.getEndereco().setId(pedestreExistente.getEndereco().getId());
        }
    }

    private boolean isEnderecoEmpty(Endereco endereco) {
        if (endereco == null) return true;
        return (endereco.getCep() == null || endereco.getCep().isBlank())
                && (endereco.getLogradouro() == null || endereco.getLogradouro().isBlank())
                && (endereco.getNumero() == null || endereco.getNumero().isBlank())
                && (endereco.getComplemento() == null || endereco.getComplemento().isBlank())
                && (endereco.getBairro() == null || endereco.getBairro().isBlank())
                && (endereco.getCidade() == null || endereco.getCidade().isBlank())
                && (endereco.getEstado() == null || endereco.getEstado().isBlank());
    }

    private void tratarResponsaveis(Pedestre pedestre, Long idClienteLogado) {
        Map<Long, Pedestre> responsaveisMap = new LinkedHashMap<>();
        if (pedestre.getResponsaveis() != null) {
            for (Pedestre r : pedestre.getResponsaveis()) {
                if (r == null || r.getId() == null) {
                    continue;
                }
                if (pedestre.getId() != null && pedestre.getId().equals(r.getId())) {
                    throw new BusinessException("O pedestre não pode ser responsável por si mesmo.");
                }
                if (!responsaveisMap.containsKey(r.getId())) {
                    Pedestre encontrado = pedestreGateway.findById(r.getId())
                            .orElseThrow(() -> new BusinessException("Responsável (pedestre) não encontrado."));
                    if (Boolean.TRUE.equals(encontrado.getRemovido())) {
                        throw new BusinessException("Responsável (pedestre) não encontrado.");
                    }
                    if (encontrado.getCliente() == null || !encontrado.getCliente().getId().equals(idClienteLogado)) {
                        throw new BusinessException("O responsável selecionado não pertence ao seu cliente.");
                    }
                    responsaveisMap.put(encontrado.getId(), encontrado);
                }
            }
        }
        pedestre.setResponsaveis(new ArrayList<>(responsaveisMap.values()));

        Map<Long, Responsavel> responsaveisEntidadeMap = new LinkedHashMap<>();
        if (pedestre.getResponsaveisEntidade() != null) {
            for (Responsavel re : pedestre.getResponsaveisEntidade()) {
                if (re == null || re.getId() == null) {
                    continue;
                }
                if (!responsaveisEntidadeMap.containsKey(re.getId())) {
                    Responsavel encontrado = responsavelGateway.findById(re.getId())
                            .orElseThrow(() -> new BusinessException("Responsável não encontrado."));
                    if (Boolean.TRUE.equals(encontrado.getRemovido())) {
                        throw new BusinessException("Responsável não encontrado.");
                    }
                    if (encontrado.getCliente() == null || !encontrado.getCliente().getId().equals(idClienteLogado)) {
                        throw new BusinessException("O responsável selecionado não pertence ao seu cliente.");
                    }
                    responsaveisEntidadeMap.put(encontrado.getId(), encontrado);
                }
            }
        }
        pedestre.setResponsaveisEntidade(new ArrayList<>(responsaveisEntidadeMap.values()));
    }

    private void validarCamposObrigatoriosDinamicos(
            Pedestre pedestre,
            Map<String, String> parametrosCliente
    ) {
        List<String> listaObrigatorios = obterListaStringParametro(
                parametrosCliente, ParametroCatalogo.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE);
        if (listaObrigatorios == null || listaObrigatorios.isEmpty()) {
            return;
        }

        Set<String> obrigatorios = Set.copyOf(listaObrigatorios);

        if (obrigatorios.contains("data.nascimento") && pedestre.getDataNascimento() == null) {
            throw new BusinessException("Data de nascimento é obrigatória.");
        }
        if (obrigatorios.contains("email") && (pedestre.getEmail() == null || pedestre.getEmail().isBlank())) {
            throw new BusinessException("E-mail é obrigatório.");
        }
        if (obrigatorios.contains("cpf") && (pedestre.getCpf() == null || pedestre.getCpf().isBlank())) {
            throw new BusinessException("CPF é obrigatório.");
        }
        if (obrigatorios.contains("genero") && pedestre.getGenero() == null) {
            throw new BusinessException("Gênero é obrigatório.");
        }
        if (obrigatorios.contains("rg") && (pedestre.getRg() == null || pedestre.getRg().isBlank())) {
            throw new BusinessException("RG é obrigatório.");
        }
        if (obrigatorios.contains("telefone") && (pedestre.getTelefone() == null || pedestre.getTelefone().isBlank())) {
            throw new BusinessException("Telefone é obrigatório.");
        }
        if (obrigatorios.contains("celular") && (pedestre.getCelular() == null || pedestre.getCelular().isBlank())) {
            throw new BusinessException("Celular é obrigatório.");
        }
        if (obrigatorios.contains("observacoes") && (pedestre.getObservacoes() == null || pedestre.getObservacoes().isBlank())) {
            throw new BusinessException("Observações são obrigatórias.");
        }
        if (obrigatorios.contains("responsavel")) {
            boolean temResponsavelPedestre = pedestre.getResponsaveis() != null && !pedestre.getResponsaveis().isEmpty();
            boolean temResponsavelEntidade = pedestre.getResponsaveisEntidade() != null && !pedestre.getResponsaveisEntidade().isEmpty();
            if (!temResponsavelPedestre && !temResponsavelEntidade) {
                throw new BusinessException("Responsável é obrigatório.");
            }
        }
        if (obrigatorios.contains("empresa")) {
            if (pedestre.isVisitante()) {
                boolean temVisitada = (pedestre.getEmpresaVisitada() != null && !pedestre.getEmpresaVisitada().isBlank())
                        || pedestre.getEmpresaVisitadaRef() != null;
                if (!temVisitada) {
                    throw new BusinessException("Empresa visitada é obrigatória.");
                }
            } else if (pedestre.getEmpresa() == null) {
                throw new BusinessException("Empresa é obrigatória.");
            }
        }
        if (pedestre.isPedestre()) {
            if (obrigatorios.contains("departamento") && pedestre.getDepartamento() == null) {
                throw new BusinessException("Departamento é obrigatório.");
            }
            if (obrigatorios.contains("cargo") && pedestre.getCargo() == null) {
                throw new BusinessException("Cargo é obrigatório.");
            }
            if (obrigatorios.contains("centro.custo") && pedestre.getCentroCusto() == null) {
                throw new BusinessException("Centro de custo é obrigatório.");
            }
        }
        if (obrigatorios.contains("endereco")
                && (pedestre.getEndereco() == null
                || pedestre.getEndereco().getCep() == null
                || pedestre.getEndereco().getCep().isBlank())) {
            throw new BusinessException("Endereço é obrigatório.");
        }
        if (obrigatorios.contains("cartao.acesso")
                && (pedestre.getCodigoCartaoAcesso() == null || pedestre.getCodigoCartaoAcesso().isBlank())) {
            throw new BusinessException("Código do cartão de acesso é obrigatório.");
        }
        if (obrigatorios.contains("foto")
                && (pedestre.getFoto() == null || pedestre.getFoto().length == 0)) {
            throw new BusinessException("A foto é obrigatória.");
        }
    }

    private void validarCpfEUnicidades(
            Pedestre pedestre,
            Long idClienteLogado,
            Map<String, String> parametrosCliente
    ) {
        boolean validarCpfValido = obterBooleanParametro(parametrosCliente, ParametroCatalogo.VALIDA_CPF_VALIDO);
        if (validarCpfValido && pedestre.getCpf() != null && !pedestre.getCpf().isBlank()) {
            if (!isCpfValido(pedestre.getCpf())) {
                throw new BusinessException("CPF inválido.");
            }
        }

        boolean validarMatriculaDuplicada = obterBooleanParametro(parametrosCliente, ParametroCatalogo.VALIDAR_MATRICULAS_DUPLICADAS);
        if (validarMatriculaDuplicada && pedestre.getMatricula() != null && !pedestre.getMatricula().isBlank()) {
            if (pedestreGateway.existsAtivoByMatriculaAndCliente(pedestre.getMatricula(), idClienteLogado, pedestre.getId())) {
                throw new BusinessException("Matrícula já cadastrada para outro pedestre.");
            }
        }

        boolean validarCpfDuplicado = obterBooleanParametro(parametrosCliente, ParametroCatalogo.VALIDAR_CPF_DUPLICADO);
        if (validarCpfDuplicado && pedestre.getCpf() != null && !pedestre.getCpf().isBlank()) {
            if (pedestreGateway.existsAtivoByCpfAndCliente(pedestre.getCpf(), idClienteLogado, pedestre.getId())) {
                throw new BusinessException("CPF já cadastrado para outro pedestre.");
            }
        }

        boolean validarRgDuplicado = obterBooleanParametro(parametrosCliente, ParametroCatalogo.VALIDAR_RG_DUPLICADO);
        if (validarRgDuplicado && pedestre.getRg() != null && !pedestre.getRg().isBlank()) {
            if (pedestreGateway.existsAtivoByRgAndCliente(pedestre.getRg(), idClienteLogado, pedestre.getId())) {
                throw new BusinessException("RG já cadastrado para outro pedestre.");
            }
        }

        boolean validarCartaoDuplicado = obterBooleanParametro(parametrosCliente, ParametroCatalogo.VALIDAR_CARTAO_ACESSO_DUPLICADO);
        if (validarCartaoDuplicado
                && pedestre.getCodigoCartaoAcesso() != null
                && !pedestre.getCodigoCartaoAcesso().isBlank()) {
            if (pedestreGateway.existsAtivoByCartaoNormalizadoAndCliente(
                    pedestre.getCodigoCartaoAcesso(), idClienteLogado, pedestre.getId())) {
                throw new BusinessException("Cartão de acesso já cadastrado para outro pedestre ativo.");
            }
        }
    }

    public static boolean isCpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11 || digits.matches("(\\d)\\1{10}")) {
            return false;
        }
        try {
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                soma += (digits.charAt(i) - '0') * peso--;
            }
            int resto = 11 - (soma % 11);
            char digito1 = (resto >= 10) ? '0' : (char) (resto + '0');
            if (digito1 != digits.charAt(9)) {
                return false;
            }

            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += (digits.charAt(i) - '0') * peso--;
            }
            resto = 11 - (soma % 11);
            char digito2 = (resto >= 10) ? '0' : (char) (resto + '0');
            return digito2 == digits.charAt(10);
        } catch (Exception e) {
            return false;
        }
    }

    private void tratarRegrasAcesso(Pedestre pedestre, Usuario usuarioLogado) {
        Long idCliente = usuarioLogado.getCliente().getId();
        List<PedestreRegra> regras = pedestre.getRegras() != null
                ? new ArrayList<>(pedestre.getRegras())
                : new ArrayList<>();

        List<PedestreRegra> ativas = regras.stream()
                .filter(r -> r != null && (r.getRemovido() == null || !r.getRemovido()) && r.getDataRemovido() == null)
                .toList();

        if (ativas.size() > 1) {
            throw new BusinessException("Não é permitido cadastrar mais de uma regra ativa para o pedestre.");
        }

        for (PedestreRegra pr : ativas) {
            if (pr.getRegra() == null || pr.getRegra().getId() == null) {
                throw new BusinessException("A regra de acesso é obrigatória.");
            }

            Regra regra = regraGateway.findById(pr.getRegra().getId())
                    .orElseThrow(() -> new BusinessException("Regra de acesso não encontrada."));

            if (Boolean.TRUE.equals(regra.getRemovido())) {
                throw new BusinessException("Regra de acesso não encontrada.");
            }

            if (regra.getCliente() == null || !regra.getCliente().getId().equals(idCliente)) {
                throw new BusinessException("A regra de acesso não pertence ao seu cliente.");
            }

            pr.setRegra(regra);
            pr.setRemovido(false);
            aplicarEValidarCamposPedestreRegra(pr, regra);
        }

        if (ativas.isEmpty()
                && pedestre.isVisitante()
                && !Boolean.TRUE.equals(pedestre.getSempreLiberado())) {
            PedestreRegra regraPadrao = criarPedestreRegraPadraoVisitante(usuarioLogado);
            regras.add(regraPadrao);
        }

        pedestre.setRegras(regras);
    }

    private void aplicarEValidarCamposPedestreRegra(PedestreRegra pr, Regra regra) {
        TipoRegra tipo = regra.getTipo();

        if (TipoRegra.ACESSO_CREDITO.equals(tipo)) {
            if (pr.getQtdeDeCreditos() == null) {
                pr.setQtdeDeCreditos(regra.getQtdeDeCreditos());
            }
            if (pr.getQtdeDeCreditos() == null) {
                throw new BusinessException("A quantidade de créditos é obrigatória.");
            }
            pr.setQtdeTotalDeCreditos(pr.getQtdeDeCreditos());
            if (pr.getDiasValidadeCredito() == null) {
                pr.setDiasValidadeCredito(regra.getDiasValidadeCredito());
            }
        } else if (TipoRegra.ACESSO_PERIODO.equals(tipo)) {
            if (pr.getDataInicioPeriodo() == null) {
                pr.setDataInicioPeriodo(regra.getDataInicioPeriodo());
            }
            if (pr.getDataFimPeriodo() == null) {
                pr.setDataFimPeriodo(regra.getDataFimPeriodo());
            }
            if (pr.getDataInicioPeriodo() == null || pr.getDataFimPeriodo() == null) {
                throw new BusinessException("As datas inicial e final do período são obrigatórias.");
            }
            if (pr.getDataInicioPeriodo().isAfter(pr.getDataFimPeriodo())) {
                throw new BusinessException("A data inicial do período não pode ser posterior à data final.");
            }
        } else if (TipoRegra.ACESSO_UNICO.equals(tipo)) {
            pr.setQtdeDeCreditos(1L);
            pr.setQtdeTotalDeCreditos(1L);
        } else if (TipoRegra.ACESSO_ESCALA_3_3.equals(tipo)) {
            if (pr.getDataInicioPeriodo() == null) {
                pr.setDataInicioPeriodo(regra.getDataInicioPeriodo());
            }
            if (pr.getDataInicioEscala3_3() == null) {
                pr.setDataInicioEscala3_3(pr.getDataInicioPeriodo());
            }
        }

        if (tipo != null && tipo.usaHorarios()) {
            if ((pr.getHorarios() == null || pr.getHorarios().isEmpty())
                    && regra.getHorarios() != null
                    && !regra.getHorarios().isEmpty()) {
                List<Horario> horariosCopia = regra.getHorarios().stream()
                        .filter(h -> h.getRemovido() == null || !h.getRemovido())
                        .<Horario>map(h -> Horario.builder()
                                .nome(h.getNome())
                                .status(h.getStatus())
                                .diasSemana(h.getDiasSemana())
                                .horarioInicio(h.getHorarioInicio())
                                .horarioFim(h.getHorarioFim())
                                .qtdeDeCreditos(h.getQtdeDeCreditos())
                                .removido(false)
                                .build())
                        .toList();
                pr.setHorarios(horariosCopia);
            }
        } else {
            pr.setHorarios(new ArrayList<>());
        }
    }

    private PedestreRegra criarPedestreRegraPadraoVisitante(Usuario usuarioLogado) {
        Long idCliente = usuarioLogado.getCliente().getId();
        Regra regra = regraGateway.findByNomeAndCliente(NOME_REGRA_PADRAO_VISITANTE, idCliente)
                .orElseGet(() -> regraGateway.save(Regra.builder()
                        .nome(NOME_REGRA_PADRAO_VISITANTE)
                        .tipoPedestre(TipoPedestre.VISITANTE)
                        .tipo(TipoRegra.ACESSO_UNICO)
                        .status(Status.ATIVO)
                        .qtdeDeCreditos(1L)
                        .cliente(usuarioLogado.getCliente())
                        .removido(false)
                        .build()));

        return PedestreRegra.builder()
                .regra(regra)
                .qtdeDeCreditos(1L)
                .qtdeTotalDeCreditos(1L)
                .removido(false)
                .build();
    }

    private void tratarEquipamentos(Pedestre pedestre, Long idClienteLogado) {
        if (pedestre.getEquipamentos() == null || pedestre.getEquipamentos().isEmpty()) {
            pedestre.setEquipamentos(new ArrayList<>());
            return;
        }

        List<PedestreEquipamento> lista = new ArrayList<>(pedestre.getEquipamentos());
        for (PedestreEquipamento pe : lista) {
            if (pe == null || Boolean.TRUE.equals(pe.getRemovido())) {
                continue;
            }
            if (pe.getEquipamento() == null || pe.getEquipamento().getId() == null) {
                throw new BusinessException("O equipamento é obrigatório.");
            }
            Equipamento equip = equipamentoGateway.findById(pe.getEquipamento().getId())
                    .orElseThrow(() -> new BusinessException("Equipamento não encontrado."));
            if (Boolean.TRUE.equals(equip.getRemovido())) {
                throw new BusinessException("Equipamento não encontrado.");
            }
            if (equip.getCliente() == null || !equip.getCliente().getId().equals(idClienteLogado)) {
                throw new BusinessException("O equipamento selecionado não pertence ao seu cliente.");
            }
            pe.setEquipamento(equip);
            pe.setRemovido(false);
        }
        pedestre.setEquipamentos(lista);
    }

    private void tratarDocumentos(Pedestre pedestre, Pedestre pedestreExistente) {
        if (pedestre.getDocumentos() == null || pedestre.getDocumentos().isEmpty()) {
            pedestre.setDocumentos(new ArrayList<>());
            return;
        }

        Map<Long, byte[]> arquivosExistentes = new HashMap<>();
        if (pedestreExistente != null && pedestreExistente.getDocumentos() != null) {
            for (Documento docExistente : pedestreExistente.getDocumentos()) {
                if (docExistente != null && docExistente.getId() != null) {
                    arquivosExistentes.put(docExistente.getId(), docExistente.getArquivo());
                }
            }
        }

        List<Documento> lista = new ArrayList<>(pedestre.getDocumentos());
        for (Documento doc : lista) {
            if (doc == null || Boolean.TRUE.equals(doc.getRemovido())) {
                continue;
            }
            if (doc.getNome() == null || doc.getNome().isBlank()) {
                throw new BusinessException("O nome do documento é obrigatório.");
            }
            String nomeNormalizado = doc.getNome().trim().replaceAll("\\s+", "_");
            if (!isFormatoAceitoDocumento(nomeNormalizado)) {
                throw new BusinessException("Formato de documento inválido. Formatos aceitos: .png, .jpeg, .jpg, .pdf.");
            }
            doc.setNome(nomeNormalizado);

            if (doc.getArquivo() == null || doc.getArquivo().length == 0) {
                if (doc.getId() != null && arquivosExistentes.containsKey(doc.getId())) {
                    doc.setArquivo(arquivosExistentes.get(doc.getId()));
                } else {
                    throw new BusinessException("O arquivo do documento é obrigatório.");
                }
            }
            doc.setRemovido(false);
        }
        pedestre.setDocumentos(lista);
    }

    public static boolean isFormatoAceitoDocumento(String nome) {
        if (nome == null) {
            return false;
        }
        String lower = nome.toLowerCase(Locale.ROOT);
        return lower.endsWith(".png")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".jpg")
                || lower.endsWith(".pdf");
    }

    private void tratarBiometrias(
            Pedestre pedestre,
            Pedestre pedestreExistente,
            Map<String, String> parametrosCliente
    ) {
        if (pedestre.getBiometrias() == null) {
            if (pedestreExistente != null && pedestreExistente.getBiometrias() != null) {
                pedestre.setBiometrias(new ArrayList<>(pedestreExistente.getBiometrias()));
            } else {
                pedestre.setBiometrias(new ArrayList<>());
            }
            return;
        }

        Map<Long, Biometria> existentesMap = new HashMap<>();
        if (pedestreExistente != null && pedestreExistente.getBiometrias() != null) {
            for (Biometria b : pedestreExistente.getBiometrias()) {
                if (b != null && b.getId() != null) {
                    existentesMap.put(b.getId(), b);
                }
            }
        }

        List<Biometria> lista = new ArrayList<>(pedestre.getBiometrias());
        int ativasCount = 0;
        for (Biometria bio : lista) {
            if (bio == null || Boolean.TRUE.equals(bio.getRemovido())) {
                continue;
            }
            if (bio.getDedo() == null) {
                throw new BusinessException("O dedo da biometria é obrigatório.");
            }
            if (bio.getId() != null && existentesMap.containsKey(bio.getId())) {
                Biometria existente = existentesMap.get(bio.getId());
                if (bio.getTemplate() == null || bio.getTemplate().length == 0) {
                    bio.setTemplate(existente.getTemplate());
                }
                if (bio.getSample() == null || bio.getSample().length == 0) {
                    bio.setSample(existente.getSample());
                }
            }
            bio.setRemovido(false);
            ativasCount++;
        }

        String limiteStr = obterValorParametro(parametrosCliente, ParametroCatalogo.LIMITE_DIGITAIS_PEDESTRE);
        if (limiteStr != null && !limiteStr.equalsIgnoreCase("Sem limite")) {
            try {
                int limite = Integer.parseInt(limiteStr.trim());
                if (ativasCount > limite) {
                    throw new BusinessException("Limite máximo de digitais excedido para o pedestre.");
                }
            } catch (NumberFormatException ignored) {
                // "Sem limite"
            }
        }

        pedestre.setBiometrias(lista);
    }

    private void tratarMensagensPersonalizadas(Pedestre pedestre) {
        if (pedestre.getMensagensPersonalizadas() == null || pedestre.getMensagensPersonalizadas().isEmpty()) {
            pedestre.setMensagensPersonalizadas(new ArrayList<>());
            return;
        }

        List<MensagemEquipamento> lista = new ArrayList<>(pedestre.getMensagensPersonalizadas());
        for (MensagemEquipamento msg : lista) {
            if (msg == null || Boolean.TRUE.equals(msg.getRemovido())) {
                continue;
            }
            if (msg.getMensagem() == null || msg.getMensagem().isBlank()) {
                throw new BusinessException("O texto da mensagem personalizada é obrigatório.");
            }
            msg.setMensagem(msg.getMensagem().trim());
            if (msg.getNome() != null) {
                msg.setNome(msg.getNome().trim());
            }
            if (msg.getStatus() == null) {
                msg.setStatus(Status.ATIVO);
            }
            msg.setRemovido(false);
        }
        pedestre.setMensagensPersonalizadas(lista);
    }

    private void tratarCotas(Pedestre pedestre, Usuario usuarioLogado) {
        if (pedestre.getCotas() == null || pedestre.getCotas().isEmpty()) {
            pedestre.setCotas(new ArrayList<>());
            return;
        }

        List<HistoricoCota> lista = new ArrayList<>(pedestre.getCotas());
        for (HistoricoCota cota : lista) {
            if (cota == null || Boolean.TRUE.equals(cota.getRemovido())) {
                continue;
            }
            if (cota.getMes() == null || cota.getMes() < 0 || cota.getMes() > 11) {
                throw new BusinessException("Mês da cota inválido (deve ser entre 0 e 11).");
            }
            if (cota.getAno() == null || cota.getAno() < 1900) {
                throw new BusinessException("Ano da cota inválido.");
            }
            if (cota.getCotaMensal() == null || cota.getCotaMensal() < 0) {
                throw new BusinessException("O valor da cota mensal é obrigatório.");
            }
            cota.setCliente(usuarioLogado.getCliente());
            cota.setRemovido(false);
        }
        pedestre.setCotas(lista);
    }

    private void tratarAcessoApp(Pedestre pedestre, Pedestre pedestreExistente) {
        if (pedestre.getLogin() != null && !pedestre.getLogin().isBlank()) {
            pedestre.setLogin(pedestre.getLogin().trim());
            if (pedestre.getSenha() != null && !pedestre.getSenha().isBlank()) {
                pedestre.setSenha(passwordGateway.hash(pedestre.getSenha()));
            } else if (pedestreExistente != null) {
                pedestre.setSenha(pedestreExistente.getSenha());
            }
        } else {
            pedestre.setLogin(null);
            pedestre.setSenha(null);
            pedestre.setPerfilApp(null);
        }
    }

    private byte[] comprimirFotoJpeg(byte[] original, int maxLadoPx) {
        if (original == null || original.length == 0) {
            return original;
        }
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(original));
            if (img == null) {
                return original;
            }
            int largura = img.getWidth();
            int altura = img.getHeight();
            int maiorLado = Math.max(largura, altura);
            if (maiorLado > maxLadoPx) {
                double escala = (double) maxLadoPx / maiorLado;
                largura = Math.max(1, (int) Math.round(largura * escala));
                altura = Math.max(1, (int) Math.round(altura * escala));
            }
            BufferedImage saida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = saida.createGraphics();
            g.drawImage(img, 0, 0, largura, altura, null);
            g.dispose();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(saida, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            return original;
        }
    }
}
