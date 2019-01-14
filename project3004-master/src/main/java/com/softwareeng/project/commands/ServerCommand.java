package com.softwareeng.project.commands;


/**
 * SAY THE WORD "to" INFRONT OF EACH COMMAND TO UNDERSTAND DIRECTION OF THE COMMAND
 */
public enum ServerCommand {
    /****
     * START MAIN MENU COMMANDS
     */

    /**
     *
     */
    SHOW_PLAYER_SELECTION_VIEW,


    /**
     * make non-first players wait
     */
    WAIT_FOR_SETUP,


    /**
     * multiple reasons for not being able to join for example no room in current game
     * Syntax: CANNOT_JOIN {message}
     * Usage: CANNOT_JOIN The game is full
     */
    CANNOT_JOIN,

    /**
     * END MAIN MENU COMMANDS
     */

    /**
     * Displays each players cards
     */
    INIT_GAMEVIEW,

    /**
     * join an already setup game
     */
    SHOW_GAMEVIEW,


    /**
     * game terminated
     * Syntax: EXIT {message}
     */
    EXIT,


/**************************
 * GAMEVIEW COMMANDS
 **************************/

//TODO: we have 3 different setup methods, is 1 command enough for all three?
    /**
     * the client will set up the GUI based on numOfPlayers. 2 - 4 player ids can be sent
     * args: playerId - A list of player IDs
     * Syntax: SERVER_SETUP_GAME_GUI {playerId...}
     * Usage: SERVER_SETUP_GAME_GUI 0 1
     * Usage: SERVER_SETUP_GAME_GUI 0 1 2
     * Usage: SERVER_SETUP_GAME_GUI 0 1 2 3
     */
    SETUP_GAME_GUI,

    /**
     * adds a card to a specific player's hand
     * Syntax: ADD_CARD_TO_HAND {playerId} {cardId} {imgUrl}
     */


    ADD_CARD_TO_HAND,

    /**
     * set the text for message box
     * Syntax: SET_MESSAGE_BOX {message}
     */
    SET_MESSAGE_BOX,

    /**
     * to remove a card from a specific hand
     * <p>
     * Syntax: REMOVE_CARD_FROM_HAND {playerId} {cardId}
     */
    REMOVE_CARD_FROM_HAND,

    /**
     * to flip a story card
     * <p>
     * Syntax: FLIP_STORY_CARD {cardId} {imgUrl}
     */
    FLIP_STORY_CARD,

    /**
     * remove the card that was flipped
     * <p>
     * Syntax: REMOVE_FLIPPED_STORY_CARD
     */
    REMOVE_FLIPPED_STORY_CARD,

    /**
     * Shows story deck on gui
     * <p>
     * FILL_STORY_DECK
     */
    FILL_STORY_DECK,

    /**
     * shows adventure deck on gui
     * <p>
     * Syntax: FILL_ADVENTURE_DECK
     */
    FILL_ADVENTURE_DECK,

    /**
     * adds card to the asset area of a specific player
     * <p>
     * Syntax: ADD_CARD_TO_ASSETS {playerId} {cardId} {imgUrl}
     */
    ADD_CARD_TO_ASSETS,

    /**
     * removes card from specific player's assets
     * <p>
     * Syntax: REMOVE_CARD_FROM_ASSETS {plyaerId} {cardId}
     */
    REMOVE_CARD_FROM_ASSETS,

    /**
     * adds card to the play area of a specific player
     * <p>
     * Syntax: ADD_CARD_TO_PLAY_AREA {playerId} {cardId} {imgUrl}
     */
    ADD_CARD_TO_PLAY_AREA,

    /**
     * removes card from specific player's play area
     * <p>
     * Syntax: REMOVE_CARD_FROM_PLAY_AREA {plyaerId} {cardId}
     */
    REMOVE_CARD_FROM_PLAY_AREA,

    /**
     * empties play area of a specific player
     * <p>
     * Syntax: REMOVE_ALL_CARDS_FROM_PLAY_AREA {playerId}
     */
    REMOVE_ALL_CARDS_FROM_PLAY_AREA,

    /**
     * prompts specific player for end turn
     * <p>
     * Syntax: SHOW_END_TURN_BUTTON {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    SHOW_END_TURN_BUTTON,

    /**
     * hide button by id
     * <p>
     * Syntax: HIDE_BUTTON {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    HIDE_BUTTON,

    /**
     * prompts the player to flip the story deck
     */
    PROMPT_FLIP_STORY,

    /**
     * prompts specific player for yes option
     * <p>
     * Syntax: SHOW_END_TURN_BUTTON {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_YES_NO,


    /**
     * prompts specific player for play cards
     * <p>
     * Syntax: PROMPT_PLAY_CARDS {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_PLAY_CARDS,

    /**
     * prompts specific player for ready
     * <p>
     * Syntax: PROMPT_TURN_READY {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_TURN_READY,

    /**
     * prompts specific player to discard cards
     * <p>
     * Syntax: PROMPT_DISCARD_CARDS {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_DISCARD_CARDS,

    /**
     * prompts specific player to discard cards
     * <p>
     * Syntax: PROMPT_BID_CARDS {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_BID_CARDS,

    /**
     * prompts specific player to increase bids
     * <p>
     * Syntax: PROMPT_INCREASE_BIDS {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_INCREASE_BIDS,

    /**
     * prompts specific player to decrease bids
     * <p>
     * Syntax: PROMPT_DECREASE_BIDS {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_DECREASE_BIDS,

    /**
     * prompts specific player to play ally or amour cards
     * <p>
     * Syntax: PROMPT_PLAY_ALLY {} {} arguments left empty for Ahmad to decide if he wants to send an event or disconnect events here
     */
    PROMPT_PLAY_ALLY,

    PROMPT_TEST_DISCARD,

    ADD_SHIELDS,

    REMOVE_SHIELDS,

    SET_SHIELDS,

    CHANGE_PLAYER_RANK,

    FILL_DECK


}
