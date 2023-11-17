// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your
import com.opencsv.exceptions.CsvValidationException;
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import com.opencsv.*;

public class AdminMenu {
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
    public static void admin_menu() {


        JFrame f = new JFrame("Admin Functions");
        //f.setLayout(null); // Use no layout manager
        f.setSize(1000, 1000);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //


        JButton create_but=new JButton("Create/Reset Database");//creating instance of JButton to create or reset database
        create_but.setBounds(500,20,120,25);//x axis, y axis, width, height
        create_but.addActionListener(new ActionListener() { //Perform action
            public void actionPerformed(ActionEvent e){

                Create.connect(); //Call create function
                JOptionPane.showMessageDialog(null,"Database Created/Reset!"); //Open a dialog box and display the message

            }
        });

        JTextField searchField = new JTextField();
        searchField.setBounds(100, 20, 200, 25);
        f.add(searchField);


        JButton view_but=new JButton("View Books");//creating instance of JButton to view books
        view_but.setBounds(100,50,120,25);//x axis, y axis, width, height
        view_but.addActionListener(new ActionListener() {
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

        JButton users_but=new JButton("View Users");//creating instance of JButton to view users
        users_but.setBounds(100,100,120,25);//x axis, y axis, width, height
        users_but.addActionListener(new ActionListener() { //Perform action on click button
                                        public void actionPerformed(ActionEvent e){

                                            JFrame f = new JFrame("Users List");
                                            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                                            Connection connection = connect();
                                            String sql="select * from users"; //retrieve all users
                                            try {
                                                Statement stmt = connection.createStatement();
                                                stmt.executeUpdate("USE LIBRARY"); //use database
                                                stmt=connection.createStatement();
                                                ResultSet rs=stmt.executeQuery(sql);
                                                JTable book_list= new JTable();
                                                book_list.setModel(DbUtils.resultSetToTableModel(rs));
                                                //mention scroll bar
                                                JScrollPane scrollPane = new JScrollPane(book_list);

                                                f.add(scrollPane); //add scrollpane
                                                f.setSize(800, 400); //set size for frame
                                                f.setVisible(true);
                                                f.setLocationRelativeTo(null);
                                            } catch (SQLException e1) {
                                                // TODO Auto-generated catch block
                                                JOptionPane.showMessageDialog(null, e1);
                                            }


                                        }
                                    }
        );

        JButton issued_but=new JButton("View Issued Books");//creating instance of JButton to view the issued books
        issued_but.setBounds(100,150,160,25);//x axis, y axis, width, height
        issued_but.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e){

                                             JFrame f = new JFrame("Users List");
                                             //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                                             Connection connection = connect();
                                             String sql = "SELECT B.ISBN, B.BTITLE, " +
                                                     "GROUP_CONCAT(A.NAME SEPARATOR ', ') AS Authors, " +
                                                     "B.AVAILABILITY " +
                                                     "FROM LIBRARY.BOOK B " +
                                                     "LEFT JOIN LIBRARY.BOOK_AUTHORS BA ON B.ISBN = BA.ISBN " +
                                                     "LEFT JOIN LIBRARY.AUTHORS A ON BA.AuthorID = A.AuthorID " +
                                                     "WHERE B.AVAILABILITY = 0 " +
                                                     "GROUP BY B.ISBN, B.BTITLE, B.AVAILABILITY";
                                             try {
                                                 Statement stmt = connection.createStatement();
                                                 stmt.executeUpdate("USE LIBRARY");
                                                 stmt=connection.createStatement();
                                                 ResultSet rs=stmt.executeQuery(sql);
                                                 JTable book_list= new JTable();
                                                 book_list.setModel(DbUtils.resultSetToTableModel(rs));

                                                 JScrollPane scrollPane = new JScrollPane(book_list);

                                                 f.add(scrollPane);
                                                 f.setSize(800, 400);
                                                 f.setVisible(true);
                                                 f.setLocationRelativeTo(null);
                                             } catch (SQLException e1) {
                                                 // TODO Auto-generated catch block
                                                 JOptionPane.showMessageDialog(null, e1);
                                             }

                                         }
                                     }
        );


        JButton add_user=new JButton("Add User"); //creating instance of JButton to add users
        add_user.setBounds(100,200,120,25); //set dimensions for button

        add_user.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                JFrame g = new JFrame("Enter User Details"); //Frame to enter user details
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //Create label
                JLabel l1,l2;
                l1=new JLabel("Username");  //label 1 for username
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("Password");  //label 2 for password
                l2.setBounds(30,50, 100,30);

