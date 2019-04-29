package com.softwareeng.project.server.Models;

public class TestCardModel extends CardModel {
    public int minimumBid;

    public TestCardModel(String name) {
        super(name);
        minimumBid = 0;
    }

    public TestCardModel(String name, int minimum) {
        super(name);
        minimumBid = minimum;
    }
}
