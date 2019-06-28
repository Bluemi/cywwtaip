package manage;

import bots.Bot;

public class RateSystem {

    enum BotTasks{
        OVERDRAW, ENERGY, PASSIVE, GAP
    }

    //TODO Rating-Multiplier jeder Bot bekommt pro Rating Kategorie anhand seiner ability einen eigenen eignungsScore

    public BotTasks[] generateRating(Bot bot){
        //TODO using multilplier for defined cases
        // returns array mit bewertungen für jeden Task
        return null;
    }
}
