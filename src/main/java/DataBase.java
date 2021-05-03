import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class DataBase {
    private String url = "jdbc:sqlite:";

    public DataBase(String fileName) {
        url += fileName;
        createDataBase();
    }


    public void createDataBase() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "\nid INTEGER AUTO_INCREMENT," +
                    "\nnumber TEXT," +
                    "\npin TEXT," +
                    "\nbalance INTEGER DEFAULT 0);"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewCardIntoDatabase(String cardNumber, String pin) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String query = String.format("INSERT INTO card (number, pin) VALUES ('%s','%s')", cardNumber, pin);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCorrectPin(String cardNumber, String pin) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String query = String.format("SELECT pin FROM card WHERE number = %s", cardNumber);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            return rs.getString("pin").equals(pin);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getBalance(String cardNumber) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String query = String.format("SELECT balance FROM card WHERE number = %s", cardNumber);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            return rs.getInt("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isCardExist(String cardNumber) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String query = String.format("SELECT number FROM card WHERE number = %s", cardNumber);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ignored) {}

        return false;
    }

    public void addIncome(String cardNumber, int income) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String updateCard = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateCard)) {
            statement.setInt(1, income);
            statement.setString(2, cardNumber);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doTransfer(String sender, String receiver, int money) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String insertSender = String.format(
                "UPDATE card SET balance = balance - %d WHERE number = %s", money, sender);
        String insertReceiver = String.format(
                "UPDATE card SET balance = balance + %d WHERE number = %s", money, receiver);


        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(insertSender);
                statement.executeUpdate(insertReceiver);

                connection.commit();
                System.out.println("Success!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCard(String cardNumber) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String insert = String.format("DELETE FROM card WHERE number = %s", cardNumber);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
