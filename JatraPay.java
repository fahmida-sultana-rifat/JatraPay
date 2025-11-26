import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// model
// c1
class TransportService {
    private String route;
    private double fare;
    public TransportService(String route, double fare) {
        this.route = route;
        this.fare = fare;
    }
    public double calculateFare() {
        return fare;
    }
    public String getRouteName() {
        return route;
    }
    public double getBaseFare() {
        return fare;
    }
}

// c2
class PremiumService extends TransportService {
    private double premiumCharge;
    public PremiumService(String route, double base, double charge) {
        super(route, base);
        this.premiumCharge = charge;
    }
    public double calculateFare() {
        return getBaseFare() + premiumCharge;
    }
    public double getPremiumCharge() {
        return premiumCharge;
    }
}

// c3
class Passenger {
    private String name;
    private String type;
    public Passenger(String name, String type) {
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
}

// c4
class Payment {
    private String method;
    public Payment(String method) {
        this.method = method;
    }
    public boolean processPayment(double amount) {
        return true;
    }
    public String getMethod() {
        return method;
    }
}

// controller
// c5
class TransactionController {
    public String processTransaction(Passenger passenger, TransportService service, Payment payment,
            double fareEntered) {
        payment.processPayment(fareEntered);
        return "Completed";
    }
}

// view
// c6
public class JatraPay {
    private JFrame frame;
    private JComboBox<String> startCombo;
    private JComboBox<String> destCombo;

    private JTextField fareField;
    private JRadioButton qrRadio, ussdRadio;
    private JButton processButton;
    private TransactionController controller;

    private final String[] LOCATIONS = {
            "GEC", "Bahaddarhat", "Muradpur", "Oxygen", "Chawkbazar", "Station Road"
    };

    public JatraPay() {
        controller = new TransactionController();
        initialize();
    }

    private void initialize() {

        frame = new JFrame("JatraPay - makes your journey smooth");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout()); 

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        leftPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("JatraPay Transaction Details"),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
            )
        );
        leftPanel.setBackground(Color.WHITE);

        
        // start
        leftPanel.add(new JLabel("Enter Starting Location:"));
        startCombo = new JComboBox<>(LOCATIONS);
        startCombo.setEditable(true);
        startCombo.setMaximumSize(new Dimension(700, 50)); 
        startCombo.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        leftPanel.add(startCombo);

        // destination
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(new JLabel("Enter Destination:"));
        destCombo = new JComboBox<>(LOCATIONS);
        destCombo.setEditable(true);
        destCombo.setMaximumSize(new Dimension(700, 50)); 
        destCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftPanel.add(destCombo);

        // fare
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(new JLabel("Enter Fare Amount (1-100TK):"));
        fareField = new JTextField();
        fareField.setMaximumSize(new Dimension(700, 70)); 
        fareField.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        leftPanel.add(fareField);

        //pay method
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(new JLabel("Payment Method:"));

        qrRadio = new JRadioButton("QR");
        qrRadio.setSelected(true);
        qrRadio.setBackground(Color.WHITE); 
        
        ussdRadio = new JRadioButton("USSD");
        ussdRadio.setBackground(Color.WHITE);

        ButtonGroup bg = new ButtonGroup();
        bg.add(qrRadio);
        bg.add(ussdRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.add(qrRadio);
        radioPanel.add(ussdRadio);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(radioPanel);

        processButton = new JButton("Process");
        processButton.setMaximumSize(new Dimension(700, 50)); 
        processButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(processButton);
        leftPanel.add(Box.createVerticalStrut(20));

        frame.add(leftPanel, new GridBagConstraints());
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        // Action
        processButton.addActionListener(e -> onProcess());

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    private void onProcess() {
        Object startObject = startCombo.getSelectedItem();
        Object destObject = destCombo.getSelectedItem();
        String start = (startObject != null) ? startObject.toString().trim() : "";
        String dest = (destObject != null) ? destObject.toString().trim() : "";

        if (start.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Starting location and destination cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (start.equals(dest)) {
            JOptionPane.showMessageDialog(frame, "Starting location and destination cannot be the same.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // fare check
        String fareText = fareField.getText().trim();
        double fareValue;

        try {
            fareValue = Double.parseDouble(fareText);
            if (fareValue < 1 || fareValue > 100)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Enter a valid fare between 1 - 100 TK.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String paymentMethod = qrRadio.isSelected() ? "QR" : "USSD";
        TransportService service = new TransportService(start + " → " + dest, fareValue);
        Passenger passenger = new Passenger("Default User", "Regular");
        Payment payment = new Payment(paymentMethod);
        String status = controller.processTransaction(passenger, service, payment, fareValue);

        // receipt
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        String receipt = "Date & Time: " + now + "\n\n" +
                "From " + start + " to " + dest + ", payment of " +
                String.format("%.0f", fareValue) + " taka has been completed.\n\n" +
                "Payment Method: " + paymentMethod + "\n" +
                "Status: " + status;

        JOptionPane.showMessageDialog(frame, receipt, "✅ Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
        startCombo.setSelectedItem("");
        destCombo.setSelectedItem("");
        fareField.setText("");
        qrRadio.setSelected(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JatraPay::new);
    }
}
