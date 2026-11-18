package br.com.fiap3ess.rotaseca.adapter.out.repository.persistence;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.LeituraChuvaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeituraChuvaJpaRepository extends JpaRepository<LeituraChuvaEntity, Long> {
}
