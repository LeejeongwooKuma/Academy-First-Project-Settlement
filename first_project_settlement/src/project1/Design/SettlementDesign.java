package project1.Design;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class SettlementDesign extends JDialog {
	private JTextField jtfStartPeriod, jtfEndPeriod;
	private JComboBox<String> jcbQuarterlySettlement, jcbdeleveryState, jcbcarName, jcbOil;
	private JButton jbtnSearch, jbtnReset;
	private CardLayout cardLayout;
	private JPanel jpCardPanel;
	private DefaultTableModel dtmSettlementInfot, dtmMonthlyInfo;
	private JTable jtaSettlementInfo, jtaMonthlyInfo;
	private JPanel jpMain, jpSelectOption, jpWrapperPanel, jpMdPeriod;
	private GridBagConstraints gbc;
	private JLabel jlbPeriod, jlbOption, jlbTitle, jlbSetPeriod, jlbTilde, jlbMdPeriod, jlbSell, jlbProfit, jlbCount;
	private Font logoFont, boldFont;
	private TitledBorder jpSelectOptionBorder, jpTotalSummaryBorder;
	private JSpinner yearSpinner;
	
	
	/**
	 * 월별 판매 화면 테이블(향후 db 사용해서 데이터 변경되는 식으로 바꿀 예정)
	 * 
	 * @return
	 */
	public JTable monthlyTable() {
		String[] columnNames = { "월별", "총 판매 금액", "총 세금", "총 정산 금액", "실적 증감률" };
		dtmMonthlyInfo = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false; // 모든 셀 수정 금지
			}// end isCellEditable
		};
		jtaMonthlyInfo = new JTable(dtmMonthlyInfo);

		// 월별 판매 화면에서 합계 부분의 폰트 조정
		BoldTotalRowRendererSettlement renderer = new BoldTotalRowRendererSettlement();
		for (int i = 0; i < jtaMonthlyInfo.getColumnCount(); i++) {
			// 모든 열에 커스텀 렌더러를 적용합니다.
			jtaMonthlyInfo.getColumnModel().getColumn(i).setCellRenderer(renderer);
		} // end for

		return jtaMonthlyInfo;
	}// monthlyTable

	/**
	 * 정산 화면 테이블
	 * 
	 * @return
	 */
	public JTable settlementTable() {
		String[] columnNames = { "차량코드", "차량명", "유종", "판매금액", "세금", "정산금액", "주문일자" };
		dtmSettlementInfot = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false; // 모든 셀 수정 금지
			}// isCellEditable
		};

		DefaultTableCellRenderer cell = new DefaultTableCellRenderer();
		cell.setHorizontalAlignment(JLabel.RIGHT);

		jtaSettlementInfo = new JTable(dtmSettlementInfot);
		jtaSettlementInfo.getColumn("판매금액").setCellRenderer(cell);
		jtaSettlementInfo.getColumn("세금").setCellRenderer(cell);
		jtaSettlementInfo.getColumn("정산금액").setCellRenderer(cell);
		jtaSettlementInfo.getColumn("주문일자").setCellRenderer(cell);
		Font f = new Font("맑은 고딕", Font.BOLD, 12);
		jtaSettlementInfo.setFont(f);
		;

		return jtaSettlementInfo;
	}// settlementTable

	/**
	 * 테이블의 특정 행(합계)부분의 폰트만 바꿔줄 메소드
	 */
	public class BoldTotalRowRendererSettlement extends DefaultTableCellRenderer {
		private final Font boldFont = new Font("맑은 고딕", Font.BOLD, 12);
		private final Font normalFont = new Font("맑은 고딕", Font.BOLD, 12);

		// 실적증감률 및 합계 컬럼의 Font 디자인 변경
		private static final int GROWTH_RATE_COLUMN = 4;
		private static final String TOTAL_ROW_KEY = "합계";

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// 기본 렌더러 컴포넌트 가져오기 (색상 변경 전)
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			// JTable의 셀 값을 문자열로 가져오기.
			String cellValue = (value == null) ? "" : value.toString();

			// 0번째 열(월별)의 값을 확인.
			Object firstColumnValue = table.getModel().getValueAt(row, 0);
			boolean isTotalRow = firstColumnValue != null && firstColumnValue.toString().equals(TOTAL_ROW_KEY);
			// 합계 행 폰트 설정
			if (isTotalRow) {
				// 폰트 및 색상 변경
				c.setForeground(Color.RED); // 폰트 색상을 빨간색으로 설정
			} // end if
				// 실적증감률에서 조건별로 폰트 적용( +는 파란색, -는 빨간색)
			else if (column == GROWTH_RATE_COLUMN) {
				c.setFont(normalFont);
				if (cellValue.equals("N/A")) {
					c.setForeground(Color.BLACK);
				} else if (cellValue.startsWith("-")) {
					c.setForeground(Color.RED);
				} else {
					c.setForeground(Color.BLUE);
				} // end if
			} // end if

			// 일반 행들은 기본 폰트로 변경
			else {
				// 일반 행으로 복구
				c.setFont(normalFont);
				if (!isSelected) {
					c.setForeground(Color.BLACK);
				} // end if
			} // end if

//	        // 셀의 텍스트를 오른쪽 정렬 (숫자 데이터의 일반적인 처리)
			setHorizontalAlignment(SwingConstants.RIGHT);

			// 0번째 열("월별")은 왼쪽 정렬
			if (column == 0) {
				setHorizontalAlignment(SwingConstants.LEFT);
			} // end if

			return c;
		}// getTableCellRendererComponent
	}// BoldTotalRowRendererSettlement

	/**
	 * 월별 현황 뷰 생성
	 * 
	 * @return
	 */
	private JPanel createMonthlyView() {
		JPanel jpMonthly = new JPanel(new BorderLayout());

		JScrollPane jspMonthlyInfo = new JScrollPane(monthlyTable());
		jpMonthly.add(jspMonthlyInfo, BorderLayout.CENTER);

		return jpMonthly;
	}

	/**
	 * 정산내역 뷰 + 테이블
	 * 
	 * @return
	 */
	private JPanel createSettlementView() {
		JPanel jpSettlement = new JPanel(new BorderLayout());

		// 정산 요약 패널
		jpSettlement.add(totalSummaryPanel(), BorderLayout.NORTH);

		// 테이블 추가
		JScrollPane jspSettlementInfo = new JScrollPane(settlementTable());
		jpSettlement.add(jspSettlementInfo, BorderLayout.CENTER);

		return jpSettlement;
	}

	public SettlementDesign() {

	}// SettlementDesign

	/**
	 * 기본 생성자로 디자인 생성
	 * 
	 * @param dt
	 */
	public SettlementDesign(JFrame dt) {
		super(dt, "다이아로그");

		this.setLayout(new BorderLayout()); // JFrame Layout
		// 상단 "쌍용중고차" 타이틀
		setTitle();

		jpMain = new JPanel(new BorderLayout());
		this.add(jpMain, BorderLayout.CENTER);

		// 상단의 옵션패널(옵션,기간)
		optionPanel();

		// 박스레이아웃으로 수직으로 옵션,정산내역/월별 현황 뷰를 묶음
		JPanel jpNorthContents = new JPanel();
		jpNorthContents.setLayout(new BoxLayout(jpNorthContents, BoxLayout.Y_AXIS));

		// 옵션 패널 (jpWrapperPanel)을 컨테이너 상단에 추가 (옵션 정보 + 버튼 포함)
		jpNorthContents.add(jpWrapperPanel);

		jpMain.add(jpNorthContents, BorderLayout.NORTH);

		// CardLayout 설정 및 뷰 추가
		cardLayout = new CardLayout();
		jpCardPanel = new JPanel(cardLayout);

		// 정산내역 뷰 추가
		jpCardPanel.add(createSettlementView(), "cardSettlement");

		// 월별 판매 현황 뷰 추가
		jpCardPanel.add(createMonthlyView(), "cardMonthly");

		// jpMain의 CENTER에 카드 패널 배치
		jpMain.add(jpCardPanel, BorderLayout.CENTER);

		// 초기 화면 설정
		cardLayout.show(jpCardPanel, "cardSettlement"); // 기본은 정산내역 뷰

		// 이벤트 기록
		SettlementEvt sme = new SettlementEvt(this);
		jcbQuarterlySettlement.addActionListener(sme);
		jcbdeleveryState.addActionListener(sme);
		jcbcarName.addActionListener(sme);
		jcbOil.addActionListener(sme);
		jbtnSearch.addActionListener(sme);
		jbtnReset.addActionListener(sme);

		addWindowListener(sme);
		setFont();
		setBounds(100, 100, 800, 800);
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}// SettlementDesign

	/**
	 * 중간에 총 판매 금액 등의 내용이 담긴 패널. 향후 값 받아서 유동적으로 변경되게 바꿀 예정
	 * 
	 * @return
	 */
	public JPanel totalSummaryPanel() {
		// 중간에 총 판매 금액 및 총 정산액 등의 정보가 담긴 패널
		JPanel jpTotalSummary = new JPanel(new GridLayout(1, 2));
//       jpTotalSummary.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jpTotalSummary.setBackground(new Color(230, 230, 230));

		jpTotalSummaryBorder = new TitledBorder("정산내역");
		jpTotalSummary.setBorder(jpTotalSummaryBorder);

		// 왼쪽 기간 부분에 쓸 Panel
		jpMdPeriod = new JPanel(new GridLayout(2, 1));
		jpMdPeriod.setBackground(new Color(200, 200, 200));
		jlbSetPeriod = new JLabel("설정된 정산 기간", SwingConstants.CENTER);

		jpMdPeriod.add(jlbSetPeriod);
//      옵션에서 설정한 기간 출력
		jlbMdPeriod = new JLabel("설정된 기간이 없습니다. 기간을 설정하여 주십시오.", SwingConstants.CENTER);
		jpMdPeriod.add(jlbMdPeriod);
		jpTotalSummary.add(jpMdPeriod);

		// 오른쪽에 간단한 전산 내역 담을 패널
		JPanel jpMdSummary = new JPanel(new BorderLayout());
		// border레이아웃 안에 정보를 세 줄로 나눠 담을 GridLayout
		JPanel jpDetailsSummary = new JPanel(new GridLayout(3, 1)); //

		// 텍스트를 담을 FlowLayout 패널을 만들어 오른쪽 정렬을 유도
		FlowLayout rightAlignFlow = new FlowLayout(FlowLayout.RIGHT);

		// 총 판매 금액
		jlbSell = new JLabel("총 판매 금액 : 만원");
		JPanel p1 = new JPanel(rightAlignFlow);
		p1.setFont(boldFont);
		p1.add(jlbSell);
		jpDetailsSummary.add(p1);

		// 총 정산액 (순이익)
		jlbProfit = new JLabel("총 정산액(순이익) :만원");
		JPanel p2 = new JPanel(rightAlignFlow);
		p2.add(jlbProfit);
		jpDetailsSummary.add(p2);

		// 총 판매 건수
		jlbCount = new JLabel("총 판매 건수 : 건");
		JPanel p3 = new JPanel(rightAlignFlow);
		p3.add(jlbCount);
		jpDetailsSummary.add(p3);

		jpMdSummary.add(jpDetailsSummary, BorderLayout.CENTER);

		// 오른쪽 패널을 메인 패널의 2열에 추가
		jpTotalSummary.add(jpMdSummary);

		return jpTotalSummary;
	}// totalSummaryPanel

	/**
	 * 타이틀_쌍용중고차 추가
	 */
	public void setTitle() {
		jlbTitle = new JLabel("쌍용중고차", JLabel.CENTER);
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		titlePanel.add(jlbTitle);
		this.add(titlePanel, BorderLayout.NORTH);
	}// setTitle

	private LocalDate ldStartPo = null;
	private LocalDate ldEndPo = null;

	/**
	 * 상단의 로고 다음에 나올 기간, 옵션 선택 부분
	 */
	public void optionPanel() {
		// 기간, 옵션 부분 GridBagLayout으로 설정
		jpSelectOption = new JPanel(new GridBagLayout());
		jpWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		gbc = new GridBagConstraints();

		// 타이틀 보더 적용
		jpSelectOptionBorder = new TitledBorder("옵션 정보");
		jpSelectOption.setBorder(jpSelectOptionBorder);

		// 여백
		gbc.insets = new Insets(5, 5, 5, 5);
		// 기간 패널 (배경색 등 설정)
		jlbPeriod = new JLabel("기간");
		jlbPeriod.setOpaque(true);
		jlbPeriod.setBackground(Color.gray);
		jlbPeriod.setForeground(Color.white);
		gbAdd(0, 0, 0.0);
		gbc.fill = GridBagConstraints.BOTH;
		jpSelectOption.add(jlbPeriod, gbc);

		// 기간 부분의 기본값을 현재 날짜 ~ 1년 전 으로 설정.
		ldStartPo = LocalDate.now();
		ldEndPo = ldStartPo.minusYears(1);
		// 기간 시작일
		jtfStartPeriod = new JTextField(String.valueOf(ldEndPo));
		gbAdd(1, 0, 0.3);
		jpSelectOption.add(jtfStartPeriod, gbc);
		// 중간에 들어갈 ~
		jlbTilde = new JLabel("~", SwingConstants.CENTER);
		gbAdd(2, 0, 0.0);
		jpSelectOption.add(jlbTilde, gbc);
		// 기간 끝
		jtfEndPeriod = new JTextField(String.valueOf(ldStartPo));
		gbAdd(3, 0, 0.3);
		jpSelectOption.add(jtfEndPeriod, gbc);
		// 분기별 판매 현황의 기준 년도 설정
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		SpinnerNumberModel yearModel = new SpinnerNumberModel(currentYear, 1995, 2025, 1);
		yearSpinner = new JSpinner(yearModel);
		// 년도만 표시하도록 포맷 설정
		yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "0000년"));
		JComponent editor = yearSpinner.getEditor();

		// 해당 년도 입력 막기
