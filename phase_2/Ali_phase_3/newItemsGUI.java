import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout; // Import BorderLayout from java.awt

public class newItemsGUI extends JFrame implements ActionListener {
    static JFrame f;
    public static List<JButton> ingredientsButtons = new ArrayList<>();
    public static JButton addSub;
    public static JButton returnButton;
    public static JTextField itemNameField;

    static Connection conn = null;

    public newItemsGUI() {
        itemNameField = new JTextField(20);
    }

    public static void itemGUI() {
        // TODO STEP 1
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_03g_db",
                    "csce331_903_jp_moore",
                    "coll1n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        ingredientsButtons = new ArrayList<>();
        try {
            // create a statement object
            Statement stmt = conn.createStatement();
            // create a SQL statement
            // TODO Step 2
            String sqlStatement = "SELECT * FROM stock ORDER BY stockid;";
            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);
            String prevName = "";
            while (result.next()) {
                ingredientsButtons.add(new JButton(result.getString("stockname")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }

        // create a new frame
        f = new JFrame("Menu View");

        // create an object
        newItemsGUI s = new newItemsGUI();

        // create buttons
        JPanel pButtons = new JPanel();
        pButtons.setLayout(new GridLayout(15, ingredientsButtons.size(), 10, 10));
        pButtons.setBackground(new Color(0xCC601D));
        Font buttonFont = new Font("Arial", Font.PLAIN, 12);

        for (JButton button : ingredientsButtons) {
            button.setPreferredSize(new Dimension(400, 30));
            button.setBackground(new Color(0xE6E6E6));
            button.setFont(buttonFont);
            button.addActionListener(s);
            pButtons.add(button);
        }

        // Create a panel for input fields
        JPanel pInput = new JPanel();
        pInput.setLayout(new BoxLayout(pInput, BoxLayout.LINE_AXIS));
        pInput.setPreferredSize(new Dimension(400, 15));
        pInput.add(new JLabel("Item Name: "));
        pInput.add(itemNameField);

        addSub = new JButton("Add");
        addSub.setPreferredSize(new Dimension(400, 30));
        addSub.setBackground(new Color(0x77DD77));
        addSub.setFont(buttonFont);
        addSub.addActionListener(s);

        // Move the "Return" button to the left of the "Add" button
        returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(400, 30));
        returnButton.setBackground(new Color(0xFF6961));
        returnButton.addActionListener(s);


        pButtons.add(returnButton);
        pButtons.add(addSub);

        // Use BorderLayout for the main panel
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.setBackground(new Color(0xCC601D));
        pMain.add(pButtons, BorderLayout.CENTER);
        pMain.add(pInput, BorderLayout.NORTH);


        // Add the main panel to the frame
        f.add(pMain);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1000, 1000);
        f.pack();
        f.setVisible(true);

        // Closing the connection...
    }

    // If a button is pressed
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Return")) {
            f.dispose();
        } else if (s.equals("Add")) {
            // Get the item name from the input field
            String itemName = itemNameField.getText();

            // You can now use itemName and the selected ingredients to add the new item to your database.
            // Add the necessary database logic here.
        } else {
            // Handle ingredient selection here.
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                itemGUI();
            }
        });
    }
}
