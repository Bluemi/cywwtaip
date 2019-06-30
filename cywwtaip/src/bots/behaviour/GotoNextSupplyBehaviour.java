package bots.behaviour;

import bots.Bot;
import math.Vector3D;

public class GotoNextSupplyBehaviour implements BotBehaviour {
    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        Vector3D nextPowerSupply = MoveLogic.getNextPowerSupplyCenter(bot.getPosition());
        return MoveLogic.getDirectionUpdateToPosition(bot, nextPowerSupply);
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return Math.abs(bot.getPosition().absMax()) > MoveLogic.SUPPLY_BORDER;
    }
}
