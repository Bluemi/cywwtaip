package bots.behaviour;

import bots.Bot;

public class StayBehaviour implements BotBehaviour {
    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        return (float) Math.PI;
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return false;
    }
}
