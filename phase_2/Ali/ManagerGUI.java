import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
//this is awful, but necessary because util clashes with awt and we use many things from both
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Font;



/*
 * This class handles creating the GUI for updating inventory
 *
 *@author JP
 *@param none
 *@returns none
 *@throws none 
 */
public class ManagerGUI extends JFrame implements ActionListener {
    static JFrame f;
    public static List<JButton> stockButtons;
    public static JButton addSub;

    /*
    * This method queries stock from the database, and then creates a POS system GUI containing them
    *
    *@author JP,Alireza,Jaein,Kaamish
    *@param none
    *@returns none
    *@throws none 
    */
    public static void managerGUI()
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
      //JOptionPane.showMessageDialog(null,"Opened database successfully");

      stockButtons = new ArrayList<JButton>();
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        //TODO Step 2
        String sqlStatement = "SELECT * FROM stock ORDER BY stockid;";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
	String prevName = "";
        while (result.next()) {
          stockButtons.add(new JButton(result.getString("stockname") + " - " + result.getString("amount") + " " + result.getString("unit")));
	  
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      // create a new frame
      f = new JFrame("Stock View");

      // create a object
      ManagerGUI s = new ManagerGUI();

      // create buttons
      JPanel pButtons = new JPanel(new GridLayout(6, stockButtons.size(), 10, 10));
      //JPanel p = new JPanel();
      pButtons.setBackground(new Color(0xCC601D));
      Font buttonFont = new Font("Arial", Font.PLAIN, 18);      

      for (JButton button : stockButtons) {
        button.setPreferredSize(new Dimension(450, 120));
        button.setBackground(new Color(0xE6E6E6));
        button.setFont(buttonFont);
        button.addActionListener(s);
        pButtons.add(button);
      }	

      addSub = new JButton("Add");
      addSub.setPreferredSize(new Dimension(450, 120));
      addSub.setBackground(new Color(0x77DD77));
      addSub.setFont(buttonFont);
      addSub.addActionListener(s);
      pButtons.add(addSub);

      JPanel pMain = new JPanel(new BorderLayout());
      pMain.setBackground(new Color(0xCC601D));
      pMain.add(pButtons, BorderLayout.CENTER);

      JButton returnButton = new JButton("Return");
      returnButton.setPreferredSize(new Dimension(450, 120));
      returnButton.setBackground(new Color(0xFF6961));
      returnButton.addActionListener(s);
      pMain.add(returnButton, BorderLayout.SOUTH);

      // add actionlistener to button
      //b.addActionListener(s);

      //TODO Step 3 
      //JTextArea text = new JTextArea(name);
      //TODO Step 4
      //p.add(text);
      // add button to panel
      //p.add(b);

      // add panel to frame
      f.add(pMain);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      // set the size of frame
      f.setLayout(new FlowLayout());
      f.setSize(1000, 1000);
      f.pack();
      f.setVisible(true);

      //closing the connection
      try {
        conn.close();
        //JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }

    // if button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("Return")) {
            f.dispose();
        }
	else if (s.equals("Add")) {
	    addSub.setText("Subtract");
	}
	else if (s.equals("Subtract")) {
	    addSub.setText("Add");
	}
	else {
	    Connection conn = null;
      		//TODO STEP 1
      	    try {
              conn = DriverManager.getConnection(
              "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_03g_db",
              "csce331_903_jp_moore",
              "coll1n");
            } catch (Exception exc) {
            exc.printStackTrace();
            System.err.println(e.getClass().getName()+": "+exc.getMessage());
            System.exit(0);
        }
	    try {
	    //get stockname
	    String[] sSplit = s.split(" ");
    	    //update database	    
	    Statement stmt = conn.createStatement();

	    int addorsub = -1;
	    if(addSub.getText().equals("Add")) {addorsub = 1;}

    	    stmt.executeUpdate("UPDATE stock SET amount = amount + " + addorsub + " WHERE stockname = \'" + sSplit[0] + "\';");
    	    //get id
    	    ResultSet stockID = stmt.executeQuery("SELECT stockid FROM stock WHERE stockname = \'" + sSplit[0] + "\';");

		
	    //System.out.println(sSplit[0] + " " + sSplit[1] + " " + (Integer.parseInt(sSplit[2]) + 1) + " " + sSplit[3]);
	    stockID.next();
	    stockButtons.get(Integer.parseInt(stockID.getString("stockid"))).setText(sSplit[0] + " " + sSplit[1] + " " + (Integer.parseInt(sSplit[2]) + addorsub) + " " + sSplit[3]); 

	    } catch(Exception exc) {System.err.println(e.getClass().getName()+": "+exc.getMessage());};
	}
    }
}
