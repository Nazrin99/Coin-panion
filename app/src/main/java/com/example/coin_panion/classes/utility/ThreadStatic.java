package com.example.coin_panion.classes.utility;

public class ThreadStatic {
    public static void run(Thread thread){
        while(thread.isAlive()){

        }
        thread.start();
        while(thread.isAlive()){

        }
        return;
    }
}
