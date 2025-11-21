package org.lucasnogueira.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TELEMETRIA")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Telemetria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_TELEMETRIA")
    private Long id;

    @Column(name = "NO_SERVICO", length = 100)
    private String nomeServico;

    @Column(name = "DT_REFERENCIA")
    private LocalDate dataReferencia;

    @Column(name = "QT_CHAMADAS")
    private Long quantidadeChamadas;

    @Column(name = "QT_CHAMADAS_SUCESSO")
    private Long quantidadeChamadasSucesso;

    @Column(name = "QT_CHAMADAS_ERRO")
    private Long quantidadeChamadasErro;

    @Column(name = "TEMPO_RESPOSTA_MS")
    private BigDecimal tempoRespostaMs;

    @Column(name = "TS_CRIACAO")
    private LocalDateTime tsCriacao;

    @Column(name = "TS_ATUALIZACAO")
    private LocalDateTime tsAtualizacao;

    @PrePersist
    protected void onCreate() {
        tsCriacao = LocalDateTime.now();
        tsAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        tsAtualizacao = LocalDateTime.now();
    }

    /**
     * Calcula o tempo médio de resposta em milissegundos
     */
    public BigDecimal calcularTempoMedio() {
        if (quantidadeChamadas == 0) {
            return BigDecimal.ZERO;
        }
        return tempoRespostaMs.divide(BigDecimal.valueOf(quantidadeChamadas), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calcula o percentual de sucesso
     */
    public BigDecimal calcularPercentualSucesso() {
        if (quantidadeChamadas == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(quantidadeChamadasSucesso)
                .divide(BigDecimal.valueOf(quantidadeChamadas), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
