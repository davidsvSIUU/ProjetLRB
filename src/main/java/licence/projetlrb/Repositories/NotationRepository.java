package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Notation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotationRepository extends JpaRepository<Notation, Long> {
    Optional<Notation> findByIdEtudiantAndIdDevoir(Integer idEtudiant, Integer idDevoir);
    List<Notation> findByIdDevoir(Integer idDevoir);

    List<Notation> findByIdEtudiant(Integer id);
}