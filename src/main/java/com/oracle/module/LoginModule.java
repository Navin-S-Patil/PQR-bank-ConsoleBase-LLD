package com.oracle.module;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.Entity.Transaction;
import com.oracle.controller.CustomerController;
import com.oracle.controller.AccountController;

import java.sql.SQLOutput;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LoginModule {

    public static void Login(Scanner scanner, CustomerController customerController, AccountController accountController) {
        boolean running = true;

        while (running) {
            System.out.println("\nLogin Menu");
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Admin");
            System.out.println("3. Go Back to previous menu");
            System.out.print("Please choose an option: ");

            int loginChoice;
            try {
                loginChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (loginChoice) {
                    case 1:
                        customerLogin(scanner, customerController, accountController);
                        break;
                    case 2:
                        System.out.println("Admin login not implemented yet.");
                        break;
                    case 3:
                        System.out.println("Going back to previous menu...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }

    private static void customerLogin(Scanner scanner, CustomerController customerController, AccountController accountController) {
        System.out.print("Enter Registered Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Optional<Customer> customerOptional = customerController.customerLogin(email, password);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            System.out.println("Logged in Successfully, " + customer.getFirstName() + "!");
            customerServices(scanner, customer, accountController);
        } else {
            System.out.println("Invalid Email or Password");
        }
    }

    private static void customerServices(Scanner scanner, Customer customer, AccountController accountController) {
        boolean servicesRunning = true;

        while (servicesRunning) {
            System.out.println("\nCustomer Services");
            System.out.println("1. View Balance & Account Number");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");
            System.out.print("Please choose a service: ");

            int serviceChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (serviceChoice) {
                case 1:
                    viewBalance(customer, accountController);
                    break;
                case 2:
                    deposit(scanner, customer, accountController);
                    break;
                case 3:
                    withdraw(scanner, customer, accountController);
                    break;
                case 4:
                    transfer(scanner, customer, accountController);
                    break;
                case 5:
                    viewTransactionHistory(scanner, customer, accountController);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    servicesRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewBalance(Customer customer, AccountController accountController) {
        Optional<List<Account>> accountsOptional = accountController.getBalance(customer);
        if (accountsOptional.isPresent()) {
            List<Account> accounts = accountsOptional.get();
            if (accounts.isEmpty()) {
                System.out.println("No accounts found for this customer.");
            } else {
                System.out.println("Account balances for " + customer.getFirstName() + " " + customer.getLastName() + ":");
                for (Account account : accounts) {
                    System.out.printf("Account No: %s, Type: %s, Balance: $%.2f%n",
                            account.getAccountNo(),
                            account.getAccountType(),
                            account.getBalance());
                }
            }
        } else {
            System.out.println("Unable to retrieve account information. Please try again later.");
        }
    }

    private static void deposit(Scanner scanner, Customer customer, AccountController accountController) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            try {
                System.out.println("\nSelect the Account to be Deposited: ");

                Optional<List<Account>> accountsOptional = accountController.getAccounts(customer);

                if (accountsOptional.isPresent() && !accountsOptional.get().isEmpty()) {
                    List<Account> accounts = accountsOptional.get();
                    System.out.println("Your accounts:");
                    for (int i = 0; i < accounts.size(); i++) {
                        Account account = accounts.get(i);
                        System.out.printf("%d. Account No: %s, Type: %s, Current Balance: $%.2f%n",
                                i + 1, account.getAccountNo(), account.getAccountType(), account.getBalance());
                    }

                    System.out.print("Enter the number of the account you want to deposit to: ");
                    int accountChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (accountChoice > 0 && accountChoice <= accounts.size()) {
                        Account selectedAccount = accounts.get(accountChoice - 1);

                        System.out.print("Enter the amount to deposit: $");
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        if (amount > 0) {
                            System.out.print("Enter a description for this deposit: ");
                            String description = scanner.nextLine();

                            boolean success = accountController.deposit(selectedAccount.getAccountNo(), amount, description);

                            if (success) {
                                System.out.printf("Successfully deposited $%.2f to account %s%n", amount, selectedAccount.getAccountNo());
                                return; // Exit the method on successful deposit
                            } else {
                                System.out.println("Deposit failed. Please try again.");
                            }
                        } else {
                            System.out.println("Invalid amount. Please enter a positive number.");
                        }
                    } else {
                        System.out.println("\nInvalid account selection.");
                    }
                } else {
                    System.out.println("No accounts found for this customer.");
                    return; // Exit the method if no accounts found
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }

            attempts++;
            if (attempts < MAX_ATTEMPTS) {
                System.out.println("You have " + (MAX_ATTEMPTS - attempts) + " attempt(s) left.");
            }
        }

        System.out.println("Maximum attempts reached. Returning to the main menu.");
    }

    private static void withdraw(Scanner scanner, Customer customer, AccountController accountController) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            try {
                System.out.println("Select the Account to withdraw from: ");

                Optional<List<Account>> accountsOptional = accountController.getAccounts(customer);

                if (accountsOptional.isPresent() && !accountsOptional.get().isEmpty()) {
                    List<Account> accounts = accountsOptional.get();
                    System.out.println("Your accounts:");
                    for (int i = 0; i < accounts.size(); i++) {
                        Account account = accounts.get(i);
                        System.out.printf("%d. Account No: %s, Type: %s, Current Balance: $%.2f%n",
                                i + 1, account.getAccountNo(), account.getAccountType(), account.getBalance());
                    }

                    System.out.print("Enter the number of the account you want to withdraw from: ");
                    int accountChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (accountChoice > 0 && accountChoice <= accounts.size()) {
                        Account selectedAccount = accounts.get(accountChoice - 1);

                        System.out.print("Enter the amount to withdraw: $");
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        if (amount > 0) {
                            System.out.print("Enter a description for this withdrawal: ");
                            String description = scanner.nextLine();

//                            if(selectedAccount.getBalance() < amount){
//                                System.out.println("Insufficient funds");
//                                System.out.println("You don't have sufficient funds");
//                                return; // Exit the method on insufficient funds
//                            }
                            boolean success = accountController.withdraw(selectedAccount.getAccountNo(), amount, description);

                            if (success) {
                                System.out.printf("Successfully withdrew $%.2f from account %s%n", amount, selectedAccount.getAccountNo());
                                return; // Exit the method on successful withdrawal
                            } else {
                                System.out.println("Withdrawal failed. Please try again.");
                                break;
                            }
                        } else {
                            System.out.println("Invalid amount. Please enter a positive number.");
                        }
                    } else {
                        System.out.println("Invalid account selection.");
                    }
                } else {
                    System.out.println("No accounts found for this customer.");
                    return; // Exit the method if no accounts found
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }

            attempts++;
            if (attempts < MAX_ATTEMPTS) {
                System.out.println("You have " + (MAX_ATTEMPTS - attempts) + " attempt(s) left.");
            }
        }

        System.out.println("Maximum attempts reached. Returning to the main menu.");
    }

    private static void transfer(Scanner scanner, Customer customer, AccountController accountController) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            try {
                System.out.println("Select the Account to transfer from: ");

                Optional<List<Account>> accountsOptional = accountController.getAccounts(customer);

                if (accountsOptional.isPresent() && !accountsOptional.get().isEmpty()) {
                    List<Account> accounts = accountsOptional.get();
                    System.out.println("Your accounts:");
                    for (int i = 0; i < accounts.size(); i++) {
                        Account account = accounts.get(i);
                        System.out.printf("%d. Account No: %s, Type: %s, Current Balance: $%.2f%n",
                                i + 1, account.getAccountNo(), account.getAccountType(), account.getBalance());
                    }

                    System.out.print("Enter the number of the account you want to transfer from: ");
                    int accountChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (accountChoice > 0 && accountChoice <= accounts.size()) {
                        Account selectedAccount = accounts.get(accountChoice - 1);

                        System.out.print("Enter the beneficiary's account number: ");
                        String beneficiaryAccountNumber = scanner.nextLine();

                        System.out.print("Enter the beneficiary's account type (SAVINGS / CHECKING): ");
                        String beneficiaryAccountType = scanner.nextLine();

                        System.out.print("Enter the amount to transfer: $");
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        if (amount > 0) {
                            System.out.print("Enter a description for this transfer: ");
                            String description = scanner.nextLine();

                            boolean success = accountController.transfer(selectedAccount.getAccountNo(), amount, beneficiaryAccountNumber, beneficiaryAccountType, description);

                            if (success) {
                                System.out.printf("Successfully transferred $%.2f from account %s to account %s%n",
                                        amount, selectedAccount.getAccountNo(), beneficiaryAccountNumber);
                                return; // Exit the method on successful transfer
                            } else {
                                System.out.println("Transfer failed. Please try again.");
                            }
                        } else {
                            System.out.println("Invalid amount. Please enter a positive number.");
                        }
                    } else {
                        System.out.println("Invalid account selection.");
                    }
                } else {
                    System.out.println("No accounts found for this customer.");
                    return; // Exit the method if no accounts found
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }

            attempts++;
            if (attempts < MAX_ATTEMPTS) {
                System.out.println("You have " + (MAX_ATTEMPTS - attempts) + " attempt(s) left.");
            }
        }

        System.out.println("Maximum attempts reached. Returning to the main menu.");
    }

    private static void viewTransactionHistory(Scanner scanner, Customer customer, AccountController accountController) {
        // Implement view transaction history logic
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            try {
                System.out.println("Select the Account to See the Transactions from: ");

                Optional<List<Account>> accountsOptional = accountController.getAccounts(customer);

                if (accountsOptional.isPresent() && !accountsOptional.get().isEmpty()) {
                    List<Account> accounts = accountsOptional.get();
                    System.out.println("Your accounts:");
                    for (int i = 0; i < accounts.size(); i++) {
                        Account account = accounts.get(i);
                        System.out.printf("%d. Account No: %s, Type: %s, Current Balance: $%.2f%n",
                                i + 1, account.getAccountNo(), account.getAccountType(), account.getBalance());
                    }

                    System.out.print("Enter the number of the account you want to see the Transaction History from: ");
                    int accountChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (accountChoice > 0 && accountChoice <= accounts.size()) {
                        Account selectedAccount = accounts.get(accountChoice - 1);

                        List<Transaction> success = accountController.getTransactionHistory(selectedAccount.getAccountNo());

                        if (!success.isEmpty()) {
                            System.out.println("\nTransaction History for Account No: " + selectedAccount.getAccountNo());
                            System.out.println("--------------------------------------------------------------------------------------------------------");
                            System.out.printf("%-10s | %-23s | %-15s | %-35s | %-10s%n",
                                    "Trans ID", "Date", "Amount", "Description", "Type");
                            System.out.println("--------------------------------------------------------------------------------------------------------");

                            for (Transaction transaction : success) {
                                System.out.printf("%-10d | %-23s | $%-14.2f | %-35s | %-10s%n",
                                        transaction.getId(),
                                        transaction.getTransactionDate().toString(),
                                        transaction.getAmount(),
                                        truncateString(transaction.getDescription(), 35),
                                        transaction.getTransactionType());
                            }
                            System.out.println("--------------------------------------------------------------------------------------------------------");
                            return; // Exit the method after displaying transactions
                        } else {
                            System.out.println("No transactions found for this account.");
                            break;
                        }

                    } else {
                        System.out.println("Invalid account selection.");
                    }
                } else {
                    System.out.println("No accounts found for this customer.");
                    return; // Exit the method if no accounts found
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }

            attempts++;
            if (attempts < MAX_ATTEMPTS) {
                System.out.println("You have " + (MAX_ATTEMPTS - attempts) + " attempt(s) left.");
            }
        }
        System.out.println("Maximum attempts reached. Returning to the main menu.");
    }

    private static String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}