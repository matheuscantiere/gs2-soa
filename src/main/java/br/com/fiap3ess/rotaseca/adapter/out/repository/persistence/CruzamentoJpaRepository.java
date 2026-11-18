package br.com.fiap3ess.rotaseca.adapter.out.repository.persistence;

import br.com.fiap3ess.rotaseca.adapter.out.repository.entity.CruzamentoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CruzamentoJpaRepository extends JpaRepository<CruzamentoEntity, String> {
    Page<CruzamentoEntity> findAllByAtivoTrue(Pageable paginacao);

    List<CruzamentoEntity> findAllByAtivoTrueOrderByOrdemAscIdAsc();

    List<CruzamentoEntity> findAllByAtivoTrueAndLatitudeNotNullAndLongitudeNotNull();

    Optional<CruzamentoEntity> findByIdAndAtivoTrue(String id);
}
