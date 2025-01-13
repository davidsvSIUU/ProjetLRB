package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Notation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotationRepository extends JpaRepository<Notation, Integer> {
    List<Notation> findByIdEtudiant(Integer idEtudiant);
}
