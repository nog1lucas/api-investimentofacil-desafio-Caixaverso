package org.lucasnogueira.utils.mappers;

import org.lucasnogueira.adapters.outbound.dto.HistoricoSimulacaoResponseDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class SimulacaoResumoMapper {
    public static HistoricoSimulacaoResponseDTO fromObjectArray(Object[] row) {
        HistoricoSimulacaoResponseDTO dto = new HistoricoSimulacaoResponseDTO();
        dto.setId((Long) row[0]);
        dto.setClienteId((Integer) row[1]);
        dto.setProduto((String) row[2]);
        dto.setValorInvestido((BigDecimal) row[3]);
        dto.setValorFinal((BigDecimal) row[4]);
        dto.setPrazoMeses((Integer) row[5]);
        dto.setDataSimulacao((OffsetDateTime) row[6]);
        return dto;
    }
}