package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Devoir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevoirRepository extends JpaRepository<Devoir, Integer> {
    List<Devoir> findByIdClasse(Integer idClasse);
}