package bank;

import security.key.BankPublicKey;
import security.key.BankSymmetricKey;
import security.*;

public class MobileApp {

    private String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }
    private final String AppId = randomUniqueStringGen();
    public String getAppId() {
        return AppId;
    }

    String id, password;
    public MobileApp(String id, String password){
        this.id = id;
        this.password = password;
    }

    BankSymmetricKey symKey;
    public Encrypted<BankSymmetricKey> sendSymKey(BankPublicKey publickey){
        symKey = new BankSymmetricKey(randomUniqueStringGen());
        return new Encrypted<>(symKey, publickey);
    }

    public Encrypted<Message> deposit(int amount){
        Message message = new Message("deposit", id, password, amount);
        return new Encrypted<>(message, symKey);
    }

    public Encrypted<Message> withdraw(int amount){
        Message message = new Message("withdraw", id, password, amount);
        return new Encrypted<>(message, symKey);
    }

    public boolean processResponse(Encrypted<Boolean> obj) {
        if(obj == null) return false;
        Boolean response = obj.decrypt(symKey);
        if(response != null){
            return response;
        } else return false;
    }
}

