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
@Table(name = "partie_devoir")
public class PartieDevoir {
    @Id
    @Column(name = "id_partie", nullable = false)
    private Integer id;

    @Column(name = "id_devoir", nullable = false)
    private Integer idDevoir;

    @Column(name = "points", nullable = false, precision = 4, scale = 2)
    private BigDecimal points;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdDevoir() {
        return idDevoir;
    }

    public void setIdDevoir(Integer idDevoir) {
        this.idDevoir = idDevoir;
    }

    public BigDecimal getPoints() {
        return points;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }
}