
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
                    //System.out.print("yes");
                    //Statement stmt = connection.createStatement();
                    //Drop database if it pre-exists to reset the complete database
                    //String sql = "DROP DATABASE library";
                    //stmt.executeUpdate(sql);
                    databaseExists = true;
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
                //CREATE BOOK_AUTHOR TABLE
                stmt.executeUpdate("CREATE TABLE BOOK_AUTHOR(ISBN VARCHAR(255) NOT NULL  PRIMARY KEY, BAUTHOR VARCHAR(50))");
                //CREATE AUTHOR TABLE
                stmt.executeUpdate("CREATE TABLE AUTHOR(AuthorID INT NOT NULL  PRIMARY KEY, NAME VARCHAR(50))");
                //CREATE BORROWER TABLE
                stmt.executeUpdate("CREATE TABLE BORROWER(CARDID INT NOT NULL  PRIMARY KEY,SSN INT NOT NULL, BNAME VARCHAR(50),ADDRESS VARCHAR(50),PHONE_NUMBER INT)");
                //Create Books table
                stmt.executeUpdate("CREATE TABLE BOOK(ISBN VARCHAR(255) NOT NULL  PRIMARY KEY, BTITLE VARCHAR(50))");
                //CREATE TABLE BOOK_LOANS
                stmt.executeUpdate("CREATE TABLE BOOK_LOANS(LOAN_ID INT NOT NULL  PRIMARY KEY,ISBN VARCHAR(255),CARDID INT,RETURN_DATE VARCHAR(20),DUE_DATE VARCHAR(50),ISSUED_DATE VARCHAR(20))");
                //CREATE TABLE FINES
                stmt.executeUpdate("CREATE TABLE FINES(LOAN_ID INT NOT NULL  PRIMARY KEY,FINE_AMOUNT INT,PAID INT)");
                //Insert into books table
                // stmt.executeUpdate("INSERT INTO BOOKS(BTITLE, BAUTHOR, AVAILABILITY) VALUES ('War and Peace', 'Mystery', 200),  ('The Guest Book', 'Fiction', 300), ('The Perfect Murder','Mystery', 150), ('Accidental Presidents', 'Biography', 250), ('The Wicked King','Fiction', 350)");

                resultSet.close();
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}