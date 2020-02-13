import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;

public class ATM {
    private Map<Integer, Integer> banknotes = new TreeMap<Integer, Integer>();
    private int minimumAmount;
    
    ///////////////////////////////////////////////////////
    // Constructor: set amounts for all bills, set minimum amount
    // and print amount of money in the ATM
    public ATM(Map<Integer, Integer> banknotes) {
        this.banknotes     = banknotes;
        this.minimumAmount = this.setMinimumAmount();
        System.out.println("<<<>>> Money in the ATM: " + this.banknotes + "<<<>>>");
    }
    
    ///////////////////////////////////////////////////////
    // smallest bill value in the ATM
    // since bills are stored in descending order,
    // that would be the last element of the map
    public int setMinimumAmount() {
        return ((TreeMap<Integer, Integer>) this.banknotes).lastKey();
    }
    
    ///////////////////////////////////////////////////////
    public int getMinimumAmount() {
        return this.minimumAmount;
    }
    
    ///////////////////////////////////////////////////////
    public boolean checkAmountValidity(int number) throws InvalidRequestedAmount {
        int     smallestBill         = this.getMinimumAmount();
        boolean isGreaterThanZero    = (boolean) (number > 0);
        boolean divisableByMinAmount = (boolean) (number%smallestBill == 0);
        
        if (!isGreaterThanZero || !divisableByMinAmount) {
            throw new InvalidRequestedAmount(">>> Requested amount should be divisable by " + smallestBill);
        }
        
        return true;
    }
    
    ///////////////////////////////////////////////////////
    /* 
     * Calculate how many notes to withdraw for each
     * banknote type in the ATM (200, 100, 50, etc.)
     *
     * @param requestedAmount: how much money the user has requested
     * @return Map with notes and their amounts
     * @throws NotEnoughMoneyInATM
     */
    public Map<Integer, Integer> prepareCashForDisposal(int requestedAmount) throws NotEnoughMoneyInATM {
        // result map with bills which the user should receive
        Map<Integer, Integer> result = new TreeMap<Integer, Integer>(Collections.reverseOrder());

        // how much money the user requested; should be 0 by the end of the cycle
        int amountLeft = requestedAmount;

        // cycle through all note values in the ATM
        for (Map.Entry<Integer, Integer> entry : this.banknotes.entrySet()) {
            
            int billValue  = entry.getKey();
            int billAmount = entry.getValue();
            
            if (billAmount > 0) {
                
                // how many notes of the current value
                // fit inside the amount that's left (eg. 320/100 = 3)
                int billsPerValue = (int) Math.floor(amountLeft/billValue);
                
                if (billsPerValue > 0) {
                    // if billsPerValue is equal to 2, but there's only 1 note left,
                    // the user should get only 1 note – hence the min function
                    int noteAmount = Math.min(billsPerValue, billAmount);
                    
                    result.put(billValue, noteAmount);
                    
                    // update the amount of money the user expects
                    amountLeft -= noteAmount * billValue;
                    
                    // if the amount is already zero, no need to cycle through the other banknotes
                    if (amountLeft == 0) {
                        break;
                    }
                }
            }
        }
        
        // if at the end of the cycle the amount of money is not zero,
        // this means there was not enough money in the ATM
        if (amountLeft > 0) {
            throw new NotEnoughMoneyInATM(">>> Insufficient funds in ATM. Try again later.");
        }
        
        return result;
    }
    
    ///////////////////////////////////////////////////////
    // after cash disposal lower the total amount of notes
    // for each banknote value of the map passed as parameter
    public void removeBillsFromATM(Map<Integer, Integer> notes) {
        for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
            int billValue       = entry.getKey();
            int amountInAtm     = this.banknotes.get(billValue);
            int withdrawnAmount = entry.getValue();
            
             this.banknotes.put(billValue, (amountInAtm - withdrawnAmount));
        }
    }

    ///////////////////////////////////////////////////////
    // to check whether there's printer in ink in the printer
    public boolean isPrinterInOrder() throws PrinterError {
        boolean error = false;
        // TODO: call method to check for errors

        if (error) {        
            throw new PrinterError(">>> There's an error with the printer.");
        }

        return true;
    }
}