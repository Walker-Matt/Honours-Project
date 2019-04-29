package com.softwareeng.project.server.Models;

public class QuestCardModel extends CardModel {
    public int numOfStages;
    public String foeCard;

    public QuestCardModel(String name, int stages, String foe) {
        super(name);

        numOfStages = stages;
        foeCard = foe;
    }

    public QuestCardModel(String name, int stages) {
        super(name);

        numOfStages = stages;
        foeCard = "";
    }

}
