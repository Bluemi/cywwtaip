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
    private static final int LOOK_BACKWARD_DISTANCE = 10;
    private static final int NUM_UNSTUCK_MILLIS = 500;

    private List<GraphNode> path;
    private boolean hasFinished;
    private Vector3D targetPosition;
    private long unstuckDetectedTime;
    private boolean stuckDetected;

    public CompletePathBehaviour(@NotNull List<GraphNode> path) {
        this.path = path;
        assert path.size() > 0;

        this.targetPosition = GraphInformation.getPositionOf(getLastPathNode());
        this.hasFinished = false;
        this.unstuckDetectedTime = 0;
        this.stuckDetected = false;
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

        long currentMillis = System.currentTimeMillis();

        if (bot.isStuck() && !stuckDetected) {
            unstuckDetectedTime = currentMillis;
            stuckDetected = true;
            return (float) Math.PI;
        }

        if (stuckDetected) {
            if (currentMillis - unstuckDetectedTime > NUM_UNSTUCK_MILLIS) {
                hasFinished = true; // do not go path again, after being stuck there
            }

            look_forward_index = nearestIndex - LOOK_BACKWARD_DISTANCE; // look back, if stuck
            if (look_forward_index < 0) {
                return 0.f;
            }
        }

        if (look_forward_index >= path.size()) {
            this.hasFinished = true;
            return MoveLogic.getDirectionUpdateToPosition(bot, this.targetPosition);
        }

        GraphNode nextNode = path.get(look_forward_index);

        return MoveLogic.getDirectionUpdateToPosition(bot, GraphInformation.getPositionOf(nextNode));
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return hasFinished || bot.getDistanceSquaredTo(path.get(path.size() - 1)) < FINISH_DISTANCE;
    }
}
