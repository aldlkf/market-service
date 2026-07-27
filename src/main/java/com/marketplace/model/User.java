package com.marketplace.model;

import java.math.BigDecimal;
import java.util.Objects;

public class User {

    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;

    public User(){
    }

    public User(Long id, String name, String email, BigDecimal balance){
        this.id = id;
        this.name = name;
        this.email = email;
        this.balance = balance;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public BigDecimal getBalance(){
        return balance;
    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }

    public String getFormattedBalance(){
        return String.format("%,.2f KZT", balance);
    }

    @Override
    public String toString(){
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + getFormattedBalance() +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if(this==o)return true;
        if (o== null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

}
