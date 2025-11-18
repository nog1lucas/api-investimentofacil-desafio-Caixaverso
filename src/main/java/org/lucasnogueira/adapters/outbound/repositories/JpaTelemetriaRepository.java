package org.lucasnogueira.adapters.outbound.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.lucasnogueira.adapters.outbound.entities.JpaTelemetriaEntity;

@ApplicationScoped
public class JpaTelemetriaRepository implements PanacheRepository<JpaTelemetriaEntity> {

}