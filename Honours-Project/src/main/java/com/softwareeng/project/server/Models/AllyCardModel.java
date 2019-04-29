package com.softwareeng.project.server.Models;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class AllyCardModel extends BattleCardModel {
	final static Logger log = Logger.getLogger(AllyCardModel.class);
    public int cardBid;

    public AllyCardModel(String name) {
        super(name);

        cardBid = 0;
    }

    public AllyCardModel(String name, int points, int bids) {
        super(name, points);

        cardBid = bids;
    }

    public AllyCardModel activateSpecial(AllyCardModel card, String questName, ArrayList<CardModel> cardsInPlay) {
        AllyCardModel tempCard = new AllyCardModel(card.cardName);
        tempCard.battlePoints = card.battlePoints;
        tempCard.cardBid = card.cardBid;

        switch (cardName) {
            case "Sir Gawain":
                if (questName == "Test of the Green Knight") {
                	log.info("Sir Gawain special activated. Battlepoints increased to 30");
                    tempCard.battlePoints = 30;
                }
                break;
            case "King Pellinore":
                if (questName == "Search for the Questing Beast") {
                	log.info("King Pellinore special activated. Battlepoints increased to 10 and bid increased to 4");
                    tempCard.battlePoints = 10;
                    tempCard.cardBid = 4;
                }
                break;
            case "Sir Percival":
                if (questName == "Search for the Holy Grail") {
                	log.info("Sir Percival special activated. Battlepoints increased to 25");
                    tempCard.battlePoints = 25;
                }
                break;
            case "Sir Tristan":
                if (cardInPlay(cardsInPlay, "Queen Iseult")) {
                	log.info("Sir Tristan special activated. Battlepoints increased to 30");
                    tempCard.battlePoints = 30;
                }
                break;
            case "Queen Iseult":
                if (cardInPlay(cardsInPlay, "Sir Tristan")) {
                	log.info("Queen Iseult special activated. Bid increased to 4");
                    tempCard.cardBid = 4;
                } else {
                    tempCard.cardBid = 2;
                }
                break;
            case "Sir Lancelot":
                if (questName == "Defend the Queen's Honor") {
                	log.info("Sir Lancelot special activated. Battlepoints increased to 40");
                    tempCard.battlePoints = 40;
                }
                break;
            case "Merlin":
                //TODO
                break;
            default:
                break;
        }

        return tempCard;
    }

    //This function finds out if the card is in play  
    public boolean cardInPlay(ArrayList<CardModel> cardsInPlay, String cardName) {
        for (CardModel c : cardsInPlay) {
            if (c.cardName == cardName) {
                return true;
            }
        }
        return false;
    }
}
