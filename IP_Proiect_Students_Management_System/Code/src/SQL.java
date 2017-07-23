import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import Components.CustomJTable;

public class SQL implements Subject {
   private String JDBC_DRIVER;  
   private String DB_URL;
   private String USER;
   private String PASS;
   private Connection conn;
   private Statement stmt;
   private ResultSet rs;
   private ArrayList<String> allColumnsType;
   private ArrayList<Observer> observers;

   public SQL(Window window) throws ClassNotFoundException, SQLException {
	   observers = new ArrayList<> ();
	   addObserver(window);
	   JDBC_DRIVER = "com.mysql.jdbc.Driver";
	   DB_URL = "jdbc:mysql://cristi.xyz:10024/ip_usr?useSSL=false";
	   USER = "ip_usr";
	   PASS = "asDWUO29odmLS$s*(@";
   }

   public void createConnection() {
	   try {
		   Class.forName(JDBC_DRIVER);
	   } catch (ClassNotFoundException e) {
		   System.out.println(e);
	   }

	   try {
		   conn = DriverManager.getConnection(DB_URL, USER, PASS);
	   } catch (SQLException e) {
		   notifyAllObservers(Main.NO_INTERNET);
	   }
   }

   /*
    * Obtine intr-o lista tipul tuturor coloanelor din tabel.
    * ATENTIE : in lista vor fi adaugate si tipul coloanelor "invizibile"
    */
   public void findAllColumnsType(String tableName) {
	   allColumnsType = new ArrayList<> ();

	   try {
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE 1 = 2");
		   ResultSetMetaData metaData = rs.getMetaData();
		   int columnCount = metaData.getColumnCount();
		   
		   for (int column = 1; column <= columnCount; column++)
			   allColumnsType.add(metaData.getColumnTypeName(column));

	   } catch (SQLException e) {
		   System.out.println(e);
	   }
   }

   private DefaultTableModel buildTableModel(ResultSet rs) {
	    Vector<String> columnNames = null;
	    Vector<Vector<Object>> data = null;

		try {
			ResultSetMetaData metaData = rs.getMetaData();

		    // names of columns
		    columnNames = new Vector<String>();
		    int columnCount = metaData.getColumnCount();
		    
		    for (int column = 1; column <= columnCount; column++)
		        columnNames.add(metaData.getColumnLabel(column));
	
		    // data of the table
		    data = new Vector<Vector<Object>>();
		    while (rs.next()) {
		        Vector<Object> vector = new Vector<Object>();
		        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++)
		            vector.add(rs.getObject(columnIndex));
		        data.add(vector);
		    }
		} catch (SQLException e) {
			System.out.println(e);
		}
		
	    return new DefaultTableModel(data, columnNames);
	}
   
   public CustomJTable retrieveTable(String tableName, String query) {
	   CustomJTable table = null;
	   findAllColumnsType(tableName);

	   try {
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery(query);
		   table = new CustomJTable(buildTableModel(rs));
		  
		   rs.close();
		   stmt.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	  
	   return table;
   }
   
   /*
    * Executa comanda primita ca parametru pe server.
    * Intoarce ID-ul intrarii adaugate (valoarea cheii primare) 
    * in cazul unei comenzi INSERT. Pentru alte queries intoarce -1.
    */
   public int executeQuery(String query) {
	   int insertedID = -1;
	   
	   try {
		   stmt = conn.createStatement();
		   stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
		   rs = stmt.getGeneratedKeys();
		   
		   if (rs.next())
			   insertedID = rs.getInt(1);
		   
		   rs.close();
		   stmt.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	   
	   return insertedID;
   }
   
   public Vector<Vector<Object>> selectEntries(Vector<String> columns, String tableName, String cond) {
	   Vector<Vector<Object>> entries = new Vector<> ();
	   Vector<Object> entryVals;
	   String query = "SELECT ";
	   
	   for (int i = 0; i < columns.size(); i++)
		   query += (i + 1 == columns.size()) ? columns.get(i) : columns.get(i) + ", "; 
	   query += " FROM " + tableName + " WHERE " + cond;

	   try {
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery(query);
		   
		   while (rs.next()) {
			   entryVals = new Vector<Object>();
		  	   
			   for (int i = 1; i <= columns.size(); i++)
				   entryVals.add(rs.getObject(i));
			   
			   entries.add(entryVals);
		   }
		   
		   stmt.close();
	   } catch (SQLException e) {
		   System.out.println(e);
	   }
	   
	   return entries;
   }

   /*
    * Pornind de la campurile unui rand de tabela, formeaza
    * comanda de inserare a acestuia in tabela serverului.
    * Intoarce ID-ul intrarii adaugate (valoarea cheii primare) 
    * in cazul unei comenzi INSERT. Pentru alte queries intoarce -1.
    */
   public int insertEntry(String tableName, Vector<Object> row) {
	   String query = "INSERT INTO " + tableName + " VALUES (";

	   for (int i = 0; i < row.size(); i++) {
		   if (row.get(i) == null)
			   query += "null";
		   else if (row.get(i).toString().equals("DEFAULT"))
			   query += "DEFAULT";
		   else if (allColumnsType.get(i).equals("INT"))
			   query += row.get(i);
		   else
			   query += "'" + row.get(i) + "'";
		   
		   query += (i + 1 == row.size()) ? ')' : ", "; 
	   }

	   return executeQuery(query);
   }
   
   public void deleteEntry(String tableName, String cond) {
	   String query = "DELETE FROM " + tableName;
	   query += " WHERE " + cond;
	   executeQuery(query);
   }
   
   public void updateEntry(String tableName, Vector<Object> row, String cond) {
	   deleteEntry(tableName, cond);
	   insertEntry(tableName, row);
   }

   /*
    * Intoarce continutul unei singure celule sau null
    * daca nu face match pe niciuna.
    */
   public Object getOneValue(String tableName, String columnName, String condition) {
		Vector<Vector<Object>> res = null;
		Vector<String> cols = new Vector<> ();
		Object cellValue;
		cols.add(columnName);

		res = selectEntries(cols, tableName, condition);
		if (res.isEmpty())
			return null;
		
		cellValue = res.get(0).get(0);
		if (cellValue == null)
			return null;
		
		return cellValue;
   }

   /**
    * Numara cate linii din tabela respecta conditia si intoarce acest rezultat.
    * @param tableName
    * @param condition
    * @return
    */
   public int executeCountQuery(String tableName, String condition) {
	   String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + condition;
	   int countResult = -1;

	   try {
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery(query);
		   
		   if (rs.next())
			   countResult = rs.getInt(1);
		   
		   rs.close();
		   stmt.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	   
	   return countResult;
   }

   public void destroyConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
   }

	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void notifyAllObservers(String nextPanelName) {
		for (Observer o : observers)
			o.update(nextPanelName);
	}

	@Override
	public void notifyAllObservers(String nextPanelName, ArrayList<String> args) {
	}
}