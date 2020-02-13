import java.util.Scanner;

public class Card {
    static Scanner  sc = new Scanner(System.in);
    
    // holds how many instances of the class were initiated
    // used for the name of the card
    private static int counter = 0;
    private String name;
    
    // card statuses
    private boolean isBlocked  = false,
                    isInserted = false;
    
    // card PIN restrictions
    private String  PIN;
    private int     maxPINattempts   = 3,
                    wrongPINattempts = 0;
    
    // card financial restrictions
    private double  accountBalance;
    private int     maxWithdrawAmount;
    
    ///////////////////////////////////////////////////////
    // normally PIN should be read off the card,
    // and a connection to the bank is needed for the balance,
    // but in this case PIN is randomly generated,
    // and balance & per-transaction limit are passed as a parameter
    Card (double balance, int limit) {
        Card.counter++;
        this.name = "Card #" + Card.counter;
        generateRandPIN();
        this.accountBalance    = balance;
        this.maxWithdrawAmount = limit;
    }
    
    ///////////////////////////////////////////////////////
    // for simplicity's sake the PIN will not be hashed
    private void generateRandPIN() {
        int rand = (int) (Math.random() * 9999);
        this.PIN = String.format("%04d", rand);
    }
        
    ///////////////////////////////////////////////////////
    public String toString() {
        return ">>> " + this.name + "\n>>> PIN: "
               + this.PIN + "\n>>> Balance: "
               + String.format("%1.2f", this.getBalance())
               + "\n>>> Transaction limit: "+this.maxWithdrawAmount;  
    }
    
    ///////////////////////////////////////////////////////
    public double getBalance() {
        return this.accountBalance;
    }
    
    ///////////////////////////////////////////////////////
    // simulate insertion of the card
    // if it's marked as blocked, throw an exception
    public void insertCard() throws CardIsBlocked {
        this.isInserted = true;
        
        if (this.isCardBlocked()) {
            blockCard();
        }
    }
    
    ///////////////////////////////////////////////////////
    public boolean isCardBlocked() {
        return this.isBlocked;
    }
    
    ///////////////////////////////////////////////////////
    // mark the card as blocked and throw an exception
    private void blockCard() throws CardIsBlocked {
        this.isBlocked = true;
        throw new CardIsBlocked(">>> Your card is blocked. Please contact your bank!");
    }
    
    ///////////////////////////////////////////////////////
    // verifies PIN:
    // returns true on match
    // returns false on card block or premature exit
    public boolean verifyPIN() throws CardNotInserted, CardIsBlocked {
        checkIfCardIsInserted();
        
        String enteredPIN;
        
        while (this.wrongPINattempts < this.maxPINattempts) {
            do {
                System.out.print("<<< Please enter your PIN code - " + (this.maxPINattempts - this.wrongPINattempts) + " attempt(s) left (0 to exit): ");
                enteredPIN = sc.nextLine().toString();
            }
            while (!enteredPIN.matches("^[0-9]{4}|0$"));
            
            // if the user entered 0, don't verify the PIN
            if (enteredPIN.equals("0")) {
                return false;
            }

            // if the PIN was incorrect, increment the counter
            else if (!enteredPIN.equals(this.PIN)) {
                System.out.println(">>> Wrong PIN code.");
                this.wrongPINattempts++;
            }
            
            else {
                this.wrongPINattempts = 0;
                return true;
            }
        }
        
        // on correct PIN or 0 the while cycle gets broken by a return statement
        // otherwise, the user has maxed out their guesses -> block the card
        this.blockCard();
        
        return false;
    }
    
    ///////////////////////////////////////////////////////
    // to check daily daily limit,
    // transactions need to be stored in a DB
    public void checkDailyLimit() throws ExceededDailyLimit {
        // basic idea is to sum the withdrawn amount for the current date
        // and if it exceeds the daily limit, an exception is being thrown:
        
        // throw new ExceededDailyLimit(">>> You have exceeded your daily limit.");
    }
    
    ///////////////////////////////////////////////////////
    // each card has its own transaction limit
    // which cannot be surpassed in a single withdrawal
    public void checkTransactionLimit(int requestedAmount) throws ExceededTransactionLimit {
        if (requestedAmount > this.maxWithdrawAmount) {
            throw new ExceededTransactionLimit(">>> You cannot withdraw more than " + this.maxWithdrawAmount + "HRK");
        }
    }
    
    ///////////////////////////////////////////////////////
    public void withdrawFromBalance(int requestedAmount) throws NotEnoughFunds, CardNotInserted {
        if (requestedAmount > this.accountBalance) {
            throw new NotEnoughFunds(">>> You don't have enough funds on your balance.");
        }
        
        this.accountBalance -= requestedAmount;
    }
    
    ///////////////////////////////////////////////////////
    public boolean changePIN() throws CardNotInserted, SamePINOnChange {
        checkIfCardIsInserted(); // makes sure the card is inserted
        String newPIN;
        
        // if the user enters anything different than 4 digits or 0,
        // ask them to reenter their new PIN
        do {
            System.out.print("<<< Enter new PIN (4 digits, 0 to exit): ");
            newPIN = sc.nextLine().toString();
        }
        while (!newPIN.matches("^[0-9]{4}|0$"));
        
        // if the user entered 0, the change was not successful
        if ("0".equals(newPIN)) {
            return false;
        }

        // if new pin same as old
        else if (newPIN.equals(this.PIN)) {
            throw new SamePINOnChange(">>> New PIN cannot be the same as old PIN.");
        }
        
        // otherwise change the PIN, notify the user and return true
        else {
            this.PIN = newPIN;
            return true;
        }
    }
    
    ///////////////////////////////////////////////////////
    private void checkIfCardIsInserted() throws CardNotInserted {
        if (!this.isInserted) {
            throw new CardNotInserted(">>> Please insert card");
        }
    }
    
    ///////////////////////////////////////////////////////
    public void ejectCard() throws CardNotInserted {
        checkIfCardIsInserted();
        this.isInserted = false;
    }

}
