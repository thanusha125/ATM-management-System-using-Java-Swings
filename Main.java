import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import java.util.ArrayList; // Import ArrayList class
import java.util.Date; 
class User 
{
    private String cardNumber;
    private String pin;
    private String name;
    private int balance;
    private Map<String, Loan> loans;
    private Map<Integer, Integer> denominations;
    private ArrayList<String> transactionHistory;

    public User(String cardNumber, String pin, String name)
    {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.name = name;
        this.balance = 0;
        this.loans = new HashMap<>();
        this.denominations = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    public String getCardNumber() 
    {
        return cardNumber;
    }

    public String getPin() 
    {
        return pin;
    }

    public String getName()
    {
        return name;
    }

    public int getBalance() 
    {
        return balance;
    }

    public void deposit(int amount)
    {
        balance += amount;
        addToTransactionHistory("Deposit: " + amount + " on " + new Date());
        updateCSV();
    }

    public boolean withdraw(int amount) 
    {
        if (balance >= amount)
        {
            balance -= amount;
            updateCSV();
	        addToTransactionHistory("Withdrawal: " + amount + " on " + new Date());
            return true;
        }
        return false;
    }

    public void changePin(String newPin)
    {
        this.pin = newPin;
        updateCSV();
    }

    public boolean withdrawWithDenomination(int amount) 
    {
        if (balance >= amount && amount % 50 == 0)
        {
            int remainingAmount = amount;

            for (int denomination : denominations.keySet()) 
            {
                int numNotes = remainingAmount / denomination;
                if (numNotes > 0 && denominations.get(denomination) >= numNotes) 
                {
                    remainingAmount -= numNotes * denomination;
                    System.out.println("Dispensing " + numNotes + " notes of " + denomination + " rupees.");
                }
            }

            if (remainingAmount == 0) 
            {
                balance -= amount;
                updateDenominations(amount);
                updateCSV();
                return true;
            }
        }
        return false;
    }

    private void updateDenominations(int amount) 
    {
        for (int denomination : denominations.keySet()) 
        {
            int numNotes = amount / denomination;
            if (numNotes > 0 && denominations.get(denomination) >= numNotes) 
            {
                denominations.put(denomination, denominations.get(denomination) - numNotes);
                amount -= numNotes * denomination;
            }
        }
    }

    public void addDenomination(int denomination, int numNotes)
    {
        denominations.put(denomination, numNotes);
    }

    public void applyForLoan(String loanType, int amount, double interestRate) 
    {
        double totalAmount = amount * (1 + (interestRate / 100.0));
        if (totalAmount <= 0)
        {
            JOptionPane.showMessageDialog(null, "Invalid loan amount or interest rate.", "Loan Application", JOptionPane.ERROR_MESSAGE);
            return;
        }
        balance += totalAmount;
        Loan loan = new Loan(loanType, amount, interestRate);
        loans.put(loanType, loan);
	updateCSV();
	    String loanInfo = "Loan application successful.\nLoan Type: " + loanType + "\nAmount: " + amount + "\nTotal Amount: " + totalAmount;
        JOptionPane.showMessageDialog(null, loanInfo, "Loan Application", JOptionPane.INFORMATION_MESSAGE);
        
       
        addToTransactionHistory("Loan Application - Type: " + loanType + ", Amount: " + amount + ", Total Amount: " + totalAmount + " on " + new Date());

    }
	public void viewLoans() {
        if (loans.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No active loans.");
        } else {
            StringBuilder loanDetails = new StringBuilder("Active loans:\n");
            for (Loan loan : loans.values()) {
                loanDetails.append("Type: ").append(loan.getLoanType()).append(", Amount: ")
                        .append(loan.getAmount()).append(", Interest Rate: ").append(loan.getInterestRate()).append("%\n");
            }
            JOptionPane.showMessageDialog(null, loanDetails.toString());
        }
    }
	private void addToTransactionHistory(String transactionDetails) {
        transactionHistory.add(transactionDetails);
        JOptionPane.showMessageDialog(null, "Transaction added:\n" + transactionDetails);
    }

    public void displayTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No transaction history available.");
        } else {
            StringBuilder transactionDetails = new StringBuilder("Transaction History:\n");
            for (String transaction : transactionHistory) {
                transactionDetails.append(transaction).append("\n");
            }
            JOptionPane.showMessageDialog(null, transactionDetails.toString());
        }
    }




    
    
    private class Loan
    {
        private String loanType;
        private int amount;
        private double interestRate;

        public Loan(String loanType, int amount, double interestRate)
        {
            this.loanType = loanType;
            this.amount = amount;
            this.interestRate = interestRate;
        }

        public String getLoanType()
        {
            return loanType;
        }

        public int getAmount()
        {
            return amount;
        }

        public double getInterestRate()
        {
            return interestRate;
        }
    }
