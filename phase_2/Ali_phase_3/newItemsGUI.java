import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;

public class newItemsGUI extends JFrame implements ActionListener {
    static JFrame f;
    public static List<JButton> ingredientsButtons = new ArrayList<>();
    public static JButton addSub;
    public static JButton clearButton;
    public static JButton returnButton;
    public static JTextField itemNameField;
    public static JTextField priceField;
    public static JRadioButton dairyRadio;
    public static JRadioButton notDairyRadio;
    public static ButtonGroup dairyButtonGroup;
    public static DefaultListModel<String> selectedIngredientsModel = new DefaultListModel<>();
    public static JList<String> selectedIngredientsList = new JList<>(selectedIngredientsModel);

    static Connection conn = null;

    public newItemsGUI() {
        itemNameField = new JTextField(20);
        priceField = new JTextField(20);
        dairyRadio = new JRadioButton("Dairy");
        notDairyRadio = new JRadioButton("Not Dairy");
        dairyButtonGroup = new ButtonGroup();
        dairyButtonGroup.add(dairyRadio);
        dairyButtonGroup.add(notDairyRadio);
    }

    public static void itemGUI() {
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
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT * FROM stock ORDER BY stockid;";
            ResultSet result = stmt.executeQuery(sqlStatement);
            String prevName = "";
            while (result.next()) {
                ingredientsButtons.add(new JButton(result.getString("stockname")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }

        f = new JFrame("Menu View");
        newItemsGUI s = new newItemsGUI();

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

        selectedIngredientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedIngredientsList.setVisibleRowCount(5);
        selectedIngredientsList.setLayoutOrientation(JList.VERTICAL);

        JPanel pInput = new JPanel();
        pInput.setLayout(new BoxLayout(pInput, BoxLayout.LINE_AXIS));
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

        clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(200, 50));
        clearButton.setBackground(new Color(0xCCCCCC));
        clearButton.setFont(buttonFont);
        clearButton.addActionListener(s);

        returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(200, 50));
        returnButton.setBackground(new Color(0xFF6961));
        returnButton.addActionListener(s);

        pButtons.add(returnButton);
        pButtons.add(addSub);
        pButtons.add(clearButton);

        JPanel pMain = new JPanel(new BorderLayout());
        pMain.setBackground(new Color(0xCC601D));
        pMain.add(pButtons, BorderLayout.CENTER);
        pMain.add(selectedIngredientsList, BorderLayout.EAST);
        pMain.add(pInput, BorderLayout.NORTH);

        f.add(pMain);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1000, 1000);
        f.pack();
        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Return")) {
            f.dispose();
        } else if (s.equals("Add")) {
            String itemName = itemNameField.getText();
            String price = priceField.getText();
            boolean isDairy = dairyRadio.isSelected();

            try {
                Statement maxIdStmt = conn.createStatement();
                String maxIdQuery = "SELECT MAX(itemid) FROM items;";
                ResultSet maxIdResult = maxIdStmt.executeQuery(maxIdQuery);
                int maxItemId = 0;
                if (maxIdResult.next()) {
                    maxItemId = maxIdResult.getInt(1);
                }
                maxItemId++;

                Statement maxKeyStmt = conn.createStatement();
                String maxKeyQuery = "SELECT MAX(key) FROM items;";
                ResultSet maxKeyResult = maxKeyStmt.executeQuery(maxKeyQuery);
                int maxKey = 0;
                if (maxKeyResult.next()) {
                    maxKey = maxKeyResult.getInt(1);
                }

                for (int i = 0; i < selectedIngredientsModel.getSize(); i++) {
                    String selectedIngredient = selectedIngredientsModel.getElementAt(i);
                    maxKey++;
                    int stockId = getStockIdForIngredientName(selectedIngredient);
                    String insertQuery = "INSERT INTO items (key, itemid, itemname, stockid, dairy, price) VALUES (?, ?, ?, ?, ?, ?);";
                    PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                    insertStatement.setInt(1, maxKey);
                    insertStatement.setInt(2, maxItemId);
                    insertStatement.setString(3, itemName);
                    insertStatement.setInt(4, stockId);
                    insertStatement.setBoolean(5, isDairy);
                    insertStatement.setDouble(6, Double.parseDouble(price));
                    insertStatement.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Items added to the database.");
                selectedIngredientsModel.clear(); // Clear selected ingredients after adding items
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error adding items to the database: " + ex.getMessage());
            }
        } else if (s.equals("Clear")) { // Clear button action
            selectedIngredientsModel.clear(); // Clear selected ingredients
        } else {
            if (ingredientsButtons.contains(e.getSource())) {
                JButton ingredientButton = (JButton) e.getSource();
                String ingredientName = ingredientButton.getText();
                selectedIngredientsModel.addElement(ingredientName);
            }
        }
    }

    private int getStockIdForIngredientName(String ingredientName) {
        try {
            String query = "SELECT stockid FROM stock WHERE stockname = ?;";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, ingredientName);

            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("stockid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                itemGUI();
            }
        });
    }
}
