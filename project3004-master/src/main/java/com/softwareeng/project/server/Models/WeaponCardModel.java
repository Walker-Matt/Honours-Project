package com.softwareeng.project.server.Models;


/*
  Need this class to be able to differentiate Weapon cards from Foe ones... or differntiate them in some other way
  (i.e. type...).
  Previous hierarchy was not ideal... see explanation below.
  Doing "instanceof BattleCardModel" will return true for Allies and Foes, because they are a subclass of BattleCardModel.
  So when looking for a BattleCardModel, doing instanceof BattleModel does not guarantee that that object is indeed of type WeaponCardModel.
  And obviously, in some instances we want to just get WeaponModel objects and nothing else (esp for AI)
 */
public class WeaponCardModel extends BattleCardModel {

    public WeaponCardModel(String name, int power) {
        super(name, power);
    }

}
