package com.example.coin_panion.classes.utility;

public class ThreadStatic {
    public static void run(Thread thread){
        thread.start();
        while(thread.isAlive()){
        }
    }
}
