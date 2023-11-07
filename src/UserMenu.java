// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class UserMenu {
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
    public static void user_menu(String UID) {

        JFrame f=new JFrame("User Functions"); //Give dialog box name as User functions
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit user menu on closing the dialog box
        JTextField searchField = new JTextField();
        searchField.setBounds(100, 20, 200, 25);
        f.add(searchField);

        // Create a JButton for searching
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(100, 60, 80, 25);
        f.add(searchButton);

        JButton importUsersButton = new JButton("Import Users");
        importUsersButton.setBounds(100, 100, 150, 25);
        f.add(importUsersButton);

        importUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String csvFilePath = "/Users/gauravsharma/Desktop/DATABASE PROJECT/borrowers.csv";

                try (Connection connection = connect()) {
                    connection.setAutoCommit(false); // Disable autocommit

                    String insertUserSql = "INSERT INTO LIBRARY.USERS (USERNAME, PASSWORD, ADMIN) VALUES (?, ?, 0)";

                    try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserSql)) {
                        CSVParser csvParser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(true).build();
                        CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFilePath))
                                .withCSVParser(csvParser)
                                .withSkipLines(1) // Skip the header line
                                .build();

                        String[] nextRecord;
                        while ((nextRecord = csvReader.readNext()) != null) {
                            String username = nextRecord[0]; // Assuming the username is in the first column
                            insertUserStatement.setString(1, username);
                            insertUserStatement.setString(2, username);
                            insertUserStatement.executeUpdate();
                        }
                    } catch (SQLException | IOException exception) {
                        throw new RuntimeException(exception);
                    }

                    connection.commit(); // Commit the transaction
                    connection.setAutoCommit(true); // Re-enable autocommit

                    JOptionPane.showMessageDialog(null, "Users imported successfully.");
                } catch (CsvValidationException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error importing users: " + ex.getMessage());
                }
            }
        });

            searchButton.addActionListener(new ActionListener() {
                Connection connection = connect();

                public void actionPerformed(ActionEvent e) {
                    String searchText = searchField.getText();

                    // Construct your SQL query to search for books by book name, ISBN, or author
                    String sql = "SELECT B.ISBN, B.BTITLE, " +
                            "GROUP_CONCAT(A.NAME SEPARATOR ', ') AS Authors, " +
                            "B.AVAILABILITY " +
                            "FROM LIBRARY.BOOK B " +
                            "LEFT JOIN LIBRARY.BOOK_AUTHORS BA ON B.ISBN = BA.ISBN " +
                            "LEFT JOIN LIBRARY.AUTHORS A ON BA.AuthorID = A.AuthorID " +
                            "WHERE B.BTITLE LIKE '%" + searchText + "%' " +
                            "OR B.ISBN LIKE '%" + searchText + "%' " +
                            "OR A.NAME LIKE '%" + searchText + "%' " +
                            "GROUP BY B.ISBN, B.BTITLE, B.AVAILABILITY";

                    try {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        JTable bookList = new JTable();
                        bookList.setModel(DbUtils.resultSetToTableModel(rs));

                        // Rename the table columns for better display
                        bookList.getColumnModel().getColumn(0).setHeaderValue("ISBN");
                        bookList.getColumnModel().getColumn(1).setHeaderValue("Book Title");
                        bookList.getColumnModel().getColumn(2).setHeaderValue("Book Author(s)");
                        bookList.getColumnModel().getColumn(3).setHeaderValue("Book Availability");

                        JScrollPane scrollPane = new JScrollPane(bookList);

                        JFrame resultFrame = new JFrame("Search Results for " + searchText);
                        resultFrame.add(scrollPane);
                        resultFrame.setSize(800, 400);
                        resultFrame.setVisible(true);
                        //resultFrame.setLocationRelativeTo null);
                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(null, e1);
                    }
                }
            });


            JButton my_book=new JButton("My Books");//creating instance of JButton
        my_book.setBounds(100,140,120,25);//x axis, y axis, width, height
        my_book.addActionListener(new ActionListener() { //Perform action
                                      public void actionPerformed(ActionEvent e){


                                          JFrame f = new JFrame("My Books"); //View books issued by user
                                          //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                          int UID_int = Integer.parseInt(UID); //Pass user ID

                                          //.iid,issued.uid,issued.ISBN,issued.issued_date,issued.return_date,issued,
                                          Connection connection = connect(); //connect to database
                                          //retrieve data
                                          String sql="select distinct issued.*,books.BTITLE,books.BAUTHOR,books.AVAILABILITY from issued,books " + "where ((issued.uid=" + UID_int + ") and (books.ISBN in (select ISBN from issued where issued.uid="+UID_int+"))) group by iid";
                                          String sql1 = "select ISBN from issued where uid="+UID_int;
                                          try {
                                              Statement stmt = connection.createStatement();
                                              //use database
                                              stmt.executeUpdate("USE LIBRARY");
                                              stmt=connection.createStatement();
                                              //store in array
                                              ArrayList books_list = new ArrayList();



                                              ResultSet rs=stmt.executeQuery(sql);
                                              JTable book_list= new JTable(); //store data in table format
                                              book_list.setModel(DbUtils.resultSetToTableModel(rs));
                                              //enable scroll bar
                                              JScrollPane scrollPane = new JScrollPane(book_list);

                                              f.add(scrollPane); //add scroll bar
                                              f.setSize(800, 400); //set dimensions of my books frame
                                              f.setVisible(true);
                                              f.setLocationRelativeTo(null);
                                          } catch (SQLException e1) {
                                              // TODO Auto-generated catch block
                                              JOptionPane.showMessageDialog(null, e1);
                                          }

                                      }
                                  }
        );

        f.add(my_book); //add my books
        f.setSize(700,700);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);
    }
}