private void updateCSV() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv", false))) {
        // Write header
        writer.write("CardNumber,Pin,Name,Balance\n");

        // Write user data to the CSV file
        writer.write(cardNumber + "," + pin + "," + name + "," + balance + "\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    
    
        
}
class ATM
{
    private Map<String, User> users;

    public ATM()
    {
        users = new HashMap<>();
    }

    
    public void addUser(User user)
    {
        users.put(user.getCardNumber(), user);
        writeToCSV(user);  // Add this line to write the user data to CSV
    }

    private void writeToCSV(User user)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv", true))) 
        {
            // If the file doesn't exist, create the header
            if (Files.size(Paths.get("users.csv")) == 0)
            {
            writer.write("CardNumber,Pin,Name,Balance\n");
            }

            // Write user data to the CSV file
            writer.write(user.getCardNumber() + "," +
            user.getPin() + "," +
            user.getName() + "," +
            user.getBalance() + "\n");
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    public User getUser(String cardNumber) 
    {
        return users.get(cardNumber);
    }
}

class Login
{
    private JFrame frame;
    private JTextField cardNumberField;
    private JPasswordField pinField;
    private ATM atm;

    public Login(ATM atm) 
    {
        this.atm = atm;

        frame = new JFrame();
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel cardLabel = new JLabel("Card Number:");
        cardLabel.setBounds(50, 50, 100, 20);
        frame.add(cardLabel);

        cardNumberField = new JTextField();
        cardNumberField.setBounds(160, 50, 150, 20);
        frame.add(cardNumberField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(50, 100, 100, 20);
        frame.add(pinLabel);

        pinField = new JPasswordField();
        pinField.setBounds(160, 100, 150, 20);
        frame.add(pinField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(160, 150, 80, 30);
        frame.add(loginButton);

        loginButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                String cardNumber = cardNumberField.getText();
                String pin = new String(pinField.getPassword());

                User user = atm.getUser(cardNumber);

                if (user != null && user.getPin().equals(pin)) 
                {
                    Home home = new Home(user);
                    home.display();
                    frame.dispose();
                } 
                else
                {
                    JOptionPane.showMessageDialog(null, "Invalid card number or PIN");
                }
            }
        });

        frame.setVisible(true);
    }
}
class Home 
{
    private JFrame frame;
    private User user;

