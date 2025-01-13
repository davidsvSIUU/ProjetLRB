package licence.projetlrb.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "notation")
public class Notation {
    @Id
    @Column(name = "id_notation", nullable = false)
    private Integer id;

    @Column(name = "id_etudiant", nullable = false)
    private Integer idEtudiant;

    @Column(name = "id_partie", nullable = false)
    private Integer idPartie;

    @Column(name = "note", nullable = false, precision = 4, scale = 2)
    private BigDecimal note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(Integer idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public Integer getIdPartie() {
        return idPartie;
    }

    public void setIdPartie(Integer idPartie) {
        this.idPartie = idPartie;
    }

    public BigDecimal getNote() {
        return note;
    }

    public void setNote(BigDecimal note) {
        this.note = note;
    }
}