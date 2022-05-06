package bank;

public class Session {

    private String sessionKey;
    private Bank bank;
    private boolean valid;
    private int transLimit = 3;
    private int numTransaction = 0;

    Session(String sessionKey, Bank bank){
        this.sessionKey = sessionKey;
        this.bank = bank;
        valid = true;
    }

    public boolean deposit(int amount) {
        if(!valid) return false;
        if(numTransaction < transLimit){
            boolean result = bank.deposit(sessionKey, amount);
            if(++numTransaction >= transLimit){
                SessionManager.expireSession(this);
            }
            return result;
        } else return false;
    }

    public boolean withdraw(int amount) {
        if(!valid) return false;
        if(numTransaction < transLimit){
            boolean result = bank.withdraw(sessionKey, amount);
            if(++numTransaction >= transLimit){
                SessionManager.expireSession(this);
            }
            return result;
        } else return false;
    }

    public boolean transfer(String targetId, int amount) {
        if(!valid) return false;
        if(numTransaction < transLimit){
            boolean result = bank.transfer(sessionKey, targetId, amount);
            if(++numTransaction >= transLimit){
                SessionManager.expireSession(this);
            }
            return result;
        } else return false;
    }

    public void setValid(boolean bool){
        valid = bool;
    }
}
