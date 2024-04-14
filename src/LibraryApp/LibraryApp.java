import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LibraryApp {

    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USERNAME = "MyLibraryApp";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Helyes használat: java LibraryApp <Kolcsonzesek.csv> <Kolcsonzok.csv>");
            return;
        }

        String kolcsonzesekFile = args[0];
        String kolcsonzokFile = args[1];

        if (!isValidFile(kolcsonzesekFile) || !isValidFile(kolcsonzokFile)) {
            System.out.println("A megadott fájlok nem találhatók vagy hibásak.");
            return;
        }

        importData(kolcsonzesekFile, "Kolcsonzesek");
        importData(kolcsonzokFile, "Kolcsonzok");
    }

    private static boolean isValidFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void importData(String fileName, String tableName) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 2 && tableName.equals("Kolcsonzok")) {
                    insertKolcsonzok(connection, data);
                } else if (data.length == 4 && tableName.equals("Kolcsonzesek")) {
                    insertKolcsonzesek(connection, data);
                } else {
                    System.out.println("Hibás adat: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertKolcsonzok(Connection connection, String[] data) throws SQLException {
        String query = "INSERT INTO Kolcsonzok (nev, szulIdo) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, data[0]);
            preparedStatement.setString(2, data[1]);
            preparedStatement.executeUpdate();
        }
    }

    private static void insertKolcsonzesek(Connection connection, String[] data) throws SQLException {
        String query = "INSERT INTO Kolcsonzesek (kolcsonzokId, iro, mufaj, cim) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, Integer.parseInt(data[0]));
            preparedStatement.setString(2, data[1]);
            preparedStatement.setString(3, data[2]);
            preparedStatement.setString(4, data[3]);
            preparedStatement.executeUpdate();
        }
    }
}

