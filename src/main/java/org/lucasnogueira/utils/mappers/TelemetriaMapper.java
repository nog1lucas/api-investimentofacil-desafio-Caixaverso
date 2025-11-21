package org.lucasnogueira.utils.mappers;

import org.lucasnogueira.adapters.outbound.entities.JpaTelemetriaEntity;
import org.lucasnogueira.domain.telemetria.Telemetria;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TelemetriaMapper {
    TelemetriaMapper INSTANCE = Mappers.getMapper(TelemetriaMapper.class);

    Telemetria jpaToDomain(JpaTelemetriaEntity entity);

    JpaTelemetriaEntity domainToJpa(Telemetria telemetria);
}