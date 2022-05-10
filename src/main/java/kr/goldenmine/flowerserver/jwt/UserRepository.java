package kr.goldenmine.flowerserver.jwt;

import kr.goldenmine.flowerserver.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Profile, String> {

    Optional<Profile> findById(String id);
}