package com.softwareeng.project.server.Controllers.strategy;

import com.softwareeng.project.server.Controllers.PlayerController;
import com.softwareeng.project.server.Models.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

public class Strategy1 extends Strategy{
	final static Logger log = Logger.getLogger(Strategy1.class);
	PlayerController playerCon = new PlayerController(null);
	
    public Strategy1(AIPlayerModel player) {
        super(player);
    }
    @Override
    public boolean doIParticipateInTournament(ArrayList<PlayerModel> players,TournamentCardModel card) {	
    	int shields = card.bonusShields + 1;
        //Bonus shields PLUS at least one player
    	for (PlayerModel player : players) {
            if (RankCardModel.shieldsRequiredForNextUpgrade(player.rank.cardName) - player.shields - shields <= 0) {
            	log.info("AIPlayer or another player can win/evolve by winning this tournament, there AIPlayer participates");
                return true;
            }
        }
    	log.info("AIPlayer or another player can NOT win/evolve by winning this tournament, the AIPlayer is NOT participates");
    	return false;
       
        //return g.rankUpCheck(null, ((TournamentCardModel) g.getCurrentStory()).bonusShields + 1);
    }
    @Override
    public ArrayList<CardModel> getCardsToPlayForTournament(HandModel hand,ArrayList<PlayerModel> players, TournamentCardModel tourCard) {

    	int shields = tourCard.bonusShields + 1;
    	
    	ArrayList<CardModel> chosen = new ArrayList<>();
    	
    	ArrayList<String> cardsPlayed = new ArrayList<>();
    	
        if (rankUpCheckAll(shields, players)) {
        	log.info("AIPlayer: another player can can win/evolve by winning or I can win and evolve myself, played strongest possible hand");
            for (CardModel cardInHand : hand.playerHand) {
                if (cardInHand instanceof AllyCardModel || cardInHand instanceof WeaponCardModel ) {
                	 if(!cardsPlayed.contains(cardInHand.cardName)) {
                		 chosen.add(cardInHand);
                	 }
                }  
            }
        } else {
        	log.info("AIPlayer or another player can NOT win/evolve by winning this tournament, plays only weapon cards");
            //!I play only weapons I have two or more instances of
            for (CardModel cardInHand : hand.playerHand) {
                if (cardInHand instanceof WeaponCardModel && !chosen.contains(cardInHand))
                    chosen.add(cardInHand);
            }
        }
        return chosen;
    }

    @Override
    public boolean doISponsorAQuest(HandModel hand, int numStages, ArrayList<PlayerModel> players,PlayerModel currPlayer) {
        
    // Check if someone can rank up
    	
    	for (PlayerModel player : players) {
    		if(player == currPlayer){
    			continue;
    		}
    		if (RankCardModel.shieldsRequiredForNextUpgrade(player.rank.cardName) - numStages - player.shields <= 0) {
    			log.info(player.playerName + " can rank up by winning this quest");
                return false;
            }
        }
    	
    	List<FoeCardModel> foes = hand.foes();
        //one less foe to worry about
        if (hand.getTest() != null) {
            numStages--;
        }

        if (foes.size() < numStages) {
        	log.info("AIPlayer doesnt have sufficient number of foes");
            return false;
        }

        foes.sort(new Comparator<FoeCardModel>() {
            @Override
            public int compare(FoeCardModel o1, FoeCardModel o2) {
                return o1.battlePoints - o2.battlePoints;
            }
        });


        int increasing = 1;
        int currBattlePts = foes.get(0).battlePoints; //TODO: provide API that gives upper battle power depending on current quest...
        for (FoeCardModel f : foes) {
            if (currBattlePts < f.battlePoints) {
                currBattlePts = f.battlePoints;
                increasing++;
            }
        }
        boolean sponsoring = increasing >= numStages;
        log.info("AIPlayer has enough increasing foes and no player can rank up by winning Quest, therefore will sponsor");
        return sponsoring;
    }

