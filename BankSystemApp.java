import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// ==========================================
// 1. ABSTRACT BASE CLASS (Abstraction & Encapsulation)
// ==========================================
abstract class Account {
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private List<String> transactionHistory;

    public Account(String accountNumber, String accountHolderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with initial balance: $" + initialDeposit);
    }

    // Getters and Setters (Encapsulation)
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposited: $" + amount + " | Current Balance: $" + balance);
            System.out.println(" Successfully deposited $" + amount);
        } else {
            System.out.println("❌ Invalid deposit amount.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Withdrew: $" + amount + " | Current Balance: $" + balance);
            System.out.println(" Successfully withdrew $" + amount);
            return true;
        } else {
            System.out.println("❌ Insufficient funds or invalid amount.");
            return false;
        }
    }

    protected void addTransaction(String detail) {
        transactionHistory.add(detail);
    }

    public void printTransactionHistory() {
        System.out.println("\n-------------------------------------------");
        System.out.println(" Transaction History for Acc #" + accountNumber + " (" + accountHolderName + ")");
        System.out.println("-------------------------------------------");
        for (String record : transactionHistory) {
            System.out.println(" • " + record);
        }
        System.out.println("-------------------------------------------");
    }

    // Abstract method for Polymorphism
    public abstract void applyMonthlyInterestOrFees();
}

// ==========================================
// 2. SUBCLASSES (Inheritance & Polymorphism)
// ==========================================
class SavingsAccount extends Account {
    private double annualInterestRate; // e.g., 0.04 for 4%

    public SavingsAccount(String accountNumber, String holderName, double initialDeposit, double annualInterestRate) {
        super(accountNumber, holderName, initialDeposit);
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    public void applyMonthlyInterestOrFees() {
        double monthlyInterest = (getBalance() * annualInterestRate) / 12;
        deposit(monthlyInterest);
        System.out.println(" Monthly interest of $" + monthlyInterest + " applied.");
    }
}

class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, String holderName, double initialDeposit, double overdraftLimit) {
        super(accountNumber, holderName, initialDeposit);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= (getBalance() + overdraftLimit)) {
            double currentBal = getBalance();
            setBalance(currentBal - amount);
            addTransaction("Withdrew: $" + amount + " (Used Overdraft) | Current Balance: $" + getBalance());
            System.out.println(" Successfully withdrew $" + amount + " using overdraft protection.");
            return true;
        } else {
            System.out.println("❌ Withdrawal failed. Amount exceeds overdraft limit of $" + overdraftLimit);
            return false;
        }
    }

    @Override
    public void applyMonthlyInterestOrFees() {
        double monthlyFee = 12.0;
        withdraw(monthlyFee);
        System.out.println(" Monthly account maintenance fee of $" + monthlyFee + " deducted.");
    }
}

// ==========================================
// 3. BANK MANAGER CLASS (Data Handling)
// ==========================================
class Bank {
    private Map<String, Account> accounts;

    public Bank() {
        accounts = new HashMap<>();
    }

    public void createAccount(Account account) {
        if (accounts.containsKey(account.getAccountNumber())) {
            System.out.println("❌ Account Number already exists!");
            return;
        }
        accounts.put(account.getAccountNumber(), account);
        System.out.println(" Account created successfully for " + account.getAccountHolderName());
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void transferFunds(String sourceAccNum, String targetAccNum, double amount) {
        Account source = getAccount(sourceAccNum);
        Account target = getAccount(targetAccNum);

        if (source == null || target == null) {
            System.out.println("❌ Invalid account number(s).");
            return;
        }

        if (source.withdraw(amount)) {
            target.deposit(amount);
            System.out.println(" Successfully transferred $" + amount + " from " + sourceAccNum + " to " + targetAccNum);
        } else {
            System.out.println("❌ Transfer failed due to insufficient balance.");
        }
    }
}

// ==========================================
// 4. MAIN DRIVER PROGRAM
// ==========================================
public class BankSystemApp {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("==============================================");
        System.out.println("   WELCOME TO JAVA OOP BANK MANAGEMENT SYSTEM ");
        System.out.println("==============================================");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Create Savings Account");
            System.out.println("2. Create Checking Account");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Transfer Money");
            System.out.println("6. View Transaction History");
            System.out.println("7. Apply Monthly Interest / Maintenance Fee");
            System.out.println("8. Exit");
            System.out.print("Select an option (1-8): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Account Number: ");
                    String accNum = scanner.nextLine();
                    System.out.print("Enter Account Holder Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Initial Deposit: $");
                    double deposit = scanner.nextDouble();
                    bank.createAccount(new SavingsAccount(accNum, name, deposit, 0.04)); // 4% Annual Interest Rate
                    break;

                case 2:
                    System.out.print("Enter Account Number: ");
                    accNum = scanner.nextLine();
                    System.out.print("Enter Account Holder Name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter Initial Deposit: $");
                    deposit = scanner.nextDouble();
                    System.out.print("Enter Overdraft Limit: $");
                    double overdraft = scanner.nextDouble();
                    bank.createAccount(new CheckingAccount(accNum, name, deposit, overdraft));
                    break;

                case 3:
                    System.out.print("Enter Account Number: ");
                    accNum = scanner.nextLine();
                    Account acc = bank.getAccount(accNum);
                    if (acc != null) {
                        System.out.print("Enter Deposit Amount: $");
                        double amount = scanner.nextDouble();
                        acc.deposit(amount);
                    } else {
                        System.out.println("❌ Account not found!");
                    }
                    break;

                case 4:
                    System.out.print("Enter Account Number: ");
                    accNum = scanner.nextLine();
                    acc = bank.getAccount(accNum);
                    if (acc != null) {
                        System.out.print("Enter Withdrawal Amount: $");
                        double amount = scanner.nextDouble();
                        acc.withdraw(amount);
                    } else {
                        System.out.println("❌ Account not found!");
                    }
                    break;

                case 5:
                    System.out.print("Enter Source Account Number: ");
                    String srcAcc = scanner.nextLine();
                    System.out.print("Enter Target Account Number: ");
                    String destAcc = scanner.nextLine();
                    System.out.print("Enter Transfer Amount: $");
                    double amount = scanner.nextDouble();
                    bank.transferFunds(srcAcc, destAcc, amount);
                    break;

                case 6:
                    System.out.print("Enter Account Number: ");
                    accNum = scanner.nextLine();
                    acc = bank.getAccount(accNum);
                    if (acc != null) {
                        acc.printTransactionHistory();
                    } else {
                        System.out.println("❌ Account not found!");
                    }
                    break;

                case 7:
                    System.out.print("Enter Account Number: ");
                    accNum = scanner.nextLine();
                    acc = bank.getAccount(accNum);
                    if (acc != null) {
                        acc.applyMonthlyInterestOrFees();
                    } else {
                        System.out.println("❌ Account not found!");
                    }
                    break;

                case 8:
                    running = false;
                    System.out.println("\nThank you for using the Java Bank System!");
                    break;

                default:
                    System.out.println("❌ Invalid selection. Please choose an option between 1 and 8.");
            }
        }
        scanner.close();
    }
}