package com.oracle;

import com.oracle.Entity.Customer;
import com.oracle.controller.AccountController;
import com.oracle.controller.CustomerController;
import com.oracle.module.LoginModule;
import com.oracle.services.impl.AccountServiceImpl;
import com.oracle.services.impl.CustomerServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        CustomerServiceImpl customerService = new CustomerServiceImpl();
        AccountServiceImpl accountService = new AccountServiceImpl();
        AccountController accountController = new AccountController(accountService, customerService);
        CustomerController customerController = new CustomerController(customerService);

        while (running) {
            System.out.println("\nWelcome to the Application");
            System.out.println("1. For new Customers");
            System.out.println("2. To Create new Account");
            System.out.println("3. Login");
            System.out.println("4. Exit");
            System.out.print("Please choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerNewCustomer(scanner, customerController);
                    break;
                case 2:
                    createNewAccount(scanner, accountController);
                    break;
                case 3:
                    LoginModule.Login(scanner, customerController, accountController);
                    break;
                case 4:
                    System.out.println("Exiting the application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    private static void registerNewCustomer(Scanner scanner, CustomerController customerController) {
        System.out.println("You selected new Customers");
        System.out.println("Please enter all your details");

        System.out.print("First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Address: ");
        String address = scanner.nextLine();

        Date dob = null;
        while (dob == null) {
            System.out.print("Date of Birth (DDMMYYYY): ");
            String dateOfBirth = scanner.nextLine();

            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
            dateFormat.setLenient(false);
            try {
                dob = dateFormat.parse(dateOfBirth);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter the date in DDMMYYYY format.");
            }
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        customerController.createCustomer(firstName, lastName, email, phone, address, dob, password);
    }

    private static void createNewAccount(Scanner scanner, AccountController accountController) {
        System.out.println("You selected Create New Account");
        System.out.print("Enter customer ID: ");
        Long customerId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter account type (SAVINGS/CHECKING): ");
        String accountType = scanner.nextLine().toUpperCase();

        if (!accountType.equals("SAVINGS") && !accountType.equals("CHECKING")) {
            System.out.println("Invalid account type");
            return;
        }

        accountController.createAccount(customerId, accountType, password);
    }
}