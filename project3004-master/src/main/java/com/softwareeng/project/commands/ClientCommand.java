package com.softwareeng.project.commands;

public enum ClientCommand {

    /**
     * first player tells the server what he chose
     * Syntax: NUMBER_OF_PLAYERS {numOfHumanPlayers}
     * Usage:
     */
    NUMBER_OF_PLAYERS,

    STORY_DECK_CLICKED,

    PROMPT_YES_NO_REPLY,

    PROMPT_PLAY_CARDS_REPLY,

    PROMPT_DISCARD_REPLY,

    PROMPT_BIDS_REPLY,

    PROMPT_TEST_DISCARD_REPLY,

    PROMPT_END_TURN
}
