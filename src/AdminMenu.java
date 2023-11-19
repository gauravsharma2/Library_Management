// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AdminMenu {
    public static Connection connect()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("Loaded driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=root&password=root");
            //System.out.println("Connected to MySQL");
            return con;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static void admin_menu() {

        String borrower = "/Users/gauravsharma/Desktop/DATABASE PROJECT/borrowers.csv"; // Provide the path to your CSV file
        String jdbcURL = "jdbc:mysql://localhost/LIBRARY"; // Update with your database URL
        String username = "root"; // Update with your database username
        String password = "root"; // Update with your database password
        Connection connection=connect();


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
                                                     "WHERE B.AVAILABILITY < 2 " +
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


        JButton add_borrower=new JButton("Add Borrower"); //creating instance of JButton to add borrower
        add_borrower.setBounds(100,250,120,25); //set dimensions for button

        add_borrower.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                JFrame g = new JFrame("Enter Borrower Details"); //Frame to enter user details
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //Create label
                JLabel l1,l2,l3;
                l1=new JLabel("Borower name");  //label 1 for username
                l1.setBounds(30,15, 150,30);


                l2=new JLabel("SSN");  //label 2 for password
                l2.setBounds(30,75, 150,30);

                l3=new JLabel("Address");  //label 2 for password
                l3.setBounds(30,125, 150,30);

                //set text field for username
                JTextField F_user = new JTextField();
                F_user.setBounds(150, 15, 200, 30);

                //set text field for username
                JTextField F_ssn = new JTextField();
                F_ssn.setBounds(150, 75, 200, 30);

                //set text field for username
                JTextField F_address = new JTextField();
                F_address.setBounds(150, 125, 500, 30);




                JButton create_but=new JButton("Create");//creating instance of JButton for Create
                create_but.setBounds(100,250,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String username = F_user.getText();
                        String ssn = F_ssn.getText();
                        String address = F_address.getText();

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            // Check if SSN already exists in the borrower table
                            PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM borrower WHERE SSN = ?");
                            checkStatement.setString(1, ssn);
                            ResultSet resultSet = checkStatement.executeQuery();

                            if (resultSet.next()) {
                                // SSN exists in the table, show message
                                JOptionPane.showMessageDialog(null, "SSN already exists in the borrower table.");
                            } else {
                                // SSN does not exist, generate unique card ID and insert into the table
                                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO borrower (Cardid, BName, SSN, Address) VALUES (?, ?, ?, ?)");

                                // Generate a unique card ID (you can implement your logic here)
                                String cardId = AdminMenu.generateUniqueCardId(connection);

                                insertStatement.setString(1, cardId);
                                insertStatement.setString(2, username);
                                insertStatement.setString(3, ssn);
                                insertStatement.setString(4, address);

                                int rowsAffected = insertStatement.executeUpdate();

                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(null, "Borrower added!");
                                    g.dispose();
                                }
                            }
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(null, e1);
                        }
                    }
                });

                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(l3);
                g.add(F_user);
                g.add(F_ssn);
                g.add(F_address);
                g.setSize(900,600);//600 width and 600 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);
            }

        });




        JButton add_book=new JButton("Add Book"); //creating instance of JButton for adding books
        add_book.setBounds(100,300,120,25);

        add_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //set frame wot enter book details
                JFrame g = new JFrame("Enter Book Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // set labels
                JLabel l1,l2,l3,l4;
                l1=new JLabel("ISBN");  //lebel 1 for book name
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("BOOK TITLE");  //label 2 for BAUTHOR
                l2.setBounds(30,53, 100,30);

                l3=new JLabel("AVAILABILITY");  //label 2 for AVAILABILITY
                l3.setBounds(30,90, 100,30);

                l4=new JLabel("AUTHOR NAME");  //label 2 for AVAILABILITY
                l4.setBounds(30,130, 100,30);

                //set text field for book name
                JTextField F_ISBN = new JTextField();
                F_ISBN.setBounds(110, 15, 200, 30);

                //set text field for BAUTHOR
                JTextField F_BTITLE=new JTextField();
                F_BTITLE.setBounds(110, 53, 200, 30);
                //set text field for AVAILABILITY
                JTextField F_AVAILABILITY=new JTextField();
                F_AVAILABILITY.setBounds(110, 90, 200, 30);

                JTextField F_BAUTHOR=new JTextField();
                F_BAUTHOR.setBounds(110, 130, 200, 30);


                JButton create_but=new JButton("Submit");//creating instance of JButton to submit details
                create_but.setBounds(130,130,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){
                        // assign the book name, BAUTHOR, AVAILABILITY
                        String BTITLE = F_BTITLE.getText();
                        String ISBN = F_ISBN.getText();
                        String AVAILABILITY = F_AVAILABILITY.getText();
                        String BAUTHOR = F_BAUTHOR.getText();
                        //convert AVAILABILITY of integer to int
                        int AVAILABILITY_int = Integer.parseInt(AVAILABILITY);

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO BOOK(BTITLE,ISBN,AVAILABILITY) VALUES ('"+BTITLE+"','"+ISBN+"',"+AVAILABILITY_int+")");
                            stmt.executeUpdate("INSERT INTO AUTHORS(NAME) VALUES ("+BAUTHOR+")");
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
                g.add(l4);
                g.add(l2);
                g.add(F_BTITLE);
                g.add(F_ISBN);
                g.add(F_BAUTHOR);
                g.add(F_AVAILABILITY);
                g.setSize(350,200);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);

            }
        });


        JButton issue_book=new JButton("Issue Book"); //creating instance of JButton to issue books
        issue_book.setBounds(100,350,120,25);

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

                JTextField F_ISBN = new JTextField();
                F_ISBN.setBounds(110, 15, 200, 30);


                JTextField F_uid=new JTextField();
                F_uid.setBounds(110, 53, 200, 30);




                JButton create_but=new JButton("Submit");//creating instance of JButton
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){

                        String uidStr = F_uid.getText();
                        String ISBN = F_ISBN.getText();
                        Connection connection = connect();
                        // Split the ISBN string by comma
                        String[] isbnArray = ISBN.split(",");
                        if(isbnArray.length>3)
                            JOptionPane.showMessageDialog(null, "More than 3 books cannot be selected", "Book Unavailable", JOptionPane.WARNING_MESSAGE);


                        // Create an array to store ISBNs with a maximum size of 3
                        String[] limitedIsbnArray = new String[3];
                        int availability;
                        Statement stmt = null;
                        try {
                            stmt = connection.createStatement();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                        // Copy at most 3 ISBNs to the limitedIsbnArray
                        for (int i = 0; i < Math.min(3, isbnArray.length); i++) {
                            limitedIsbnArray[i] = isbnArray[i].trim(); // Trim to remove leading/trailing spaces
                            System.out.println(limitedIsbnArray[i]);
                            ResultSet bookResultSet = null;
                            try {
                                try {
                                    stmt = connection.createStatement();
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                                try {
                                    stmt.executeUpdate("USE LIBRARY");
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }

                                //CHECK IF BOOK IS AVAILABLE
                                try {
                                    bookResultSet = stmt.executeQuery("SELECT AVAILABILITY FROM BOOK WHERE ISBN = '" + limitedIsbnArray[i] + "'");
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }

                                // Check if the ResultSet has any rows
                                try {
                                    if (bookResultSet.next()) {
                                        availability = bookResultSet.getInt("AVAILABILITY");

                                        if (availability == 0) {
                                            JOptionPane.showMessageDialog(null, limitedIsbnArray[i] + " Book Not Available", "Book Unavailable", JOptionPane.WARNING_MESSAGE);
                                        } else {
                                            try {
                                                PreparedStatement statement = null;
                                                ResultSet resultSet = null;
                                                boolean isOccupied = false;

                                                // Check if the ISBN is already present for the given CARD_ID in BOOK_LOANS
                                                String query = "SELECT * FROM BOOK_LOANS WHERE ISBN = ? AND CARDID = ?";
                                                statement = connection.prepareStatement(query);
                                                statement.setString(1, limitedIsbnArray[i]);
                                                statement.setString(2, uidStr);
                                                resultSet = statement.executeQuery();

                                                // If there are results, the book is already occupied
                                                if (resultSet.next()) {
                                                    isOccupied = true;
                                                }
                                                if (isOccupied) {
                                                    JOptionPane.showMessageDialog(null, "The book is already occupied.");
                                                    // Show pop-up or message dialog here with appropriate message
                                                } else {
                                                    String borrowerInsertQuery = "INSERT INTO BORROWER (CARDID, SSN, BNAME, ADDRESS, PHONE_NUMBER) VALUES (?, ?, ?, ?, ?)";
                                                    PreparedStatement borrowerStatement = null;
                                                    try {
                                                        borrowerStatement = connection.prepareStatement(borrowerInsertQuery);
                                                    } finally {
                                                    }

                                                    try (CSVReader csvReader = new CSVReader(new FileReader(borrower))) {
                                                        String[] headers = csvReader.readNext(); // Assuming the first row contains headers

                                                        String[] nextRecord;
                                                        boolean cardIdExists = false;

                                                        // Check if the CARDID already exists in the BORROWER table
                                                        PreparedStatement checkCardIdStatement = connection.prepareStatement("SELECT COUNT(*) FROM BORROWER WHERE CARDID = ?");
                                                        checkCardIdStatement.setString(1, uidStr);

                                                        ResultSet existingCardIdResult = checkCardIdStatement.executeQuery();
                                                        if (existingCardIdResult.next()) {
                                                            int count = existingCardIdResult.getInt(1);
                                                            cardIdExists = (count > 0);
                                                        }

                                                        if (!cardIdExists) {
                                                            // CARDID doesn't exist, proceed to insert into BORROWER table
                                                            while ((nextRecord = csvReader.readNext()) != null) {
                                                                String csvCardId = nextRecord[0];

                                                                // Check if the current row's cardId matches the required cardId
                                                                if (Objects.equals(csvCardId, uidStr)) {
                                                                    // Extract other details from the CSV row
                                                                    String ssn = nextRecord[1];
                                                                    String name = nextRecord[2] + " " + nextRecord[3]; // Concatenate first_name and last_name
                                                                    String address = nextRecord[5];
                                                                    String phoneNumber = nextRecord[8];

                                                                    borrowerStatement.setString(1, csvCardId);
                                                                    borrowerStatement.setString(2, ssn); // Sample SSN
                                                                    borrowerStatement.setString(3, name); // Sample Borrower Name
                                                                    borrowerStatement.setString(4, address); // Sample Address
                                                                    borrowerStatement.setString(5, phoneNumber); // Sample Phone Number

                                                                    borrowerStatement.executeUpdate();

                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    } catch (CsvValidationException | IOException | SQLException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                                    // Define a counter to keep track of the number of books borrowed by the user
                                                    int booksBorrowed = 0;
                                                    if (limitedIsbnArray[i] != null) {
                                                        try {
                                                            // Check the number of books borrowed by the user
                                                            stmt.executeUpdate("USE LIBRARY");
                                                            PreparedStatement countBooksStatement = connection.prepareStatement("SELECT COUNT(*) FROM BOOK_LOANS WHERE CARDID = ?");
                                                            countBooksStatement.setString(1, uidStr);
                                                            ResultSet countResult = countBooksStatement.executeQuery();

                                                            if (countResult.next()) {
                                                                booksBorrowed = countResult.getInt(1);
                                                            }

                                                            if (booksBorrowed >= 3) {
                                                                // Display a message if the user has already borrowed 3 books
                                                                JOptionPane.showMessageDialog(null, "The user already has 3 books borrowed.", "Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                                                            } else {
                                                                // Update availability to 0
                                                                try {
                                                                    stmt.executeUpdate("UPDATE BOOK SET AVAILABILITY = AVAILABILITY - 1 WHERE ISBN = '" + limitedIsbnArray[i] + "'");

                                                                } catch (SQLException ex) {
                                                                    throw new RuntimeException(ex);
                                                                }
                                                                // Add an entry to the LOAN table if the user has less than 3 books borrowed
                                                                PreparedStatement loanStatement = connection.prepareStatement("INSERT INTO BOOK_LOANS (ISBN, CARDID,DUE_DATE, ISSUED_DATE) VALUES (?, ?, ?, ?)");
                                                                loanStatement.setString(1, limitedIsbnArray[i]);
                                                                loanStatement.setString(2, uidStr);

                                                                // Get current date and 14 days later date
                                                                LocalDate currentDate = LocalDate.now();
                                                                LocalDate dueDate = currentDate.plusDays(14);

                                                                loanStatement.setDate(4, Date.valueOf(currentDate));
                                                                loanStatement.setDate(3, Date.valueOf(dueDate));

                                                                loanStatement.executeUpdate();
                                                            }
                                                        } catch (SQLException ex) {
                                                            throw new RuntimeException(ex);
                                                        }
                                                    } else {
                                                        // Handle the case where the ISBN is not found in the database
                                                        JOptionPane.showMessageDialog(null, limitedIsbnArray[i] + " ISBN Not Found", "ISBN Not Found", JOptionPane.WARNING_MESSAGE);
                                                    }

                                                }
                                            } catch (SQLException | RuntimeException ex) {
                                                throw new RuntimeException(ex);
                                            } finally {
                                                // Close the ResultSet and statement in a finally block
                                                try {
                                                    bookResultSet.close();
                                                } catch (SQLException Q) {
                                                    Q.printStackTrace();
                                                }
                                            }

                                        }
                                    }
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }

                            } catch (RuntimeException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                    }
                    });


                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(F_uid);
                g.add(F_ISBN);
                //g.add(F_issue);
                g.setSize(350,250);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);


            }
        });


        JButton return_book=new JButton("Return Book"); //creating instance of JButton to return books
        return_book.setBounds(100,400,160,25);

        return_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                JFrame g = new JFrame("Enter Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //set labels
                JLabel l1,l2,l3,l4;
                l1=new JLabel("ISBN)");  //Label 1 for Issue ID
                l1.setBounds(30,15, 100,30);


                l4=new JLabel("CARD NUMBER");
                l4.setBounds(30,50, 150,30);

                JTextField ISBN = new JTextField();
                ISBN.setBounds(110, 15, 200, 30);


                JTextField CARDID=new JTextField();
                CARDID.setBounds(180, 50, 130, 30);


                JButton create_but=new JButton("Return");//creating instance of JButton to mention return date and calculcate fine
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {

                        String isbn = ISBN.getText();
                        String cardid = CARDID.getText();

                        Connection connection = connect();
                        try {
                            // Get current date
                            LocalDate currentDate = LocalDate.now();

                            // Update the tuple in BOOK_LOANS based on ISBN for RETURN_DATE
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            PreparedStatement updateStatement = connection.prepareStatement("UPDATE BOOK_LOANS SET RETURN_DATE = ? WHERE ISBN = '"+ isbn +"' OR CARDID = '"+cardid+"'");
                            updateStatement.setDate(1, Date.valueOf(currentDate));
                            // Execute the update
                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("Successfully updated return date for the tuple.");
                            } else {
                                System.out.println("Tuple not found or no changes made.");
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                });
                g.add(l4);
                g.add(create_but);
                g.add(l1);
                g.add(ISBN);
                g.add(CARDID);
                g.setSize(350,250);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);
            }
        });

       JButton check_fine=new JButton("CHECK FINE"); //creating instance of JButton to return books
        check_fine.setBounds(100,450,160,25);

        check_fine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                JFrame g = new JFrame("Enter ID");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //set labels
                JLabel l1;
                l1=new JLabel("ID)");  //Label 1 for Issue ID
                l1.setBounds(30,15, 100,30);


                JTextField ID=new JTextField();
                ID.setBounds(110, 15, 130, 30);


                JButton create_but=new JButton("CHECK");//creating instance of JButton to mention return date and calculcate fine
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {

                        String id = ID.getText();
                        PreparedStatement selectStatement = null;
                        PreparedStatement updateStatement = null;
                        ResultSet resultSet = null;

                        Connection connection = connect();
                        try {

                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            // SQL query to select relevant book loans
                            String selectQuery = "SELECT LOAN_ID, DUE_DATE, RETURN_DATE FROM BOOK_LOANS";
                            selectStatement = connection.prepareStatement(selectQuery);
                            resultSet = selectStatement.executeQuery();
                            LocalDate dueDate;

                            while (resultSet.next()) {
                                int loanID = resultSet.getInt("LOAN_ID");
//                                if(loanID==1)
//                                    dueDate = LocalDate.of(2023, 11, 10);
//                                else {
                                    dueDate = resultSet.getDate("DUE_DATE").toLocalDate();
                              //  }
                                LocalDate returnDate = resultSet.getDate("RETURN_DATE") != null ?
                                        resultSet.getDate("RETURN_DATE").toLocalDate() : LocalDate.now();
                                System.out.println(dueDate+"  "+returnDate);

                                long daysDifference = ChronoUnit.DAYS.between(dueDate, returnDate);
                                double fineAmount = daysDifference * 0.25;

                                if (fineAmount < 0) {
                                    fineAmount = 0; // Ensure fine is not negative
                                }

                                // Update fines table with calculated fine amount
                                String updateQuery = "INSERT INTO FINES (LOAN_ID, FINE_AMOUNT, PAID) VALUES (?, ?, ?) " +
                                        "ON DUPLICATE KEY UPDATE FINE_AMOUNT = VALUES(FINE_AMOUNT)";
                                updateStatement = connection.prepareStatement(updateQuery);
                                updateStatement.setInt(1, loanID);
                                updateStatement.setDouble(2, fineAmount);
                                updateStatement.setBoolean(3, false); // Assuming initially fines are not paid

                                int rowsAffected = updateStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    System.out.println("LoanID: " + loanID + ", Fine Amount: $" + fineAmount);
                                    // Here you can perform additional actions if needed
                                }
                            }
                            String loanIDQuery = "SELECT BL.LOAN_ID " +
                                    "FROM BOOK_LOANS BL " +
                                    "JOIN BORROWER B ON BL.CARDID = B.CARDID " +
                                    "WHERE B.CARDID = ?";
                            PreparedStatement loanIDStatement = connection.prepareStatement(loanIDQuery);
                            loanIDStatement.setString(1, id);
                            resultSet = loanIDStatement.executeQuery();

                            while (resultSet.next()) {
                                String loanID = resultSet.getString("LOAN_ID");

                                // Prepare the update query to set PAID to TRUE for the found LOAN_ID
                                String updateQuery = "UPDATE FINES SET PAID = TRUE WHERE LOAN_ID = ?";
                                updateStatement = connection.prepareStatement(updateQuery);
                                updateStatement.setString(1, loanID);

                                // Execute the update
                                int rowsAffected = updateStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    System.out.println("PAID status updated for Loan ID: " + loanID);
                                    // Additional actions after successful update
                                } else {
                                    System.out.println("No records updated for Loan ID: " + loanID);
                                    // Handle if no records were updated
                                }
                            }
                        } catch (SQLException q) {
                            q.printStackTrace();
                        } finally {
                            // Close resources in finally block
                            try {
                                if (resultSet != null) resultSet.close();
                                if (selectStatement != null) selectStatement.close();
                                if (updateStatement != null) updateStatement.close();
                                if (connection != null) connection.close();
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }

                        }
                        }
                });
                g.add(create_but);
                g.add(l1);
                g.add(ID);
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
        f.add(add_borrower);
        f.add(check_fine);
        f.setSize(1000,600);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);

    }
    public static String generateUniqueCardId(Connection connection) throws SQLException {
        String uniqueCardId = "";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("USE LIBRARY");

        // Iterate to find a unique ID
        int idCounter = 1;
        boolean uniqueIdFound = false;
        while (!uniqueIdFound) {
            uniqueCardId = String.format("ID%06d", idCounter);

            // Check if the ID already exists in the borrower table
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM borrower WHERE Cardid = '" + uniqueCardId + "'");
            if (!resultSet.next()) {
                uniqueIdFound = true; // ID is unique
            } else {
                idCounter++; // Increment counter for the next iteration
            }
        }

        return uniqueCardId;
    }
}
