import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class StartingPoint {
    
    static Scanner sc          = new Scanner(System.in);
    static int     cardsCount  = 3;
    static Map<Integer, Integer> banknotes;
    
    ///////////////////////////////////////////////////////
    public static void main(String[] args) {
        ATM bankomat = initializeATM();
        Card[] cards = initializeSomeCards();
        
        // keep coming back to the welcome screen
        // when the user is done
        while (true) {
            welcomeScreen(bankomat, cards);
        }
    }
     
    ///////////////////////////////////////////////////////
    // generate an ATM with the amount of bills in it:
    // (banknote, count)
    private static ATM initializeATM() {
        return new ATM(new TreeMap<Integer, Integer>(Collections.reverseOrder()) {{
            put(200, 400);
            put(100, 600);
            put(50, 800);
            put(20, 1000);
            put(10, 2000);
        }});
    }
    
    ///////////////////////////////////////////////////////
    // generate some cards with different balances
    private static Card[] initializeSomeCards() {
        Card[] cards = new Card[cardsCount];
        
        cards[0] = new Card(724.65, 400);
        cards[1] = new Card(215.24, 200);
        cards[2] = new Card(12, 200);
        
        return cards;
    }
    
    ///////////////////////////////////////////////////////
    // ask the user to insert their card 
    private static void welcomeScreen(ATM bankomat, Card[] cards) {
        int whichCard;
        String input;
        
        try {
            do {
                System.out.print("\n<<< Please pick which card to insert from 1 to " + cardsCount + ": ");
                input     = sc.nextLine().toString();
                whichCard = Integer.parseInt(input);
            }
            while (!input.matches("^[0-9]+$") || (whichCard == 0) || (whichCard > cardsCount));
            
            Card selectedCard = cards[whichCard-1]; // -1 since card array starts at 0
            
            // if the PIN was correct, show actions
            // otherwise block or exit -> eject card
            if (insertCardAndCheckPIN(selectedCard)) {
                chooseAction(bankomat, selectedCard);
            }
            else {
                ejectCardAction(selectedCard);
            }
        }
        
        // for any error occurring,
        // print the message if it's card related
        catch (Exception e) {
            if (e instanceof CardNotInserted) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    ///////////////////////////////////////////////////////
    // on card insertion show PIN and balance (if card is not blocked)
    // if it is or gets blocked (maxed out attempts),
    // show error message
    private static boolean insertCardAndCheckPIN(Card selectedCard) throws CardNotInserted {
        boolean verified = false;
        if (!selectedCard.isCardBlocked()) {
            System.out.println(selectedCard.toString());
        }
        
        try {
            selectedCard.insertCard();
            verified = selectedCard.verifyPIN();
        }
        catch (CardIsBlocked e) {
            System.out.println(e.getMessage());
        }
        
        return verified;
    }
    
    ///////////////////////////////////////////////////////
    private static void chooseAction(ATM bankomat, Card card) {
        String input;
        int action;
        
        try {
            do {
                System.out.println("Choose action:");
                System.out.println("Withdraw money (1)");
                System.out.println("Check balance  (2)");
                System.out.println("Change PIN     (3)");
                System.out.println("- - - - - - - - -");
                System.out.println("Exit           (0)");
            
                input  = sc.nextLine().toString();
                action = Integer.parseInt(input);
            }
            while (action < 0 || action > 3);
            
            switch (action) {
                case 1:
                    withdrawalAction(bankomat, card);
                    break;
                    
                case 2:
                    printBalanceAction(bankomat, card);
                    break;
                    
                case 3:
                    changePINAction(bankomat, card);
                    break;
                    
                case 0:
                    ejectCardAction(card);
                    break;
            }
        }
        
        // if the exception was thrown by the parseInt (NumberFormatException)
        // print Invalid number message; otherwise print the thrown error message
        // afterwards, ask the user to choose an action again
        catch (Exception e) {
            String errorMsg = (e instanceof NumberFormatException) ? ">>> Invalid number." : e.getMessage();
            System.out.println(errorMsg);

            chooseAction(bankomat, card);
        }

    }
    
    ///////////////////////////////////////////////////////
    private static void withdrawalAction(ATM atm, Card card) throws CardNotInserted {
        BufferedReader input = new BufferedReader( new InputStreamReader(System.in) );
        Map<Integer, Integer> cash;
        
        try {
            System.out.print("<<< Enter amount (0 to exit): ");
            int requestedAmount = Integer.parseInt(input.readLine());
            
            // if the user has entered 0, eject the card
            if (requestedAmount == 0) {
                ejectCardAction(card);
            }
            
            // otherwise proceed with the withdrawal
            else {
                // check that amount is > 0 and % to min amount
                atm.checkAmountValidity(requestedAmount);
                
                // make sure the ATM has sufficient funds
                cash = atm.prepareCashForDisposal(requestedAmount);
                
                // make sure the user has not exceeded their daily withdrawal limit
                card.checkDailyLimit();
                
                // make sure the user is not surpassing their withdrawal limit
                card.checkTransactionLimit(requestedAmount);
                
                // make sure the user has sufficient funds
                card.withdrawFromBalance(requestedAmount);
                
                // refresh the amount of bills in the ATM
                atm.removeBillsFromATM(cash);
                
                System.out.println(">>> Take your money: " + cash);
                
                // print balance and eject the card
                printBalanceAction(atm, card);
            }
        }
        
        catch (Exception e) {
            String errorMsg = (e instanceof NumberFormatException || e instanceof IOException)
                              ? ">>> Invalid number."
                              : e.getMessage();
            
            System.out.println(errorMsg);
            
            // if there's not enough money in the ATM, or daily limit is exceeded
            // DON'T ask the user to enter amount again, eject card instead
            if (e instanceof NotEnoughMoneyInATM || e instanceof ExceededDailyLimit) {
                ejectCardAction(card);
            }
            
            // otherwise repeat withdrawal steps
            else {
                withdrawalAction(atm, card);
            }
        }
    }
    
    ///////////////////////////////////////////////////////
    private static void printBalanceAction(ATM atm, Card card) throws CardNotInserted {
        // try to print a receipt if there's paper and ink in the printer
        try {
            if (atm.isPrinterInOrder()) {
                double balance = card.getBalance();
                System.out.println(String.format(">>> Current balance: %1.2f", balance));
            }
        }

        // if not, notify the user
        catch (PrinterError e) {
            System.out.println(e.getMessage());
        }

        // in the end, eject the card
        finally {
            ejectCardAction(card);
        }
    }
    
    ///////////////////////////////////////////////////////
    // if the PIN change was successful,
    // allow to user to choose another action
    // otherwise eject the card
    private static void changePINAction(ATM atm, Card card) throws CardNotInserted {
        try {
            if (card.changePIN()) {
                System.out.println(">>> Your PIN was updated successfully!");
                chooseAction(atm, card);
            }
            else {
                ejectCardAction(card);
            }
        }
        catch (SamePINOnChange e) {
            System.out.println(e.getMessage());
            changePINAction(atm, card);
        }
    }
    
    ///////////////////////////////////////////////////////
    private static void ejectCardAction(Card card) throws CardNotInserted {
        card.ejectCard();
        System.out.println(">>> Card is ejected.");
    }

}