    //Should be called only if sponsored
    @Override
    public ArrayList<CardModel> getCardsToPlayForQuest(HandModel hand, int numStages, int currStage) {
    	ArrayList<CardModel> list = new ArrayList<CardModel>();
    	ArrayList<AllyCardModel> allies = hand.allies();
        ArrayList<WeaponCardModel> weapons = hand.uniqueWeapons();
        ArrayList<FoeCardModel> foes = hand.foes();
        
        if (currStage == numStages) {
            list.addAll(allies);
            list.addAll(weapons);
            log.info("AIPlayer plays strongest possible hand (at least 50 if possible)");
            return list;
        //Second last stage
        }else if((numStages - currStage) == 1) {
        	
        	for (CardModel c : hand.playerHand) {
        		if(c instanceof TestCardModel) {
        			list.add(c);
        			log.info("AIPlayer plays test card because it is second last stage");
        			return list;
        		}else {
        			log.info("AIPlayer plays strongest foe and most number of weapon card, it is second last stage but no test card");
        			//Allies
                	sortCards((ArrayList)allies);
                	Collections.reverse(allies);
                	
                	list.add(allies.get(0));
                	
                	//Weapons
                	int tempCounter = 0;
                	CardModel tempObject;
                	boolean foundWeapon = false;
                	
                	for (WeaponCardModel w : weapons) {
                		tempObject = weapons.get(0);
                		for(WeaponCardModel w2 :weapons) {
                			if (tempObject == w2) {
                				tempCounter++;
                			}
                		}
                		if(tempCounter > 1) {
                			foundWeapon = true;
                			list.add(tempObject);
                			tempCounter = 0;
                		}
                		if(foundWeapon == true){
                			return list;
                		}
                		else {
                			list.add((CardModel) weapons.get(0));
                			return list;
                		}
                	}
                	
        			} 
        		}
        } else {
        	log.info("strongest allie card is played with the most plentiful weapon card");
        	//Allies
        	sortCards((ArrayList)allies);
        	Collections.reverse(allies);
        	
        	list.add(allies.get(0));
        	
        	//Weapons
        	int tempCounter = 0;
        	CardModel tempObject;
        	boolean foundWeapon = false;
        	
        	for (WeaponCardModel w : weapons) {
        		tempObject = weapons.get(0);
        		for(WeaponCardModel w2 :weapons) {
        			if (tempObject == w2) {
        				tempCounter++;
        			}
        		}
        		if(tempCounter > 1) {
        			foundWeapon = true;
        			list.add(tempObject);
        			tempCounter = 0;
        		}
        		if(foundWeapon == true){
        			return list;
        		}
        	}
    			list.add((CardModel) weapons.get(0));
    			return list;
    		}
        return list;
    }

    @Override
    public boolean doIParticipateInQuest(HandModel hand, int numStages) {
       
    	List<AllyCardModel> allies = hand.allies();
        List<WeaponCardModel> weapons = hand.weapons();
        if((allies.size() + weapons.size() / 2) > numStages && foesLessThan(hand, 20).size() >= 2) {
        	log.info("AIPlayer has 2 weapons/allies per stage and 2 foes less than 20 points, therefore participates");
        }
        return ((allies.size() + weapons.size() / 2) > numStages && foesLessThan(hand, 20).size() >= 2);
    }
    
    @Override
    public int nextBid(HandModel hand, int roundNumber) {
        if (roundNumber == 1) {
        	log.info("AIPlayer round is 1 and bids number of foes less than 20");
            return foesLessThan(hand, 20).size();
        } else {
            return 0;
        }
    }

    @Override
    public ArrayList<Integer> discardAfterWinningTest(HandModel hand, int roundNumber) {
        return foesLessThan(hand, 20);
    }
    
