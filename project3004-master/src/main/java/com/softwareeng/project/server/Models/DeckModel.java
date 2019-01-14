package com.softwareeng.project.server.Models;

import java.util.ArrayList;

public abstract interface DeckModel {
    ArrayList<CardModel> getCards();
}
