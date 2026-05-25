package quickchatmessaging;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class QuickChatMessaging {

    static Scanner input = new Scanner(System.in);

    // User Details
    static String firstName;
    static String lastName;
    static String username;
    static String cellNumber;
    static String password;

    // Store Messages
    static ArrayList<Message> sentMessages = new ArrayList<>();

    // JSON File
    static final String FILE_NAME = "messages.json";

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("      QUICKCHAT APPLICATION");
        System.out.println("=================================");

        // Register User
        registerUser();

        // Login User
        loginUser();

        // Load Saved Messages
        loadMessages();

        // Open QuickChat Menu
        quickChatMenu();

        input.close();
    }

    // =========================================
    // GET NON EMPTY INPUT
    // =========================================

    public static String getNonEmptyInput(String message) {

        String value;

        while (true) {

            System.out.print(message);

            value = input.nextLine().trim();

            if (!value.isEmpty()) {

                return value;

            } else {

                System.out.println("Input cannot be empty!");
            }
        }
    }

    // =========================================
    // REGISTER USER
    // =========================================

    public static void registerUser() {

        System.out.println("\n========== REGISTRATION ==========");

        firstName = getNonEmptyInput("Enter First Name: ");

        lastName = getNonEmptyInput("Enter Last Name: ");

        // Username Validation
        while (true) {

            username = getNonEmptyInput(
                    "Create Username (must contain '_' and be max 5 characters): ");

            if (checkUsername(username)) {

                System.out.println("Username successfully captured.");

                break;

            } else {

                System.out.println(
                        "Invalid username! "
                      + "Username must contain '_' "
                      + "and be no more than 5 characters.");
            }
        }

        // Cell Number Validation
        while (true) {

            cellNumber = getNonEmptyInput(
                    "Enter SA Cell Number (e.g. +27821234567): ");

            if (isValidSACell(cellNumber)) {

                System.out.println("Cell phone number successfully captured.");

                break;

            } else {

                System.out.println(
                        "Invalid South African cell number!");
            }
        }

        // Password Validation
        while (true) {

            String pass1 =
                    getNonEmptyInput("Create Password: ");

            String pass2 =
                    getNonEmptyInput("Confirm Password: ");

            if (!pass1.equals(pass2)) {

                System.out.println("Passwords do not match!");

                continue;
            }

            if (isValidPassword(pass1)) {

                password = pass1;

                System.out.println("Password successfully captured.");

                break;

            } else {

                System.out.println(
                        "Password must contain:\n"
                      + "- At least 8 characters\n"
                      + "- A capital letter\n"
                      + "- A number\n"
                      + "- A special character");
            }
        }

        System.out.println("\nRegistration completed successfully!");
    }

    // =========================================
    // LOGIN USER
    // =========================================

    public static void loginUser() {

        System.out.println("\n============= LOGIN =============");

        int attempts = 0;

        boolean loggedIn = false;

        while (attempts < 5) {

            String loginUser =
                    getNonEmptyInput("Enter Username: ");

            String loginPass =
                    getNonEmptyInput("Enter Password: ");

            if (loginUser.equals(username)
                    && loginPass.equals(password)) {

                loggedIn = true;

                System.out.println(
                        "\nWelcome "
                      + firstName
                      + " "
                      + lastName
                      + ", it is great to see you again.");

                break;

            } else {

                attempts++;

                System.out.println(
                        "Incorrect username or password!");

                if (attempts < 5) {

                    System.out.println(
                            "Attempts remaining: "
                          + (5 - attempts));
                }
            }
        }

        if (!loggedIn) {

            System.out.println(
                    "Too many failed login attempts.");

            System.exit(0);
        }
    }

    // =========================================
    // QUICKCHAT MENU
    // =========================================

    public static void quickChatMenu() {

        int choice;

        do {

            System.out.println("\n============= MENU =============");
            System.out.println("1. Send Messages");
            System.out.println("2. View Sent Messages");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            while (!input.hasNextInt()) {

                System.out.println("Please enter a valid number!");
                input.next();
            }

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {

                case 1:

                    sendMessages();

                    break;

                case 2:

                    displayMessages();

                    break;

                case 3:

                    saveMessages();

                    System.out.println(
                            "Messages saved successfully.");

                    System.out.println(
                            "Thank you for using QuickChat!");

                    break;

                default:

                    System.out.println(
                            "Invalid menu option!");
            }

        } while (choice != 3);
    }

    // =========================================
    // SEND MESSAGES
    // =========================================

    public static void sendMessages() {

        System.out.print(
                "How many messages would you like to send? ");

        while (!input.hasNextInt()) {

            System.out.println("Enter a valid number!");
            input.next();
        }

        int total = input.nextInt();

        input.nextLine();

        for (int i = 0; i < total; i++) {

            System.out.println(
                    "\n========== MESSAGE "
                  + (i + 1)
                  + " ==========");

            String recipient;

            // Validate Recipient
            while (true) {

                recipient =
                        getNonEmptyInput(
                                "Enter recipient number: ");

                Message temp =
                        new Message(recipient, "");

                String result =
                        temp.checkRecipientCell();

                System.out.println(result);

                if (result.equals(
                        "Cell phone number successfully captured.")) {

                    break;
                }
            }

            // Validate Message
            Message messageObj;

            while (true) {

                String msg =
                        getNonEmptyInput(
                                "Enter message: ");

                messageObj =
                        new Message(recipient, msg);

                String validation =
                        messageObj.checkMessageLength();

                System.out.println(validation);

                if (validation.equals(
                        "Message ready to send.")) {

                    break;
                }
            }

            // Create Message Hash
            messageObj.createMessageHash();

            // Message Options
            System.out.println("\n1. Send Message");
            System.out.println("2. Disregard Message");
            System.out.println("3. Store Message");
            System.out.print("Choose option: ");

            while (!input.hasNextInt()) {

                System.out.println("Please enter a valid option!");
                input.next();
            }

            int option = input.nextInt();

            input.nextLine();

            String result =
                    messageObj.sentMessage(option);

            System.out.println(result);

            // Display Message Details
            if (option == 1 || option == 3) {

                System.out.println(
                        "\n======= MESSAGE DETAILS =======");

                System.out.println(
                        "Message ID: "
                      + messageObj.messageID);

                System.out.println(
                        "Message Hash: "
                      + messageObj.messageHash);

                System.out.println(
                        "Recipient: "
                      + messageObj.recipient);

                System.out.println(
                        "Message: "
                      + messageObj.message);
            }
        }

        System.out.println(
                "\nTotal Messages Sent: "
              + sentMessages.size());
    }

    // =========================================
    // DISPLAY SENT MESSAGES
    // =========================================

    public static void displayMessages() {

        if (sentMessages.isEmpty()) {

            System.out.println(
                    "\nNo messages sent yet.");

            return;
        }

        System.out.println(
                "\n======= SENT MESSAGES =======");

        for (int i = 0; i < sentMessages.size(); i++) {

            System.out.println(
                    "\nMessage "
                  + (i + 1));

            System.out.println(
                    sentMessages.get(i).printMessages());
        }
    }

    // =========================================
    // SAVE MESSAGES
    // =========================================

    public static void saveMessages() {

        try {

            StringBuilder json =
                    new StringBuilder("[");

            for (int i = 0; i < sentMessages.size(); i++) {

                Message m =
                        sentMessages.get(i);

                json.append("{");

                json.append("\"messageID\":\"")
                        .append(m.messageID)
                        .append("\",");

                json.append("\"recipient\":\"")
                        .append(m.recipient)
                        .append("\",");

                json.append("\"message\":\"")
                        .append(m.message.replace("\"", "\\\""))
                        .append("\",");

                json.append("\"messageHash\":\"")
                        .append(m.messageHash)
                        .append("\"");

                json.append("}");

                if (i < sentMessages.size() - 1) {

                    json.append(",");
                }
            }

            json.append("]");

            Files.write(
                    Paths.get(FILE_NAME),
                    json.toString().getBytes());

        } catch (IOException e) {

            System.out.println(
                    "Error saving messages: "
                  + e.getMessage());
        }
    }

    // =========================================
    // LOAD MESSAGES
    // =========================================

    public static void loadMessages() {

        try {

            if (!Files.exists(Paths.get(FILE_NAME))) {

                return;
            }

            String content =
                    new String(
                            Files.readAllBytes(
                                    Paths.get(FILE_NAME)));

            content = content.trim();

            if (content.equals("[]")) {

                return;
            }

            content =
                    content.substring(
                            1,
                            content.length() - 1);

            String[] objects =
                    content.split("\\},\\{");

            for (String obj : objects) {

                obj = obj.replace("{", "")
                        .replace("}", "");

                String[] parts =
                        obj.split("\",\"");

                String id =
                        parts[0].split(":\"")[1]
                                .replace("\"", "");

                String recipient =
                        parts[1].split(":\"")[1]
                                .replace("\"", "");

                String message =
                        parts[2].split(":\"")[1]
                                .replace("\"", "")
                                .replace("\\\"", "\"");

                String hash =
                        parts[3].split(":\"")[1]
                                .replace("\"", "");

                Message m =
                        new Message(recipient, message);

                m.messageID = id;
                m.messageHash = hash;

                sentMessages.add(m);
            }

            System.out.println(
                    "\nLoaded "
                  + sentMessages.size()
                  + " saved messages.");

        } catch (Exception e) {

            System.out.println(
                    "No previous messages found.");
        }
    }

    // =========================================
    // USERNAME VALIDATION
    // =========================================

    public static boolean checkUsername(String username) {

        return username.contains("_")
                && username.length() <= 5;
    }

    // =========================================
    // SA CELL VALIDATION
    // =========================================

    public static boolean isValidSACell(String number) {

        String regex =
                "^(\\+27|0)[6-8][0-9]{8}$";

        return Pattern.matches(regex, number);
    }

    // =========================================
    // PASSWORD VALIDATION
    // =========================================

    public static boolean isValidPassword(String pass) {

        String regex =
                "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        return Pattern.matches(regex, pass);
    }
}

