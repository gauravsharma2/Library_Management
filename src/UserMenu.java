// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
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
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit user menu on closing the dialog box
        JTextField searchField = new JTextField();
        searchField.setBounds(300, 20, 200, 25);
        f.add(searchField);

        // Create a JButton for searching
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(520, 20, 80, 25);
        f.add(searchButton);

        searchButton.addActionListener(new ActionListener() {
            Connection connection = connect();
            public void actionPerformed(ActionEvent e) {
                String bookName = searchField.getText(); // Get the book name from the text field

                JFrame f = new JFrame("Search Results for " + bookName);
                // ... (rest of your code for creating a new JFrame and database connection)

                // Construct your SQL query to search for books by name
                String sql = "SELECT * FROM BOOKS WHERE BTITLE LIKE '%" + bookName + "%'";
                try {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    JTable bookList = new JTable();
                    bookList.setModel(DbUtils.resultSetToTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(bookList);

                    f.add(scrollPane);
                    f.setSize(800, 400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null, e1);
                }
            }
        });

        JButton my_book=new JButton("My Books");//creating instance of JButton
        my_book.setBounds(150,20,120,25);//x axis, y axis, width, height
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
        f.setSize(300,100);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);
    }
}
