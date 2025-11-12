package project1.Design;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class GetConnection {
	private static GetConnection getCon;

	//private 선언으로 외부에서 객체 생성 불가
	private GetConnection() {
		super();
	}// GetConnection

	public static GetConnection getInstance() {
		if (getCon == null) {// 객체가 생성되어 있지 않을 때만
			getCon = new GetConnection();// 새로 객체를 생성하라.

		} // end if
		return getCon;
	}// getInstance

	public static Connection getConn() throws SQLException, IOException {
		
		// 1. 드라이버를 로딩
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // end catch
		
		//25.11.02 최승준
		//ㅁ RunnableJar 실행을 위한 경로 수정
		// Properties 파일 경로를 ClassLoader를 사용하여 스트림으로 읽어온다.
		
		///Properties 객체 생성하여 FileStream 연결 후 값 가져오기.
		Properties prop = new Properties();
		
		String propPath = "properties/database.properties";
		
		try {
		// ClassLoader를 사용하여 리소스 스트림을 얻는다.
		InputStream inputStream = GetConnection.class.getClassLoader().getResourceAsStream(propPath);
		
		if(inputStream ==null) {// 리소스를 찾지 못한 경우 (경로 문제)
			
			throw new IOException(propPath+" 파일을 를 Classpath에서 찾을 수 없습니다.");	
		
		}//end if
		
		// Properties 객체에 InputStream을 로드
        prop.load(inputStream);
		
		
		}catch(IOException e) {// 스트림 로드 중 예외 처리
			throw new IOException("properties 파일 로드 중 오류가 발생했습니다. -"+e.getMessage());	
		}//end catch
 
//		---------------------------------------------------------------
		//ㅁ File 클래스로 properties 경로를 가져옴.
		// 2. 로딩된 드라이버를 사용하여 커넥션 얻기 => Properties 도입.
		//Properties 파일 경로를 가져와서 File 연결.

//		//상대경로
//		File file = new File("src/properties/database.properties");
//		if(!file.exists()) {//파일이 존재하지 않는지 체크
//			throw new IOException("Properties가 지정된 위치가 없습니다.");
//		}//end if
//		
//		//Properties 객체 생성하여 FileStream 연결 후 값 가져오기.
//		Properties prop = new Properties();
//		prop.load(new FileInputStream(file));
//		
		String url = prop.getProperty("url");
		String id = prop.getProperty("id");
		String pass = prop.getProperty("pass");


		//Connection 객체 생성 후 Return
		Connection con = DriverManager.getConnection(url, id, pass);
		
		return con;
	}// getCon
	
	/**
	 * Connection 닫는 Method.
	 * @param con
	 * @param stmt
	 * @param rs
	 * @throws SQLException 
	 */
	public void dbClose(Connection con, Statement stmt, ResultSet rs) throws SQLException {
		try {
			if(rs != null) {rs.close();}
			if(stmt != null) {stmt.close();}	
		}finally { //다른 게 다 예외 나와도 커넥션 만큼은 반드시 끊는다.
			if(con != null) {con.close();}
		}//end finally
	}//dbclose
	
}// class
