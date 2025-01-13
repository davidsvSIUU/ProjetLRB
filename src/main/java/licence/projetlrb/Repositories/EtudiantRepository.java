// EtudiantRepository.java
package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {
    @Query("SELECT e FROM Etudiant e WHERE e.idClasse IS NULL")
    List<Etudiant> findDisponibles();
}