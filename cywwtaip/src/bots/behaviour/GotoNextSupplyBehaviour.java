package bots.behaviour;

import bots.Bot;
import math.Vector3D;

public class GotoNextSupplyBehaviour implements BotBehaviour {
    public static final float SUPPLY_BORDER = 0.94f;

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        Vector3D nextPowerSupply = MoveLogic.getNextPowerSupplyCenter(bot.getPosition());
        return MoveLogic.getDirectionUpdateToPosition(bot, nextPowerSupply);
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return Math.abs(bot.getPosition().absMax()) > SUPPLY_BORDER;
    }
}