    public Home(User user)
    {
        this.user = user;

        frame = new JFrame();
        frame.setSize(400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel nameLabel = new JLabel("Welcome, " + user.getName() + "!");
	  
        nameLabel.setBounds(50, 20, 300, 30);
        frame.add(nameLabel);

        JButton depositButton = new JButton("Deposit");
        depositButton.setBounds(50, 70, 100, 30);
        frame.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(200, 70, 100, 30);
        frame.add(withdrawButton);

        JButton balanceButton = new JButton("Check Balance");
        balanceButton.setBounds(50, 120, 250, 30);
        frame.add(balanceButton);

        JButton applyLoanButton = new JButton("Apply for Loan");
        applyLoanButton.setBounds(50, 170, 250, 30);
        frame.add(applyLoanButton);

        JButton viewLoansButton = new JButton("View Loans");
        viewLoansButton.setBounds(50, 220, 250, 30);
        frame.add(viewLoansButton);

	    JButton changePinButton = new JButton("Change PIN");
        changePinButton.setBounds(50, 270, 100, 30);
        frame.add(changePinButton);

        changePinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newPin = JOptionPane.showInputDialog("Enter new PIN:");
                user.changePin(newPin);
                JOptionPane.showMessageDialog(null, "PIN changed successfully.");
            }
        });	

        JButton withdrawWithDenominationButton = new JButton("Withdraw with Denomination");
        withdrawWithDenominationButton.setBounds(50, 300, 250, 30);
        frame.add(withdrawWithDenominationButton);
	
        JButton transactionHistoryButton = new JButton("Transaction History");
        transactionHistoryButton.setBounds(50, 400, 250, 30);
        frame.add(transactionHistoryButton);

        
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(200, 450, 100, 30);
        frame.add(exitButton);

        
        depositButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                String amountString = JOptionPane.showInputDialog("Enter deposit amount:");
                int amount = Integer.parseInt(amountString);
                user.deposit(amount);
                JOptionPane.showMessageDialog(null, "Deposit successful. New balance: " + user.getBalance());
            }
        });

        withdrawButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                String amountString = JOptionPane.showInputDialog("Enter withdrawal amount:");
                int amount = Integer.parseInt(amountString);
                if (user.withdraw(amount))
                {
                    JOptionPane.showMessageDialog(null, "Withdrawal successful. New balance: " + user.getBalance());
                } 
                else 
                {
                    JOptionPane.showMessageDialog(null, "Insufficient funds.");
                }
            }
        });

        balanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Your balance is: " + user.getBalance());
            }
        });

	applyLoanButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        JCheckBox educationLoanCheckbox = new JCheckBox("Education Loan(12.2% per annum) ");
        JCheckBox personalLoanCheckbox = new JCheckBox("Personal Loan(10.25% per annum)");
        JCheckBox goldLoanCheckbox = new JCheckBox("Gold Loan(9.75% per annum)");
        JCheckBox homeLoanCheckbox = new JCheckBox("Home Loan(8.25% per annum)");
        JCheckBox vehicleLoanCheckbox = new JCheckBox("Vehicle Loan(8.50% per annum)");

        JPanel panel = new JPanel();
        panel.add(educationLoanCheckbox);
        panel.add(personalLoanCheckbox);
        panel.add(goldLoanCheckbox);
        panel.add(homeLoanCheckbox);
        panel.add(vehicleLoanCheckbox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Select Loan Type", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedLoanType = "";
            int amount = 0;

            if (educationLoanCheckbox.isSelected()) {
                selectedLoanType = "Education Loan(12.2% per annum)";
            }
            if (personalLoanCheckbox.isSelected()) {
                selectedLoanType = "Personal Loan(10.25% per annum)";
            }
            if (goldLoanCheckbox.isSelected()) {
                selectedLoanType = "Gold Loan(9.75% per annum)";
            }
            if (homeLoanCheckbox.isSelected()) {
                selectedLoanType = "Home Loan(8.25% per annum)";
            }
            if (vehicleLoanCheckbox.isSelected()) {
                selectedLoanType = "Vehicle Loan(8.50% per annum)";
            }

            if (!selectedLoanType.isEmpty()) {
                String amountString = JOptionPane.showInputDialog("Enter loan amount for " + selectedLoanType + ":");
                if (amountString != null && !amountString.isEmpty()) {
                    try {
                        amount = Integer.parseInt(amountString);
                        double interestRate = 0; // Set an appropriate interest rate for the selected loan type

                        // Set interest rate based on the selected loan type
                        if (selectedLoanType.contains("Education Loan")) {
                            interestRate = 12.2;
                        } else if (selectedLoanType.contains("Personal Loan")) {
                            interestRate = 10.25;
                        } else if (selectedLoanType.contains("Gold Loan")) {
                            interestRate = 9.75;
                        } else if (selectedLoanType.contains("Home Loan")) {
                            interestRate = 8.25;
                        } else if (selectedLoanType.contains("Vehicle Loan")) {
                            interestRate = 8.50;
                        }

                        user.applyForLoan(selectedLoanType, amount, interestRate);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid loan amount.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a loan amount.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a loan type.");
            }
        }
    }
});


        viewLoansButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                user.viewLoans();
            }
        });

        withdrawWithDenominationButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JCheckBox tenCheckBox = new JCheckBox("Rs. 10");
        JCheckBox twentyCheckBox = new JCheckBox("Rs. 20");
        JCheckBox fiftyCheckBox = new JCheckBox("Rs. 50");
        JCheckBox hundredCheckBox = new JCheckBox("Rs. 100");
        JCheckBox twoHundredCheckBox = new JCheckBox("Rs. 200");
        JCheckBox fiveHundredCheckBox = new JCheckBox("Rs. 500");

        panel.add(tenCheckBox);
        panel.add(twentyCheckBox);
        panel.add(fiftyCheckBox);
        panel.add(hundredCheckBox);
        panel.add(twoHundredCheckBox);
        panel.add(fiveHundredCheckBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Select Denominations", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int withdrawalAmount = 0;

            if (tenCheckBox.isSelected()) {
                withdrawalAmount += 10;
            }
            if (twentyCheckBox.isSelected()) {
                withdrawalAmount += 20;
            }
            if (fiftyCheckBox.isSelected()) {
                withdrawalAmount += 50;
            }
            if (hundredCheckBox.isSelected()) {
                withdrawalAmount += 100;
            }
            if (twoHundredCheckBox.isSelected()) {
                withdrawalAmount += 200;
            }
            if (fiveHundredCheckBox.isSelected()) {
                withdrawalAmount += 500;
            }

            if (withdrawalAmount == 0) {
                JOptionPane.showMessageDialog(null, "Please select at least one denomination.");
            } else {
                String amountString = JOptionPane.showInputDialog("Enter the total amount to withdraw:");
                if (amountString != null && !amountString.isEmpty()) {
                    try {
                        int requestedAmount = Integer.parseInt(amountString);
                        if (requestedAmount == withdrawalAmount) {
                            // Withdrawal logic
                            if (user.withdrawWithDenomination(withdrawalAmount)) {
                                JOptionPane.showMessageDialog(null, "Total amount withdrawn: Rs. " + requestedAmount);
                                // Update GUI or perform other necessary actions after successful withdrawal
                            } else {
                                JOptionPane.showMessageDialog(null, "Insufficient funds or invalid denominations.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, " amount withdrawed with selected denominations.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid amount.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter the total withdrawal amount.");
                }
            }
        }
    }
});
    transactionHistoryButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        user.displayTransactionHistory();
    }
});
    exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExitPage exitPage = new ExitPage();
            }
        });   
}

    public void display() {
        frame.setVisible(true);
    }
}
class Register {
    private JFrame frame;
    private JTextField cardNumberField;
    private JPasswordField pinField;
    private JTextField nameField;
    private ATM atm;

