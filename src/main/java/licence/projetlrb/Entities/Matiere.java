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
@Table(name = "matiere")
public class Matiere {
    @Id
    @Column(name = "id_matiere", nullable = false)
    private Integer id;

    @Column(name = "denomination", nullable = false, length = 100)
    private String denomination;

}