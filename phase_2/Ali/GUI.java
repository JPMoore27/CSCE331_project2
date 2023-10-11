import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame implements ActionListener {
    static JFrame f;
    Connection conn = null;
    JPanel itemPanel;
    JPanel textAreaPanel;
    JTextArea selectedItemsTextArea;
    JPanel buttonPanel;
    JPanel totalPanel;
    JLabel totalLabel;
    double totalAmount = 0.0;
    Map<String, Integer> selectedItems = new HashMap<>(); // Map to track selected items and their quantities

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
        itemPanel.setLayout(new GridLayout(5, 5, 5, 5));

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

        List<String> itemsWithPrices = getItemsWithPrices();
        totalAmount = 0.0;
        selectedItems.clear(); // Clear the selected items and quantities map

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

        JButton clearOrderButton = new JButton("Clear Order");
        clearOrderButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        clearOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearOrder();
            }
        });
        buttonPanel.add(clearOrderButton);

        JButton payButton = new JButton("Pay");
        payButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String customerName = JOptionPane.showInputDialog("Enter Customer Name:");
                if (customerName != null) {
                    pay(customerName);
                }
            }
        });
        buttonPanel.add(payButton);

        itemFrame.add(textAreaPanel, BorderLayout.WEST);
        itemFrame.add(itemPanel, BorderLayout.CENTER);
        itemFrame.add(buttonPanel, BorderLayout.SOUTH);

        itemFrame.add(totalPanel, BorderLayout.NORTH);
        itemFrame.pack();
        itemFrame.setVisible(true);
    }

    private void updateTotalAndTextArea(String item) {
        // Parse the item name and price
        String itemName = item.split(" - Price: \\$")[0];
        double price = Double.parseDouble(item.split(" - Price: \\$")[1]);

        // Update selected items and their quantities
        if (selectedItems.containsKey(itemName)) {
            selectedItems.put(itemName, selectedItems.get(itemName) + 1);
        } else {
            selectedItems.put(itemName, 1);
        }

        totalAmount += price;
        updateTotalLabel();
        updateSelectedItemsTextArea();
    }

    private void updateTotalLabel() {
        totalLabel.setText("Total Amount Due: $" + String.format("%.2f", totalAmount));
    }

    private void updateSelectedItemsTextArea() {
        selectedItemsTextArea.setText(""); // Clear the text area
        for (Map.Entry<String, Integer> entry : selectedItems.entrySet()) {
            selectedItemsTextArea.append(entry.getKey() + " x" + entry.getValue() + "\n");
        }
    }

    private void clearOrder() {
        totalAmount = 0.0;
        updateTotalLabel();
        selectedItems.clear(); // Clear the selected items and quantities map
        updateSelectedItemsTextArea();
    }

    private void pay(String customerName) {
        String[] options = {"Dine-In", "Takeout"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Is this order for dine-in or takeout?",
                "Order Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        String orderType;
        if (choice == 0) {
            orderType = "Dine-In";
        } else {
            orderType = "Takeout";
        }

        // Perform the payment operation here
        StringBuilder paymentMessage = new StringBuilder("Payment processed for ");
        paymentMessage.append(customerName).append(" (").append(orderType).append("). Total Amount: $").append(String.format("%.2f", totalAmount)).append("\n");
        for (Map.Entry<String, Integer> entry : selectedItems.entrySet()) {
            paymentMessage.append(entry.getKey()).append(" x").append(entry.getValue()).append("\n");
        }
        JOptionPane.showMessageDialog(null, paymentMessage.toString());
        clearOrder();
    }


    private List<String> getItemsWithPrices() {
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
}
