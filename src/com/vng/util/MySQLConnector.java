/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.util;
import com.vng.log.LogHelper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 *
 * @author thinhnn3
 */
public class MySQLConnector
{
	private static Connection connect;
	private String table_name = "";
	
	public MySQLConnector()
	{
		try
		{
			ConnectMySQLDB();
		}
		catch (Exception e)
		{
			LogHelper.LogException("Exception in connect MySQL database", e);
		}
	}
	
	private int ConnectMySQLDB() throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/sgmb_charge_log", "root", "0sJdQxwfpReUWmwRwNz0" );
		
		return CheckTableExist();
//		String query = "CREATE TABLE IF NOT EXISTS " + GetTableName() + "("
//				+ "idx int(5) NOT NULL AUTO_INCREMENT PRIMARY KEY,"
//				+ "trans_id char(32) NOT NULL,"
//				+ "date char(32) NOT NULL,"
//				+ "gross char(32) NOT NULL,"
//				+ "net char(32) NOT NULL,"
//				+ "operator char(32) NOT NULL,"
//				+ "type char(32) NOT NULL,"
//				+ "uid char(32) NOT NULL,"
//				+ "result char(32) NOT NULL,"
//				+ "game_coin char(32) NOT NULL,"
//				+ "promotion_coin char(32) NOT NULL,"
//				+ "coin_before char(32) NOT NULL,"
//				+ "promotion_coin_before char(32) NOT NULL,"
//				+ "coin_after char(32) NOT NULL,"
//				+ "promotion_coin_after char(32) NOT NULL)"
//				+ "ENGINE=MyISAM DEFAULT CHARSET=latin1";
//		Statement s = connect.createStatement();
//		int result = s.executeUpdate(query);
//		
//		return result;
		
	}
	
	public int InsertTransactionLog(String trans_id, String date, String gross, String net, String operator, String type, String uid, String result,
			String game_coin, String promotion_coin, String coin_before, String promotion_coin_before, String coin_after, String promotion_coin_after) throws Exception
	{
		// create table if not exist
		CheckTableExist();
		
		PreparedStatement preparedStatement = connect.prepareStatement("insert into " + GetTableName() + " values (default,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		preparedStatement.setString(1, trans_id);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, gross);
		preparedStatement.setString(4, net);
		preparedStatement.setString(5, operator);
		preparedStatement.setString(6, type);
		preparedStatement.setString(7, uid);
		preparedStatement.setString(8, result);
		preparedStatement.setString(9, game_coin);
		preparedStatement.setString(10, promotion_coin);
		preparedStatement.setString(11, coin_before);
		preparedStatement.setString(12, promotion_coin_before);
		preparedStatement.setString(13, coin_after);
		preparedStatement.setString(14, promotion_coin_after);
		int r = preparedStatement.executeUpdate();
		
		return r;
	}
	
	public boolean IsTransactionExist(String id)// throws Exception
	{
		try
		{
			String query = "SELECT count(*) FROM " + GetTableName() + " WHERE trans_id='" + id + "'";

			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			int count = 0;
			if (resultSet.next())
			{
				count = Integer.parseInt(resultSet.getString(1));
//				System.out.println("count = " + count);
			}

			return count > 0 ? true : false;
		}
		catch (Exception e)
		{
			LogHelper.LogException("IsTransactionExist", e);
			return true;
		}
	}
	
	private String GetTableName()
	{
		// new table each month
		String month = new SimpleDateFormat("MM_yyyy").format(Calendar.getInstance().getTime());
		table_name = "data" + "_" + month;
		
		return table_name;
	}
	
	private int CheckTableExist()
	{
		try
		{
			String query = "CREATE TABLE IF NOT EXISTS " + GetTableName() + "("
					+ "idx int(5) NOT NULL AUTO_INCREMENT PRIMARY KEY,"
					+ "trans_id char(32) NOT NULL,"
					+ "date char(32) NOT NULL,"
					+ "gross char(32) NOT NULL,"
					+ "net char(32) NOT NULL,"
					+ "operator char(32) NOT NULL,"
					+ "type char(32) NOT NULL,"
					+ "uid char(32) NOT NULL,"
					+ "result char(32) NOT NULL,"
					+ "game_coin char(32) NOT NULL,"
					+ "promotion_coin char(32) NOT NULL,"
					+ "coin_before char(32) NOT NULL,"
					+ "promotion_coin_before char(32) NOT NULL,"
					+ "coin_after char(32) NOT NULL,"
					+ "promotion_coin_after char(32) NOT NULL)"
					+ "ENGINE=MyISAM DEFAULT CHARSET=latin1";
			Statement s = connect.createStatement();
			int result = s.executeUpdate(query);

			return result;
		}
		catch (Exception e)
		{
			LogHelper.LogException("MySQLConnector.CheckTableExist", e);
			return -1;
		}
	}
}
