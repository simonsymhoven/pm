package main;

import javafx.application.Application;


public class StartPoint {
    public static void main(String[] args) {
       System.setProperty("javafx.preloader", FirstPreloader.class.getCanonicalName());
       Application.launch(Main.class, args);
    }
}
