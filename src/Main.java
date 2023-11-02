// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class Main {

    public static class ex {
        public static int days = 0;
    }

    public static void main(String[] args) throws SQLException {
        Login.login();
        Create.create();
        ExcelToDatabaseLoader();
    }

    public static void ExcelToDatabaseLoader() throws SQLException {
        String csvFilePath = "/Users/gauravsharma/Desktop/DATABASE PROJECT/books.csv"; // Provide the path to your CSV file
        //String borrower = "/Users/gauravsharma/Desktop/DATABASE PROJECT/borrowers.csv"; // Provide the path to your CSV file
        String jdbcURL = "jdbc:mysql://localhost/LIBRARY"; // Update with your database URL
        String username = "root"; // Update with your database username
        String password = "950958Gaurav@"; // Update with your database password

        Connection connection=connect();
        Statement stmt = connection.createStatement();
        ResultSet resultSet = connection.getMetaData().getCatalogs();

        int batchSize = 20;
        connection = null;

        try {
            long start = System.currentTimeMillis();

            // Create a CSVReader with custom settings
            CSVParser csvParser = new CSVParserBuilder().withSeparator('\t').withIgnoreQuotations(true).build();
            CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .withSkipLines(1) // Skip the header row
                    .build();

            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            String sql = "INSERT INTO BOOK (ISBN, BTITLE) VALUES (?, ?)";
            PreparedStatement bookStatement = connection.prepareStatement(sql);

            String authorSql = "INSERT INTO AUTHOR (Name) VALUES (?)";
            PreparedStatement authorStatement = connection.prepareStatement(authorSql, Statement.RETURN_GENERATED_KEYS);

            String bookauthorSql = "INSERT INTO BOOK_AUTHORS (ISBN) VALUES (?)";
            PreparedStatement bookauthorStatement = connection.prepareStatement(bookauthorSql);

            int count = 0;

            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                String isbn = nextRecord[1]; // Assuming ISBN is in the second column (0-based index)
                String title = nextRecord[2]; // Assuming book title is in the third column (0-based index)
                String authors = nextRecord[3]; // Assuming authors are in the fourth column (0-based index)

                // Insert book
                boolean bookExists = checkIfBookExists(connection, isbn);
                if (!bookExists) {
                    bookStatement.setString(1, isbn);
                    bookStatement.setString(2, title);
                    bookStatement.addBatch();
                }

                        authorStatement.setString(1, authors);
                        authorStatement.addBatch();

                if (++count % batchSize == 0) {
                    bookStatement.executeBatch();
                    authorStatement.executeBatch();
                    ResultSet generatedKeys = authorStatement.getGeneratedKeys();
                    bookauthorStatement.executeBatch();
                    connection.commit();
                }
            }

            bookStatement.executeBatch();
            authorStatement.executeBatch();
            bookauthorStatement.executeBatch();

            connection.commit();
            connection.close();

            long end = System.currentTimeMillis();
            System.out.printf("Import done in %d ms\n", (end - start));
        } catch (IOException ex1) {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        } catch (SQLException ex2) {
            System.out.println("Database error");
            ex2.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
    }



    private static boolean checkIfBookExists(Connection connection, String isbn) throws SQLException {
        String query = "SELECT ISBN FROM BOOK WHERE ISBN = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, isbn);

        ResultSet resultSet = statement.executeQuery();
        return resultSet.next(); // Returns true if a record with the ISBN exists
    }

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
}


