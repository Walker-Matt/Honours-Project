package com.softwareeng.project.server.Controllers;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.softwareeng.project.server.Models.*;

public class HandController {
    final static Logger log = Logger.getLogger(HandController.class);

    DeckController deckCon = new DeckController();

    public HandController() {
    }

    DeckController currentDeck = new DeckController();

    public ArrayList<CardModel> generateHand(int x, DeckModel deck) {
        ArrayList<CardModel> playerHand = new ArrayList<CardModel>(x);

        for (int i = 0; i < x; i++) {
            CardModel cardModel = null;
            while (cardModel == null)
                cardModel = currentDeck.drawFromDeck(deck);
            playerHand.add(cardModel);
        }

        return playerHand;
    }

    public void removeClickCardFromPlayerHand(PlayerModel p, int cardId) {
        for (CardModel c : p.hand.playerHand) {
            if (c.cardId == cardId) {
                p.hand.playerHand.remove(c);
                break;
            }
        }
    }

    public CardModel drawACardAndShow(PlayerModel p, int currentPlayerId, DeckModel deck) {
        CardModel c = deckCon.drawFromDeck(deck);

        p.hand.playerHand.add(c);

        log.info(p.playerName + " drew " + c.cardName);

        return c;
    }

    public void addClickCardToSelectedHand(PlayerModel currentPlayer, ArrayList<Integer> listOfIds) {
        for (Integer idOfClickedCard : listOfIds) {
            for (CardModel adventureCard : currentPlayer.hand.playerHand) {
                if (adventureCard.cardId == idOfClickedCard) {
                    currentPlayer.hand.selectedCards.add(adventureCard);
                    currentPlayer.hand.playerHand.remove(adventureCard);
                    break;
                }
            }
        }
    }

    public void removeClickCardFromSelectedHand(PlayerModel currentPlayer, ArrayList<Integer> listOfIds) {
        for (Integer idOfClickedCard : listOfIds) {
            for (CardModel adventureCard : currentPlayer.hand.selectedCards) {
                if (adventureCard.cardId == idOfClickedCard) {
                    currentPlayer.hand.playerHand.add(adventureCard);
                    currentPlayer.hand.selectedCards.remove(adventureCard);
                    break;
                }
            }
        }
    }

    //removes card from hand to be discarded, needs to be tested***
    //TODO Clean up
    public void discardFromHand(PlayerModel player, CardModel card, AdventureDeckModel discardDeck) {
        for (int i = 0; i < player.hand.playerHand.size(); i++) {
            if (player.hand.playerHand.get(i) == card) {
                player.hand.playerHand.remove(i);
                discardDeck.getCards().add(card);
                break;
            } else {
                continue;
            }
        }
    }

    public void discardFromSelected(PlayerModel player, CardModel card, AdventureDeckModel discardDeck) {
        for (int i = 0; i < player.hand.selectedCards.size(); i++) {
            if (player.hand.selectedCards.get(i) == card) {
                player.hand.selectedCards.remove(i);
                discardDeck.getCards().add(card);
                break;
            } else {
                continue;
            }
        }
    }

}
