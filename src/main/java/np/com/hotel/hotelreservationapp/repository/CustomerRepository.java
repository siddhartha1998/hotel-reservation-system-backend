package np.com.hotel.hotelreservationapp.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import np.com.hotel.hotelreservationapp.model.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	Optional<Customer> findByUsername(String username);

	Boolean existsByUsername(String username);

	@Modifying
	@Query(value="UPDATE tbl_customer_details SET active="+false+" WHERE id=?1",nativeQuery = true)
	public void deleteById(Long id);

	@Query(value="SELECT * FROM tbl_customer_details WHERE active='1'",nativeQuery = true)
	public List<Customer> findAll();

	@Query(value="SELECT * FROM tbl_customer_details WHERE active='0'", nativeQuery=true)
	List<Customer>findInactiveCustomer();
	
	
}
