package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    UserImage findByImageName(String imageName);

    UserImage findByImageNameAndUser(String imageName, User user);

    UserImage findByUser(User user);
}
