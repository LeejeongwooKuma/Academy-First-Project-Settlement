package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// GetConnection 클래스 경로를 가정하고 사용합니다.
// import kr.co.sist.pstmt.dao.GetConnection; 

import javax.swing.ImageIcon;

public class testDAO {
    private static testDAO tDAO;
    // GetConnection 인스턴스는 싱글톤 패턴이므로 getInstance()로 가져오는 것이 좋습니다.
    // private GetConnection gc = GetConnection.getInsance(); 

    public static testDAO getInstance() {
        if (tDAO == null) {
            tDAO = new testDAO();
        }
        return tDAO;
    }

    // 메소드 이름을 'insertfile'로 사용하겠습니다.
    public void insertfile(testDTO lDTO) throws SQLException, IOException {

        Connection con = null;
        PreparedStatement pstmt = null;
        // DB 연결 객체를 DAO 메소드 내부에서 가져옵니다.
        // GetConnection 클래스는 별도로 존재한다고 가정합니다.
        GetConnection gc = GetConnection.getInsance(); 

        FileInputStream[] fins = new FileInputStream[4];

        try {
            con = gc.getConn();

            // 쿼리문: 테이블명과 컬럼명은 이전에 논의된 'BLOBTEST_FOUR_PICS'와 'PHOTO_X'를 사용합니다.
            String insertTest = "INSERT INTO blobtest (CODE, fi1, fi2, fi3, fi4) VALUES (?, ?, ?, ?, ?)";
            pstmt = con.prepareStatement(insertTest);

            // 4-1. CODE 설정
            pstmt.setInt(1, lDTO.getNum());

            // 4-2. 4개의 파일 데이터를 setBinaryStream으로 설정
            // DTO의 File 객체들을 사용합니다.
            fins[0] = new FileInputStream(lDTO.getFi1());
            fins[1] = new FileInputStream(lDTO.getFi2());
            fins[2] = new FileInputStream(lDTO.getFi3());
            fins[3] = new FileInputStream(lDTO.getFi4());

            pstmt.setBinaryStream(2, fins[0], (int) lDTO.getFi1().length());
            pstmt.setBinaryStream(3, fins[1], (int) lDTO.getFi2().length());
            pstmt.setBinaryStream(4, fins[2], (int) lDTO.getFi3().length());
            pstmt.setBinaryStream(5, fins[3], (int) lDTO.getFi4().length());

            // 5. 쿼리문 수행
            pstmt.executeUpdate();

            System.out.println("✅ 데이터 삽입 완료! CODE: " + lDTO.getNum());

        } finally {
            // 6. 연결 끊기 및 자원 해제
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            
            // FileInputStream 닫기 (항상 닫아줘야 합니다)
            for (FileInputStream fin : fins) {
                if (fin != null) {
                    try { fin.close(); } catch (IOException e) { /* 무시 */ }
                }
            }
            
            // Connection 닫기
            gc.dbClose(con, pstmt, null);
        }
        
        
    }
    // insertFriendsMgr 메소드는 제거 (insertfile로 대체되었으므로)
    
    public ImageIcon[] selectImages(int code) throws SQLException, IOException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        GetConnection gc = GetConnection.getInsance();
        
        ImageIcon[] imageIcons = new ImageIcon[4]; // 4개의 이미지를 담을 배열
        
        // 4개의 PHOTO 컬럼을 모두 조회하는 쿼리
        String selectSql = "SELECT fi1, fi2, fi3, fi4 FROM blobtest WHERE CODE = ?";

        try {
            con = gc.getConn();
            pstmt = con.prepareStatement(selectSql);
            pstmt.setInt(1, code);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 4개의 BLOB 컬럼에서 순서대로 데이터를 가져옵니다.
                for (int i = 0; i < 4; i++) {
                    // 컬럼 인덱스는 1부터 시작합니다. (PHOTO_1이 1번)
                    Blob blobData = rs.getBlob(i + 1);
                    
                    if (blobData != null) {
                        // 1. BLOB 데이터를 바이트 배열로 변환합니다.
                        byte[] imageBytes = blobData.getBytes(1, (int) blobData.length());
                        
                        // 2. 바이트 배열로 ImageIcon을 생성합니다.
                        imageIcons[i] = new ImageIcon(imageBytes);
                    } else {
                        // 데이터가 NULL인 경우, null 처리하거나 기본 아이콘을 설정할 수 있습니다.
                        imageIcons[i] = null; 
                    }
                }
            } else {
                // 해당 CODE의 데이터가 없는 경우
                return null;
            }

        } finally {
            gc.dbClose(con, pstmt, rs);
        }
        return imageIcons;
    }
    
    
    
}