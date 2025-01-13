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
@Table(name = "classe")
public class Classe {
    @Id
    @Column(name = "id_classe", nullable = false)
    private Integer id;

    @Column(name = "denomination", nullable = false, length = 100)
    private String denomination;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }
}