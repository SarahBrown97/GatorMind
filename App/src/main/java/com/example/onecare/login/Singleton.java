package com.example.onecare.login;

public class Singleton {
    private String username;
    private static final Singleton instance = new Singleton();
    public static Singleton getInstance(){
     return instance;
    }
    private Singleton(){
    }
    public void setUsername(String username){
        this.username= username;
    }
    public String getUsername(){
        return username;
    }
}