//      if (editor instanceof JSpinner.DefaultEditor) {
//            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
//            textField.setEditable(false); 
//        }
		gbAdd(4, 0, 0.2);

		jpSelectOption.add(yearSpinner, gbc);
		// 분기별 판매 현황
		String[] QuarterlyArr = { "분기별 판매현황", "1분기", "2분기", "3분기", "4분기", "1년", "기간 입력" }; // ⬅️ "월별 판매 현황" 추가
		jcbQuarterlySettlement = new JComboBox<String>(QuarterlyArr);
		gbAdd(5, 0, 0.4);
		jpSelectOption.add(jcbQuarterlySettlement, gbc);

		// 2행 시작, 옵션 설정
		jlbOption = new JLabel("옵션");
		jlbOption.setOpaque(true);
		jlbOption.setBackground(Color.gray);
		jlbOption.setForeground(Color.white);
		gbAdd(0, 1, 0.0);
		jpSelectOption.add(jlbOption, gbc);
		// 옵션 행의 탁송 상태, 차종, 유종에 해당하는 콤보박스
		String[] deleveryStateArr = { "탁송 상태", "탁송 준비", "탁송 중", "탁송 완료" };
		SettlementEvt sme = new SettlementEvt();
		String[] carNameArr = sme.searchCarName(); 
		String[] oilArr = { "유종", "LPG", "가솔린", "디젤","전기","하이브리드", };

		jcbdeleveryState = new JComboBox<String>(deleveryStateArr);
		gbAdd(1, 1, 0.0);
		jpSelectOption.add(jcbdeleveryState, gbc);

		jcbcarName = new JComboBox<String>(carNameArr);
		gbAdd(2, 1, 0.0);
		jpSelectOption.add(jcbcarName, gbc);

		jcbOil = new JComboBox<String>(oilArr);
		gbAdd(3, 1, 0.0);
		jpSelectOption.add(jcbOil, gbc);

		// 버튼들을 담을 FlowLayout 패널
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jbtnSearch = new JButton("검색");
		jbtnReset = new JButton("초기화");
		buttonPanel.add(jbtnSearch);
		buttonPanel.add(jbtnReset);

		jpWrapperPanel.setLayout(new BoxLayout(jpWrapperPanel, BoxLayout.Y_AXIS));

		// jpSelectOption (옵션 정보) 추가
		jpWrapperPanel.add(jpSelectOption);
		// 버튼 패널 추가
		jpWrapperPanel.add(buttonPanel);
	}// optionPanel

	/**
	 * 폰트 적용
	 */
	public void setFont() {
		logoFont = new Font("맑은 고딕", Font.BOLD, 25);
		boldFont = new Font("맑은 고딕", Font.BOLD, 15);
		Font boldFont2 = new Font("맑은 고딕", Font.BOLD, 13);
		// Title폰트
		jlbTitle.setFont(logoFont);
		// Tilde 폰트
		jlbTilde.setFont(logoFont);
		// 상단의 옵션 폰트
		jlbOption.setFont(boldFont);
		// 상단의 기간 폰트
		jlbPeriod.setFont(boldFont);
		// 상단 기간의 start 폰트
		jtfStartPeriod.setFont(boldFont2);
		// 상단 기간의 end 폰트
		jtfEndPeriod.setFont(boldFont2);
		// 중간에 설정된 기간
		jlbSetPeriod.setFont(boldFont);
		// JSpinner 폰트
		yearSpinner.setFont(boldFont2);
		// 중간에 설정된 기간의 기간 부분
		jlbMdPeriod.setFont(boldFont);
		// TitleBorder 폰트
		jpSelectOptionBorder.setTitleFont(boldFont);
		jpTotalSummaryBorder.setTitleFont(boldFont);
		// 콤보박스
		jcbdeleveryState.setFont(boldFont2);
		jcbcarName.setFont(boldFont2);
		jcbOil.setFont(boldFont2);
		jcbQuarterlySettlement.setFont(boldFont2);
		// Table의 헤더부분 폰트
		JTableHeader tableHeader = jtaSettlementInfo.getTableHeader();
		tableHeader.setFont(boldFont2);

		// 월별 현황 테이블 헤더 폰트 적용..월별,총 판매금액...
		if (jtaMonthlyInfo != null) {
			JTableHeader monthlyTableHeader = jtaMonthlyInfo.getTableHeader();
			monthlyTableHeader.setFont(boldFont2);
		} // end if

	}// setFont

	/**
	 * GridBagLayout 설정 값 메소드 *
	 * 
	 * @param x x위치
	 * @param y y위치
	 * @param w 넓이
	 */
	public void gbAdd(int x, int y, double w) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.weightx = w; // 넓이
	}// gbAdd

	public JTextField getJtfStartPeriod() {
		return jtfStartPeriod;
	}

	public void setJtfStartPeriod(JTextField jtfStartPeriod) {
		this.jtfStartPeriod = jtfStartPeriod;
	}

	public JTextField getJtfEndPeriod() {
		return jtfEndPeriod;
	}

	public void setJtfEndPeriod(JTextField jtfEndPeriod) {
		this.jtfEndPeriod = jtfEndPeriod;
	}

	public JComboBox<String> getJcbQuarterlySettlement() {
		return jcbQuarterlySettlement;
	}

	public void setJcbQuarterlySettlement(JComboBox<String> jcbQuarterlySettlement) {
		this.jcbQuarterlySettlement = jcbQuarterlySettlement;
	}

	public JComboBox<String> getJcbdeleveryState() {
		return jcbdeleveryState;
	}

	public void setJcbdeleveryState(JComboBox<String> jcbdeleveryState) {
		this.jcbdeleveryState = jcbdeleveryState;
	}

	public JComboBox<String> getJcbcarName() {
		return jcbcarName;
	}

	public void setJcbcarName(JComboBox<String> jcbcarName) {
		this.jcbcarName = jcbcarName;
	}

	public JComboBox<String> getJcbOil() {
		return jcbOil;
	}

	public void setJcbOil(JComboBox<String> jcbOil) {
		this.jcbOil = jcbOil;
	}

	public JButton getJbtnSearch() {
		return jbtnSearch;
	}

	public void setJbtnSearch(JButton jbtnSearch) {
		this.jbtnSearch = jbtnSearch;
	}

	public JButton getJbtnReset() {
		return jbtnReset;
	}

	public void setJbtnReset(JButton jbtnReset) {
		this.jbtnReset = jbtnReset;
	}

	public CardLayout getCardLayout() {
		return cardLayout;
	}

	public void setCardLayout(CardLayout cardLayout) {
		this.cardLayout = cardLayout;
	}

	public JPanel getJpCardPanel() {
		return jpCardPanel;
	}

	public void setJpCardPanel(JPanel jpCardPanel) {
		this.jpCardPanel = jpCardPanel;
	}

	public DefaultTableModel getDtmSettlementInfot() {
		return dtmSettlementInfot;
	}

	public void setDtmSettlementInfot(DefaultTableModel dtmSettlementInfot) {
		this.dtmSettlementInfot = dtmSettlementInfot;
	}

	public DefaultTableModel getDtmMonthlyInfo() {
		return dtmMonthlyInfo;
	}

	public void setDtmMonthlyInfo(DefaultTableModel dtmMonthlyInfo) {
		this.dtmMonthlyInfo = dtmMonthlyInfo;
	}

	public JTable getJtaSettlementInfo() {
		return jtaSettlementInfo;
	}

	public void setJtaSettlementInfo(JTable jtaSettlementInfo) {
		this.jtaSettlementInfo = jtaSettlementInfo;
	}

	public JTable getJtaMonthlyInfo() {
		return jtaMonthlyInfo;
	}

	public void setJtaMonthlyInfo(JTable jtaMonthlyInfo) {
		this.jtaMonthlyInfo = jtaMonthlyInfo;
	}

	public JPanel getJpMain() {
		return jpMain;
	}

	public void setJpMain(JPanel jpMain) {
		this.jpMain = jpMain;
	}

	public JPanel getJpSelectOption() {
		return jpSelectOption;
	}

	public void setJpSelectOption(JPanel jpSelectOption) {
		this.jpSelectOption = jpSelectOption;
	}

	public JPanel getJpWrapperPanel() {
		return jpWrapperPanel;
	}

	public void setJpWrapperPanel(JPanel jpWrapperPanel) {
		this.jpWrapperPanel = jpWrapperPanel;
	}

	public JPanel getJpMdPeriod() {
		return jpMdPeriod;
	}

	public void setJpMdPeriod(JPanel jpMdPeriod) {
		this.jpMdPeriod = jpMdPeriod;
	}

	public GridBagConstraints getGbc() {
		return gbc;
	}

	public void setGbc(GridBagConstraints gbc) {
		this.gbc = gbc;
	}

	public JLabel getJlbPeriod() {
		return jlbPeriod;
	}

	public void setJlbPeriod(JLabel jlbPeriod) {
		this.jlbPeriod = jlbPeriod;
	}

	public JLabel getJlbOption() {
		return jlbOption;
	}

	public void setJlbOption(JLabel jlbOption) {
		this.jlbOption = jlbOption;
	}

	public JLabel getJlbTitle() {
		return jlbTitle;
	}

	public void setJlbTitle(JLabel jlbTitle) {
		this.jlbTitle = jlbTitle;
	}

	public JLabel getJlbSetPeriod() {
		return jlbSetPeriod;
	}

	public void setJlbSetPeriod(JLabel jlbSetPeriod) {
		this.jlbSetPeriod = jlbSetPeriod;
	}

	public JLabel getJlbTilde() {
		return jlbTilde;
	}

	public void setJlbTilde(JLabel jlbTilde) {
		this.jlbTilde = jlbTilde;
	}

	public JLabel getJlbMdPeriod() {
		return jlbMdPeriod;
	}

	public void setJlbMdPeriod(JLabel jlbMdPeriod) {
		this.jlbMdPeriod = jlbMdPeriod;
	}

	public JLabel getJlbSell() {
		return jlbSell;
	}

	public void setJlbSell(JLabel jlbSell) {
		this.jlbSell = jlbSell;
	}

	public JLabel getJlbProfit() {
		return jlbProfit;
	}

	public void setJlbProfit(JLabel jlbProfit) {
		this.jlbProfit = jlbProfit;
	}

	public JLabel getJlbCount() {
		return jlbCount;
	}

	public void setJlbCount(JLabel jlbCount) {
		this.jlbCount = jlbCount;
	}

	public Font getLogoFont() {
		return logoFont;
	}

	public void setLogoFont(Font logoFont) {
		this.logoFont = logoFont;
	}

	public Font getBoldFont() {
		return boldFont;
	}

	public void setBoldFont(Font boldFont) {
		this.boldFont = boldFont;
	}

	public TitledBorder getJpSelectOptionBorder() {
		return jpSelectOptionBorder;
	}

	public void setJpSelectOptionBorder(TitledBorder jpSelectOptionBorder) {
		this.jpSelectOptionBorder = jpSelectOptionBorder;
	}

	public TitledBorder getJpTotalSummaryBorder() {
		return jpTotalSummaryBorder;
	}

	public void setJpTotalSummaryBorder(TitledBorder jpTotalSummaryBorder) {
		this.jpTotalSummaryBorder = jpTotalSummaryBorder;
	}

	public JSpinner getYearSpinner() {
		return yearSpinner;
	}

	public void setYearSpinner(JSpinner yearSpinner) {
		this.yearSpinner = yearSpinner;
	}

	public LocalDate getLdStartPo() {
		return ldStartPo;
	}

	public void setLdStartPo(LocalDate ldStartPo) {
		this.ldStartPo = ldStartPo;
	}

	public LocalDate getLdEndPo() {
		return ldEndPo;
	}

	public void setLdEndPo(LocalDate ldEndPo) {
		this.ldEndPo = ldEndPo;
	}

}// class