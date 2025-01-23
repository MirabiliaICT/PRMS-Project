package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Entity
@Getter
@Setter
@Table(name = "gltf_model")
public class GltfModel {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Lob
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "property_id")
    private Property property;


}
