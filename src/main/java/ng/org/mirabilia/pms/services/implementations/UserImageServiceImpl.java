package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.repositories.UserImageRepository;
import ng.org.mirabilia.pms.services.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserImageServiceImpl implements UserImageService {

    @Autowired
    UserImageRepository userImageRepository;

    public UserImageServiceImpl(UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
    }

    public UserImageServiceImpl() {
    }

    @Override
    public UserImage getUserImageByName(String name) {
        return userImageRepository.findByImageName(name);
    }

    @Override
    public UserImage getUserImageByUser(User user) {
        return userImageRepository.findByUser(user);
    }

    @Override
    public void saveUserImage(UserImage userImage) {
        userImageRepository.save(userImage);
    }
}
