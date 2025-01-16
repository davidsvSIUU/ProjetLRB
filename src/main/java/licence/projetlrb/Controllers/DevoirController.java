package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Entities.Matiere;
import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Entities.Partie_Devoir;
import licence.projetlrb.Services.DevoirService;
import licence.projetlrb.Services.MatiereService;
import licence.projetlrb.Services.ClasseService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DevoirController {

    private final DevoirService devoirService;
    private final ClasseService classeService;
    private final MatiereService matiereService;

    @Autowired
    public DevoirController(DevoirService devoirService, ClasseService classeService, MatiereService matiereService) {
        this.devoirService = devoirService;
        this.classeService = classeService;
        this.matiereService = matiereService;
    }


    /**
     * Affiche la page de gestion des devoirs
     */
    @GetMapping("/gestiondevoirs")
    public String gestionDevoirs(Model model) {
        ResponseDTO<List<Devoir>> response = devoirService.rechercherDevoirs();
        ResponseDTO<List<Classe>> responseClasse = classeService.rechercherClasses();
        ResponseDTO<List<Matiere>> responseMatiere = matiereService.rechercherMatieres();
        if (response.isSuccess() && responseClasse.isSuccess() && responseMatiere.isSuccess()) {
            model.addAttribute("devoirs", response.getData());
            model.addAttribute("classes", responseClasse.getData());
            model.addAttribute("matieres", responseMatiere.getData());
        } else {
            model.addAttribute("error", response.getMessage());
        }
        return "gestiondevoirs";
    }

    /**
     * Enregistre ou met à jour un devoir
     */
    @PostMapping("/devoir/enregistrer")
    public String enregistrer(@ModelAttribute Devoir devoir,
                              @RequestParam(value = "pointsParPartie", required = false) List<String> pointsParPartie,
                              Model model) {
        try {
            ResponseDTO<Devoir> response = devoirService.enregistrer(devoir, pointsParPartie);
            if (response.isSuccess()) {
                model.addAttribute("success", "Devoir enregistré avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/gestiondevoirs";
    }


    /**
     * Supprime un devoir
     */
    @PostMapping("/devoir/supprimer/{id}")
    public String supprimer(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Void> response = devoirService.supprimer(id);
            if (response.isSuccess()) {
                model.addAttribute("success", "Devoir supprimé avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/gestiondevoirs";
    }

    /**
     * Affiche le formulaire de modification d'un devoir
     */
    @GetMapping("/devoir/modifier/{id}")
    public String afficherModification(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Devoir> response = devoirService.rechercherParId(id);
            ResponseDTO<List<Classe>> responseClasse = classeService.rechercherClasses();
            ResponseDTO<List<Matiere>> responseMatiere = matiereService.rechercherMatieres();
            if (response.isSuccess() && responseClasse.isSuccess() && responseMatiere.isSuccess()) {
                model.addAttribute("devoir", response.getData());
                model.addAttribute("classes", responseClasse.getData());
                model.addAttribute("matieres", responseMatiere.getData());
            } else {
                model.addAttribute("error", response.getMessage());
                return "redirect:/gestiondevoirs";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
            return "redirect:/gestiondevoirs";
        }

        return "gestiondevoirs";
    }

    @GetMapping("/devoir/{id}")
    @ResponseBody
    public ResponseDTO<Devoir> getDevoir(@PathVariable Integer id) {
        try {
            return devoirService.rechercherParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("la récupération du devoir: " + e.getMessage());
        }
    }

    @GetMapping("/devoir/partie/{id}")
    @ResponseBody
    public ResponseDTO<List<Partie_Devoir>> getPartieDevoir(@PathVariable Integer id) {
        try {
            return devoirService.rechercherPartiesParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("la récupération des parties du devoir: " + e.getMessage());
        }
    }
}