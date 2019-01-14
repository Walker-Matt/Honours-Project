package com.softwareeng.project.server.Controllers;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.softwareeng.project.commands.ServerCommand;
import com.softwareeng.project.server.Models.*;
import com.softwareeng.project.server.ClientHandler;;

public class CardController {
    final static Logger log = Logger.getLogger(CardController.class);

    ArrayList<ClientHandler> clients;

    public CardController(ArrayList<ClientHandler> c) {
        clients = c;
    }

    public int getBattlePoints(String cardName, CardModel card, ArrayList<CardModel> cardsInPlay) {
        //TODO clean up later
        if (card instanceof AllyCardModel) {
            AllyCardModel ally = (AllyCardModel) card;
            AllyCardModel tempAlly = ally.activateSpecial(ally, cardName, cardsInPlay);
            return tempAlly.battlePoints;
        } else if (cardName.indexOf(card.cardName) == -1 && card.cardName != "Search for the Holy Grail") { //card name does not appear in the quest name
            if (card instanceof FoeCardModel) {
                FoeCardModel foe = (FoeCardModel) card;
                return foe.battlePoints;
            } else if (card instanceof BattleCardModel) {
                BattleCardModel battle = (BattleCardModel) card;
                return battle.battlePoints;
            }
        } else { //card name does appear in the quest name
            if (card instanceof FoeCardModel) {
                FoeCardModel foe = (FoeCardModel) card;

                for (ClientHandler cH : clients) {
                    cH.out.println(ServerCommand.SET_MESSAGE_BOX + " " + "BattlePoints for " + card.cardName + " increased to " + foe.upperBattlePoints);
                }


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return foe.upperBattlePoints;
            } else if (card instanceof BattleCardModel) {
                BattleCardModel battle = (BattleCardModel) card;
                return battle.battlePoints;
            }
        }
        return 0;
    }

}
