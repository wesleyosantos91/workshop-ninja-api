package br.org.soujava.bsb.api.domain.repository;

import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NinjaRepository extends JpaRepository<NinjaEntity, Integer> {
}
