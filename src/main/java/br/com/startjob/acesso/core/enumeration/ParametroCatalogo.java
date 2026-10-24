package br.com.startjob.acesso.core.enumeration;

import br.com.startjob.acesso.core.domain.parametro.OpcaoParametro;
import br.com.startjob.acesso.core.exception.BusinessException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum ParametroCatalogo {

    // --- SEÇÃO: GERAIS ---
    HABILITA_APP_PEDESTRE(
            "Habilita App do Pedestre",
            "Habilita App do Pedestre",
            SecaoParametro.GERAIS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    HABILITA_RELATORIO_RONA(
            "Habilita relatorio rona",
            "Habilita relatorio rona",
            SecaoParametro.GERAIS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    HABILITA_MODO_CORRESPONDENCIA(
            "Modulo correspondencia",
            "Modulo correspondencia",
            SecaoParametro.GERAIS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    EXPORTAR_FOTOS_HABILITADO(
            "Exportar: habilitar exportacao",
            "Exportar: habilitar exportacao",
            SecaoParametro.GERAIS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    EXPORTAR_FOTOS_PADRAO_NOME(
            "Exportar: padrao do nome ({matricula|nome})",
            "Exportar: padrao do nome ({matricula|nome})",
            SecaoParametro.GERAIS,
            TipoDadoParametro.TEXTO,
            "{matricula|nome}"
    ),
    EXPORTAR_FOTOS_FORMATO(
            "Exportar: formato (jpg ou png)",
            "Exportar: formato (jpg ou png)",
            SecaoParametro.GERAIS,
            TipoDadoParametro.TEXTO,
            "jpg"
    ),
    ENABLE_SEND_EVENTS(
            "enableSendEvents",
            "Habilita envio de eventos",
            SecaoParametro.GERAIS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    SYNC_INTERVAL_MINUTES(
            "syncIntervalMinutes",
            "Intervalo da sincronização automática com a web (minutos)",
            SecaoParametro.GERAIS,
            TipoDadoParametro.INTEGER,
            "5",
            0,
            999999
    ),

    // --- SEÇÃO: CADASTRO ---
    GERAR_MATRICULA_SEQUENCIAL(
            "Gerar matricula sequencial",
            "Gerar matricula sequencial",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    FORMATO_DATA_PARA_IMPORTACAO(
            "Formato de data para importação de dados",
            "Formato de data para importação de dados",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.FORMATO_DATA,
            "dd/MM/yyyy"
    ),
    FORMATO_HORA_PARA_IMPORTACAO(
            "Formato de hora para importação de dados",
            "Formato de hora para importação de dados",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.FORMATO_HORA,
            "HH:mm"
    ),
    CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE(
            "Campos obrigatórios para cadastro de pedestres",
            "Campos obrigatórios para cadastro de pedestres",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.LISTA_STRING,
            "",
            null,
            null,
            List.of(
                    new OpcaoParametro("data.nascimento", "Data de nascimento"),
                    new OpcaoParametro("email", "E-mail"),
                    new OpcaoParametro("cpf", "CPF"),
                    new OpcaoParametro("genero", "Gênero"),
                    new OpcaoParametro("rg", "RG"),
                    new OpcaoParametro("telefone", "Telefone"),
                    new OpcaoParametro("celular", "Celular"),
                    new OpcaoParametro("observacoes", "Observações"),
                    new OpcaoParametro("responsavel", "Responsável"),
                    new OpcaoParametro("empresa", "Empresa"),
                    new OpcaoParametro("departamento", "Departamento"),
                    new OpcaoParametro("cargo", "Cargo"),
                    new OpcaoParametro("centro.custo", "Centro de custo"),
                    new OpcaoParametro("endereco", "Endereço"),
                    new OpcaoParametro("cartao.acesso", "Código do cartão de acesso"),
                    new OpcaoParametro("foto", "Foto")
            ),
            true
    ),
    VALIDAR_MATRICULAS_DUPLICADAS(
            "Validar matriculas duplicadas",
            "Validar matriculas duplicadas",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    VALIDAR_CPF_DUPLICADO(
            "Validar CPF duplicado",
            "Validar CPF duplicado",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    VALIDA_CPF_VALIDO(
            "Validar CPF (DIGITO VERIFICADOR)",
            "Validar CPF (DIGITO VERIFICADOR)",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    VALIDAR_RG_DUPLICADO(
            "Validar RG duplicado",
            "Validar RG duplicado",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    CADASTRO_EM_LOTE(
            "Realiza cadastros em lote (limpa tela de cadastro ao finalizar)",
            "Realiza cadastros em lote (limpa tela de cadastro ao finalizar)",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    LOCAL_PADRAO_PEDESTRE(
            "Local padrao pedestre",
            "Local padrao pedestre",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.TEXTO,
            ""
    ),
    LOCAL_PADRAO_VISITANTE(
            "Local padrao visitante",
            "Local padrao visitante",
            SecaoParametro.CADASTRO,
            TipoDadoParametro.TEXTO,
            ""
    ),

    // --- SEÇÃO: CARTÃO E MATRÍCULA ---
    ESCOLHER_QTDE_DIGITOS_CARTAO(
            "Escolher quantidade de digítos do cartão (até 10)",
            "Escolher quantidade de dígitos do cartão (1 a 16)",
            SecaoParametro.CARTAO,
            TipoDadoParametro.INTEGER,
            "10",
            1,
            16
    ),
    PERMITIR_CAMPO_ADICIONAL_CRACHA(
            "Permitir campo adicional de crachá/matricula",
            "Permitir campo adicional de crachá/matricula",
            SecaoParametro.CARTAO,
            TipoDadoParametro.BOOLEAN,
            "true"
    ),
    VALIDAR_CARTAO_ACESSO_DUPLICADO(
            "Validar cartão de acesso duplicado",
            "Validar cartão de acesso duplicado",
            SecaoParametro.CARTAO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    PREENCHER_CARTAO_COM_MATRICULA(
            "preencher cartao com matricula",
            "preencher cartao com matricula",
            SecaoParametro.CARTAO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    PREENCHER_CARTAO_AUTO(
            "preencher cartao automaticamente (8 digitos)",
            "preencher cartao automaticamente (8 digitos)",
            SecaoParametro.CARTAO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    REMOVE_CARTAO_EXCLUIDO(
            "Remove o cartão dos Pedestres removidos",
            "Remove o cartão dos Pedestres removidos",
            SecaoParametro.CARTAO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    ENABLE_OFFLINE_CARD(
            "enableOfflineCard",
            "Habilita cartão offline",
            SecaoParametro.CARTAO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    DECREMENTA_ENTRADA(
            "decrementaEntrada",
            "Decrementa crédito na entrada",
            SecaoParametro.CARTAO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    CARD_MASTER(
            "cardMaster",
            "Cartão master",
            SecaoParametro.CARTAO,
            TipoDadoParametro.TEXTO,
            ""
    ),

    // --- SEÇÃO: BIOMETRIA ---
    LIMITE_DIGITAIS_PEDESTRE(
            "Limite de cadastro de digitais",
            "Limite de cadastro de digitais",
            SecaoParametro.BIOMETRIA,
            TipoDadoParametro.SELECT,
            "Sem limite",
            null,
            null,
            List.of(
                    new OpcaoParametro("Sem limite", "Sem limite"),
                    new OpcaoParametro("1", "1 digital"),
                    new OpcaoParametro("2", "2 digitais")
            ),
            true
    ),

    // --- SEÇÃO: FACIAL E HIKVISION ---
    HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL(
            "Habilita acesso por reconhecimento facial",
            "Habilita acesso por reconhecimento facial",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    ENVIO_FACIAL(
            "Habilitar envio face hikivision",
            "Habilitar envio face hikivision",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO(
            "Geração de link para cadastro facial externo: validade do link (em dias)",
            "Geração de link para cadastro facial externo: validade do link (em dias)",
            SecaoParametro.FACIAL,
            TipoDadoParametro.INTEGER,
            "3",
            1,
            365
    ),
    REMOVE_VISITANTE_CAMERA_SAIDA(
            "removeVisitanteCameraSaida",
            "Remove visitante da câmera na saída",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    SAIDA_HAKIVISION_SEM_VERIFICAR(
            "saidaHakivisionSemVerificar",
            "Saída Hikvision sem verificar",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    SHOULD_MAKE_IMAGE_RESIZE(
            "shouldMakeImageResize",
            "Redimensionar fotos no envio",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    IMAGE_SIZE_REQUEST_SERVER(
            "imageSizeRequestServer",
            "Tamanho da imagem no servidor",
            SecaoParametro.FACIAL,
            TipoDadoParametro.INTEGER,
            "300",
            0,
            999999
    ),
    IMAGE_TARGET_WIDTH_REQUEST_SERVER(
            "imageTargetWidthRequestServer",
            "Largura alvo da imagem",
            SecaoParametro.FACIAL,
            TipoDadoParametro.INTEGER,
            "200",
            0,
            999999
    ),
    IMAGE_TARGET_HEIGHT_REQUEST_SERVER(
            "imageTargetHeightRequestServer",
            "Altura alvo da imagem",
            SecaoParametro.FACIAL,
            TipoDadoParametro.INTEGER,
            "240",
            0,
            999999
    ),
    SINC_HIKIVISION(
            "sincHikivision",
            "Sincroniza pedestres na Hikvision",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    REMOVE_INATIVOS(
            "removeInativos",
            "Remove inativos da Hikvision",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "true"
    ),
    BLOCK_INATIVOS(
            "blockInativos",
            "Bloqueia inativos na Hikvision",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    VISITANTE_HIKIVISION(
            "visitanteHikivision",
            "Envia visitante para Hikvision",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    HIKI_VISION_PLAN_HORARIO(
            "hikiVisionPlanHorario",
            "Usa plano de horário Hikvision",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    DELETE_PHOTO_FROM_INACTIVE_PEDESTRIAN(
            "deletePhotoFromInactivePedestrian",
            "Apaga foto de pedestre inativo",
            SecaoParametro.FACIAL,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),

    // --- SEÇÃO: QR CODE ---
    PERMITIR_ACESSO_QR_CODE(
            "Permitir acesso via QR Code",
            "Permitir acesso via QR Code",
            SecaoParametro.QR_CODE,
            TipoDadoParametro.BOOLEAN,
            "true"
    ),
    TIPO_QR_CODE(
            "Permitir acesso via QR Code: Habilita QRCode dinâmico",
            "Permitir acesso via QR Code: Habilita QRCode dinâmico",
            SecaoParametro.QR_CODE,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    TIPO_QR_CODE_PADRAO(
            "Permitir acesso via QR Code: Tipo padrão",
            "Permitir acesso via QR Code: Tipo padrão",
            SecaoParametro.QR_CODE,
            TipoDadoParametro.SELECT,
            "ESTATICO",
            null,
            null,
            List.of(
                    new OpcaoParametro("ESTATICO", "Estático"),
                    new OpcaoParametro("DINAMICO_TEMPO", "Dinâmico por tempo"),
                    new OpcaoParametro("DINAMICO_USO", "Dinâmico por uso")
            ),
            true
    ),
    TEMPO_QRCODE_DINAMICO(
            "Permitir acesso via QR Code: Tempo para renovação do tipo QRCode Dinâmico por tempo (em minutos)",
            "Permitir acesso via QR Code: Tempo para renovação do tipo QRCode Dinâmico por tempo (em minutos)",
            SecaoParametro.QR_CODE,
            TipoDadoParametro.INTEGER,
            "5",
            1,
            1440
    ),

    // --- SEÇÃO: REGRAS DE ACESSO ---
    EXIBE_CAMPO_SEMPRE_LIBERADO_PARA_TODOS(
            "Exibe escolha \"Sempre liberado\" para todos os usuários",
            "Exibe escolha \"Sempre liberado\" para todos os usuários",
            SecaoParametro.ACESSO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    TEMPO_TOLERANCIA_ENTRADA_E_SAIDA(
            "Tempo de tolerancia (Entrada/Saida)",
            "Tempo de tolerancia (Entrada/Saida)",
            SecaoParametro.ACESSO,
            TipoDadoParametro.INTEGER,
            "5",
            1,
            60
    ),
    RESTRICT_ACCESS(
            "restrictAccess",
            "Restringe quantidade de acessos",
            SecaoParametro.ACESSO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    RESTRICT_ACCESS_DAYS(
            "restrictAccessDays",
            "Limite de acessos no período",
            SecaoParametro.ACESSO,
            TipoDadoParametro.INTEGER,
            "0",
            0,
            999999
    ),
    SAIDA_SEM_VERIFICAR(
            "saidaSemVerificar",
            "Saída sem verificar regra",
            SecaoParametro.ACESSO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    MIN_TIME_BETWEEN_ACCESS(
            "minTimeBetweenAccess",
            "Anti-passback / tempo mínimo entre entradas (minutos, 0 = desligado) — apenas catraca online",
            SecaoParametro.ACESSO,
            TipoDadoParametro.INTEGER,
            "0",
            0,
            60
    ),
    ANTI_PASSBACK_APLICAR_SAIDA(
            "antiPassbackAplicarSaida",
            "Aplicar anti-passback também na saída (apenas catraca online)",
            SecaoParametro.ACESSO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    PORCENTAGEM_REVISTA(
            "porcentagemRevista",
            "Porcentagem de revista",
            SecaoParametro.ACESSO,
            TipoDadoParametro.INTEGER,
            "0",
            0,
            999999
    ),
    IGNORAR_ACESSOS_HORARIO_ERRADO(
            "ignorarAcessosHorarioErrado",
            "Ignorar acessos com horário errado",
            SecaoParametro.ACESSO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    ENABLE_DSR(
            "enableDsr",
            "Habilita DSR",
            SecaoParametro.ACESSO,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),

    // --- SEÇÃO: CATRACA E LEITORES ---
    DOIS_DISPOSITIVOS(
            "doisDispositivos",
            "Dois dispositivos",
            SecaoParametro.CATRACA,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    SAIDA_ESPECIFICA(
            "saidaEspecifica",
            "Saída específica",
            SecaoParametro.CATRACA,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    RECONECT_DEVICE_ON_RECEIVE_CURRENT_EVENT(
            "reconectDeviceOnReceiveCurrentEvent",
            "Reconectar dispositivo ao receber evento atual",
            SecaoParametro.CATRACA,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    DOIS_SENTIDOS(
            "doisSentidos",
            "Dois sentidos",
            SecaoParametro.CATRACA,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    REENVIO_DE_MENSAGEM_PADRAO_ONLINE(
            "reenvioDeMensagemPadraoOnline",
            "Reenvio de mensagem padrão online",
            SecaoParametro.CATRACA,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),

    // --- SEÇÃO: MENSAGENS DA CATRACA ---
    MESSAGE_ENTRY_ALLOWED(
            "messageEntryAllowed",
            "Entrada liberada",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "ENTRADA LIBERADA"
    ),
    MESSAGE_EXIT_ALLOWED(
            "messageExitAllowed",
            "Saída liberada",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "SAIDA LIBERADA"
    ),
    MESSAGE_ALLOWED(
            "messageAllowed",
            "Acesso permitido",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "ACESSO LIBERADO"
    ),
    MESSAGE_NOT_ALLOWED(
            "messageNotAllowed",
            "Acesso negado",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "ACESSO NEGADO"
    ),
    MESSAGE_NOT_ALLOWED_TODAY(
            "messageNotAllowedToday",
            "Fora do dia permitido",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "NAO LIBERADO HOJE"
    ),
    MESSAGE_NOT_ALLOWED_NOW(
            "messageNotAllowedNow",
            "Fora do horário",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "FORA DE HORARIO"
    ),
    MESSAGE_NOT_ALLOWED_NO_CREDITS(
            "messageNotAllowedNoCredits",
            "Sem créditos",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "SEM CREDITOS"
    ),
    MESSAGE_NOT_FOUND(
            "messageNotFound",
            "Não encontrado",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "NAO ENCONTRADO"
    ),
    MESSAGE_ERROR(
            "messageError",
            "Erro na verificação",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "ERRO"
    ),
    MESSAGE_WAIT(
            "messageWait",
            "Aguarde (lendo digital)",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "AGUARDE"
    ),
    MESSAGE_ACCESS_AUTHORIZED(
            "messageAccessAuthorized",
            "Mensagem de acesso autorizado (catraca)",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "AUTORIZADO",
            null,
            null,
            null,
            false
    ),
    MESSAGE_AUTHORIZED(
            "messageAuthorized",
            "Mensagem de acesso autorizado (motor)",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "AUTORIZADO",
            null,
            null,
            null,
            false
    ),
    MESSAGE_TOLERANCE_PERIOD(
            "messageTolerancePeriod",
            "Mensagem de período de tolerância",
            SecaoParametro.MENSAGENS,
            TipoDadoParametro.TEXTO,
            "TOLERANCIA",
            null,
            null,
            null,
            false
    ),

    // --- SEÇÃO: MENSAGENS AVANÇADAS ---
    MESSAGE_ALLOWED_ONLY_ONCE(
            "messageAllowedOnlyOnce",
            "Já acessou hoje (restrição diária)",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "JA UTILIZADO"
    ),
    MESSAGE_NOT_ALLOWED_FACE_REQUIRED(
            "messageNotAllowedFaceRequired",
            "Face obrigatória",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "FACE OBRIGATORIA"
    ),
    MESSAGE_NOT_ALLOWED_ORIGEM(
            "messageNotAllowedOrigem",
            "Equipamento restrito",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "EQUIPAMENTO RESTRITO"
    ),
    MESSAGE_NOT_ALLOWED_SENSOR(
            "messageNotAllowedSensor",
            "Urna: depositar cartão",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "DEPOSITE O CARTAO"
    ),
    MESSAGE_NOT_ALLOWED_BOX(
            "messageNotAllowedBox",
            "Urna: não depositar cartão",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "NAO DEPOSITE"
    ),
    REVISTA_REQUIRED(
            "RevistaRequired",
            "Revista obrigatória",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "REVISTA"
    ),
    MESSAGE_ENROLLMENT(
            "messageEnrollment",
            "Cadastro de digital na catraca",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "POSICIONE O DEDO"
    ),
    MESSAGE_ENROLLMENT_FINISHED(
            "messageEnrollmentFinished",
            "Digital cadastrada na catraca",
            SecaoParametro.MENSAGENS_AVANCADAS,
            TipoDadoParametro.TEXTO,
            "CADASTRO OK"
    ),

    // --- SEÇÃO: INTEGRAÇÕES ---
    CHAVE_DE_INTEGRACAO_COMTELE(
            "Chave de integração Comtele",
            "Chave de integração Comtele",
            SecaoParametro.INTEGRACAO,
            TipoDadoParametro.TEXTO,
            ""
    ),
    HORARIO_DISPARO_SOC(
            "Horário de disparo da integração SOC (-1 para inativar)",
            "Horário de disparo da integração SOC (-1 para inativar)",
            SecaoParametro.INTEGRACAO,
            TipoDadoParametro.INTEGER,
            "-1",
            -1,
            23
    ),
    MESSAGE_SMS_AFTER_PASS_IN_DEVICE(
            "messageSMSAfterPassInDevice",
            "Mensagem SMS após passagem",
            SecaoParametro.INTEGRACAO,
            TipoDadoParametro.TEXTO,
            ""
    ),

    // --- SEÇÃO: ROTINAS AUTOMÁTICAS ---
    HOUR_AUTOMATIC_ROUTINES(
            "hourAutomaticRoutines",
            "Hora das rotinas diárias (0 a 23)",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.INTEGER,
            "0",
            0,
            23
    ),
    LIMPAR_VISITANTE_FIM_DO_DIA(
            "limparVisitanteFimDoDia",
            "No fim do dia, limpar cartão do visitante sem foto ou a face na Hikvision (se não houver período vigente)",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "true"
    ),
    BAIXAR_FOTOS_FIM_DO_DIA(
            "baixarFotosFimDoDia",
            "Baixar fotos dos pedestres no fim do dia",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "true"
    ),
    ENVIAR_DIGITAIS_FIM_DO_DIA(
            "enviarDigitaisFimDoDia",
            "Enviar digitais para as catracas no fim do dia",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "true"
    ),
    ENABLE_DIRECTION_CLEAR(
            "enableDirectionClear",
            "Resetar sentido e quantidade de acessos no fim do dia",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    ENABLE_CARD_RESET(
            "enableCardReset",
            "Resetar status de cartão/comanda no fim do dia",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    VERIFICAR_AGENDAMENTOS_LIBERACAO(
            "verificarAgendamentosLiberacao",
            "Verificar agendamentos de liberação",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "true"
    ),
    REPROCCESS_HIKIVISION_ERRORS(
            "reproccessHikivisionErrors",
            "Intervalo para reprocessar erros da Hikvision (minutos, 0 desliga)",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.INTEGER,
            "10",
            0,
            999999
    ),
    HIKI_VISION_FINGER_REGISTRATION(
            "hikiVisionFingerRegistration",
            "Reprocessar digitais com erro na Hikvision",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "false"
    ),
    HIKIVISION_TIME_PROCESSING(
            "hikivisionTimeProcessing",
            "Intervalo para reprocessar digitais da Hikvision (minutos, 0 desliga)",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.INTEGER,
            "10",
            0,
            999999
    ),
    TIME_RECONECT_DEVICES(
            "timeReconectDevices",
            "Intervalo para reconectar catracas (segundos, mínimo 5)",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.INTEGER,
            "5",
            0,
            999999
    ),
    ENABLE_RETOMADA_APOS_SUSPENSAO(
            "enableRetomadaAposSuspensao",
            "Reconectar catracas depois que o computador volta da suspensão",
            SecaoParametro.ROTINAS,
            TipoDadoParametro.BOOLEAN,
            "true"
    );

    public static final String LEGACY_NOME_VALIDAR_MATRICULAS_DUPLICADAS = " ";
    public static final String DEPRECATED_TEMPO_EXPIRACAO_CADASTRO_FACIAL =
            "Tempo para EXPIRAÇÃO da liberação de cadastro facial remoto (em horas)";

    private final String nomePersistido;
    private final String rotulo;
    private final SecaoParametro secao;
    private final TipoDadoParametro tipo;
    private final String valorPadraoPersistido;
    private final Integer min;
    private final Integer max;
    private final List<OpcaoParametro> opcoes;
    private final boolean visivel;

    ParametroCatalogo(
            String nomePersistido,
            String rotulo,
            SecaoParametro secao,
            TipoDadoParametro tipo,
            String valorPadraoPersistido
    ) {
        this(nomePersistido, rotulo, secao, tipo, valorPadraoPersistido, null, null, null, true);
    }

    ParametroCatalogo(
            String nomePersistido,
            String rotulo,
            SecaoParametro secao,
            TipoDadoParametro tipo,
            String valorPadraoPersistido,
            Integer min,
            Integer max
    ) {
        this(nomePersistido, rotulo, secao, tipo, valorPadraoPersistido, min, max, null, true);
    }

    ParametroCatalogo(
            String nomePersistido,
            String rotulo,
            SecaoParametro secao,
            TipoDadoParametro tipo,
            String valorPadraoPersistido,
            Integer min,
            Integer max,
            List<OpcaoParametro> opcoes,
            boolean visivel
    ) {
        this.nomePersistido = nomePersistido;
        this.rotulo = rotulo;
        this.secao = secao;
        this.tipo = tipo;
        this.valorPadraoPersistido = valorPadraoPersistido;
        this.min = min;
        this.max = max;
        this.opcoes = opcoes;
        this.visivel = visivel;
    }

    public String getCodigo() {
        return name();
    }

    public String getNomePersistido() {
        return nomePersistido;
    }

    public String getRotulo() {
        return rotulo;
    }

    public SecaoParametro getSecao() {
        return secao;
    }

    public TipoDadoParametro getTipo() {
        return tipo;
    }

    public String getValorPadraoPersistido() {
        return valorPadraoPersistido;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public List<OpcaoParametro> getOpcoes() {
        return opcoes;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public static String normalizarNomeLegado(String nome) {
        if (nome == null) {
            return null;
        }
        if (nome.trim().isEmpty() || LEGACY_NOME_VALIDAR_MATRICULAS_DUPLICADAS.equals(nome)) {
            return VALIDAR_MATRICULAS_DUPLICADAS.getNomePersistido();
        }
        return nome;
    }

    public static boolean isDepreciado(String nome) {
        return DEPRECATED_TEMPO_EXPIRACAO_CADASTRO_FACIAL.equals(nome);
    }

    public static ParametroCatalogo porCodigoOuNome(String identificador) {
        if (identificador == null) {
            return null;
        }
        String normalizado = normalizarNomeLegado(identificador);
        for (ParametroCatalogo item : values()) {
            if (item.name().equalsIgnoreCase(identificador.trim())
                    || item.nomePersistido.equals(normalizado)) {
                return item;
            }
        }
        return null;
    }

    public Object getValorPadraoTipado() {
        return converterPersistidoParaTipado(valorPadraoPersistido);
    }

    public String normalizarValorPersistidoLeitura(String valorBruto) {
        String efetivo = valorBruto;
        if ((efetivo == null || efetivo.trim().isEmpty())
                && valorPadraoPersistido != null
                && !valorPadraoPersistido.isEmpty()) {
            efetivo = valorPadraoPersistido;
        }
        if (efetivo == null) {
            efetivo = "";
        }
        if (this == LIMITE_DIGITAIS_PEDESTRE && "Sem limites".equalsIgnoreCase(efetivo.trim())) {
            return "Sem limite";
        }
        if (this == ESCOLHER_QTDE_DIGITOS_CARTAO) {
            try {
                int qtde = Integer.parseInt(efetivo.trim());
                if (qtde < 1) {
                    return "1";
                }
                if (qtde > 16) {
                    return "16";
                }
                return String.valueOf(qtde);
            } catch (NumberFormatException e) {
                return "1";
            }
        }
        return efetivo;
    }

    public Object converterPersistidoParaTipado(String valorBruto) {
        String valor = normalizarValorPersistidoLeitura(valorBruto);
        return switch (tipo) {
            case BOOLEAN -> "true".equalsIgnoreCase(valor.trim());
            case INTEGER -> {
                try {
                    yield Integer.valueOf(valor.trim());
                } catch (NumberFormatException e) {
                    yield Integer.valueOf(valorPadraoPersistido);
                }
            }
            case LISTA_STRING -> {
                if (valor.isBlank()) {
                    yield Collections.emptyList();
                }
                yield Arrays.stream(valor.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
            case TEXTO, FORMATO_DATA, FORMATO_HORA, SELECT -> valor;
        };
    }

    public String validarEFormatarParaPersistencia(Object valorEntrada) {
        if (valorEntrada == null) {
            throw new BusinessException("O valor do parâmetro '" + getCodigo() + "' não pode ser nulo.");
        }

        return switch (tipo) {
            case BOOLEAN -> validarBooleano(valorEntrada);
            case INTEGER -> validarInteiro(valorEntrada);
            case FORMATO_DATA, FORMATO_HORA -> validarFormatoDataHora(valorEntrada);
            case SELECT -> validarSelect(valorEntrada);
            case LISTA_STRING -> validarListaString(valorEntrada);
            case TEXTO -> validarTexto(valorEntrada);
        };
    }

    private String validarBooleano(Object valorEntrada) {
        if (valorEntrada instanceof Boolean b) {
            return b ? "true" : "false";
        }
        if (valorEntrada instanceof String s) {
            String trimmed = s.trim();
            if ("true".equalsIgnoreCase(trimmed) || "false".equalsIgnoreCase(trimmed)) {
                return Boolean.parseBoolean(trimmed) ? "true" : "false";
            }
        }
        throw new BusinessException("Valor booleano inválido para o parâmetro '" + getCodigo() + "': " + valorEntrada);
    }

    private String validarInteiro(Object valorEntrada) {
        int numero;
        if (valorEntrada instanceof Number n) {
            double d = n.doubleValue();
            if (d != Math.rint(d)) {
                throw new BusinessException("O parâmetro '" + getCodigo() + "' exige um número inteiro.");
            }
            numero = n.intValue();
        } else if (valorEntrada instanceof String s) {
            try {
                numero = Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                throw new BusinessException("Valor numérico inválido para o parâmetro '" + getCodigo() + "': " + valorEntrada);
            }
        } else {
            throw new BusinessException("Valor numérico inválido para o parâmetro '" + getCodigo() + "': " + valorEntrada);
        }

        if (min != null && numero < min) {
            throw new BusinessException(
                    "O valor do parâmetro '" + getCodigo() + "' deve ser no mínimo " + min + ".");
        }
        if (max != null && numero > max) {
            throw new BusinessException(
                    "O valor do parâmetro '" + getCodigo() + "' deve ser no máximo " + max + ".");
        }
        return String.valueOf(numero);
    }

    private String validarFormatoDataHora(Object valorEntrada) {
        if (!(valorEntrada instanceof String s) || s.trim().isEmpty()) {
            throw new BusinessException("O formato de data/hora do parâmetro '" + getCodigo() + "' não pode ser vazio.");
        }
        String pattern = s.trim();
        if (pattern.length() > 255) {
            throw new BusinessException("O valor do parâmetro '" + getCodigo() + "' excede 255 caracteres.");
        }
        try {
            DateTimeFormatter.ofPattern(pattern);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Formato de data/hora inválido para o parâmetro '" + getCodigo() + "': " + pattern);
        }
        return pattern;
    }

    private String validarSelect(Object valorEntrada) {
        if (!(valorEntrada instanceof String s)) {
            throw new BusinessException("O parâmetro '" + getCodigo() + "' exige um valor textual.");
        }
        String trimmed = s.trim();
        if (this == LIMITE_DIGITAIS_PEDESTRE && "Sem limites".equalsIgnoreCase(trimmed)) {
            trimmed = "Sem limite";
        }
        if (opcoes != null && !opcoes.isEmpty()) {
            final String candidato = trimmed;
            boolean valido = opcoes.stream().anyMatch(o -> o.valor().equals(candidato));
            if (!valido) {
                String permitidos = opcoes.stream().map(OpcaoParametro::valor).collect(Collectors.joining(", "));
                throw new BusinessException(
                        "Valor inválido para o parâmetro '" + getCodigo() + "'. Valores permitidos: " + permitidos);
            }
        }
        return trimmed;
    }

    private String validarListaString(Object valorEntrada) {
        List<String> itens = new ArrayList<>();
        if (valorEntrada instanceof Collection<?> col) {
            for (Object item : col) {
                if (item == null || !(item instanceof String str)) {
                    throw new BusinessException("Todos os itens da lista do parâmetro '" + getCodigo() + "' devem ser texto.");
                }
                String trimmed = str.trim();
                if (!trimmed.isEmpty()) {
                    itens.add(trimmed);
                }
            }
        } else if (valorEntrada instanceof String s) {
            if (!s.trim().isEmpty()) {
                Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(str -> !str.isEmpty())
                        .forEach(itens::add);
            }
        } else {
            throw new BusinessException("O parâmetro '" + getCodigo() + "' exige uma lista de strings.");
        }

        Set<String> unicos = new LinkedHashSet<>(itens);
        if (opcoes != null && !opcoes.isEmpty()) {
            Set<String> permitidos = opcoes.stream().map(OpcaoParametro::valor).collect(Collectors.toSet());
            for (String item : unicos) {
                if (!permitidos.contains(item)) {
                    throw new BusinessException(
                            "Item inválido '" + item + "' para o parâmetro '" + getCodigo() + "'.");
                }
            }
        }

        if (unicos.isEmpty()) {
            return "";
        }
        String resultado = String.join(",", unicos) + ",";
        if (resultado.length() > 255) {
            throw new BusinessException("O valor do parâmetro '" + getCodigo() + "' excede 255 caracteres.");
        }
        return resultado;
    }

    private String validarTexto(Object valorEntrada) {
        if (!(valorEntrada instanceof String s)) {
            throw new BusinessException("O parâmetro '" + getCodigo() + "' exige um valor textual.");
        }
        if (s.length() > 255) {
            throw new BusinessException("O valor do parâmetro '" + getCodigo() + "' excede 255 caracteres.");
        }
        return s;
    }
}
