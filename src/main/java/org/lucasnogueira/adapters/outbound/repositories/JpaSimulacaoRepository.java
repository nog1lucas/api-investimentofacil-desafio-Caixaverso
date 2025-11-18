package org.lucasnogueira.adapters.outbound.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.lucasnogueira.adapters.outbound.entities.JpaSimulacaoEntity;

@ApplicationScoped
public class JpaSimulacaoRepository implements PanacheRepository<JpaSimulacaoEntity> {

}
