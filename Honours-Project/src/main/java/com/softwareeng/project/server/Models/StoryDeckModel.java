package com.softwareeng.project.server.Models;

import java.util.ArrayList;

public class StoryDeckModel implements DeckModel {
    public static final int STORY_DECK_SIZE = 28;
    public ArrayList<CardModel> cards;

    public StoryDeckModel() {
        cards = new ArrayList<CardModel>(STORY_DECK_SIZE);
    }

    public ArrayList<CardModel> getCards() {
        return cards;
    }
}
