package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;

public interface UserImageService {
    UserImage getUserImageByName(String name);

    UserImage getUserImageByUser(User user);

    void saveUserImage(UserImage userImage);

}
