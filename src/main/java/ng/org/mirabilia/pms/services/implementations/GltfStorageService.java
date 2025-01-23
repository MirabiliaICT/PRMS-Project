package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.GltfModel;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.repositories.GltfModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class GltfStorageService {

    @Autowired
    private GltfModelRepository repository;
    @Transactional
    public void saveFileToDatabase(GltfModel model) {
        model.setId(model.getId());
        model.setProperty(model.getProperty());
        model.setName(model.getName());
        model.setData(model.getData());
        repository.save(model);
    }

    @Transactional
    public void deleteExistingModel(GltfModel model) {
        repository.deleteById(model.getId());
    }


}
