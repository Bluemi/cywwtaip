package bots.behaviour;

import bots.Bot;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

public class GotoNextSupplyBehaviour implements BotBehaviour {
    DriveToPointBehaviour driveToPointBehaviour;

    public GotoNextSupplyBehaviour() {
        this.driveToPointBehaviour = null;
    }

    @Override
    public void init(Bot bot) {
        Vector3D nextPowerSupply = MoveLogic.getNextPowerSupplyCenter(bot.getPosition());
        GraphNode targetGraphNode = GraphInformation.getClosestGraphNodeTo(bot.getCurrentGraphNode(), nextPowerSupply);

        this.driveToPointBehaviour = new DriveToPointBehaviour(targetGraphNode);
        this.driveToPointBehaviour.init(bot);
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        return driveToPointBehaviour.getMoveDirectionUpdate(bot);
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return bot.isInSupply();
    }
}
