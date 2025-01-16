package licence.projetlrb.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "notation")
public class Notation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notation", nullable = false)
    private Long id;

    @Column(name = "id_etudiant", nullable = false)
    private Integer idEtudiant;
    @Column(name = "id_devoir", nullable = false)
    private Integer idDevoir;

    @Column(name = "note", nullable = false, precision = 4, scale = 2)
    private BigDecimal note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(Integer idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public Integer getIdDevoir() {
        return idDevoir;
    }

    public void setIdDevoir(Integer idDevoir) {
        this.idDevoir = idDevoir;
    }

    public BigDecimal getNote() {
        return note;
    }

    public void setNote(BigDecimal note) {
        this.note = note;
    }
}