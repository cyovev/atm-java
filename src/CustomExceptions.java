/////////////////////// ATM RELATED \\\\\\\\\\\\\\\\\\\\\\\\
class NotEnoughMoneyInATM extends Exception {
    NotEnoughMoneyInATM(String s){
        super(s);
    }
}

class InvalidRequestedAmount extends Exception {
    InvalidRequestedAmount(String s){
        super(s);
    }
}

class PrinterError extends Exception {
    PrinterError(String s){
        super(s);
    }
}

/////////////////////// CARD RELATED \\\\\\\\\\\\\\\\\\\\\\\\
class CardIsBlocked extends Exception {
    CardIsBlocked(String s){
        super(s);
    }
}

class CardNotInserted extends Exception {
    CardNotInserted(String s){
        super(s);
    }
}

class ExceededDailyLimit extends Exception {
    ExceededDailyLimit(String s){
        super(s);
    }
}

class ExceededTransactionLimit extends Exception {
    ExceededTransactionLimit(String s){
        super(s);
    }
}

class NotEnoughFunds extends Exception {
    NotEnoughFunds(String s){
        super(s);
    }
}

class SamePINOnChange extends Exception {
    SamePINOnChange(String s){
        super(s);
    }
}