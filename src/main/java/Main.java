import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    private static DataBase dataBase;
    private static String currentAccount;
    private static boolean isLogged = false;

    public static void main(String[] args) {
        dataBase = new DataBase("card.s3db");
        start();
    }

    public static void start() {
        while (true) {
            printMenu();
            int input = scanner.nextInt();
            scanner.nextLine();

            if (input == 0) {
                exit();
                break;
            }

            if (isLogged) {
                if (input == 1) {
                    getBalance();
                } else if (input == 2) {
                    addIncome();
                } else if (input == 3) {
                    transfer();
                } else if (input == 4) {
                    deleteAccount();
                } else if (input == 5) {
                    logOut();
                }
            } else {
                if (input == 1) {
                    createCard();
                } else if (input == 2) {
                    logIn();
                }
            }
        }
        scanner.close();
    }

    public static void printMenu() {
        if (isLogged) {
            System.out.println("\n1. Balance" +
                    "\n2. Add income" +
                    "\n3. Do transfer" +
                    "\n4. Close account" +
                    "\n5. Log out" +
                    "\n0. Exit");
        } else {
            System.out.println("1. Create an account \n" +
                    "2. Log into account\n" +
                    "0. Exit");
        }
    }

    public static void getBalance() {
        System.out.println("\nBalance: " + dataBase.getBalance(currentAccount));
    }

    public static void addIncome() {
        System.out.println("Enter income:");
        int income = scanner.nextInt();
        dataBase.addIncome(currentAccount, income);
        System.out.println("Income was added!");
    }

    public static void transfer() {
        System.out.println("Enter card number:");
        String number = scanner.nextLine();
        if (currentAccount.equals(number)) {
            System.out.println("You can't transfer money to the same account!");
        } else {
            if (luhnAlgorithm(number) % 10 != 0) {
                System.out.println("Probably you made a mistake in the card number. Please try again!");
            } else {
                if (dataBase.isCardExist(number)) {
                    System.out.println("Enter how much money you want to transfer:");
                    int money = scanner.nextInt();
                    if (money > dataBase.getBalance(currentAccount)) {
                        System.out.println("Not enough money!");
                    } else {
                        dataBase.doTransfer(currentAccount, number, money);
                    }
                } else {
                    System.out.println("Such a card does not exist.");
                }
            }
        }
    }

    public static void deleteAccount() {
        dataBase.deleteCard(currentAccount);
        currentAccount = null;
        isLogged = false;
        System.out.println("The account has been closed!\n");
    }

    public static void logOut() {
        currentAccount = null;
        isLogged = false;
        System.out.println("\nYou have successfully logged out!\n");
    }

    public static void exit() {
        System.out.println("\nBye!");
        System.exit(0);
    }

    public static void createCard() {
        String cardNumber = createCardNumber();
        String pin = createPin();

        System.out.printf("\nYour card has been created" +
                "\nYour card number:" +
                "\n%s" +
                "\nYour card PIN:" +
                "\n%s" +
                "\n\n", cardNumber, pin);

        dataBase.addNewCardIntoDatabase(cardNumber, pin);
    }

    public static void logIn() {
        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        if (dataBase.isCorrectPin(cardNumber, pin)) {
            System.out.println("\nYou have successfully logged in!");
            currentAccount = cardNumber;
            isLogged = true;
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }

    public static String createCardNumber() {
        StringBuilder sb = new StringBuilder("400000");
        Random random = new Random();

        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }

        sb.append(luhnAlgorithm(sb.toString()));

        return sb.toString();
    }

    public static String createPin() {
        StringBuilder pin = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(10));
        }

        return pin.toString();
    }

    public static int luhnAlgorithm(String cardNumber) {
        int sum = 0;
        String[] arr = cardNumber.split("");

        for (int i = 0; i < cardNumber.length(); i++) {
            int n = Integer.parseInt(arr[i]);
            if (i % 2 == 0) {
                n *= 2;
                n = (n > 9) ? n - 9 : n;
            }
            sum += n;
        }

        return (sum % 10 == 0) ? 0 : 10 - sum % 10;
    }
}
