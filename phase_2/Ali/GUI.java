import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame implements ActionListener {
    static JFrame f;
    Connection conn = null;
    JPanel itemPanel;
    JPanel textAreaPanel; // New panel for the text area
    JTextArea selectedItemsTextArea;
    JPanel buttonPanel; // New panel for buttons
    JPanel totalPanel;
    JLabel totalLabel;
    double totalAmount = 0.0;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 30;

    public static void main(String[] args) {
        GUI gui = new GUI();
        f = new JFrame("Order Management");

        JPanel p = new JPanel();

        JButton addOrderButton = new JButton("Add New Order");
        addOrderButton.addActionListener(gui);

        p.add(addOrderButton);

        f.add(p);
        f.setSize(800, 600);
        f.setVisible(true);
    }

    public GUI() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_03g_db",
                    "csce331_903_amahdavi",
                    "arma12271381");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null, "Database connection established");
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.equals("Add New Order")) {
            showItemsOrderedByItemID();
        }
    }

    private void showItemsOrderedByItemID() {
        JFrame itemFrame = new JFrame("Select Items");
        itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(5, 5, 5, 5)); // 5 rows, 5 columns with 5px gaps

        selectedItemsTextArea = new JTextArea(20, 40);
        selectedItemsTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(selectedItemsTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textAreaPanel = new JPanel();
        textAreaPanel.add(scrollPane);

        totalLabel = new JLabel("Total Amount Due: $0.00");
        totalPanel = new JPanel();
        totalPanel.add(totalLabel);

        List<String> itemsWithPrices = getItemsOrderedByItemID();
        totalAmount = 0.0;

        for (String item : itemsWithPrices) {
            JButton itemButton = new JButton(item);
            itemButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            itemButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateTotalAndTextArea(item);
                }
            });
            itemPanel.add(itemButton);
        }

        buttonPanel = new JPanel();

        // Create a "Clear Order" button
        JButton clearOrderButton = new JButton("Clear Order");
        clearOrderButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        clearOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearOrder();
            }
        });
        buttonPanel.add(clearOrderButton);

        // Create a "Pay" button
        JButton payButton = new JButton("Pay");
        payButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pay();
            }
        });
        buttonPanel.add(payButton);

        itemFrame.add(textAreaPanel, BorderLayout.WEST); // Add text area to the left
        itemFrame.add(itemPanel, BorderLayout.CENTER);
        itemFrame.add(buttonPanel, BorderLayout.SOUTH);

        itemFrame.add(totalPanel, BorderLayout.NORTH);
        itemFrame.pack();
        itemFrame.setVisible(true);
    }

    private List<String> getItemsOrderedByItemID() {
        List<String> itemsWithPrices = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT DISTINCT ItemName, Price FROM items ORDER BY ItemName";
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                String itemName = result.getString("ItemName");
                double price = result.getDouble("Price");
                itemsWithPrices.add(itemName + " - Price: $" + price);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }
        return itemsWithPrices;
    }

    private void updateTotalAndTextArea(String item) {
        double price = Double.parseDouble(item.split(" - Price: \\$")[1]);
        totalAmount += price;
        updateTotalLabel();
        updateSelectedItemsTextArea(item);
    }

    private void updateTotalLabel() {
        totalLabel.setText("Total Amount Due: $" + String.format("%.2f", totalAmount));
    }

    private void updateSelectedItemsTextArea(String item) {
        selectedItemsTextArea.append(item + "\n");
    }

    private void clearOrder() {
        totalAmount = 0.0;
        updateTotalLabel();
        selectedItemsTextArea.setText(""); // Clear the text area
    }

    private void pay() {
        // Perform the payment operation here
        JOptionPane.showMessageDialog(null, "Payment processed. Total Amount: $" + String.format("%.2f", totalAmount));
        clearOrder(); // Clear the order after payment
    }
}
