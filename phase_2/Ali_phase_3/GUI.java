import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
    Map<String, List<String>> itemAddons = new HashMap<>(); // Map to track selected addons for each item

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 30;
    private static final Color ITEM_PANEL_COLOR = new Color(0xCC601D);

    // Declare paymentMessage at the class level
    private StringBuilder paymentMessage = new StringBuilder();

    public static void main(String[] args) {
        // Entry point of the application
        GUI gui = new GUI();
        f = new JFrame("Order Management");

        JPanel p = new JPanel();
        p.setBackground(ITEM_PANEL_COLOR);

        JButton addOrderButton = new JButton("Add New Order");
        addOrderButton.addActionListener(gui);

        JButton addStockButton = new JButton("View Stock");
        addStockButton.addActionListener(gui);

        p.add(addOrderButton);
        p.add(addStockButton);

        f.add(p);
        f.setSize(800, 600);
        f.setVisible(true);
    }

    public GUI() {
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
    }

    public void actionPerformed(ActionEvent e) {
        // Handle actions when a button is clicked
        String s = e.getActionCommand();

        if (s.equals("Add New Order")) {
            showItemsOrderedByItemID();
        } else if (s.equals("View Stock")) {
            newItemsGUI.itemGUI();
        }
    }

    private void showItemsOrderedByItemID() {
        // Display the items available for ordering
        JFrame itemFrame = new JFrame("Select Items");
        itemFrame.setBackground(ITEM_PANEL_COLOR);
        itemPanel = new JPanel();
        itemPanel.setBackground(ITEM_PANEL_COLOR);
        itemPanel.setLayout(new GridLayout(5, 5, 5, 5));

        selectedItemsTextArea = new JTextArea(20, 40);
        selectedItemsTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(selectedItemsTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textAreaPanel = new JPanel();
        textAreaPanel.add(scrollPane);

        totalLabel = new JLabel("Total Amount Due: $0.00");
        totalLabel.setBackground(ITEM_PANEL_COLOR);
        totalPanel = new JPanel();
        totalPanel.setBackground(ITEM_PANEL_COLOR);
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
                    showAddOnsForItem(item); // Display addons when an item is clicked
                }
            });
            itemButton.setBackground(new Color(0xE6E6E6));
            itemPanel.add(itemButton);
        }

        buttonPanel = new JPanel();
        buttonPanel.setBackground(ITEM_PANEL_COLOR);

        JButton clearOrderButton = new JButton("Clear Order");
        clearOrderButton.setBackground(new Color(0xff2400)); // Red color
        clearOrderButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        clearOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearOrder();
            }
        });
        buttonPanel.add(clearOrderButton);

        JButton payButton = new JButton("Pay");
        payButton.setBackground(new Color(0x4cbb17)); // Green color
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

    private void showAddOnsForItem(String item) {
        // Display available addons for a selected item
        JPanel addOnPanel = new JPanel();
        addOnPanel.setBackground(ITEM_PANEL_COLOR); // Match the color to the items panel
        addOnPanel.setLayout(new GridLayout(3, 3, 5, 5)); // 3x3 button layout

        try {
            // Query the database for addon names and prices for the selected item
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT AddonName, Price FROM addons";
            ResultSet result = stmt.executeQuery(sqlStatement);

            // Create a list to track selected addons for this item
            final List<String> selectedAddonsForItem = new ArrayList<>(); // Use final here

            // Iterate through the result and display addon names and prices
            while (result.next()) {
                String addonName = result.getString("AddonName");
                double addonPrice = result.getDouble("Price");
                JButton addonButton = new JButton(addonName + ": $" + addonPrice);
                addonButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!selectedAddonsForItem.contains(addonName)) {
                            selectedAddonsForItem.add(addonName);
                            updateTotalAndTextArea(addonName + ": $" + addonPrice);
                        } else {
                            selectedAddonsForItem.remove(addonName);
                            updateTotalAndTextArea(addonName + ": $" + addonPrice);
                        }
                    }
                });
                addonButton.setBackground(new Color(0xE6E6E6));
                addOnPanel.add(addonButton);
            }

            // Create a "Done!" button
            JButton doneButton = new JButton("Done!");
            doneButton.setBackground(new Color(0x4cbb17)); // Green color
            doneButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Close the addon panel
                    itemAddons.put(item, selectedAddonsForItem);
                    ((JFrame) SwingUtilities.getRoot(addOnPanel)).dispose();
                }
            });
            addOnPanel.add(doneButton);

            JFrame addonFrame = new JFrame("Addons for " + item);
            addonFrame.add(addOnPanel);
            addonFrame.pack();
            addonFrame.setVisible(true);

            stmt.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving addons for the item.");
        }
    }

    private void updateTotalAndTextArea(String item) {
        // Update the total amount and selected items text area
        totalAmount += getPriceForItem(item, 1);
        totalLabel.setText("Total Amount Due: $" + String.format("%.2f", totalAmount));

        if (selectedItems.containsKey(item)) {
            selectedItems.put(item, selectedItems.get(item) + 1);
        } else {
            selectedItems.put(item, 1);
        }

        // Update the text area with the selected items
        selectedItemsTextArea.setText("");
        for (Map.Entry<String, Integer> entry : selectedItems.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            selectedItemsTextArea.append(itemName + " x" + quantity + "\n");
        }
    }

    private void clearOrder() {
        // Clear the selected items and update the total amount
        totalAmount = 0.0;
        totalLabel.setText("Total Amount Due: $0.00");
        selectedItems.clear();
        selectedItemsTextArea.setText("");
    }

    private double getPriceForItem(String item, int quantity) {
        // Calculate the price for an item with a given quantity
        double itemPrice = 0.0;
        String fullItemName = item;
        String itemName = fullItemName.split(": \\$")[0];


        try {
            // Query the database for the price of the selected item
            PreparedStatement stmt = conn.prepareStatement("SELECT Price FROM items WHERE ItemName = ?");
            stmt.setString(1, itemName);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                itemPrice = result.getDouble("Price");
            } else {
                // Item not found in the 'items' table, check the 'addons' table
                PreparedStatement addonStmt = conn.prepareStatement("SELECT Price FROM addons WHERE AddonName = ?");
                addonStmt.setString(1, itemName);
                ResultSet addonResult = addonStmt.executeQuery();

                if (addonResult.next()) {
                    itemPrice = addonResult.getDouble("Price");
                } else {
                    //System.out.println("No matching item found in the database for itemName: " + itemName);
                }

                addonStmt.close();
                addonResult.close();
            }

            stmt.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving the price for the item.");
        }

        return itemPrice * quantity;
    }


    private List<String> getItemsWithPrices() {
        // Retrieve the names of available items along with their prices
        List<String> itemsWithPrices = new ArrayList<>();

        try {
            // Query the database for item names and prices
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT Distinct ItemName, Price FROM items";
            ResultSet result = stmt.executeQuery(sqlStatement);

            while (result.next()) {
                String itemName = result.getString("ItemName");
                double itemPrice = result.getDouble("Price");
                itemsWithPrices.add(itemName + ": $" + itemPrice);
            }

            stmt.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving item names and prices.");
        }

        return itemsWithPrices;
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

        // Declare and initialize currentTime within the try block
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO orders (orderid, itemid, quantity, time, customername, takeout, price) VALUES (?, ?, ?, ?, ?, ?, ?)");

            int maxOrderId = getMaxOrderIdFromOrdersTable();

            for (Map.Entry<String, Integer> entry : selectedItems.entrySet()) {
                String fullItemName = entry.getKey();
                String itemName = fullItemName.split(": \\$")[0];
                int quantity = entry.getValue();

                int itemId = getItemIdFromItemsTable(itemName);
                int newOrderId = maxOrderId + 1;
                String orderType;

                if (itemId == -1) {
                    double price = getPriceForItem(itemName, quantity);
                    itemId = -1;
                    orderType = "Takeout";

                    insertStatement.setInt(1, newOrderId);
                    insertStatement.setInt(2, itemId);
                    insertStatement.setInt(3, quantity);
                    insertStatement.setTimestamp(4, currentTime);
                    insertStatement.setString(5, customerName);
                    insertStatement.setBoolean(6, true);
                    insertStatement.setDouble(7, price);
                } else {
                    orderType = (choice == 0) ? "Dine-In" : "Takeout";
                    updateStock(itemId);
                    insertStatement.setInt(1, newOrderId);
                    insertStatement.setInt(2, itemId);
                    insertStatement.setInt(3, quantity);
                    insertStatement.setTimestamp(4, currentTime);
                    insertStatement.setString(5, customerName);
                    insertStatement.setBoolean(6, orderType.equals("Takeout"));
                    insertStatement.setDouble(7, getPriceForItem(itemName, quantity));
                }

                insertStatement.executeUpdate();

                paymentMessage.append(itemName).append(" x").append(quantity).append(" - $").append(String.format("%.2f", getPriceForItem(itemName, quantity))).append("\n");

                maxOrderId = newOrderId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error processing payment.");
        }

        JOptionPane.showMessageDialog(null, paymentMessage.toString());
        clearOrder();
        itemAddons.clear();
    }

    private int getMaxOrderIdFromOrdersTable() {
        int maxOrderId = 0;

        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT MAX(orderid) FROM orders";
            ResultSet result = stmt.executeQuery(sqlStatement);

            if (result.next()) {
                maxOrderId = result.getInt(1);
            }

            stmt.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving the maximum order ID.");
        }

        return maxOrderId;
    }

    private int getItemIdFromItemsTable(String itemName) {
        int itemId = -1;

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT itemid FROM items WHERE itemname = ?");
            System.out.println("ItemName is: " + itemName);
            stmt.setString(1, itemName);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                itemId = result.getInt("itemid");
            } else {
                System.out.println("No matching item found in the database for itemName: " + itemName);
            }

            stmt.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving item ID from the items table: " + e.getMessage());
        }

        return itemId;
    }

    private void updateStock(int stockID) {
        try {
            // Create a PreparedStatement to update the stock
            PreparedStatement updateStatement = conn.prepareStatement(
                    "UPDATE stock AS s " +
                            "SET amount = amount - use " +
                            "FROM items AS i " +
                            "WHERE s.stockid = i.stockid " +
                            "AND i.stockid = ?");

            // Provide the specific stock_id as a parameter
            updateStatement.setInt(1, stockID);

            // Execute the update statement
            int rowsUpdated = updateStatement.executeUpdate();

            // Check if rows were updated and display a warning if the amount becomes 0
            if (rowsUpdated == 0) {
                JOptionPane.showMessageDialog(null, "Warning: Stock quantity reached 0.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error processing stock update.");
        }
    }

}