    @Override
    //Discards first card from left to right that has more than 1 card of the same kind
    public ArrayList<Integer> discardToManyCards(HandModel hand){
    	
    	
    	ArrayList<CardModel> playerCards = hand.playerHand;
    	ArrayList<Integer> cardsToDiscardIds = new ArrayList<>();
    	CardModel discardCard = playerCards.get(0);
		cardsToDiscardIds.add(discardCard.cardId);
    	return cardsToDiscardIds;
    }

    public ArrayList<Integer> getNonSponsorCardsToPlayQuest(HandModel hand, int stageNum,QuestCardModel qCard) {
    	ArrayList<CardModel> cards = hand.playerHand;
        ArrayList<Integer> cardsIds = new ArrayList<>();
        ArrayList<String> cardsPlayed = new ArrayList<>();
        ArrayList<CardModel> cardsToSort = new ArrayList<>();
        
        //Get rid of all other types of cards only ally/amour and weapon cards
        for(CardModel c : cards) {
        	if(c instanceof AllyCardModel ||  c instanceof WeaponCardModel) {
        		cardsToSort.add(c);
        	}
        }
        cardsToSort = sortCards (cardsToSort);
        
        if(qCard.numOfStages == stageNum) {
        	log.info("AIPlayer: last stage played strongest valid combination");
        	for(CardModel c : cardsToSort) {
        		if(cardsPlayed.contains(c.cardName)) {
        			continue;
        		}else {
        			cardsPlayed.add(c.cardName);
        			cardsIds.add(c.cardId);
        		}
        	}
        	
        } else {
        	log.info("AIPlayer:Not last stage, play 2 allie cards if possible");
        	int allieCounter = 0;
        	
        	for(CardModel c : cardsToSort) {
        		if(c instanceof AllyCardModel && allieCounter < 2) {
        			if(cardsPlayed.contains(c.cardName)) {
        				continue;
        			}else {
        				cardsPlayed.add(c.cardName);
        				cardsIds.add(c.cardId);
        				allieCounter++;
        			}
        		}
        	}
        	
        	if (allieCounter < 2) { 
        		log.info("AIPlayer: Not enough allie cards played, play weakest weapon card to play 2 cards");
        		for (int i = cardsToSort.size() ; i >=0 ; i--) {
        			if(allieCounter < 2) {
        			if(cardsPlayed.contains(cardsToSort.get(i).cardName)){
        				continue;
        			}else if (cardsToSort.get(i) instanceof WeaponCardModel) {
        					cardsPlayed.add(cardsToSort.get(i).cardName);
        					cardsIds.add(cardsToSort.get(i).cardId);
        					allieCounter++;
        			}
        		  }
        		}
        	}
        }
       
		return cardsIds;
    }

    private ArrayList<CardModel> sortCards (ArrayList<CardModel> cards){
    	
    	Collections.sort(cards, new bpComparator());
    	Collections.reverse(cards);
		return cards;
    }
    
    //For sorting objects in sortCards
    class bpComparator implements Comparator<CardModel>{
		@Override
		public int compare(CardModel o1, CardModel o2) {
			return ((BattleCardModel) o1).battlePoints - ((BattleCardModel) o2).battlePoints;
		}
    	
    }
    
    private boolean validatePlayerCards(ArrayList<CardModel> cards) {
		HashSet<CardModel> uniqueSet = new HashSet<>();

		if (cards.size() == 0)
			return true;

		for (CardModel c : cards) {
			if (c instanceof FoeCardModel || c instanceof TestCardModel) {
				return false;
			}
			uniqueSet.add(c);
		}

		if (uniqueSet.size() != cards.size()) { // check for duplicates
			return false;
		}

		return true;
	}
    private boolean rankUpCheckAll(int shields, ArrayList<PlayerModel> players) {
        for (PlayerModel player : players) {
            if (RankCardModel.shieldsRequiredForNextUpgrade(player.rank.cardName) - player.shields - shields <= 0) {
                return true;
            }
        }
        return false;
    }
}