                //set text field for username
                JTextField F_user = new JTextField();
                F_user.setBounds(110, 15, 200, 30);

                //set text field for password
                JPasswordField F_pass=new JPasswordField();
                F_pass.setBounds(110, 50, 200, 30);
                //set radio button for admin
                JRadioButton a1 = new JRadioButton("Admin");
                a1.setBounds(55, 80, 200,30);
                //set radio button for user
                JRadioButton a2 = new JRadioButton("User");
                a2.setBounds(130, 80, 200,30);
                //add radio buttons
                ButtonGroup bg=new ButtonGroup();
                bg.add(a1);bg.add(a2);


                JButton create_but=new JButton("Create");//creating instance of JButton for Create
                create_but.setBounds(100,250,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){

                        String username = F_user.getText();
                        String password = F_pass.getText();
                        Boolean admin = false;

                        if(a1.isSelected()) {
                            admin=true;
                        }

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('"+username+"','"+password+"',"+admin+")");
                            JOptionPane.showMessageDialog(null,"User added!");
                            g.dispose();

                        }

                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });


                g.add(create_but);
                g.add(a2);
                g.add(a1);
                g.add(l1);
                g.add(l2);
                g.add(F_user);
                g.add(F_pass);
                g.setSize(350,200);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);


            }
        });


        JButton add_book=new JButton("Add Book"); //creating instance of JButton for adding books
        add_book.setBounds(100,250,120,25);

        add_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //set frame wot enter book details
                JFrame g = new JFrame("Enter Book Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // set labels
                JLabel l1,l2,l3;
                l1=new JLabel("Book Name");  //lebel 1 for book name
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("BAUTHOR");  //label 2 for BAUTHOR
                l2.setBounds(30,53, 100,30);

                l3=new JLabel("AVAILABILITY");  //label 2 for AVAILABILITY
                l3.setBounds(30,90, 100,30);

                //set text field for book name
                JTextField F_BTITLE = new JTextField();
                F_BTITLE.setBounds(110, 15, 200, 30);

                //set text field for BAUTHOR
                JTextField F_BAUTHOR=new JTextField();
                F_BAUTHOR.setBounds(110, 53, 200, 30);
                //set text field for AVAILABILITY
                JTextField F_AVAILABILITY=new JTextField();
                F_AVAILABILITY.setBounds(110, 90, 200, 30);


                JButton create_but=new JButton("Submit");//creating instance of JButton to submit details
                create_but.setBounds(130,130,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){
                        // assign the book name, BAUTHOR, AVAILABILITY
                        String BTITLE = F_BTITLE.getText();
                        String BAUTHOR = F_BAUTHOR.getText();
                        String AVAILABILITY = F_AVAILABILITY.getText();
                        //convert AVAILABILITY of integer to int
                        int AVAILABILITY_int = Integer.parseInt(AVAILABILITY);

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO BOOKS(BTITLE,BAUTHOR,AVAILABILITY) VALUES ('"+BTITLE+"','"+BAUTHOR+"',"+AVAILABILITY_int+")");
                            JOptionPane.showMessageDialog(null,"Book added!");
                            g.dispose();

                        }

                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });

                g.add(l3);
                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(F_BTITLE);
                g.add(F_BAUTHOR);
                g.add(F_AVAILABILITY);
                g.setSize(350,200);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);

            }
        });


        JButton issue_book=new JButton("Issue Book"); //creating instance of JButton to issue books
        issue_book.setBounds(100,300,120,25);

        issue_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //enter details
                JFrame g = new JFrame("Enter Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //create labels
                JLabel l1,l2,l3,l4;
                l1=new JLabel("Book ID(ISBN)");  // Label 1 for Book ID
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("User ID(UID)");  //Label 2 for user ID
                l2.setBounds(30,53, 100,30);

                l3=new JLabel("Period(days)");  //Label 3 for period
                l3.setBounds(30,90, 100,30);

                l4=new JLabel("Issued Date(DD-MM-YYYY)");  //Label 4 for issue date
                l4.setBounds(30,127, 150,30);

                JTextField F_ISBN = new JTextField();
                F_ISBN.setBounds(110, 15, 200, 30);


                JTextField F_uid=new JTextField();
                F_uid.setBounds(110, 53, 200, 30);

                JTextField F_period=new JTextField();
                F_period.setBounds(110, 90, 200, 30);

                //JTextField F_issue=new JTextField();
                //F_issue.setBounds(180, 130, 130, 30);


                JButton create_but=new JButton("Submit");//creating instance of JButton
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){

                        String uid = F_uid.getText();
                        String ISBN = F_ISBN.getText();
                        String period = F_period.getText();

                        Calendar calendar = Calendar.getInstance();
                        Date todayDate = (Date) calendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String todayDateString = dateFormat.format(todayDate);

                        JTextField F_issue = new JTextField(todayDateString);
                        F_issue.setBounds(180, 130, 130, 30);

                        int period_int = Integer.parseInt(period);

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO ISSUED(UID,ISBN,ISSUED_DATE,PERIOD) VALUES ('"+uid+"','"+ISBN+"','"+todayDateString+"',"+period_int+")");
                            JOptionPane.showMessageDialog(null,"Book Issued!");
                            g.dispose();

                        }

                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });


                g.add(l3);
                g.add(l4);
                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(F_uid);
                g.add(F_ISBN);
                g.add(F_period);
                //g.add(F_issue);
                g.setSize(350,250);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);


            }
        });


        JButton return_book=new JButton("Return Book"); //creating instance of JButton to return books
        return_book.setBounds(100,350,160,25);

        return_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                JFrame g = new JFrame("Enter Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //set labels
                JLabel l1,l2,l3,l4;
                l1=new JLabel("Issue ID(IID)");  //Label 1 for Issue ID
                l1.setBounds(30,15, 100,30);


                l4=new JLabel("Return Date(DD-MM-YYYY)");
                l4.setBounds(30,50, 150,30);

                JTextField F_iid = new JTextField();
                F_iid.setBounds(110, 15, 200, 30);


                JTextField F_return=new JTextField();
                F_return.setBounds(180, 50, 130, 30);


                JButton create_but=new JButton("Return");//creating instance of JButton to mention return date and calculcate fine
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){

                        String iid = F_iid.getText();
                        String return_date = F_return.getText();

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            //Intialize date1 with NULL value
                            String date1=null;
                            String date2=return_date; //Intialize date2 with return date

                            //select issue date
                            ResultSet rs = stmt.executeQuery("SELECT ISSUED_DATE FROM ISSUED WHERE IID="+iid);
                            while (rs.next()) {
                                date1 = rs.getString(1);

                            }

                            try {
                                Date date_1= (Date) new SimpleDateFormat("dd-MM-yyyy").parse(date1);
                                Date date_2= (Date) new SimpleDateFormat("dd-MM-yyyy").parse(date2);
                                //subtract the dates and store in diff
                                long diff = date_2.getTime() - date_1.getTime();
                                //Convert diff from milliseconds to days
                                Main.ex.days=(int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));


                            } catch (ParseException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }


                            //update return date
                            stmt.executeUpdate("UPDATE ISSUED SET RETURN_DATE='"+return_date+"' WHERE IID="+iid);
                            g.dispose();


                            Connection connection1 = connect();
                            Statement stmt1 = connection1.createStatement();
                            stmt1.executeUpdate("USE LIBRARY");
                            ResultSet rs1 = stmt1.executeQuery("SELECT PERIOD FROM ISSUED WHERE IID="+iid); //set period
                            String diff=null;
                            while (rs1.next()) {
                                diff = rs1.getString(1);

                            }
                            int diff_int = Integer.parseInt(diff);
                            if (Main.ex.days > diff_int)  { //If number of days are more than the period then calculcate fine

                                //System.out.println(ex.days);
                                int fine = (Main.ex.days-diff_int)*10; //fine for every day after the period is Rs 10.
                                //update fine in the system
                                stmt1.executeUpdate("UPDATE ISSUED SET FINE="+fine+" WHERE IID="+iid);
                                String fine_str = ("Fine: Rs. "+fine);
                                JOptionPane.showMessageDialog(null,fine_str);

                            }

                            JOptionPane.showMessageDialog(null,"Book Returned!");

                        }


                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });
                g.add(l4);
                g.add(create_but);
                g.add(l1);
                g.add(F_iid);
                g.add(F_return);
                g.setSize(350,250);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);
            }
        });

        f.add(create_but);
        f.add(return_book);
        f.add(issue_book);
        f.add(add_book);
        f.add(issued_but);
        f.add(users_but);
        f.add(view_but);
        f.add(add_user);
        f.setSize(600,200);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);

    }
}
