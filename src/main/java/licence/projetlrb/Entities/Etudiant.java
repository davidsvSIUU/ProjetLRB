package licence.projetlrb.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "etudiant")
public class Etudiant {
    @Id
    @Column(name = "id_etudiant", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "photo")
    private String photo;

    @Column(name = "id_classe")
    private Integer idClasse;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(Integer idClasse) {
        this.idClasse = idClasse;
    }
}