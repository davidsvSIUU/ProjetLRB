package licence.projetlrb.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "devoir")
public class Devoir {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devoir", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "date_devoir", nullable = false)
    private LocalDate dateDevoir;

    @Column(name = "coefficient", nullable = false, precision = 4, scale = 2)
    private BigDecimal coefficient;

    @Column(name = "id_classe", nullable = false)
    private Integer idClasse;

    @Column(name = "id_matiere", nullable = false)
    private Integer idMatiere;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDateDevoir() {
        return dateDevoir;
    }

    public void setDateDevoir(LocalDate dateDevoir) {
        this.dateDevoir = dateDevoir;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public Integer getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(Integer idClasse) {
        this.idClasse = idClasse;
    }

    public Integer getIdMatiere() {
        return idMatiere;
    }

    public void setIdMatiere(Integer idMatiere) {
        this.idMatiere = idMatiere;
    }
}