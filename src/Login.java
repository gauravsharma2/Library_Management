// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login {
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
    public static void login() {

        JFrame f=new JFrame("Login");//creating instance of JFrame
        JLabel l1,l2;
        l1=new JLabel("Username");  //Create label Username
        l1.setBounds(30,20, 100,30); //x axis, y axis, width, height

        l2=new JLabel("Password");  //Create label Password
        l2.setBounds(30,70, 100,30);

        JTextField F_user = new JTextField(); //Create text field for username
        F_user.setBounds(130, 20, 200, 30);

        JPasswordField F_pass=new JPasswordField(); //Create text field for password
        F_pass.setBounds(130, 70, 200, 30);

        JButton login_but=new JButton("Login");//creating instance of JButton for Login Button
        login_but.setBounds(130,130,80,25);//Dimensions for button
        login_but.addActionListener(new ActionListener() {  //Perform action

            public void actionPerformed(ActionEvent e){

                String username = F_user.getText(); //Store username entered by the user in the variable "username"
                String password = F_pass.getText(); //Store password entered by the user in the variable "password"

                if(username.isEmpty()) //If username is null
                {
                    JOptionPane.showMessageDialog(null,"Please enter username"); //Display dialog box with the message
                }
                else if(password.isEmpty()) //If password is null
                {
                    JOptionPane.showMessageDialog(null,"Please enter password"); //Display dialog box with the message
                }
                else { //If both the fields are present then to login the user, check whether the user exists already
                    //System.out.println("Login connect");
                    try (Connection connection = connect()) {
                        try {
                            assert connection != null;
                            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            stmt.executeUpdate("USE LIBRARY"); //Use the database with the name "Library"
                            String st = ("SELECT * FROM USERS WHERE USERNAME='" + username + "' AND PASSWORD='" + password + "'"); //Retreive username and passwords from users
                            ResultSet rs = stmt.executeQuery(st); //Execute query
                            if (rs.next() == false) { //Move pointer below
                                System.out.print("No user");
                                JOptionPane.showMessageDialog(null, "Wrong Username/Password!"); //Display Message

                            } else {
                                f.dispose();
                                rs.beforeFirst();  //Move the pointer above
                                while (rs.next()) {
                                    String admin = rs.getString("ADMIN"); //user is admin
                                    //System.out.println(admin);
                                    String UID = rs.getString("USERNAME"); //Get user ID of the user
                                    if (admin.equals("1")) { //If boolean value 1
                                        AdminMenu.admin_menu(); //redirect to admin menu
                                    } else {
                                        UserMenu.user_menu(UID);//redirect to user menu for that user ID
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }  //Connect to the database
                    catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });


        f.add(F_pass); //add password
        f.add(login_but);//adding button in JFrame
        f.add(F_user);  //add user
        f.add(l1);  // add label1 i.e. for username
        f.add(l2); // add label2 i.e. for password

        f.setSize(400,300);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);

    }
}
