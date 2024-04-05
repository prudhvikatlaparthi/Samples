package com.pru.lib;

public class Person {
    private final String name;
    private final String game;

    Person(String name, String game) {
        this.name = name;
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public String getGame() {
        return game;
    }
}
