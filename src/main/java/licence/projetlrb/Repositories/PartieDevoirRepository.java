package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.PartieDevoir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartieDevoirRepository extends JpaRepository<PartieDevoir, Integer> {
    List<PartieDevoir> findByIdDevoir(Integer idDevoir);
}