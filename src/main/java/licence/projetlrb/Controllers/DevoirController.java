package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.*;
import licence.projetlrb.Services.*;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DevoirController {
    @Autowired
    private EtudiantService etudiantService;
    private final DevoirService devoirService;
    private final ClasseService classeService;
    private final MatiereService matiereService;
    private final NotationService notationService;
    @Autowired
    public DevoirController(DevoirService devoirService, ClasseService classeService, MatiereService matiereService, NotationService notationService) {
        this.devoirService = devoirService;
        this.classeService = classeService;
        this.matiereService = matiereService;
        this.notationService = notationService;
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
    @GetMapping("/devoir/gerernotation/{id}")
    public String gererNotation(@PathVariable("id") Integer idDevoir, Model model) {
        try {
            // Récupérer le devoir
            ResponseDTO<Devoir> responseDevoir = devoirService.rechercherParId(idDevoir);
            if (!responseDevoir.isSuccess()) {
                model.addAttribute("error", responseDevoir.getMessage());
                return "redirect:/gestiondevoirs";
            }

            // Récupérer la liste des étudiants de la classe associée au devoir
            ResponseDTO<List<Etudiant>> responseEtudiants = etudiantService.rechercherEtudiantsParClasse(
                    responseDevoir.getData().getIdClasse()
            );

            if (!responseEtudiants.isSuccess()) {
                model.addAttribute("error", responseEtudiants.getMessage());
                return "redirect:/gestiondevoirs";
            }

            // Récupérer les parties du devoir
            ResponseDTO<List<Partie_Devoir>> responseParties = devoirService.rechercherPartiesParId(idDevoir);

            // Initialiser la Map des notes
            Map<Integer, BigDecimal> notes = new HashMap<>();

            // Récupérer les notations existantes via le service
            ResponseDTO<List<Notation>> responseNotations = notationService.rechercherNotationsParDevoir(idDevoir);
            if (responseNotations.isSuccess() && responseNotations.getData() != null) {
                for (Notation notation : responseNotations.getData()) {
                    notes.put(notation.getIdEtudiant(), notation.getNote());
                }
            }

            // Ajouter les données au modèle
            model.addAttribute("devoir", responseDevoir.getData());
            model.addAttribute("etudiants", responseEtudiants.getData());
            model.addAttribute("parties", responseParties.getData());
            model.addAttribute("notes", notes);

            return "gerernotation";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement de la page: " + e.getMessage());
            return "redirect:/gestiondevoirs";
        }
    }

    @PostMapping("/api/notation/enregistrer")
    @ResponseBody
    public ResponseDTO<Notation> enregistrerNotation(@RequestBody Map<String, Object> notationData) {
        return notationService.enregistrer(notationData);
    }

}