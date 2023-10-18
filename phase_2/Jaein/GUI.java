import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.*;
import java.util.*;
import java.time.Instant;

public class salesReportGUI extends JFrame implements ActionListener{
    private static JFrame f = new JFrame();
    Connection conn = null;
    public static JButton salesReportButton;
    private static Timestamp start_time;
    private static Timestamp end_time;
    private static JLabel startInput = new JLabel("Please press the button and enter time window");
    private static JLabel endInput = new JLabel();
    private ResultSet result;
    private String rows[] = {"1", "2", "3"};
    private JList rowList = new JList(rows);
    // private JTable table = new JTable();

    public salesReportGUI() {
        // Constructor to initialize the GUI and establish a database connection
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_03g_db",
                    "csce331_903_jp_moore",
                    "coll1n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null, "Database connection established");

        //Make a button to activate sales report
        JButton button = new JButton("Sales Report");
        button.addActionListener(this);

        //Make a panel for sales report button
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(600, 600, 200, 600));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(button);
        panel.add(startInput);
        panel.add(endInput);
        panel.add(rowList);
        // panel.add(table);

        //Set up the frame and display
        f.add(panel, BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Sales Report");
        f.pack();
        f.setVisible(true);
    }

    /*
    * This method pops up a window to get an input, which is the start time for the time window for sales report
    *
    *@author Jaein
    *@param none
    *@returns none
    *@throws none 
    */
    public static void getStartTime() {
        String starttime = JOptionPane.showInputDialog("Enter start time of the time window (format: yyyy-mm-dd hh:mm:ss)");
        if (starttime != null) {
            start_time = Timestamp.valueOf(starttime);
            startInput.setText("Start Time: " + start_time);
        }
    }

    /*
    * This method pops up a window to get an input, which is the end time for the time window for sales report
    *
    *@author Jaein
    *@param none
    *@returns none
    *@throws none 
    */
    public static void getEndTime() {
        String endtime = JOptionPane.showInputDialog("Enter end time of the time window (format: yyyy-mm-dd hh:mm:ss)");
        if (endtime != null) {
            end_time = Timestamp.valueOf(endtime);
            endInput.setText("End Time: " + end_time);
        }
    }

    /*
    * This method completes query for the sales report and executes it to pull sales data from the order history datatable
    *
    *@author Jaein
    *@param none
    *@returns none
    *@throws SQLException
    */
    public void queryExecution() {
        try {
            Statement stmt = conn.createStatement();
            String sqlQuery = "SELECT o.itemid, i.itemname, SUM(o.quantity) AS total_quantity, SUM(o.price * o.quantity) AS total_sales " +
                                "FROM orders o " +
                                "INNER JOIN items i " +
                                "ON o.itemid = i.itemid " + 
                                "WHERE o.time >= '" + start_time + "' AND o.time <= '" + end_time + "' " + 
                                "GROUP BY o.itemid, i.itemname " +
                                "ORDER BY total_sales DESC;";
            result = stmt.executeQuery(sqlQuery);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }
        
    }

    /*
    * This method iterates through the ResultSet Result result to display the sales by menu item
    *
    *@author Jaein
    *@param none
    *@returns none
    *@throws none 
    */
    public void displayReport() {
        int index = 0;
        while (result.next()) {
            String itemid = result.getString("itemid");
            String itemname = result.getString("itemname");
            String total_quantity = result.getString("total_quantity");
            String sales = result.getString("total_sales");
            String row = "itemid: " + itemid + "  " + "itemname: " + itemname + "  " + "total_quantity: " + total_quantity + "  " + "total_sales: $" + sales;

            rows[index] = row;
            index++;
        }

        rowList = new JList(rows);
    }

    public void actionPerformed(ActionEvent e) {
        getStartTime();
        getEndTime();
        queryExecution();
    }

    public static void main(String[] args) {
        new salesReportGUI();
    }
}