    public Register(ATM atm) {
        this.atm = atm;

        frame = new JFrame();
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel cardLabel = new JLabel("Card Number:");
        cardLabel.setBounds(50, 50, 100, 20);
        frame.add(cardLabel);

        cardNumberField = new JTextField();
        cardNumberField.setBounds(160, 50, 150, 20);
        frame.add(cardNumberField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(50, 100, 100, 20);
        frame.add(pinLabel);

        pinField = new JPasswordField();
        pinField.setBounds(160, 100, 150, 20);
        frame.add(pinField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 150, 100, 20);
        frame.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(160, 150, 150, 20);
        frame.add(nameField);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(160, 200, 100, 30);
        frame.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cardNumber = cardNumberField.getText();
                String pin = new String(pinField.getPassword());
                String name = nameField.getText();

                if (!cardNumber.isEmpty() && !pin.isEmpty() && !name.isEmpty()) {
                    User user = new User(cardNumber, pin, name);
                    atm.addUser(user);

                    JOptionPane.showMessageDialog(null, "User registered successfully!");

                    Login login = new Login(atm);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                }
            }
        });

        frame.setVisible(true);
    }
}

class SuccessPage {
    private JFrame frame;

    public SuccessPage() {
        frame = new JFrame();
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel messageLabel = new JLabel("Thank you!");
        messageLabel.setBounds(150, 100, 100, 30);
        frame.add(messageLabel);

        frame.setVisible(true);
    }
}

class ExitPage {
    private JFrame frame;

    public ExitPage() {
        frame = new JFrame();
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel messageLabel = new JLabel("Are you sure you want to exit?");
        messageLabel.setBounds(100, 50, 200, 30);
        frame.add(messageLabel);

        JButton yesButton = new JButton("Yes");
        yesButton.setBounds(100, 100, 80, 30);
        frame.add(yesButton);

        JButton noButton = new JButton("No");
        noButton.setBounds(200, 100, 80, 30);
        frame.add(noButton);

        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); 
            }
        });
        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); 
            }
        });
        frame.setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        ATM atm = new ATM();
        User user1 = new User("1234567890123456", "1234", "John");
        User user2 = new User("9876543210987654", "5678", "Jane");
        atm.addUser(user1);
        atm.addUser(user2);
        Login login = new Login(atm);
        Register register = new Register(atm);
    }
}