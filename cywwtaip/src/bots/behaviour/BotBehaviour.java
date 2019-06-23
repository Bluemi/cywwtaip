package bots.behaviour;

import bots.Bot;

public interface BotBehaviour {
    float getMoveDirectionUpdate(Bot bot);
    boolean hasFinished(Bot bot);
}
