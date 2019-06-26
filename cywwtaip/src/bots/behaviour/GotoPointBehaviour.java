package bots.behaviour;

import bots.Bot;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.List;

public class GotoPointBehaviour implements BotBehaviour {
    private static final float FINISH_DISTANCE_SQUARED = 0.0001f;
    private static final int LOOK_FORWARD_DISTANCE = 1;

    private GraphNode targetGraphNode;
    private Vector3D targetGraphNodePosition;
    private boolean hasFinished;

    public GotoPointBehaviour(GraphNode targetGraphNode) {
        this.targetGraphNode = targetGraphNode;
        this.targetGraphNodePosition = GraphInformation.getPositionOf(targetGraphNode);
        this.hasFinished = false;
    }

    /**
     * Returns the direction update to look towards the next way point to get to the targetGraphNode.
     * @param bot The bot for which to update the move direction
     */
    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        if (bot.ignoresObstacles()) {
            return MoveLogic.getDirectionUpdateToPosition(bot, targetGraphNodePosition);
        } else {
            List<GraphNode> path = GraphInformation.getPathTo(bot.getCurrentGraphNode(), targetGraphNode);
            assert path != null;

            if (path.size() < (LOOK_FORWARD_DISTANCE + 1)) {
                this.hasFinished = true;
                return 0.f;
            }

            GraphNode nextNode = path.get(LOOK_FORWARD_DISTANCE);
            return MoveLogic.getDirectionUpdateToPosition(bot, GraphInformation.getPositionOf(nextNode));
        }
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return Vector3D.getDistanceSquaredBetween(bot.getPosition(), this.targetGraphNodePosition) < FINISH_DISTANCE_SQUARED;
    }
}
