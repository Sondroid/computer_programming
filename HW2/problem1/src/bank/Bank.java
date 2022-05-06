package bank;

import bank.event.*;
import security.*;
import security.key.*;

public class Bank {
    private int numAccounts = 0;
    final static int maxAccounts = 100;
    private BankAccount[] accounts = new BankAccount[maxAccounts];
    private String[] ids = new String[maxAccounts];

    public void createAccount(String id, String password) {
        createAccount(id, password, 0);
    }

    public void createAccount(String id, String password, int initBalance) {
        if(id2Idx(id) == -1){
            accounts[numAccounts] = new BankAccount(id, password, initBalance);
            ids[numAccounts++] = id;
        }
    }

    private int id2Idx(String id){
        for(int i=0; i < numAccounts; i++){
            if(ids[i].equals(id)) return i;
        }
        return -1;
    }

    public boolean deposit(String id, String password, int amount) {
        int idx = id2Idx(id);
        if(idx == -1) return false;

        if(accounts[idx].authenticate(password)){
            accounts[idx].deposit(amount);
            return true;
        }
        else return false;
    }

    public boolean withdraw(String id, String password, int amount) {
        int idx = id2Idx(id);
        if(idx == -1) return false;

        if(accounts[idx].authenticate(password)){
            return accounts[idx].withdraw(amount);
        }
        else return false;
    }

    public boolean transfer(String sourceId, String password, String targetId, int amount) {
        int sourceIdx = id2Idx(sourceId);
        if(sourceIdx == -1) return false;
        int targetIdx = id2Idx(targetId);
        if(targetIdx == -1) return false;

        if(accounts[sourceIdx].authenticate(password)){
            if(accounts[sourceIdx].send(amount)){
                accounts[targetIdx].receive(amount);
                return true;
            }
        }
        return false;
    }

    public Event[] getEvents(String id, String password) {
        int idx = id2Idx(id);
        if(idx == -1) return null;

        if(accounts[idx].authenticate(password)){
            Event[] event = accounts[idx].getEvents();
            Event[] removeNullEvent = new Event[accounts[idx].getEventsCount()];
            for(int i=0; i< accounts[idx].getEventsCount(); i++){
                removeNullEvent[i] = event[i];
            }
            return removeNullEvent;
        }
        else return null;
    }

    public int getBalance(String id, String password) {
        int idx = id2Idx(id);
        if(idx == -1) return -1;
        if(accounts[idx].authenticate(password)){
            return accounts[idx].getBalance();
        }
        else return -1;
    }

    private static String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }
    private BankAccount find(String id) {
        for (int i = 0; i < numAccounts; i++) {
            if(ids[i].equals(id)){return accounts[i];};
        }
        return null;
    }
    final static int maxSessionKey = 100;
    int numSessionKey = 0;
    String[] sessionKeyArr = new String[maxSessionKey];
    BankAccount[] bankAccountmap = new BankAccount[maxSessionKey];
    String generateSessionKey(String id, String password){
        BankAccount account = find(id);
        if(account == null || !account.authenticate(password)){
            return null;
        }
        String sessionkey = randomUniqueStringGen();
        sessionKeyArr[numSessionKey] = sessionkey;
        bankAccountmap[numSessionKey] = account;
        numSessionKey += 1;
        return sessionkey;
    }
    BankAccount getAccount(String sessionkey){
        for(int i = 0 ;i < numSessionKey; i++){
            if(sessionKeyArr[i] != null && sessionKeyArr[i].equals(sessionkey)){
                return bankAccountmap[i];
            }
        }
        return null;
    }

    boolean deposit(String sessionkey, int amount) {
        BankAccount account = getAccount(sessionkey);
        account.deposit(amount);
        return true;
    }

    boolean withdraw(String sessionkey, int amount) {
        BankAccount account = getAccount(sessionkey);
        return account.withdraw(amount);
    }

    boolean transfer(String sessionkey, String targetId, int amount) {
        BankAccount account = getAccount(sessionkey);
        int targetIdx = id2Idx(targetId);
        if(targetIdx == -1) return false;

        if(account.send(amount)){
            accounts[targetIdx].receive(amount);
            return true;
        } else return false;
    }

    private BankSecretKey secretKey;
    public BankPublicKey getPublicKey(){
        BankKeyPair keypair = Encryptor.publicKeyGen(); // generates two keys : BankPublicKey, BankSecretKey
        secretKey = keypair.deckey; // stores BankSecretKey internally
        return keypair.enckey;
    }

    int maxHandshakes = 10000;
    int numSymmetrickeys = 0;
    BankSymmetricKey[] bankSymmetricKeys = new BankSymmetricKey[maxHandshakes];
    String[] AppIds = new String[maxHandshakes];

    public int getAppIdIndex(String AppId){
        for(int i=0; i<numSymmetrickeys; i++){
            if(AppIds[i].equals(AppId)){
                return i;
            }
        }
        return -1;
    }

    public void fetchSymKey(Encrypted<BankSymmetricKey> encryptedKey, String AppId){
        if(encryptedKey == null) return;
        int idx = getAppIdIndex(AppId);
        if(idx == -1){
            bankSymmetricKeys[numSymmetrickeys] = encryptedKey.decrypt(secretKey);
            AppIds[numSymmetrickeys++] = AppId;
        } else {
            bankSymmetricKeys[idx] = encryptedKey.decrypt(secretKey);
        }

    }

    public Encrypted<Boolean> processRequest(Encrypted<Message> messageEnc, String AppId) {
        int idx = getAppIdIndex(AppId);
        if(idx == -1) return null;

        BankSymmetricKey symKey = bankSymmetricKeys[idx];
        Message message = messageEnc.decrypt(symKey);
        if(message == null){
            return null;
        } else {
            Boolean result;
            String request = message.getRequestType();
            if(request.equals("deposit")){
                result = deposit(message.getId(), message.getPassword(), message.getAmount());
            }
            else{
                result = withdraw(message.getId(), message.getPassword(), message.getAmount());
            }
            return new Encrypted<>(result, symKey);
        }
    }
}