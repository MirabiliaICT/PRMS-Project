package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "states")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    private String stateCode;

    @OneToOne(mappedBy = "stateForManager")
    private User user;

    @OneToMany(mappedBy = "state")
    private List<City> cities;
}
