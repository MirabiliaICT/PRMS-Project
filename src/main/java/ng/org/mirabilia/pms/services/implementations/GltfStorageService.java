package ng.org.mirabilia.pms.services.implementations;

import ng.org.mirabilia.pms.domain.entities.GltfModel;
import ng.org.mirabilia.pms.repositories.GltfModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class GltfStorageService {

    @Autowired
    private GltfModelRepository repository;

    public void saveFileToDatabase(String fileName, InputStream fileData) {
        try {
            GltfModel model = new GltfModel();
            model.setId(model.getId());
            model.setProperty(model.getProperty());
            model.setName(fileName);
            model.setData(fileData.readAllBytes());
            repository.save(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
