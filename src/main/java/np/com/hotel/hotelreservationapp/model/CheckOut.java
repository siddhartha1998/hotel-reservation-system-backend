package np.com.hotel.hotelreservationapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tbl_checkoutdetail")
public class CheckOut {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String checkOutDate;
	
	private String hotel;
	
	private Long room;
	
	private String typeOfPayment;
	
	
	private String totalPayment;
	
	

	public CheckOut() {

	}



	public CheckOut(String checkOutDate, String hotel, Long room, String typeOfPayment, 
			String totalPayment) {
		super();
		this.checkOutDate = checkOutDate;
		this.hotel = hotel;
		this.room = room;
		this.typeOfPayment = typeOfPayment;
		this.totalPayment = totalPayment;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getCheckOutDate() {
		return checkOutDate;
	}



	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
	}



	public String getHotel() {
		return hotel;
	}



	public void setHotel(String hotel) {
		this.hotel = hotel;
	}



	public Long getRoom() {
		return room;
	}



	public void setRoom(Long room) {
		this.room = room;
	}



	public String getTypeOfPayment() {
		return typeOfPayment;
	}



	public void setTypeOfPayment(String typeOfPayment) {
		this.typeOfPayment = typeOfPayment;
	}



	public String getTotalPayment() {
		return totalPayment;
	}



	public void setTotalPayment(String totalPayment) {
		this.totalPayment = totalPayment;
	}
	
	

	
	
	
}
