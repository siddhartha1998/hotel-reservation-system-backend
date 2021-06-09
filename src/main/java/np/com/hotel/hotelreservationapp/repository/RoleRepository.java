package np.com.hotel.hotelreservationapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import np.com.hotel.hotelreservationapp.model.ERole;
import np.com.hotel.hotelreservationapp.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);

}
