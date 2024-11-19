package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import ng.org.mirabilia.pms.domain.enums.Gender;
import ng.org.mirabilia.pms.domain.enums.Relationship;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "next_of_kin")
public class NextOfKinDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    Relationship relationship;
    Gender gender;

    String houseAddress;
    String email;
    String telePhone;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
