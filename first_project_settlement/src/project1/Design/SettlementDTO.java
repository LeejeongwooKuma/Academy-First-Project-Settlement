package project1.Design;

public class SettlementDTO {

	private String car_name, brand, oil, delevery_state, startPeriod, endPeriod, order_date, salesMonth,
			car_name_option;
	private int product_code, price, car_year, salesSum;

	public SettlementDTO() {
		super();
	}// SettlementDTO

	public SettlementDTO(String startPeriod, String endPeriod, String delevery_state, String car_name, String oil) {
		super();
		this.car_name = car_name;
		this.oil = oil;
		this.delevery_state = delevery_state;
		this.startPeriod = startPeriod;
		this.endPeriod = endPeriod;
	}// SettlementDTO

	public SettlementDTO(String salesMonth, int salesSum) {
		super();
		this.salesMonth = salesMonth;
		this.salesSum = salesSum;
	}// SettlementDTO

	public SettlementDTO(String car_name_option) {
		super();
		this.car_name_option = car_name_option;
	}// SettlementDTO

	public String getCar_name() {
		return car_name;
	}

	public String getCar_name_option() {
		return car_name_option;
	}

	public void setCar_name_option(String car_name_option) {
		this.car_name_option = car_name_option;
	}

	public void setCar_name(String car_name) {
		this.car_name = car_name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getOil() {
		return oil;
	}

	public void setOil(String oil) {
		this.oil = oil;
	}

	public String getDelevery_state() {
		return delevery_state;
	}

	public void setDelevery_state(String delevery_state) {
		this.delevery_state = delevery_state;
	}

	public String getStartPeriod() {
		return startPeriod;
	}

	public void setStartPeriod(String startPeriod) {
		this.startPeriod = startPeriod;
	}

	public String getEndPeriod() {
		return endPeriod;
	}

	public void setEndPeriod(String endPeriod) {
		this.endPeriod = endPeriod;
	}

	public int getProduct_code() {
		return product_code;
	}

	public void setProduct_code(int product_code) {
		this.product_code = product_code;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCar_year() {
		return car_year;
	}

	public void setCar_year(int car_year) {
		this.car_year = car_year;
	}

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public String getSalesMonth() {
		return salesMonth;
	}

	public void setSalesMonth(String salesMonth) {
		this.salesMonth = salesMonth;
	}

	public int getSalesSum() {
		return salesSum;
	}

	public void setSalesSum(int salesSum) {
		this.salesSum = salesSum;
	}

}// class