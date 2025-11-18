package org.lucasnogueira.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lucasnogueira.domain.telemetria.Telemetria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TELEMETRIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JpaTelemetriaEntity {

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

    public JpaTelemetriaEntity(Telemetria telemetria) {
        this.id = telemetria.getId();
        this.nomeServico = telemetria.getNomeServico();
        this.quantidadeChamadas = telemetria.getQuantidadeChamadas();
        this.quantidadeChamadasSucesso = telemetria.getQuantidadeChamadasSucesso();
        this.quantidadeChamadasErro = telemetria.getQuantidadeChamadasErro();
        this.tempoRespostaMs = telemetria.getTempoRespostaMs();
        this.tsCriacao = telemetria.getTsCriacao();
        this.tsAtualizacao = telemetria.getTsAtualizacao();
    }

    @PrePersist
    protected void onCreate() {
        tsCriacao = LocalDateTime.now();
        tsAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        tsAtualizacao = LocalDateTime.now();
    }
}
