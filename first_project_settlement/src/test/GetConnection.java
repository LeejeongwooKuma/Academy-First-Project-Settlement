package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class GetConnection {
	private static GetConnection getCon;

	private GetConnection() {

	}// GetConnection

	public static GetConnection getInsance() {
		if (getCon == null) {
			getCon = new GetConnection();
		} // end if
		return getCon;
	}// getInsance

	public Connection getConn() throws SQLException, IOException{

		// 1.드라이버로딩
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} // end catch

		// 2.로딩된 드라이버를 사용하여 커넥션 얻기 => Properties를 도입
		
//		File file = new File ("C:\\dev\\workspace\\project1\\src\\properties\\database.properties");
		File file = new File ("C:\\dev\\workspace\\project1\\src\\properties\\database.properties");
		if( !file.exists() ) { 
			throw new IOException("properties가 지정된 위치에 존재하지 않습니다.");
		}//end if
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		
		
		String url = prop.getProperty("url");
		String id = prop.getProperty("id");
		String pass = prop.getProperty("pass");
//		PreparedStatement pstmt = null;

		Connection con = DriverManager.getConnection(url, id, pass);
		con.setAutoCommit(true);
		return con;
		
	}//getConn
	
	public void dbClose(Connection con, Statement stmt, ResultSet rs) throws SQLException {
		try {
		if (rs != null) { rs.close(); }
		if (stmt != null) { stmt.close(); }
		}finally {
			if( con != null) { con.close(); }
		} //end finally
		
	}//dbClose
	
}//class