package bots.behaviour;

import bots.Bot;

public class PaintBehaviour implements BotBehaviour {
    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        return 0.f;
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return false;
    }
}
