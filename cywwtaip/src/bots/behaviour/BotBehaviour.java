package bots.behaviour;

import bots.Bot;

public interface BotBehaviour {
    default void init(Bot bot) {}
    float getMoveDirectionUpdate(Bot bot);
    boolean hasFinished(Bot bot);
}
