package bots.behaviour;

import bots.Bot;
import com.sun.istack.internal.NotNull;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.List;

public class CompletePathBehaviour implements BotBehaviour {
    private static final float FINISH_DISTANCE = 0.001f;
    private static final int LOOK_FORWARD_DISTANCE = 2;
    private static final float UNSTUCK_DIRECTION_UPDATE = 0.2f;
    private static final int NUM_UNSTUCK_FRAMES = 35;

    private List<GraphNode> path;
    private boolean hasFinished;
    private Vector3D targetPosition;
    private float unstuckDirection;
    private int unstuckCounter;

    public CompletePathBehaviour(@NotNull List<GraphNode> path) {
        this.path = path;
        assert path.size() > 0;

        this.targetPosition = GraphInformation.getPositionOf(getLastPathNode());
        this.hasFinished = false;
        this.unstuckDirection = 0.f;
        this.unstuckCounter = 0;
    }

    private int getNearestIndex(Bot bot) {
        int nearestIndex = 0;
        float nearestDistanceSquared = bot.getDistanceSquaredTo(path.get(0));
        for (int i = 1; i < path.size(); i++) {
            GraphNode node = path.get(i);
            float distanceSquared = bot.getDistanceSquaredTo(node);
            if (distanceSquared < nearestDistanceSquared) {
                nearestIndex = i;
                nearestDistanceSquared = distanceSquared;
            }
        }
        return nearestIndex;
    }

    private GraphNode getLastPathNode() {
        return path.get(path.size() - 1);
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {

        int nearestIndex = getNearestIndex(bot);
        int look_forward_index = nearestIndex + LOOK_FORWARD_DISTANCE;
        if (look_forward_index >= path.size()) {
            this.hasFinished = true;
            return MoveLogic.getDirectionUpdateToPosition(bot, this.targetPosition);
        }

        GraphNode nextNode = path.get(look_forward_index);

        if (bot.isStuck()) {
            if (unstuckDirection == 0.f) {
                Vector3D fromBotToNextNode = Vector3D.fromTo(bot.getPosition(), GraphInformation.getPositionOf(nextNode));
                if (Vector3D.isAngleAcute(bot.getLeftDirection(), fromBotToNextNode)) {
                    unstuckDirection = -UNSTUCK_DIRECTION_UPDATE;
                } else {
                    unstuckDirection = UNSTUCK_DIRECTION_UPDATE;
                }

                unstuckCounter = NUM_UNSTUCK_FRAMES;
            }
            return unstuckDirection;
        } else {
            unstuckDirection = 0.f;
        }

        if (unstuckCounter > 0) {
            unstuckCounter--;
            return unstuckDirection;
        }

        return MoveLogic.getDirectionUpdateToPosition(bot, GraphInformation.getPositionOf(nextNode));
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return hasFinished || bot.getDistanceSquaredTo(path.get(path.size() - 1)) < FINISH_DISTANCE;
    }
}
