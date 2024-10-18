package ng.org.mirabilia.pms.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    private String stateCode;

    @OneToMany(mappedBy = "state", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<City> cities;
}
