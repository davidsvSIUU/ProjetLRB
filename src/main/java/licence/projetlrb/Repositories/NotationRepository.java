package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Notation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotationRepository extends JpaRepository<Notation, Integer> {
    void deleteByIdEtudiant(Integer idEtudiant);
}