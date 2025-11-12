package project1.Design;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class SettlementEvt extends WindowAdapter implements ActionListener {
	private SettlementDesign smd;
	private int totalSell, totalCount;
	private double totalProfit;
	private DefaultTableModel dtmSettlement, dtmMonthlyInfo;
	
	public SettlementEvt() {
		
	}//SettlementEvt
	
	public SettlementEvt(SettlementDesign smd) {
		this.smd = smd;
	}// SettlementEvt

	public String[] searchCarName() {
		SettlementService sms = new SettlementService();
		List<SettlementDTO> listStDTO = sms.searchCarName();
		String[] carNameArr = new String[listStDTO.size()+1];
		carNameArr[0]="차종";
		//배열의 순서를 표시하기 위한 변수
		int i = 1;
		for(SettlementDTO smDTO : listStDTO) {
			carNameArr[i] = String.valueOf(smDTO.getCar_name_option());
			i++;
		}//end for
		return carNameArr;
	}//searchCarName
	
	// 실적 증감률에 쓰일 직전 월의 값 저장 변수
	double preMonthSales = 0, nowMonthSales = 0;
	public void searchPeriodOptionQuarter(String qtYear, String quarter) {
		dtmMonthlyInfo = smd.getDtmMonthlyInfo();
		SettlementService sms = new SettlementService();
		List<SettlementDTO> listStDTO = sms.searchPeriodOptionQuarter(qtYear, quarter);
		String[] rowData = null;

		// 검색 했는데 검색된 값이 없다면 다이아로그로 알려주고 다시 검색하라고 함.
		if (listStDTO.size() == 0) {
			JOptionPane.showMessageDialog(smd, "검색된 값이 없습니다. 옵션을 조정해주세요.");
		} // end if
		dtmMonthlyInfo.setRowCount(0);
		// 실적 증감률에 쓰일 직전 월의 값 저장 변수
		double preMonthSales = 0.0, nowMonthSales = 0.0, percentRate = 0.0;
		// x분기에서 앞의 숫자값만 저장할 변수
		int quarterInt = 0;
		quarterInt = Integer.parseInt(quarter.substring(0, 1)) * 3;
		// 분기별 판매 현황에서, 모든 월의 총 판매금액 합
		double totalSellSum = 0;
		// 1,2,3,4분기의 경우에 사용될 테이블 추가 로직
		// 테이블에 넣을 값 추가.
		for (SettlementDTO smDTO : listStDTO) {
			rowData = new String[5];
			rowData[0] = smDTO.getSalesMonth();
			rowData[1] = String.format("%,.1f", (double) smDTO.getSalesSum()) + " 만원";
			rowData[2] = String.format("%,.1f", smDTO.getSalesSum() * 0.1) + " 만원";
			rowData[3] = String.format("%,.1f", smDTO.getSalesSum() * 0.9) + " 만원";
			// 전월 기록이 0원이 아니라면
			if (preMonthSales != 0) {
				nowMonthSales = smDTO.getSalesSum();
				percentRate = ((nowMonthSales - preMonthSales) / preMonthSales) * 100;
				rowData[4] = String.format("%,.2f", percentRate) + "%";
			} else {
				// 전월 실적이 0원인 경우(첫 달)
				rowData[4] = "N/A";
			} // end else
			totalSellSum += (double) smDTO.getSalesSum();
			dtmMonthlyInfo.addRow(rowData);
			preMonthSales = smDTO.getSalesSum();
		} // end for
			// 분기별 판매 현황 테이블의 마지막 행에는, 모든 월을 더한 판매 금액을 보여줌.
		rowData = new String[4];
		rowData[0] = "합계";
		rowData[1] = String.format("%,.1f", totalSellSum) + " 만원";
		rowData[2] = String.format("%,.1f", totalSellSum * 0.1) + " 만원";
		rowData[3] = String.format("%,.1f", totalSellSum * 0.9) + " 만원";

		dtmMonthlyInfo.addRow(rowData);

	}// searchPeriodOptionQuarter

	/**
	 * 검색 버튼 -> DTO에서 값 받아오기
	 */
	public void searchPeriodOption(String startPeriod, String endPeriod, String delevery_state, String car_name,
			String oil) {
		dtmSettlement = smd.getDtmSettlementInfot();
		SettlementService sms = new SettlementService();

		List<SettlementDTO> listStDTO = sms.searchPeriodOption(startPeriod, endPeriod, delevery_state, car_name, oil);
		String[] rowData = null;
		dtmSettlement.setRowCount(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

		// 검색 버튼을 누르는데, 분기별 판매 현황 드롭박스가 아니라면, 카드레이아웃을 기본으로 바꿔주고, 드롭박스도 기본 상태로 바꿔줌.
		if (!smd.getJcbQuarterlySettlement().getSelectedItem().equals("분기별 판매현황")) {
			smd.getCardLayout().show(smd.getJpCardPanel(), "cardSettlement");
			smd.getJcbQuarterlySettlement().setSelectedIndex(0);
		} // end if

		// 검색 했는데 검색된 값이 없다면 다이아로그로 알려주고 다시 검색하라고 함.
		if (listStDTO.size() == 0) {
			JOptionPane.showMessageDialog(smd, "검색된 값이 없습니다. 옵션을 조정해주세요.");
		} // end if

		// 테이블에 넣을 값 추가.
		for (SettlementDTO smDTO : listStDTO) {
			rowData = new String[7];
			rowData[0] = String.valueOf(smDTO.getProduct_code());
			rowData[1] = smDTO.getCar_name();
			rowData[2] = smDTO.getOil();
			rowData[3] = String.format("%,d", smDTO.getPrice()) + " 만원";
			rowData[4] = String.format("%,.1f", smDTO.getPrice() * 0.1) + " 만원";
			rowData[5] = String.format("%,.1f", smDTO.getPrice() * 0.9) + " 만원";
			rowData[6] = smDTO.getOrder_date();
			dtmSettlement.addRow(rowData);
			totalSell += smDTO.getPrice();
			totalProfit += smDTO.getPrice() * 0.9;
			totalCount++;
		} // end for
		setTotalSummary();
	}// searchSettleInfo

	// 중간 부분에 있는 총 판매 현황 요약 부분 정보 셋팅
	public void setTotalSummary() {
		String inputPeriod = smd.getJtfStartPeriod().getText() + " ~ " + smd.getJtfEndPeriod().getText();
		smd.getJlbMdPeriod().setText(inputPeriod);
		smd.getJlbSell().setText("총 판매 금액 : " + String.format("%,d", totalSell) + " 만원");
		smd.getJlbProfit().setText("총 정산액(순이익) : " + String.format("%,.2f", totalProfit) + " 만원");
		smd.getJlbCount().setText("총 판매 건 수 : " + totalCount + " 건");
		totalSell = 0;
		totalProfit = 0;
		totalCount = 0;
	}// setTotalSummary

	// 리셋 버튼을 누르면 설정 옵션들을 기본 상태로 되돌림
	public void setReset() {
		smd.getJtfStartPeriod().setText(String.valueOf(smd.getLdEndPo()));
		smd.getJtfEndPeriod().setText(String.valueOf(smd.getLdStartPo()));
		smd.getJcbdeleveryState().setSelectedIndex(0);
		smd.getJcbcarName().setSelectedIndex(0);
		smd.getJcbOil().setSelectedIndex(0);
		smd.getJcbQuarterlySettlement().setSelectedIndex(0);
		smd.getYearSpinner().setValue(2025);
		if (dtmSettlement != null) {
			dtmSettlement.setRowCount(0);
		}
		;
		setTotalSummary();
	}// setReset

	/**
	 * 기간 부분의 데이터 유효성 검사 1. 공백 여부 2. 기간의 서순 체크 3. 기간 format 체크
	 */
	public boolean checkPeriod(String startPeriod, String endPeriod) {
		boolean isValid = false;

		// 1.빈 공백 입력하였는지 확인.
		if (startPeriod.trim().isEmpty() || endPeriod.trim().isEmpty()) {
			JOptionPane.showMessageDialog(smd, "기간 설정 부분을 공백 없이 모두 입력하여 주십시오.");
			isValid = false;
			return isValid;
		} // end if

		try {
			LocalDate tempStart = LocalDate.parse(startPeriod, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDate tempEnd = LocalDate.parse(endPeriod, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			// 2.옵션에서의 시작일보다 종료일이 더 빠르다면 false 반환.
			if (tempStart.isAfter(tempEnd)) {
				isValid = false;
				JOptionPane.showMessageDialog(smd, "기간 설정이 잘못되었습니다. 시작일이 종료일보다 늦게 설정되어 있습니다.");
				return isValid;
			} // end if
			isValid = true;
		} catch (DateTimeException de) {
			// 3.기간 format이 맞는지 확인
			JOptionPane.showMessageDialog(smd,
					"기간 설정 부분에서 오류가 발생하였습니다. \n기간의 값을 확인하여 주십시오. \n기간은 '2025-01-15'와 같은 형식으로 입력하여 주십시오.");
			isValid = false;
		}
		return isValid;
	}// checkPeriod

	@Override
	public void actionPerformed(ActionEvent ae) {
		// JSpinal값 가져오기

		// 분기별 드롭박스 누르면 카드레이아웃으로 테이블 변경
		if (smd.getJcbQuarterlySettlement() == ae.getSource()) {
			changeCardLayout();
			return;
		} // end if

		// 검색버튼 누르면 검색 결과 찾아서 테이블에 추가
		if (smd.getJbtnSearch() == ae.getSource()) {
			// 검색버튼 누른 후 옵션 유효성 검사 수행 후 통과 했다면 실행
			if (checkPeriod(smd.getJtfStartPeriod().getText().trim(), smd.getJtfEndPeriod().getText().trim())) {
				searchPeriodOption(smd.getJtfStartPeriod().getText(), smd.getJtfEndPeriod().getText(),
						smd.getJcbdeleveryState().getSelectedItem().toString(),
						smd.getJcbcarName().getSelectedItem().toString(), smd.getJcbOil().getSelectedItem().toString());
			} // end if
			return;
		} // end if
			// 리셋버튼 누르면 검색 조건 및 테이블 값 리셋
		if (smd.getJbtnReset() == ae.getSource()) {
			setReset();
			return;
		} // end if
	}// end actionPerformed

	/**
	 * 분기별 판매현황의 기간입력 부분에서만 쓰일 유효성 검사 - 2025-10. 월까지만 입력 받음. 일은 안됨.
	 */
	public boolean checkPeriodInput(String startPeriod, String endPeriod) {
		boolean isValid = false;

		// 1.빈 공백 입력하였는지 확인.
		if (startPeriod.trim().isEmpty() || endPeriod.trim().isEmpty()) {
			JOptionPane.showMessageDialog(smd, "기간 설정 부분을 공백 없이 모두 입력하여 주십시오.");
			isValid = false;
			return isValid;
		} // end if
		try {
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
			YearMonth tempStart = YearMonth.parse(startPeriod, dateFormat);
			YearMonth tempEnd = YearMonth.parse(endPeriod, dateFormat);
			// 2.옵션에서의 시작일보다 종료일이 더 빠르다면 false 반환.
			if (tempStart.isAfter(tempEnd)) {
				isValid = false;
				JOptionPane.showMessageDialog(smd, "기간 설정이 잘못되었습니다. 시작일이 종료일보다 늦게 설정되어 있습니다.");
				return isValid;
			} // end if
			isValid = true;
		} catch (DateTimeException de) {
			// 3.기간 format이 맞는지 확인
			JOptionPane.showMessageDialog(smd,
					"기간 설정 부분에서 오류가 발생하였습니다. \n기간의 값을 확인하여 주십시오. \n기간은 '2025-01'와 같은 형식(월까지만)으로 입력하여 주십시오.");
			isValid = false;
		}
		return isValid;
	}// checkPeriod

	/**
	 * 분기별 드롭박스 누르면 카드레이아웃으로 테이블 변경해주기
	 */
	public void changeCardLayout() {
		if (smd.getJcbQuarterlySettlement().getSelectedItem().equals("분기별 판매현황")) {
			smd.getCardLayout().show(smd.getJpCardPanel(), "cardSettlement");
		} else if (smd.getJcbQuarterlySettlement().getSelectedItem().equals("기간 입력")) {
			String startPeriod = JOptionPane.showInputDialog("시작 기간을 입력하여 주십시오.\n기간은 '2025-01'와 같은 형식(월까지만)으로 입력하여 주십시오.");
			if (startPeriod == null) {
				return;
			} // end if
			String endPeriod = JOptionPane.showInputDialog("끝 기간을 입력하여 주십시오\n기간은 '2025-01'와 같은 형식(월까지만)으로 입력하여 주십시오.");
			if (endPeriod == null) {
				return;
			}
			// 유효성 검사 후 통과하면 테이블 추가 메소드 불러오기.
			if (checkPeriodInput(startPeriod, endPeriod)) {
				// 메소드 재사용
				searchPeriodOptionQuarter(startPeriod, endPeriod);
				smd.getCardLayout().show(smd.getJpCardPanel(), "cardMonthly");
			} // end if

		} else {
			searchPeriodOptionQuarter(smd.getYearSpinner().getValue().toString(),
					smd.getJcbQuarterlySettlement().getSelectedItem().toString());
			smd.getCardLayout().show(smd.getJpCardPanel(), "cardMonthly");
		} // end if
	}// changeCardLayout

	@Override
	public void windowClosing(WindowEvent e) {
		smd.dispose();
	}// windowClosing

}// end class
