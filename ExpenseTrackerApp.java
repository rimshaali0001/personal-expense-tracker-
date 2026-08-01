import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.Scanner;
import java.util.InputMismatchException;

public class ExpenseTrackerApp {
    private static Scanner input = new Scanner(System.in);
    private static MongoDBConnection dbConnection;
    private static MongoCollection<Document> transactions;

    public static void main(String[] args) {
        try {
            dbConnection = new MongoDBConnection();
            transactions = dbConnection.getCollection();
            System.out.println("Personal Expense Tracker - Java + MongoDB");

            int choice;
            do {
                showMenu();
                choice = readInt("Enter your choice: ");

                switch (choice) {
                    case 1 -> addTransaction();
                    case 2 -> viewAllTransactions();
                    case 3 -> searchByCategory();
                    case 4 -> showBalanceSummary();
                    case 5 -> deleteByDescription();
                    case 0 -> System.out.println("Program closed.");
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } while (choice != 0);

            dbConnection.close();
        } catch (Exception e) {
            System.out.println("MongoDB connection failed.");
            System.out.println("Please make sure MongoDB is installed and running on localhost:27017.");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showMenu() {
        System.out.println("\n----- MENU -----");
        System.out.println("1. Add Transaction");
        System.out.println("2. View All Transactions");
        System.out.println("3. Search by Category");
        System.out.println("4. Balance Summary");
        System.out.println("5. Delete by Description");
        System.out.println("0. Exit");
    }

    // Builds a Transaction object first, then converts it into a MongoDB
    // Document so the two classes actually work together instead of
    // the app writing raw Documents directly.
    private static void addTransaction() {
        System.out.print("Type (Income/Expense): ");
        String type = input.nextLine();
        System.out.print("Category (Food, Travel, Salary, etc.): ");
        String category = input.nextLine();
        double amount = readDouble("Amount: ");
        System.out.print("Date (YYYY-MM-DD): ");
        String date = input.nextLine();
        System.out.print("Description: ");
        String description = input.nextLine();

        Transaction transaction = new Transaction(type, category, amount, date, description);

        Document doc = new Document("type", transaction.getType())
                .append("category", transaction.getCategory())
                .append("amount", transaction.getAmount())
                .append("date", transaction.getDate())
                .append("description", transaction.getDescription());

        transactions.insertOne(doc);
        System.out.println("Transaction saved successfully.");
    }

    private static void viewAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        for (Document doc : transactions.find()) {
            printDocument(doc);
        }
    }

    private static void searchByCategory() {
        System.out.print("Enter category to search: ");
        String category = input.nextLine();
        System.out.println("\n--- Search Results ---");
        for (Document doc : transactions.find(Filters.eq("category", category))) {
            printDocument(doc);
        }
    }

    private static void showBalanceSummary() {
        double income = 0;
        double expense = 0;

        for (Document doc : transactions.find()) {
            double amount = doc.getDouble("amount");
            String type = doc.getString("type");
            if (type.equalsIgnoreCase("Income")) {
                income += amount;
            } else if (type.equalsIgnoreCase("Expense")) {
                expense += amount;
            }
        }

        System.out.println("\n--- Balance Summary ---");
        System.out.println("Total Income : " + income);
        System.out.println("Total Expense: " + expense);
        System.out.println("Balance      : " + (income - expense));
    }

    private static void deleteByDescription() {
        System.out.print("Enter description to delete: ");
        String description = input.nextLine();
        transactions.deleteOne(Filters.eq("description", description));
        System.out.println("If matching record existed, it has been deleted.");
    }

    private static void printDocument(Document doc) {
        System.out.println("Type: " + doc.getString("type")
                + " | Category: " + doc.getString("category")
                + " | Amount: " + doc.getDouble("amount")
                + " | Date: " + doc.getString("date")
                + " | Description: " + doc.getString("description"));
    }

    // Loops until a valid integer is entered instead of crashing the
    // program when the user accidentally types letters or symbols.
    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                int value = input.nextInt();
                input.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                input.nextLine();
            }
        }
    }

    // Same idea as readInt() but for decimal amounts, since Scanner
    // throws an exception instead of returning a default value.
    private static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = input.nextDouble();
                input.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid amount.");
                input.nextLine();
            }
        }
    }
}