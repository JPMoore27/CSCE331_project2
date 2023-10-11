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
  TODO:
  1) Change credentials for your own team's database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel p
*/


/*
 * This method queries items and stock from the database, and then creates a POS system GUI containing them
 *
 *@author JP,Alireza,Jaein,Kaamish
 *@param none
 *@returns none
 *@throws none 
 */
public class GUI extends JFrame implements ActionListener {
    static JFrame f;

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
      //JOptionPane.showMessageDialog(null,"Opened database successfully");

      List<String> names = new ArrayList<String>();
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        //TODO Step 2
        String sqlStatement = "SELECT * FROM stock";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
	String prevName = "";
        while (result.next()) {
          names.add(result.getString("stockname") + " - " + result.getString("amount") + " " + result.getString("unit"));
	  
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      // create a new frame
      f = new JFrame("DB GUI");

      // create a object
      GUI s = new GUI();

      // create buttons
      JPanel pButtons = new JPanel(new GridLayout(6, names.size(), 10, 10));
      //JPanel p = new JPanel();
      pButtons.setBackground(new Color(0xCC601D));
      Font buttonFont = new Font("Arial", Font.PLAIN, 18);      

      for (String str : names) {
	JButton button = new JButton(str);
	button.setPreferredSize(new Dimension(450, 120));
	button.setBackground(new Color(0xE6E6E6));
	button.setFont(buttonFont);
	button.addActionListener(s);
        pButtons.add(button);
      }	

 
      JPanel pMain = new JPanel(new BorderLayout());
      pMain.setBackground(new Color(0xCC601D));
      pMain.add(pButtons, BorderLayout.CENTER);

      JButton returnButton = new JButton("Return");
      returnButton.setPreferredSize(new Dimension(450, 120));
      returnButton.setBackground(new Color(0xFF6961));
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
        JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }

    // if button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("Close")) {
            f.dispose();
        }
	else {
    	    ResultSet result = stmt.executeQuery("");		
	}
    }
}
