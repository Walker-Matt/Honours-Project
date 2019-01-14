package com.softwareeng.project.server.Models;

import java.util.ArrayList;

public class AdventureDeckModel implements DeckModel {
    public static final int ADV_DECK_SIZE = 125;
    public ArrayList<CardModel> cards;

    public AdventureDeckModel() {
        cards = new ArrayList<CardModel>(ADV_DECK_SIZE);
    }

    public ArrayList<CardModel> getCards() {
        return cards;
    }
}
