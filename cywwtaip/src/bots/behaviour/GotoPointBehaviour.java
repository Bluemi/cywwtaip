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
    private boolean hasFixedTargetGraphNode;

    public GotoPointBehaviour(GraphNode targetGraphNode) {
        setTargetGraphNode(targetGraphNode);
        this.hasFinished = false;
        this.hasFixedTargetGraphNode = false;
    }

    private void setTargetGraphNode(GraphNode graphNode) {
        this.targetGraphNode = graphNode;
        this.targetGraphNodePosition = GraphInformation.getPositionOf(targetGraphNode);
    }

    /**
     * In case of a target graph node, that is blocked, this function will set the target graph node to the closest
     * graph node of the original target graph node, that is not blocked.
     */
    private void fixTargetGraphNode() {
        if (targetGraphNode.blocked) {
            GraphNode newTarget = GraphInformation.getClosestGraphWithPredicate(
                    targetGraphNode,
                    (GraphNode g) -> !g.blocked
            );
            setTargetGraphNode(newTarget);
        }
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
            if (!hasFixedTargetGraphNode)
                fixTargetGraphNode();
            /*
            System.out.println("current Node: " + bot.getCurrentGraphNode());
            System.out.println("position: " + bot.getPosition());
            System.out.println("targetGraphNode: " + targetGraphNode);
            System.out.flush();
             */
            List<GraphNode> path = GraphInformation.getPathTo(bot.getCurrentGraphNode(), targetGraphNode);
            assert path != null;

            if (path == null) {
                if (targetGraphNode.blocked) {
                    throw new IllegalStateException("hey");
                } else {
                    throw new IllegalStateException("yup");
                }
            }

            /*
            System.out.println("path:");
            for (GraphNode node : path) {
                System.out.println("\t" + node);
            }
             */

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
        float distance = Vector3D.getDistanceSquaredBetween(bot.getPosition(), this.targetGraphNodePosition);
        return hasFinished || (distance < FINISH_DISTANCE_SQUARED);
    }
}
