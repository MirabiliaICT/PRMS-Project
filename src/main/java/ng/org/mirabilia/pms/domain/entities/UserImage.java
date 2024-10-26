package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Lob
    private byte[] userImage;
    private String imageName;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
