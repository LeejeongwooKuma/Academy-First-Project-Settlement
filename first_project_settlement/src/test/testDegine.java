package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException; 

public class testDegine extends JFrame {
    
    // 1. 선택된 파일 객체들을 저장할 리스트 (최대 4개)
    private List<File> selectedFiles = new ArrayList<>(4);
    
    // GUI 컴포넌트
    JPanel centerPanel;
    JLabel[] imageLabels = new JLabel[4];
    int currentImageIndex = 0;

    public testDegine() {
        // 1. 기본 설정 (복구)
        setTitle("간단 이미지 뷰어");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 2. Center 영역 (이미지 칸 4개) (복구)
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 2, 5, 5));
        
        for (int i = 0; i < 4; i++) {
            imageLabels[i] = new JLabel("사진 " + (i + 1) + " 칸", SwingConstants.CENTER);
            imageLabels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            imageLabels[i].setOpaque(true);
            imageLabels[i].setBackground(Color.LIGHT_GRAY);
            imageLabels[i].setPreferredSize(new Dimension(350, 250)); 
            centerPanel.add(imageLabels[i]);
        }
        
        // 👈 핵심 수정 1: CenterPanel을 Frame에 추가
        add(centerPanel, BorderLayout.CENTER); 

        // 3. South 영역 (버튼 3개) (복구)
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton uploadButton = new JButton("파일 올리기");
        JButton searchButton = new JButton("찾기");
        JButton addButton = new JButton("DB에 추가"); 

        // 파일 올리기 버튼 클릭 이벤트 처리 (복구)
        uploadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("파일 올리기 버튼 반응");
				if (currentImageIndex < 4) {
					priviewImg();
				} else {
					JOptionPane.showMessageDialog(testDegine.this, "4개의 칸이 모두 채워졌습니다. DB에 추가하세요.");
				}
			}
		});

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // '찾기' 로직 호출
                loadImagesFromDB();
            }
        });

        // 👈 DB에 추가 버튼 클릭 이벤트 처리
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertDataToDB(); 
            }
        });

        southPanel.add(uploadButton);
        southPanel.add(searchButton);
        southPanel.add(addButton);
        
        // 👈 핵심 수정 2: SouthPanel을 Frame에 추가
        add(southPanel, BorderLayout.SOUTH); 

        // **생성자 마지막에 추가** (복구)
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true); // 👈 모든 컴포넌트 추가 후 마지막에 호출
    }
    
    // priviewImg() 메소드는 길어지므로 생략. (정상 작동한다고 가정)
    public void priviewImg() {
        JFileChooser jfc = new JFileChooser();
        jfc.showOpenDialog(this);
        File file = jfc.getSelectedFile();
        
        if (file != null) { 
            // 1. 확장자 검사 로직
            String fileName = file.getName();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            boolean flag = false;
            String allowedExt = "png,jpg,jpeg,gif,bmp"; 
            String[] tempExtArr = allowedExt.split(",");
            for(String tempExt : tempExtArr) {
                if(flag = tempExt.equals(ext)) {
                    break;
                }
            }
            if( !flag ) { 
                JOptionPane.showMessageDialog(this, "이미지파일(" + allowedExt.replace(",", "/") + ")만 허용합니다.");
                return;
            }

            // 2. 전체 경로(Absolute Path) 사용
            String absolutePath = file.getAbsolutePath(); 
            
            // 3. 이미지 로드 및 크기 조정
            ImageIcon originalIcon = new ImageIcon(absolutePath);
            Image originalImage = originalIcon.getImage();
            int targetWidth = 350;
            int targetHeight = 250;
            Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // 4. 이미지 아이콘 설정 및 화면 갱신
            JLabel targetLabel = imageLabels[currentImageIndex];
            targetLabel.setIcon(scaledIcon);
            targetLabel.setText(null); 
            
            // 5. 선택된 File 객체를 리스트에 저장
            selectedFiles.add(file); 
            
            // 다음 칸으로 인덱스 이동
            currentImageIndex++;
            
            centerPanel.revalidate(); 
            centerPanel.repaint();    
        }
    }

    // insertDataToDB() 메소드 (DB 삽입 로직)는 변경 없음 (정상 작동한다고 가정)
    private void insertDataToDB() {
        if (currentImageIndex != 4) {
            JOptionPane.showMessageDialog(this, "4개의 사진을 모두 올려주세요! (현재: " + currentImageIndex + "개)", "경고",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codeStr = JOptionPane.showInputDialog(this, "저장할 CODE(번호)를 입력하세요 (숫자 3자리):", "CODE 입력", JOptionPane.PLAIN_MESSAGE);
        int code = 0;
        try {
            if (codeStr == null || codeStr.trim().isEmpty()) return; 
            code = Integer.parseInt(codeStr);
            if (code < 1 || code > 999) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 CODE(1~999 사이의 숫자)를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        testDTO dto = new testDTO(code, selectedFiles.get(0), selectedFiles.get(1), selectedFiles.get(2), selectedFiles.get(3));

        testDAO dao = testDAO.getInstance(); 
        try {
            dao.insertfile(dto); 
            JOptionPane.showMessageDialog(this, "CODE " + code + "로 4장의 이미지가 성공적으로 DB에 저장되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            resetUI();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 저장 중 SQL 오류 발생: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "DB 저장 중 파일 입출력 오류 발생: " + e.getMessage(), "파일 오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void resetUI() {
        currentImageIndex = 0;
        selectedFiles.clear();
        for (int i = 0; i < 4; i++) {
            imageLabels[i].setIcon(null);
            imageLabels[i].setText("사진 " + (i + 1) + " 칸");
            imageLabels[i].setBackground(Color.LIGHT_GRAY);
        }
        centerPanel.revalidate();
        centerPanel.repaint();
    }
    
 // 👈 새로운 메소드: DB에서 CODE로 이미지를 불러와 표시
    private void loadImagesFromDB() {
        // 사용자에게 CODE(번호)를 입력받습니다.
        String codeStr = JOptionPane.showInputDialog(this, "찾을 CODE(번호)를 입력하세요:", "CODE 찾기", JOptionPane.PLAIN_MESSAGE);
        int code = 0;
        
        try {
            if (codeStr == null || codeStr.trim().isEmpty()) return;
            code = Integer.parseInt(codeStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 CODE(숫자)를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        testDAO dao = testDAO.getInstance();
        try {
            // DAO에서 ImageIcon 배열을 가져옵니다.
            ImageIcon[] loadedIcons = dao.selectImages(code);
            
            if (loadedIcons == null) {
                JOptionPane.showMessageDialog(this, "CODE " + code + "에 해당하는 데이터가 DB에 없습니다.", "결과 없음", JOptionPane.INFORMATION_MESSAGE);
                // 결과를 찾지 못했으므로 UI를 초기화합니다.
                resetUI();
                return;
            }

            // UI에 이미지 표시 및 초기화
            resetUIForLoad(); // 기존 미리보기 초기화 (필요하다면)

            for (int i = 0; i < 4; i++) {
                ImageIcon icon = loadedIcons[i];
                JLabel targetLabel = imageLabels[i];
                
                if (icon != null) {
                    // DTO에 저장할 때는 크기 조정이 필요 없었지만, 화면에 표시할 때는 크기 조정이 필요합니다.
                    int targetWidth = 350;
                    int targetHeight = 250;
                    
                    Image scaledImage = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    
                    targetLabel.setIcon(scaledIcon);
                    targetLabel.setText(null);
                } else {
                    targetLabel.setIcon(null);
                    targetLabel.setText("(이미지 없음)");
                    targetLabel.setBackground(Color.RED); // 이미지가 없음을 표시
                }
            }
            
            // 화면 갱신
            centerPanel.revalidate();
            centerPanel.repaint();
            JOptionPane.showMessageDialog(this, "CODE " + code + "의 이미지를 불러왔습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 조회 중 SQL 오류 발생: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 처리 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 이미지 로드 전용 UI 초기화 (선택 파일 리스트는 건드리지 않습니다.)
    private void resetUIForLoad() {
        currentImageIndex = 0; // 미리보기 인덱스 초기화
        selectedFiles.clear(); // 미리보기 파일 리스트 초기화 (중요)
        for (int i = 0; i < 4; i++) {
            imageLabels[i].setIcon(null);
            imageLabels[i].setText("사진 " + (i + 1) + " 칸");
            imageLabels[i].setBackground(Color.LIGHT_GRAY);
        }
    }
    // ... (main 메소드)


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new testDegine();
            }
        });
    }
}