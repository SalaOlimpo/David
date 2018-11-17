package service.magic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import service.utils.Constants;

/**
 * Class used to wrap MySQL call
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class MySqlImpl {
	
	private Connection	conn;
	private Statement	st;

	/**
	 * Instantiate SQL connection
	 * 
	 * @param database
	 */
	public MySqlImpl(String database) throws Exception {
		String myDriver = "org.gjt.mm.mysql.Driver";
		String myUrl = "jdbc:mysql://" + Constants.MySQL.HOST + "/" + database;
		Class.forName(myDriver);

		conn = DriverManager.getConnection(myUrl, Constants.MySQL.USER, Constants.MySQL.PASS);
		st = conn.createStatement();
	}
	/**
	 * Closes the database connection
	 */
	public void closeDB() throws Exception {
		st.close();
	}
	
/*
 * ====================================================================================================
 */

	/**
	 * Logs the alexa query, to verify speech to text output
	 * 
	 * @param text	alexa speech to text output
	 */
	public void logQuery(String text, int guestType) throws Exception {
		String query = "INSERT INTO query_log (query, guestType) VALUES ('" + text + "', " + guestType + ");";
		st.executeUpdate(query);
	}
	/**
	 * Logs the alexa query, to verify speech to text output
	 * 
	 * @param text	alexa speech to text output
	 */
	public void clearQuery() throws Exception {
		String	query = "DELETE FROM query_log WHERE 1=1;";
		st.executeUpdate(query);
				query = "ALTER TABLE query_log AUTO_INCREMENT = 1;";
		st.executeUpdate(query);
	}
	
/*
 * ====================================================================================================
 */

	/**
	 * Logs the credit action into rhea
	 * @param username
	 * @param credit
	 */
	public void setCreditAction(String username, double credit) throws SQLException {
		String query = "INSERT INTO charges (ID_A, amount, user) VALUES (-1, " + credit + ", '" + username + "');";
		st.executeUpdate(query);
	}
	/**
	 * Logs the enable / disable action into rhea
	 * @param username
	 * @param credit
	 */
	public void setEnableAction(String username, boolean enable) throws SQLException {
		String query = "INSERT INTO operations(type, ID_A, user) VALUES (" + (enable ? '0' : '1') + ", -1, '" + username + "');";
		st.executeUpdate(query);
	}
}
