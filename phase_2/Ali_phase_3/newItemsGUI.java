import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List; // Specify java.util.List
import java.util.ArrayList; // Specify java.util.ArrayList
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;

public class newItemsGUI extends JFrame implements ActionListener {
    static JFrame f;
    public static List<JButton> ingredientsButtons = new ArrayList<>();
    public static JButton addSub;
    public static JButton returnButton;
    public static JTextField itemNameField;
    public static JTextField priceField; // Added input field for price
    public static JRadioButton dairyRadio;
    public static JRadioButton notDairyRadio;
    public static ButtonGroup dairyButtonGroup;

    static Connection conn = null;

    public newItemsGUI() {
        itemNameField = new JTextField(20);
        priceField = new JTextField(20); // Initialize the price field
        dairyRadio = new JRadioButton("Dairy");
        notDairyRadio = new JRadioButton("Not Dairy");
        dairyButtonGroup = new ButtonGroup();
        dairyButtonGroup.add(dairyRadio);
        dairyButtonGroup.add(notDairyRadio);
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

        // Create a panel for input fields, including item name, price, and radio buttons
        JPanel pInput = new JPanel();
        pInput.setLayout(new BoxLayout(pInput, BoxLayout.LINE_AXIS)); // Horizontal layout
        pInput.setBackground(new Color(0xCC601D));
        pInput.add(new JLabel("Item Name: "));
        pInput.add(itemNameField);
        pInput.add(new JLabel("Price: "));
        pInput.add(priceField);
        pInput.add(new JLabel("Dairy:"));
        pInput.add(dairyRadio);
        pInput.add(notDairyRadio);

        addSub = new JButton("Add");
        addSub.setPreferredSize(new Dimension(200, 50));
        addSub.setBackground(new Color(0x77DD77));
        addSub.setFont(buttonFont);
        addSub.addActionListener(s);

        // Move the "Return" button to the left of the "Add" button
        returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(200, 50));
        returnButton.setBackground(new Color(0xFF6961));
        returnButton.addActionListener(s);

        pButtons.add(returnButton);
        pButtons.add(addSub);

        // Use BorderLayout for the main panel
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.setBackground(new Color(0xCC601D));
        pMain.add(pButtons, BorderLayout.CENTER);
        pMain.add(pInput, BorderLayout.NORTH); // Position input fields at the top

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
            // Get the item name and price from the input fields
            String itemName = itemNameField.getText();
            String price = priceField.getText();

            // Check the selected radio button to determine if it's dairy or not
            boolean isDairy = dairyRadio.isSelected();

            // You can now use itemName, price, isDairy, and the selected ingredients to add the new item to your database.
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
