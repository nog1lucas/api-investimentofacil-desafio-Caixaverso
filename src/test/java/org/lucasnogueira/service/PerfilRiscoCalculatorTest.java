package org.lucasnogueira.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.lucasnogueira.model.dto.SimulacaoRequestDTO;
import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PerfilRiscoCalculatorTest {

    private PerfilRiscoCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PerfilRiscoCalculator();
    }

    @Test
    @DisplayName("Deve retornar perfil CONSERVADOR para valores baixos e prazo curto")
    void deveRetornarPerfilConservadorParaValoresBaixosEPrazoCurto() {
        SimulacaoRequestDTO request = criarRequest(BigDecimal.valueOf(10000), 3);

        TipoPerfilRisco resultado = calculator.calcular(request);

        assertEquals(TipoPerfilRisco.CONSERVADOR, resultado);
    }

    @Test
    @DisplayName("Deve retornar perfil MODERADO para valores médios e prazo médio")
    void deveRetornarPerfilModeradoParaValoresMediosEPrazoMedio() {
        SimulacaoRequestDTO request = criarRequest(BigDecimal.valueOf(100000), 24);

        TipoPerfilRisco resultado = calculator.calcular(request);

        assertEquals(TipoPerfilRisco.MODERADO, resultado);
    }

    @Test
    @DisplayName("Deve retornar perfil AGRESSIVO para valores altos e prazo longo")
    void deveRetornarPerfilAgressivoParaValoresAltosEPrazoLongo() {
        SimulacaoRequestDTO request = criarRequest(BigDecimal.valueOf(6000000), 80);

        TipoPerfilRisco resultado = calculator.calcular(request);

        assertEquals(TipoPerfilRisco.AGRESSIVO, resultado);
    }

    @ParameterizedTest
    @CsvSource({
            "10000, 3, CONSERVADOR",      // Score: -1 + (-3) = -4
            "20000, 6, CONSERVADOR",      // Score: -1 + (-2.5) = -3.5
            "50000, 12, CONSERVADOR",     // Score: 0.5 + (-0.5) = 0
            "100000, 24, MODERADO",       // Score: 1.5 + 0.5 = 2
            "300000, 48, MODERADO",       // Score: 2.5 + 1.5 = 4
            "1000000, 72, AGRESSIVO",     // Score: 3.5 + 2 = 5.5
            "6000000, 80, AGRESSIVO"      // Score: 5 + 2 = 7
    })
    @DisplayName("Deve calcular perfil corretamente para diferentes combinações de valor e prazo")
    void deveCalcularPerfilCorretamente(long valor, int prazo, TipoPerfilRisco perfilEsperado) {
        SimulacaoRequestDTO request = criarRequest(BigDecimal.valueOf(valor), prazo);

        TipoPerfilRisco resultado = calculator.calcular(request);

        assertEquals(perfilEsperado, resultado);
    }

    @Test
    @DisplayName("Deve calcular score máximo para valores muito altos")
    void deveCalcularScoreMaximoParaValoresMuitoAltos() {
        SimulacaoRequestDTO request = criarRequest(BigDecimal.valueOf(10000000), 80);

        TipoPerfilRisco resultado = calculator.calcular(request);

        assertEquals(TipoPerfilRisco.AGRESSIVO, resultado);
    }

    @Test
    @DisplayName("Deve calcular score mínimo para valores muito baixos")
    void deveCalcularScoreMinimoParaValoresMuitoBaixos() {
        SimulacaoRequestDTO request = criarRequest(BigDecimal.valueOf(5000), 1);

        TipoPerfilRisco resultado = calculator.calcular(request);

        assertEquals(TipoPerfilRisco.CONSERVADOR, resultado);
    }

    @Test
    @DisplayName("Deve tratar valores nos limites das faixas")
    void deveTratarValoresNosLimitesDasFaixas() {
        // Teste no limite entre CONSERVADOR e MODERADO (score = 0.5)
        SimulacaoRequestDTO requestLimite = criarRequest(BigDecimal.valueOf(50000), 12);
        assertEquals(TipoPerfilRisco.CONSERVADOR, calculator.calcular(requestLimite));

        // Teste no limite entre MODERADO e AGRESSIVO (score = 4.0)
        SimulacaoRequestDTO requestLimite2 = criarRequest(BigDecimal.valueOf(300000), 48);
        assertEquals(TipoPerfilRisco.MODERADO, calculator.calcular(requestLimite2));
    }

    @Test
    @DisplayName("Deve lidar com valores decimais")
    void deveLidarComValoresDecimais() {
        SimulacaoRequestDTO request = criarRequest(new BigDecimal("75000.50"), 18);

        TipoPerfilRisco resultado = calculator.calcular(request);

        assertNotNull(resultado);
        assertTrue(resultado == TipoPerfilRisco.CONSERVADOR ||
                resultado == TipoPerfilRisco.MODERADO ||
                resultado == TipoPerfilRisco.AGRESSIVO);
    }

    @Test
    @DisplayName("Deve ser consistente para mesmos valores")
    void deveSerConsistenteParaMesmosValores() {
        SimulacaoRequestDTO request = criarRequest(BigDecimal.valueOf(150000), 36);

        TipoPerfilRisco resultado1 = calculator.calcular(request);
        TipoPerfilRisco resultado2 = calculator.calcular(request);

        assertEquals(resultado1, resultado2);
    }

    private SimulacaoRequestDTO criarRequest(BigDecimal valor, int prazoMeses) {
        SimulacaoRequestDTO request = new SimulacaoRequestDTO();
        request.setValor(valor);
        request.setPrazoMeses(prazoMeses);
        request.setClienteId(123);
        return request;
    }
}