package com.lxz.reveno.model;

public class AccountView {
    public final long id;
    public final String name;
    public final long balance;

    public AccountView(long id, String name, long balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "AccountView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}