package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Partie_Devoir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartieDevoirRepository extends JpaRepository<Partie_Devoir, Integer> {
    List<Partie_Devoir> findByIdDevoir(Integer idDevoir);
}