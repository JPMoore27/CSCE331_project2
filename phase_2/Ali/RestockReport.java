import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/*
 * This class handles creates a report of all stock with amounts under a certain threshold
 *
 *@author JP
 *@param none
 *@returns none
 *@throws none 
 */
public class RestockReport extends JFrame {
    private JTextArea textArea;

    public RestockReport() {
        setTitle("Restock Report");
        setSize(500, 700);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));

        JScrollPane scrollPane = new JScrollPane(textArea);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplayData();
    }

    private void fetchAndDisplayData() {


        int threshold = Integer.parseInt(JOptionPane.showInputDialog("Under what threshold should items be restocked?"));

        try {

            // connect to db
            Connection connection = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_03g_db", 
                "csce331_903_jp_moore", "coll1n");

            // Do SQL statment 
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT stockname, amount FROM stock WHERE amount < " + threshold;
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            textArea.append("RESTOCK REPORT:\n");

            boolean noResults = true;
            while (resultSet.next()) {
                noResults = false;
                String stockname = resultSet.getString("stockname");
                int amount = resultSet.getInt("amount");
                textArea.append(stockname + ", " + amount + "\n");
            }

            if(noResults) {
                textArea.append("All items in proper stock\n");
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    * Asks for a threshold value then generates a restock report. Report is shown in a java swing window
    *
    *@author JP
    *@param none
    *@returns none
    *@throws SQLError if something is wrong with the database
    */
    public static void generateReport() {
        RestockReport restockReport = new RestockReport();
        restockReport.setVisible(true);
    }
}
