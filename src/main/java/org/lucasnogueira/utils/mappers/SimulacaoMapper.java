package org.lucasnogueira.utils.mappers;

import org.lucasnogueira.adapters.outbound.entities.JpaSimulacaoEntity;
import org.lucasnogueira.domain.simulacao.Simulacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SimulacaoMapper {

    SimulacaoMapper INSTANCE = Mappers.getMapper(SimulacaoMapper.class);

//    @Mappings({
//            @Mapping(source = "valorInvestido", target = "investedAmount"),
//
//    })
//    Simulacao dtoToEntity(Object dto);
//
//    @Mappings({
//            @Mapping(source = "valorInvestido", target = "investedAmount"),
//
//    })
//    Simulacao toDto(Object dto);


    @Mappings({
            @Mapping(source = "jpa.id", target = "id"),
            @Mapping(source = "jpa.codigoCliente", target = "codigoCliente"),
            @Mapping(source = "jpa.codigoProduto", target = "codigoProduto"),
            @Mapping(source = "jpa.valorInvestido", target = "valorInvestido"),
            @Mapping(source = "jpa.valorFinal", target = "valorFinal"),
            @Mapping(source = "jpa.prazoMeses", target = "prazoMeses"),
            @Mapping(source = "jpa.dataSimulacao", target = "dataSimulacao")
    })
    Simulacao jpaToDomain(JpaSimulacaoEntity jpa);
}
