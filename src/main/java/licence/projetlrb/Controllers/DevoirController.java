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
import java.util.Objects;

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

    @GetMapping("/gestionnotations")
    public String gestionNotations(@RequestParam(name = "etudiantId", required = false) Integer etudiantId,Model model) {
        ResponseDTO<List<Devoir>> responseDevoirs = devoirService.rechercherDevoirs();
        if (!responseDevoirs.isSuccess()) {
            model.addAttribute("error", responseDevoirs.getMessage());
            return "gestionnotations";
        }
        List<Devoir> devoirs = responseDevoirs.getData();
        if (devoirs.isEmpty()){
            model.addAttribute("devoirs", devoirs);
            return "gestionnotations";
        }
        ResponseDTO<List<Etudiant>> responseEtudiants = etudiantService.rechercherEtudiantsParClasse(devoirs.get(0).getIdClasse());
        if (!responseEtudiants.isSuccess()) {
            model.addAttribute("error", responseEtudiants.getMessage());
            return "gestionnotations";
        }
        List<Etudiant> etudiants = responseEtudiants.getData();

        Map<String, BigDecimal> notes = new HashMap<>();
        Map<String, Map<String, Object>> noteGlobalEtMoyenneGlobalParEtudiantParDevoir = new HashMap<>();
        Map<Integer, Map<String,Object>> bulletinParEtudiant = new HashMap<>();
        Map<Integer, List<Map<String, Object>>> moyenneParMatiereParEtudiant = new HashMap<>();
        if (etudiantId != null) {
            // Filtrer les étudiants si un ID d'étudiant est fourni
            etudiants = etudiants.stream()
                    .filter(etudiant -> Objects.equals(etudiant.getId(), etudiantId))
                    .toList();
            if (!etudiants.isEmpty()) {
                for (Devoir devoir : devoirs) {
                    ResponseDTO<List<Notation>> responseNotations = notationService.rechercherNotationsParDevoir(devoir.getId());
                    if (responseNotations.isSuccess() && responseNotations.getData() != null) {
                        for (Notation notation : responseNotations.getData()) {
                            if (Objects.equals(notation.getIdEtudiant(), etudiantId)){
                                notes.put(notation.getIdEtudiant() + "_" + devoir.getId(), notation.getNote());
                            }
                        }
                    }
                }

                ResponseDTO<Map<String, Object>> responseBulletin =  notationService.creerBulletinDeNoteParEtudiant(etudiantId);
                if(responseBulletin.isSuccess()) {
                    bulletinParEtudiant.put(etudiantId, responseBulletin.getData());
                }
                ResponseDTO<List<Map<String, Object>>> responseMatiere = notationService.rechercherMoyenneEtMoyenneGlobalParMatiereParEtudiant(etudiantId);
                if (responseMatiere.isSuccess()){
                    moyenneParMatiereParEtudiant.put(etudiantId,responseMatiere.getData());
                }
                for (Devoir devoir : devoirs){
                    ResponseDTO<Map<String, Object>> responseNote =  notationService.rechercherNoteGlobalEtMoyenneGlobaleParDevoirParEtudiant(etudiantId, devoir.getId());
                    if (responseNote.isSuccess()){
                        noteGlobalEtMoyenneGlobalParEtudiantParDevoir.put(etudiantId + "_" + devoir.getId(), responseNote.getData());
                    }
                }
            }
        }
        else {
            for (Devoir devoir : devoirs) {
                ResponseDTO<List<Notation>> responseNotations = notationService.rechercherNotationsParDevoir(devoir.getId());
                if(responseNotations.isSuccess() && responseNotations.getData() != null) {
                    for (Notation notation : responseNotations.getData()) {
                        notes.put(notation.getIdEtudiant() + "_" + devoir.getId(), notation.getNote());
                    }
                }
            }

            for (Etudiant etudiant : etudiants){
                ResponseDTO<Map<String, Object>> responseBulletin =  notationService.creerBulletinDeNoteParEtudiant(etudiant.getId());
                if(responseBulletin.isSuccess()) {
                    bulletinParEtudiant.put(etudiant.getId(), responseBulletin.getData());
                }
            }
            for (Etudiant etudiant : etudiants){
                ResponseDTO<List<Map<String, Object>>> responseMatiere = notationService.rechercherMoyenneEtMoyenneGlobalParMatiereParEtudiant(etudiant.getId());
                if (responseMatiere.isSuccess()){
                    moyenneParMatiereParEtudiant.put(etudiant.getId(),responseMatiere.getData());
                }
            }
            for (Etudiant etudiant : etudiants){
                for (Devoir devoir : devoirs){
                    ResponseDTO<Map<String, Object>> responseNote =  notationService.rechercherNoteGlobalEtMoyenneGlobaleParDevoirParEtudiant(etudiant.getId(), devoir.getId());
                    if (responseNote.isSuccess()){
                        noteGlobalEtMoyenneGlobalParEtudiantParDevoir.put(etudiant.getId() + "_" + devoir.getId(), responseNote.getData());
                    }
                }
            }

        }

        model.addAttribute("devoirs", devoirs);
        model.addAttribute("etudiants", etudiants);
        model.addAttribute("notes", notes);
        model.addAttribute("bulletinParEtudiant", bulletinParEtudiant);
        model.addAttribute("moyenneParMatiereParEtudiant", moyenneParMatiereParEtudiant);
        model.addAttribute("noteGlobalEtMoyenneGlobalParEtudiantParDevoir", noteGlobalEtMoyenneGlobalParEtudiantParDevoir);


        return "gestionnotations";
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