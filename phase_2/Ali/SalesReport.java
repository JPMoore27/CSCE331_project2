package Jaein;

import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.time.Instant;


/*
 * This method creates a POS system GUI and integrates sales report generating process
 *
 *@author Jaein
 *@param none
 *@returns none
 *@throws none 
 */
public class SalesReport extends JFrame implements ActionListener {
    static JFrame f;
    public static JButton salesReportButton;
    static Timestamp start_time;
    static Timestamp end_time;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 30;

    public static void main(String[] args)
    {
        //Building the connection
        Connection conn = null;
        //TODO STEP 1
        try {
            conn = DriverManager.getConnection(
            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_03g_db",
            "csce331_903_jp_moore",
            "coll1n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        String sqlStatement = "";

        salesReportButton = new JButton("Sales Report");
        salesReportButton.setBackground(new Color(0x030303));
        salesReportButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        salesReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start_time = getStartTime();
                end_time = getEndTime();
            }
        });
    }

    /*
    * This method pops up a window to get an input, which is the start time for the time window for sales report
    *
    *@author Jaein
    *@param none
    *@returns Timestamp start_time
    *@throws none 
    */
    public static Timestamp getStartTime() {
        String start_time = JOptionPane.showInputDialog("Enter start time of the time window");
        if (start_time != null) {
            return Timestamp.valueOf(start_time);
        }
    }

    /*
    * This method pops up a window to get an input, which is the end time for the time window for sales report
    *
    *@author Jaein
    *@param none
    *@returns Timestamp end_time
    *@throws none 
    */
    public static Timestamp getEndTime() {
        String end_time = JOptionPane.showInputDialog("Enter end time of the time window");
        if (end_time != null) {
            return Timestamp.valueOf(end_time);
        }
    }


    /*
    * This method completes query for the sales report and executes it to pull sales data from the order history datatable
    *
    *@author Jaein
    *@param Timestamp start_time, Timestamp end_time
    *@returns ResultSet result
    *@throws none 
    */
    public ResultSet queryExecution(Timestamp start_time, Timestamp end_time) {
        ResultSet result;
        return result;
    }

    /*
    * This method iterates through passed argument for parameter result to display the sales by menu item
    *
    *@author Jaein
    *@param ResultSet result
    *@returns none
    *@throws none 
    */
    public void displayReport(ResultSet result) {
        
    }

    public void actionPerformed(ActionEvent e) {
        // Handle actions when a button is clicked
        String s = e.getActionCommand();

        if (s.equals("Add New Order")) {
            showItemsOrderedByItemID();
        }
	    else if(s.equals("View Stock")) {
	        //qManagerGUI.managerGUI();
        }
    }

    // private void 
}
