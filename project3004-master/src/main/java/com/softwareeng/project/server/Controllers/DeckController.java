package com.softwareeng.project.server.Controllers;

import java.util.Collections;

import org.apache.log4j.Logger;

import com.softwareeng.project.server.Models.*;

public class DeckController {
    final static Logger log = Logger.getLogger(DeckController.class);

    public DeckController() {

    }

    //draws the last card in the deck
    public CardModel drawFromDeck(DeckModel deck) {
        return deck.getCards().remove(deck.getCards().size() - 1);
    }

    public CardModel drawSpecificCardFromDeck(DeckModel deck, String cardName) {
        for (int i = deck.getCards().size() - 1; i >= 0; i--) {
            if (deck.getCards().get(i).cardName == cardName) {
                return deck.getCards().remove(i);
            }
        }
        return drawFromDeck(deck);
    }

    //shuffles the deck before the run of the game
    public void shuffle(DeckModel deck) {
        //twice for good measure
        Collections.shuffle(deck.getCards());
        Collections.shuffle(deck.getCards());
    }

    //adds a specific card multiple times
    public void addCard(DeckModel deck, CardModel card, int frequency) {
        for (int i = 0; i < frequency; i++) {
            deck.getCards().add(card);

            //log.info(card.cardName + " added");
        }
    }

    public void discardCard(CardModel card, DeckModel discardDeck) {
        discardDeck.getCards().add(card);
    }
}
