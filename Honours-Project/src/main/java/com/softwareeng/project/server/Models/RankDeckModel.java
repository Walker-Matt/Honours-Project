package com.softwareeng.project.server.Models;

import java.util.ArrayList;

public class RankDeckModel implements DeckModel {
    public static final int RANK_DECK_SIZE = 12;
    public ArrayList<CardModel> cards;

    public RankDeckModel() {
        cards = new ArrayList<CardModel>(RANK_DECK_SIZE);

    }

    public ArrayList<CardModel> getCards() {
        return cards;
    }
}
