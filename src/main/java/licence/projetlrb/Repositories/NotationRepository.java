package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Notation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotationRepository extends JpaRepository<Notation, Integer> {
    void deleteByIdEtudiant(Integer idEtudiant);
    List<Notation> findByIdPartie(Integer idPartie);
    List<Notation> findByIdEtudiant(Integer idEtudiant);
}