package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Services.EtudiantService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des étudiants
 */
@Controller
public class EtudiantController {

    private final EtudiantService etudiantService;

    @Autowired
    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
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
        ResponseDTO<List<Etudiant>> response = etudiantService.rechercherEtudiants();
        if (response.isSuccess()) {
            model.addAttribute("etudiants", response.getData());
        } else {
            model.addAttribute("error", response.getMessage());
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
}