package ng.org.mirabilia.pms.services.implementations;


import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.PropertyImage;
import ng.org.mirabilia.pms.repositories.ImageRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImageService {

    ImageRepository imageRepository;


//    public void saveImageToDatabase(Property property, byte[] imageData) {
//        PropertyImage imageEntity = new PropertyImage();
//        imageEntity.setPropertyImages(imageData);
//        imageEntity.setProperty(property);
//        imageRepository.save(imageEntity);
//    }

    public Image createImage(Property property) {
        if (property.getPropertyImages() != null && !property.getPropertyImages().isEmpty()) {
            byte[] imageBytes = property.getPropertyImages().get(0).getPropertyImages();
            StreamResource resource = new StreamResource("property-image-" + property.getId(), () -> new ByteArrayInputStream(imageBytes));
            Image image = new Image(resource, "Property Image");
            image.setMaxHeight("100px");
            image.setMaxWidth("50px");
            return image;
        }
        return new Image("placeholder-image-url", "No Image");
    }
}
