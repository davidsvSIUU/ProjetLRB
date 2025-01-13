package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Integer> {
}