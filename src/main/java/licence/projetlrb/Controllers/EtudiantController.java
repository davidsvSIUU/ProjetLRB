package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Services.ClasseService;
import licence.projetlrb.Services.EtudiantService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des étudiants
 */
@Controller
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final ClasseService classeService;

    @Autowired
    public EtudiantController(EtudiantService etudiantService, ClasseService classeService) {
        this.etudiantService = etudiantService;
        this.classeService = classeService;
    }

    /**
     * Page d'accueil
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Affiche la page de gestion des étudiants
     */

    @GetMapping("/gestionetudiants")
    public String gestionEtudiants(Model model) {
        // Récupération des étudiants et des classes
        ResponseDTO<Map<String, Object>> etudiantsResponse = etudiantService.rechercherEtudiantsAvecClasses();
        ResponseDTO<List<Classe>> classesResponse = classeService.rechercherClasses();

        // Ajout des données au modèle
        if (classesResponse.isSuccess()) {
            model.addAttribute("classes", classesResponse.getData());
        }

        if (etudiantsResponse.isSuccess()) {
            Map<String, Object> data = etudiantsResponse.getData();
            model.addAttribute("etudiants", data.get("etudiants"));
            model.addAttribute("nomsClasses", data.get("nomsClasses"));
        }

        // Gestion des erreurs
        if (!classesResponse.isSuccess()) {
            model.addAttribute("error", classesResponse.getMessage());
        }
        if (!etudiantsResponse.isSuccess()) {
            model.addAttribute("error", etudiantsResponse.getMessage());
        }

        return "gestionetudiants";
    }
    /**
     * Enregistre ou met à jour un étudiant
     */
    @PostMapping("/etudiant/enregistrer")
    public String enregistrer(@ModelAttribute Etudiant etudiant, Model model) {
        try {
            ResponseDTO<Etudiant> response = etudiantService.enregistrer(etudiant);
            if (response.isSuccess()) {
                model.addAttribute("success", "Étudiant enregistré avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/gestionetudiants";
    }

    /**
     * Supprime un étudiant
     */
    @PostMapping("/etudiant/supprimer/{id}")
    public String supprimer(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Void> response = etudiantService.supprimer(id);
            if (response.isSuccess()) {
                model.addAttribute("success", "Étudiant supprimé avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/gestionetudiants";
    }

    /**
     * Recherche des étudiants par nom ou prénom
     */
    @GetMapping("/etudiant/rechercher")
    public String rechercherParNomOuPrenom(@RequestParam String terme, Model model) {
        try {
            ResponseDTO<List<Etudiant>> response = etudiantService.rechercherParNomOuPrenom(terme);
            if (response.isSuccess()) {
                model.addAttribute("etudiants", response.getData());
                model.addAttribute("termeRecherche", terme);
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
        }
        return "gestionetudiants";
    }

    /**
     * Affiche le formulaire de modification d'un étudiant
     */
    @GetMapping("/etudiant/modifier/{id}")
    public String afficherModification(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Etudiant> response = etudiantService.rechercherParId(id);
            if (response.isSuccess()) {
                model.addAttribute("etudiant", response.getData());
            } else {
                model.addAttribute("error", response.getMessage());
                return "redirect:/gestionetudiants";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
            return "redirect:/gestionetudiants";
        }
        return "gestionetudiants";
    }
    @GetMapping("/etudiant/{id}")
    @ResponseBody
    public ResponseDTO<Etudiant> getEtudiant(@PathVariable Integer id) {
        try {
            return etudiantService.rechercherParId(id);
        } catch (Exception e) {
            return ResponseDTO.error(" la récupération de l'étudiant: " + e.getMessage());
        }
    }
    /**
     * Recherche des étudiants selon leur affectation à une classe
     */
    @GetMapping("/etudiant/rechercher/disponibilite")
    @ResponseBody
    public ResponseDTO<List<Etudiant>> rechercherEtudiantsDisponibles(@RequestParam boolean avecClasse) {
        try {
            List<Etudiant> etudiants = etudiantService.rechercherEtudiants().getData();
            List<Etudiant> etudiantsFiltres;

            if(avecClasse) {
                etudiantsFiltres = etudiants.stream()
                        .filter(e -> e.getIdClasse() != null)
                        .collect(Collectors.toList());
            } else {
                etudiantsFiltres = etudiants.stream()
                        .filter(e -> e.getIdClasse() == null)
                        .collect(Collectors.toList());
            }
    return ResponseDTO.success(etudiantsFiltres);
} catch (Exception e) {
        return ResponseDTO.error("Erreur lors de la recherche des étudiants : " + e.getMessage());
        }
    }
}