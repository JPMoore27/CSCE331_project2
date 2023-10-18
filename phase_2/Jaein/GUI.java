package Jaein;

import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.time.Instant;


public class GUI extends JFrame implements ActionListener {
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

        salesReportButton = new JButton(text:"Sales Report");
        salesReportButton.setBackground(new Color(rgb:0x030303));
        salesReportButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        salesReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start_time = getStartTime();
                end_time = getEndTime();
            }
        });
    }

    public static Timestamp getStartTime() {
        String start_time = JOptionPane.showInputDialog(message:"Enter start time of the time window");
        if (start_time != null) {
            return Timestamp.valueOf(start_time);
        }
    }

    public static Timestamp getEndTime() {
        String end_time = JOptionPane.showInputDialog(message:"Enter end time of the time window");
        if (end_time != null) {
            return Timestamp.valueOf(end_time);
        }
    }

    public void actionPerformed(ActionEvent e) {
        // Handle actions when a button is clicked
        String s = e.getActionCommand();

        if (s.equals("Add New Order")) {
            showItemsOrderedByItemID();
        }
	    else if(s.equals("View Stock")) {
	        ManagerGUI.managerGUI();
        }
    }

    private void 
}
