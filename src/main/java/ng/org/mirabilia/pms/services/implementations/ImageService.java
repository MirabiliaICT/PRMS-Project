package ng.org.mirabilia.pms.services.implementations;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.PropertyImage;
import ng.org.mirabilia.pms.repositories.ImageRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImageService {

    ImageRepository imageRepository;


    public void saveImageToDatabase(Property property, byte[] imageData) {
        PropertyImage imageEntity = new PropertyImage();
        imageEntity.setPropertyImages(imageData);
        imageEntity.setProperty(property);
        imageRepository.save(imageEntity);
    }
}
