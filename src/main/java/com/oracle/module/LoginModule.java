package com.oracle.module;

import com.oracle.Entity.Customer;
import com.oracle.controller.CustomerController;

import java.util.Optional;
import java.util.Scanner;

public class LoginModule {

    public static void Login(Scanner scanner, CustomerController customerController){
        boolean running = true;

        while(running) {
            System.out.println();
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Admin");
            System.out.println("3. To go Back to previous menu");
            System.out.print("Please choose an option: ");

            int loginChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (loginChoice) {
                case 1:
                    //Customer-login
                    System.out.println("Enter Registered Email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enter Password: ");
                    String password = scanner.nextLine();
                    Customer customer = null;
                    Optional<Customer> customerOptional = customerController.customerLogin(email,password);
                    if (customerOptional.isPresent()) {
                        System.out.println("Logged in Successfully " + customerOptional.get().getFirstName());
                    } else {
                        System.out.println("Invalid Email or Password");
                    }
                    break;

                case 2:
                    //Admin-login
                    break;

                case 3:
                    System.out.println("Going back to previous menu...");
                    System.out.println("...");
                    System.out.println();
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

    }
}
