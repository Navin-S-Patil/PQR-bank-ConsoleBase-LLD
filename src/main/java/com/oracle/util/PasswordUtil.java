//package com.oracle.util;
//
//import org.mindrot.jbcrypt.BCrypt;
//
//public class PasswordUtil {
//
//    // Method to hash a password
//    public static String hashPassword(String plainPassword) {
//        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
//    }
//
//    // Method to check a plain password against a hashed password
//    public static boolean checkPassword(String plainPassword, String hashedPassword) {
//        return BCrypt.checkpw(plainPassword, hashedPassword);
//    }
//}
