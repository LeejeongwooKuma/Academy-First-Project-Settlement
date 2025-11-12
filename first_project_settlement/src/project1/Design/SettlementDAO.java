package project1.Design;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SettlementDAO {
	private static SettlementDAO smDAO;

	private SettlementDAO() {

	}// SettlementDAO

	// Singleton Patton을 사용해서 인스턴스 객체 얻기
	public static SettlementDAO getInstance() {
		if (smDAO == null) {
			smDAO = new SettlementDAO();
		} // end if
		return smDAO;
	}// SettlementDAO
	
	// 옵션의 차종을 실시간으로 업데이트 하기 위한 메소드
		public List<SettlementDTO> selectCarName() throws SQLException {
			List<SettlementDTO> smDTOList = new ArrayList<SettlementDTO>();
			Connection con = null;
			PreparedStatement pstmt = null;
			GetConnection gc = GetConnection.getInstance();
			ResultSet rs = null;

			try {
				con = gc.getConn();
				// 쿼리문을 설정하여 생성 객체 얻기
				String selectCarInfo = ("select distinct car_name from car_info");


				pstmt = con.prepareStatement(selectCarInfo);
				rs = pstmt.executeQuery();
				String carNameOption = null;
				SettlementDTO smDTO = null;
				while (rs.next()) {
					carNameOption = rs.getString("car_name");
					smDTO = new SettlementDTO(carNameOption);
					smDTOList.add(smDTO);
				} // end while
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				gc.dbClose(con, pstmt, null);
			} // end finally
			return smDTOList;
		}// selectCarName
	
	// 분기별 판매현황 테이블에서 쓰일 데이터 DB에서 가져오기
	public List<SettlementDTO> selectPeriodOptionQuarter(String qtYear, String quarter) throws SQLException {
		List<SettlementDTO> smDTOList = new ArrayList<SettlementDTO>();
		Connection con = null;
		PreparedStatement pstmt = null;
		GetConnection gc = GetConnection.getInstance();
		ResultSet rs = null;
		try {
			con = gc.getConn();

			String selectCarInfo = ("select to_char(oh.order_date,'yyyy-mm') sales_month,sum(ci.price) sales_sum\r\n"
					+ "from car_info ci, order_history oh\r\n" + "where ci.product_code = oh.product_code\r\n"
					+ "group by to_char(oh.order_date,'yyyy-mm')\r\n"
					+ "having to_char(oh.order_date,'yyyy-mm') between ? and ?" + "order by sales_month");

			// 다이나믹 쿼리로 값 삽입.
			// x분기에서 앞의 숫자값만 저장할 변수
			int quarterInt = 0;
			String startBindYear = null, endBindYear = null;
			pstmt = con.prepareStatement(selectCarInfo);
			// 1,2,3분기 일 경우의 로직(이 경우 월을 3월 -> 03월 이렇게 바꿔줘야 함)
			if (quarter.equals("1분기") || quarter.equals("2분기") || quarter.equals("3분기")) {
				quarterInt = Integer.parseInt(quarter.substring(0, 1)) * 3;
				startBindYear = qtYear + "-" + "0" + String.valueOf(quarterInt - 2);
				endBindYear = qtYear + "-" + "0" + String.valueOf(quarterInt);
				pstmt.setString(1, startBindYear);
				pstmt.setString(2, endBindYear);
			} else if (quarter.equals("4분기")) {// 4분기의 경우는 10,11,12월이기 때문에 굳이 0을 넣을 필요가 없음.
				quarterInt = Integer.parseInt(quarter.substring(0, 1)) * 3;
				startBindYear = qtYear + "-" + String.valueOf(quarterInt - 2);
				endBindYear = qtYear + "-" + String.valueOf(quarterInt);
				pstmt.setString(1, startBindYear);
				pstmt.setString(2, endBindYear);
			} else if (quarter.equals("1년")) {// 분기 조건이 1년일 경우.
				startBindYear = qtYear + "-" + "01";
				endBindYear = qtYear + "-" + "12";
				pstmt.setString(1, startBindYear);
				pstmt.setString(2, endBindYear);
			} else {// 사용자가 직접 값을 입력한 경우.
				startBindYear = qtYear;
				endBindYear = quarter;
				pstmt.setString(1, startBindYear);
				pstmt.setString(2, endBindYear);
			}//end if


			rs = pstmt.executeQuery();

			String salesMonth = null;
			int salesSum = 0;
			SettlementDTO smDTO = null;
			while (rs.next()) {
				salesMonth = rs.getString("sales_month");
				salesSum = rs.getInt("sales_sum");
				smDTO = new SettlementDTO(salesMonth, salesSum);
				smDTOList.add(smDTO);
			} // end while

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}//end catch
		return smDTOList;
	}// searchPeriodOptionQuarter

	// 기간, 옵션 설정 후 DB에서 데이터 가져오기
	public List<SettlementDTO> selectPeriodOption(String startPeriod, String endPeriod, String delevery_state,
			String car_name, String oil) throws SQLException {
		List<SettlementDTO> smDTOList = new ArrayList<SettlementDTO>();
		Connection con = null;
		PreparedStatement pstmt = null;
		GetConnection gc = GetConnection.getInstance();
		ResultSet rs = null;
		LocalDate startLDate = LocalDate.parse(startPeriod.trim(), DateTimeFormatter.ofPattern("yyyy-M-d"));
	    LocalDate endLDate = LocalDate.parse(endPeriod.trim(), DateTimeFormatter.ofPattern("yyyy-M-d"));

		try {
			// 입력된 날짜 값이 2025-1-1같은 경우, date형으로 바꾸면서 자동으로 2025-01-01 형식으로 바꿔줌.
			startPeriod = String.valueOf(startLDate);
	        endPeriod = String.valueOf(endLDate);


			con = gc.getConn();
			// 쿼리문을 설정하여 생성 객체 얻기
			String selectCarInfo = ("select ci.product_code, ci.car_name, ci.oil,ci.price, "
		               + "to_char(oh.order_date,'yyyy-mm-dd') to_order_date " + "from car_info ci, order_history oh "
		               + "where ci.product_code = oh.product_code and oh.order_date >= ? and oh.order_date < ?");
			StringBuilder sbSelect = new StringBuilder(selectCarInfo);
			// 다이나믹 쿼리로 값을 넣을 때, append 위치 기억 용도
			int appendCnt = 2;

			if (!delevery_state.equals("탁송 상태")) {
				sbSelect.append(" and oh.delivery_state = ?");
			} // end if
			if (!car_name.equals("차종")) {
				sbSelect.append(" and ci.car_name = ?");
			} // end if
			if (!oil.equals("유종")) {
				sbSelect.append(" and ci.oil = ?");
			} // end if
			sbSelect.append(" order by ci.product_code asc");

			pstmt = con.prepareStatement(sbSelect.toString());

			 pstmt.setTimestamp(1, Timestamp.valueOf(startLDate.atStartOfDay()));
	         pstmt.setTimestamp(2, Timestamp.valueOf(endLDate.plusDays(1).atStartOfDay()));
			if (!delevery_state.equals("탁송 상태")) {
				pstmt.setString(++appendCnt, delevery_state.trim());
			} // end if
			if (!car_name.equals("차종")) {
				pstmt.setString(++appendCnt, car_name.trim());
			} // end if
			if (!oil.equals("유종")) {
				pstmt.setString(++appendCnt, oil.trim());
			} // end if

			rs = pstmt.executeQuery();
			int productCode = 0;
			int price = 0;
			String carName = null;
			String tableOil = null;
			String orderDate = null;
			SettlementDTO smDTO = null;
			int cnt = 0;
			while (rs.next()) {
				productCode = rs.getInt("product_code");
				carName = rs.getString("car_name");
				tableOil = rs.getString("oil");
				price = rs.getInt("price");
				orderDate = rs.getString("to_order_date");
				smDTO = new SettlementDTO(startPeriod, endPeriod, delevery_state, car_name, oil);
				smDTO.setProduct_code(productCode);
				smDTO.setCar_name(carName);
				smDTO.setOil(tableOil);
				smDTO.setPrice(price);
				smDTO.setOrder_date(orderDate);
				smDTOList.add(smDTO);
				cnt++;
			} // end while

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			gc.dbClose(con, pstmt, null);
		} // end finally
		return smDTOList;
	}// searchPeriodOption

}// class