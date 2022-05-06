package bank;

import bank.event.*;

class BankAccount {
    private Event[] events = new Event[maxEvents];
    final static int maxEvents = 100;
    private int eventsCount = 0;

    private String id;
    private String password;
    private int balance;


    BankAccount(String id, String password, int balance) {
        this.id = id;
        this.password = password;
        this.balance = balance;
    }


    boolean authenticate(String password) {
        return this.password.equals(password);
    }

    void deposit(int amount) {
        balance += amount;
        events[eventsCount++] = new DepositEvent();
    }

    boolean withdraw(int amount) {
        if(balance >= amount){
            balance -= amount;
            events[eventsCount++] = new WithdrawEvent();
            return true;
        }
        else return false;
    }

    void receive(int amount) {
        balance += amount;
        events[eventsCount++] = new ReceiveEvent();
    }

    boolean send(int amount) {
        if(balance >= amount){
            balance -= amount;
            events[eventsCount++] = new SendEvent();
            return true;
        }
        else return false;
    }

    protected Event[] getEvents(){
        return events;
    }

    protected int getBalance(){
        return balance;
    }

    public int getEventsCount() {
        return eventsCount;
    }
}