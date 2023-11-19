
import java.sql.*;

import static javax.management.remote.JMXConnectorFactory.connect;

public class Create {
    public static Connection connect()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("Loaded driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=root&password=950958Gaurav@");
            //System.out.println("Connected to MySQL");
            return con;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void create() {
        try {
            Connection connection=connect();
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            boolean databaseExists = false;
            //iterate each catalog in the ResultSet
            while (resultSet.next()) {
                // Get the database name, which is at position 1
                String databaseName = resultSet.getString(1);
                if(databaseName.equals("LIBRARY")) {
                    System.out.print("yes");
                    Statement stmt = connection.createStatement();
                   // Drop database if it pre-exists to reset the complete database
                    String sql = "DROP DATABASE LIBRARY";
                    stmt.executeUpdate(sql);
                    break;
                }
            }
            resultSet.close();
            Statement stmt = connection.createStatement();
            if (!databaseExists)
            {
                String sql = "CREATE DATABASE LIBRARY"; //Create Database
                stmt.executeUpdate(sql);
                stmt.executeUpdate("USE LIBRARY"); //Use Database
                //Create Users Table
                String sql1 = "CREATE TABLE USERS(UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(30), PASSWORD VARCHAR(30), ADMIN BOOLEAN)";
                stmt.executeUpdate(sql1);
                //Insert into users table
                stmt.executeUpdate("INSERT INTO USERS(USERNAME, PASSWORD, ADMIN) VALUES('admin','admin',TRUE)");
                stmt.executeUpdate("INSERT INTO USERS(USERNAME, PASSWORD, ADMIN) VALUES('GAURAV','GAURAV',FALSE)");
                //CREATE AUTHOR TABLE
                stmt.executeUpdate("CREATE TABLE AUTHORS(AuthorID INT AUTO_INCREMENT NOT NULL  PRIMARY KEY, NAME VARCHAR(255))");
                //CREATE BORROWER TABLE
                stmt.executeUpdate("CREATE TABLE BORROWER(CARDID VARCHAR(255) NOT NULL  PRIMARY KEY,SSN VARCHAR(255) NOT NULL UNIQUE, BNAME VARCHAR(255),ADDRESS VARCHAR(255),PHONE_NUMBER VARCHAR(255))");
                //Create Books table
                stmt.executeUpdate("CREATE TABLE BOOK(ISBN VARCHAR(255) NOT NULL  PRIMARY KEY, BTITLE VARCHAR(400),AVAILABILITY INT NOT NULL DEFAULT 2)");
                //CREATE BOOK_AUTHOR TABLE
                stmt.executeUpdate("CREATE TABLE BOOK_AUTHORS(AuthorID INT NOT NULL,ISBN VARCHAR(255) NOT NULL,PRIMARY KEY (ISBN, AuthorID),FOREIGN KEY (ISBN) REFERENCES BOOK(ISBN),FOREIGN KEY (AuthorID) REFERENCES AUTHORS(AuthorID))");
                //CREATE TABLE BOOK_LOANS
                stmt.executeUpdate("CREATE TABLE BOOK_LOANS(LOAN_ID INT AUTO_INCREMENT NOT NULL  PRIMARY KEY,ISBN VARCHAR(255),CARDID VARCHAR(255),RETURN_DATE VARCHAR(20),DUE_DATE VARCHAR(50),ISSUED_DATE VARCHAR(20),FOREIGN KEY (ISBN) REFERENCES BOOK(ISBN),FOREIGN KEY (CARDID) REFERENCES BORROWER(CARDID))");
                //CREATE TABLE FINES
                stmt.executeUpdate("CREATE TABLE FINES(LOAN_ID INT NOT NULL PRIMARY KEY, FINE_AMOUNT DECIMAL(10, 2), PAID BOOLEAN DEFAULT FALSE, FOREIGN KEY (LOAN_ID) REFERENCES BOOK_LOANS(LOAN_ID))");
                resultSet.close();
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
