package bots.behaviour;

import bots.Bot;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.List;

public class GotoPointBehaviour implements BotBehaviour {
    private static final float FINISH_DISTANCE_SQUARED = 0.001f;

    private CompletePathBehaviour completePathBehaviour;
    private GraphNode targetGraphNode;
    private Vector3D targetGraphNodePosition;
    private boolean initialized;

    /**
     * Creates a new GotoPointBehaviour that navigates the given bot to the given targetGraphNode
     * @param targetGraphNode The GraphNode to navigate to
     */
    public GotoPointBehaviour(GraphNode targetGraphNode) {
        this.completePathBehaviour = null;
        this.initialized = false;
        this.targetGraphNode = targetGraphNode;
        this.targetGraphNodePosition = GraphInformation.getPositionOf(targetGraphNode);
    }

    private static GraphNode getFixedTargetGraphNode(GraphNode graphNode) {
        if (graphNode.blocked) {
            return GraphInformation.getClosestGraphWithPredicate(
                    graphNode,
                    (GraphNode g) -> !g.blocked
            );
        }
        return graphNode;
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
            if (!initialized) {
                // fix targetGraphNode + targetGraphNodePosition
                targetGraphNode = getFixedTargetGraphNode(targetGraphNode);
                targetGraphNodePosition = GraphInformation.getPositionOf(targetGraphNode);

                // create path and CompletePathBehaviour
                List<GraphNode> path = GraphInformation.getPathTo(bot.getCurrentGraphNode(), targetGraphNode);
                assert path != null;
                this.completePathBehaviour = new CompletePathBehaviour(path);
                this.initialized = true;
            }

            return this.completePathBehaviour.getMoveDirectionUpdate(bot);
        }
    }

    @Override
    public boolean hasFinished(Bot bot) {
        if (bot.ignoresObstacles()) {
            float distanceSquared = Vector3D.getDistanceSquaredBetween(bot.getPosition(), this.targetGraphNodePosition);
            return distanceSquared < FINISH_DISTANCE_SQUARED;
        } else {
            if (initialized)
                return this.completePathBehaviour.hasFinished(bot);
            else
                return false;
        }
    }
}
