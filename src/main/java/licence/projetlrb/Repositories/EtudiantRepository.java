package licence.projetlrb.Repositories;

import licence.projetlrb.Entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {
    // Méthodes existantes
    List<Etudiant> findByIdClasse(Integer idClasse);
    List<Etudiant> findByNomContainingOrPrenomContaining(String nom, String prenom);

    // Ajout de la méthode manquante
    List<Etudiant> findByIdClasseIsNull();
    int countByIdClasse(Integer idClasse);

}