// =========================================
// MESSAGE CLASS
// =========================================

class Message {

    String messageID;
    String recipient;
    String message;
    String messageHash;

    public Message(String recipient, String message) {

        this.recipient = recipient;
        this.message = message;
        this.messageID = generateMessageID();
    }

    // Generate 10 Digit Message ID
    public String generateMessageID() {

        Random rand = new Random();

        long id =
                1000000000L
              + (long)
                (rand.nextDouble()
                 * 9000000000L);

        return String.valueOf(id);
    }

    // Validate Message Length
    public String checkMessageLength() {

        if (message.length() <= 250) {

            return "Message ready to send.";

        } else {

            return
                    "Message exceeds 250 characters.";
        }
    }

    // Validate Recipient Number
    public String checkRecipientCell() {

        if (recipient.matches("^\\+\\d{10,12}$")) {

            return
                    "Cell phone number successfully captured.";

        } else {

            return
                    "Cell phone number incorrectly formatted.";
        }
    }

    // Create Message Hash
    public void createMessageHash() {

        String[] words =
                message.trim().split("\\s+");

        String firstWord =
                words[0].toUpperCase();

        String lastWord =
                words[words.length - 1]
                        .toUpperCase();

        String idPart =
                messageID.substring(0, 2);

        int messageNumber =
                QuickChatMessaging.sentMessages.size() + 1;

        this.messageHash =
                idPart
              + ":"
              + messageNumber
              + ":"
              + firstWord
              + lastWord;
    }

    // Send / Store / Disregard Message
    public String sentMessage(int option) {

        switch (option) {

            case 1:

                QuickChatMessaging.sentMessages.add(this);

                return "Message successfully sent.";

            case 2:

                return "Message disregarded.";

            case 3:

                QuickChatMessaging.sentMessages.add(this);

                return "Message successfully stored.";

            default:

                return "Invalid option selected.";
        }
    }

    // Validate Message ID
    public boolean checkMessageID() {

        return messageID.length() == 10;
    }

    // Print Message
    public String printMessages() {

        return
                "Message ID: " + messageID
              + "\nMessage Hash: " + messageHash
              + "\nRecipient: " + recipient
              + "\nMessage: " + message;
    }

    // Return Total Messages
    public int returnTotalMessages() {

        return QuickChatMessaging.sentMessages.size();
    }
}