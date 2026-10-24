package br.com.startjob.acesso.core.enumeration;

import br.com.startjob.acesso.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParametroCatalogoTest {

    @Test
    @DisplayName("Deve localizar parâmetro tanto pelo código do enum quanto pelo nome persistido ou legado")
    void deveLocalizarPorCodigoOuNome() {
        assertEquals(ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL,
                ParametroCatalogo.porCodigoOuNome("GERAR_MATRICULA_SEQUENCIAL"));
        assertEquals(ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL,
                ParametroCatalogo.porCodigoOuNome("Gerar matricula sequencial"));
        assertEquals(ParametroCatalogo.VALIDAR_MATRICULAS_DUPLICADAS,
                ParametroCatalogo.porCodigoOuNome(" "));
        assertNull(ParametroCatalogo.porCodigoOuNome("INEXISTENTE"));
    }

    @Test
    @DisplayName("Deve converter e validar parâmetros booleanos")
    void deveConverterEValidarBooleano() {
        ParametroCatalogo spec = ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL;

        assertEquals(Boolean.TRUE, spec.converterPersistidoParaTipado("true"));
        assertEquals(Boolean.FALSE, spec.converterPersistidoParaTipado("false"));
        assertEquals(Boolean.FALSE, spec.converterPersistidoParaTipado(null));

        assertEquals("true", spec.validarEFormatarParaPersistencia(true));
        assertEquals("false", spec.validarEFormatarParaPersistencia("false"));

        assertThrows(BusinessException.class, () -> spec.validarEFormatarParaPersistencia("invalido"));
    }

    @Test
    @DisplayName("Deve converter, normalizar e validar parâmetros inteiros com min e max")
    void deveConverterEValidarInteiro() {
        ParametroCatalogo digitos = ParametroCatalogo.ESCOLHER_QTDE_DIGITOS_CARTAO;

        assertEquals(10, digitos.converterPersistidoParaTipado(null));
        assertEquals(1, digitos.converterPersistidoParaTipado("0"));
        assertEquals(16, digitos.converterPersistidoParaTipado("99"));

        assertEquals("12", digitos.validarEFormatarParaPersistencia(12));
        assertEquals("8", digitos.validarEFormatarParaPersistencia("8"));

        assertThrows(BusinessException.class, () -> digitos.validarEFormatarParaPersistencia(0));
        assertThrows(BusinessException.class, () -> digitos.validarEFormatarParaPersistencia(17));
        assertThrows(BusinessException.class, () -> digitos.validarEFormatarParaPersistencia("abc"));
    }

    @Test
    @DisplayName("Deve validar formatos de data e hora")
    void deveValidarFormatoDataHora() {
        ParametroCatalogo formatoData = ParametroCatalogo.FORMATO_DATA_PARA_IMPORTACAO;
        ParametroCatalogo formatoHora = ParametroCatalogo.FORMATO_HORA_PARA_IMPORTACAO;

        assertEquals("dd/MM/yyyy", formatoData.validarEFormatarParaPersistencia("dd/MM/yyyy"));
        assertEquals("yyyy-MM-dd", formatoData.validarEFormatarParaPersistencia("yyyy-MM-dd"));
        assertEquals("HH:mm:ss", formatoHora.validarEFormatarParaPersistencia("HH:mm:ss"));

        assertThrows(BusinessException.class, () -> formatoData.validarEFormatarParaPersistencia(""));
        assertThrows(BusinessException.class, () -> formatoData.validarEFormatarParaPersistencia("padrao_invalido_xyz"));
    }

    @Test
    @DisplayName("Deve validar e normalizar parâmetros de seleção (SELECT)")
    void deveValidarSelect() {
        ParametroCatalogo limiteDigitais = ParametroCatalogo.LIMITE_DIGITAIS_PEDESTRE;

        assertEquals("Sem limite", limiteDigitais.converterPersistidoParaTipado("Sem limites"));
        assertEquals("Sem limite", limiteDigitais.validarEFormatarParaPersistencia("Sem limites"));
        assertEquals("2", limiteDigitais.validarEFormatarParaPersistencia("2"));

        assertThrows(BusinessException.class, () -> limiteDigitais.validarEFormatarParaPersistencia("10"));
    }

    @Test
    @DisplayName("Deve converter e validar lista de strings (campos obrigatórios)")
    void deveConverterEValidarListaString() {
        ParametroCatalogo campos = ParametroCatalogo.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE;

        assertEquals(List.of("cpf", "email", "foto"), campos.converterPersistidoParaTipado("cpf,email,foto,"));
        assertEquals(List.of(), campos.converterPersistidoParaTipado(""));

        assertEquals("cpf,email,foto,", campos.validarEFormatarParaPersistencia(List.of("cpf", "email", "cpf", "foto")));
        assertEquals("", campos.validarEFormatarParaPersistencia(List.of()));

        assertThrows(BusinessException.class, () -> campos.validarEFormatarParaPersistencia(List.of("campo.inexistente")));
    }
}
