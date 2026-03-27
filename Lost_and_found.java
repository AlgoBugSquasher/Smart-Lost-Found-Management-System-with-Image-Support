import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Lost_and_found extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Data storage
    private static List<Item> lostItems = new ArrayList<>();
    private static List<Item> foundItems = new ArrayList<>();
    
    // Panels
    private JPanel loginPanel, dashboardPanel, lostFormPanel, foundFormPanel, viewPanel;
    
    // Components
    private JTextField emailField, passwordField;
    private JTextField nameFieldLost, descFieldLost, locationFieldLost, dateFieldLost;
    private JTextField nameFieldFound, descFieldFound, locationFieldFound, dateFieldFound;
    private JTextArea descAreaLost, descAreaFound;
    private JList<String> itemsList;
    private DefaultListModel<String> listModel;
    
    public Lost_and_found() {
        setTitle("Smart Lost & Found System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        initLoginPanel();
        initDashboardPanel();
        initLostFormPanel();
        initFoundFormPanel();
        initViewPanel();
        
        mainPanel.add(loginPanel, "login");
        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(lostFormPanel, "lost");
        mainPanel.add(foundFormPanel, "found");
        mainPanel.add(viewPanel, "view");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }
    
    private void initLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);
        
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        loginPanel.add(emailLabel, gbc);
        
        emailField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        loginPanel.add(emailField, gbc);
        
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(passLabel, gbc);
        
        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            if ("user@test.com".equals(emailField.getText()) && "pass".equals(new String(((JPasswordField)passwordField).getPassword()))) {
                cardLayout.show(mainPanel, "dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials! Use: user@test.com / pass");
            }
        });
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginPanel.add(loginBtn, gbc);
    }
    
    private void initDashboardPanel() {
        dashboardPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dashboardPanel.add(titleLabel, gbc);
        
        JButton reportLostBtn = new JButton("Report Lost Item");
        reportLostBtn.addActionListener(e -> cardLayout.show(mainPanel, "lost"));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        dashboardPanel.add(reportLostBtn, gbc);
        
        JButton reportFoundBtn = new JButton("Report Found Item");
        reportFoundBtn.addActionListener(e -> cardLayout.show(mainPanel, "found"));
        gbc.gridx = 1; gbc.gridy = 1;
        dashboardPanel.add(reportFoundBtn, gbc);
        
        JButton viewBtn = new JButton("View Items");
        viewBtn.addActionListener(e -> {
            updateViewList();
            cardLayout.show(mainPanel, "view");
        });
        gbc.gridx = 0; gbc.gridy = 2;
        dashboardPanel.add(viewBtn, gbc);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "login");
            emailField.setText("");
            passwordField.setText("");
        });
        gbc.gridx = 1; gbc.gridy = 2;
        dashboardPanel.add(logoutBtn, gbc);
    }
    
    private void initLostFormPanel() {
        lostFormPanel = createFormPanel(true);
    }
    
    private void initFoundFormPanel() {
        foundFormPanel = createFormPanel(false);
    }
    
    private JPanel createFormPanel(boolean isLost) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        String type = isLost ? "Report Lost Item" : "Report Found Item";
        JLabel titleLabel = new JLabel(type, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        
        // Name
        JLabel nameLabel = new JLabel("Item Name:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);
        
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(nameField, gbc);
        
        // Description
        JLabel descLabel = new JLabel("Description:");
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(descLabel, gbc);
        
        JTextArea descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JScrollPane(descArea), gbc);
        
        // Location
        JLabel locLabel = new JLabel("Location:");
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(locLabel, gbc);
        
        JTextField locField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(locField, gbc);
        
        // Date
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(dateLabel, gbc);
        
        JTextField dateField = new JTextField(20);
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(dateField, gbc);
        
        // Image upload
        JLabel imgLabel = new JLabel("Image:");
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(imgLabel, gbc);
        
        JTextField imgPathField = new JTextField(15);
        imgPathField.setEditable(false);
        JButton uploadBtn = new JButton("Upload");
        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                imgPathField.setText(chooser.getSelectedFile().getPath());
            }
        });
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(imgPathField, BorderLayout.CENTER);
        imgPanel.add(uploadBtn, BorderLayout.EAST);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(imgPanel, gbc);
        
        // Submit
        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> {
            try {
                Item item = new Item(
                    nameField.getText(),
                    descArea.getText(),
                    locField.getText(),
                    dateField.getText(),
                    imgPathField.getText(),
                    isLost
                );
                if (isLost) {
                    lostItems.add(item);
                } else {
                    foundItems.add(item);
                }
                JOptionPane.showMessageDialog(this, "Item reported successfully!");
                cardLayout.show(mainPanel, "dashboard");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(submitBtn, gbc);
        
        // Back button
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
        gbc.gridy = 7;
        formPanel.add(backBtn, gbc);
        
        // Store references for this form
        if (isLost) {
            nameFieldLost = nameField;
            descAreaLost = descArea;
            locationFieldLost = locField;
            dateFieldLost = dateField;
        } else {
            nameFieldFound = nameField;
            descAreaFound = descArea;
            locationFieldFound = locField;
            dateFieldFound = dateField;
        }
        
        return formPanel;
    }
    
    private void initViewPanel() {
        viewPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("All Reported Items", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        viewPanel.add(titleLabel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        itemsList = new JList<>(listModel);
        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(itemsList);
        viewPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> updateViewList());
        buttonPanel.add(refreshBtn);
        
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
        buttonPanel.add(backBtn);
        
        viewPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void updateViewList() {
        listModel.clear();
        for (Item item : lostItems) {
            listModel.addElement("Lost - " + item.name + " @ " + item.location);
        }
        for (Item item : foundItems) {
            listModel.addElement("Found - " + item.name + " @ " + item.location);
        }
    }
    
    // Item class
    static class Item {
        String name, description, location, date, imagePath;
        boolean isLost;
        
        Item(String name, String description, String location, String date, String imagePath, boolean isLost) {
            this.name = name;
            this.description = description;
            this.location = location;
            this.date = date;
            this.imagePath = imagePath;
            this.isLost = isLost;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lost_and_found().setVisible(true));
    }
}
