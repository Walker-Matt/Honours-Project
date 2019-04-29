package com.softwareeng.project.server.Models;

import java.util.concurrent.ThreadLocalRandom;

public class CardModel {
    public String cardName;
    public String imageUrl;
    public int cardId;

    public CardModel(String name, int id) {
        cardName = name;
        cardId = id;
        //ensure that corresponding images are saved using the exact name with a _ replacing every space
        //ex. "Green Knight" -> "Green_Knight.jpg"
        imageUrl = convertNameToJPG(name);
    }

    public CardModel(String n) {
        this(n, ThreadLocalRandom.current().nextInt(1, 100000 + 1));
    }

    private String convertNameToJPG(String name) {
        name = name.trim();

        return name.replace(" ", "_") + ".JPG";
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || !(o instanceof CardModel)) {
            return false;
        }
        CardModel c = (CardModel) o;
        return this.cardName.equals(c.cardName);
    }
}
