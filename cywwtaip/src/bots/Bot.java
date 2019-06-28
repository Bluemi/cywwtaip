package bots;

import bots.behaviour.*;
import com.sun.istack.internal.NotNull;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.ArrayDeque;

public class Bot {
    private static final float STUCK_DISTANCE_SQUARED = GraphInformation.MIN_NEIGHBOR_DISTANCE_SQUARED * 2;
    private static final int STUCK_QUEUE_SIZE = 5;
    private static final int MAX_STUCK_NOT_CHANGED_DURATION = 100;


    BotType botType;
    Vector3D position;
    Vector3D direction;
    BotBehaviour behaviour;
    GraphNode currentGraphNode;
    BotLogger logger;
    ArrayDeque<GraphNode> lastGraphNodes;
    long lastGraphNodeUpdate;

    /**
     * Creates a new Bot
     * @param botType The type of this bot (normal, mobile, wide)
     * @param graphNode A random graph node to get access to the graph
     */
    public Bot(@NotNull BotType botType, @NotNull GraphNode graphNode, @NotNull BotBehaviour behaviour, String teamName) {
        this.botType = botType;
        this.position = new Vector3D(1.f, 0.f, 0.f);
        this.direction = new Vector3D(1.f, 0.f, 0.f);
        this.behaviour = behaviour;
        this.currentGraphNode = graphNode;
        this.logger = new BotLogger(teamName, botType);
        this.lastGraphNodes = new ArrayDeque<>(STUCK_QUEUE_SIZE);
        this.lastGraphNodeUpdate = -1;
    }

    public void setBehaviour(BotBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    private void addLastGraphNode(GraphNode g) {
        if (lastGraphNodes.size() >= STUCK_QUEUE_SIZE) {
            lastGraphNodes.poll();
        }
        lastGraphNodes.add(g);
    }

    public void updatePosition(Vector3D position) {
        if (!this.position.equals(position)) {
            this.position = position;
            GraphNode closestGraphNode = GraphInformation.getClosestGraphNodeTo(currentGraphNode, position);

            if (currentGraphNode != closestGraphNode) {
                this.currentGraphNode = closestGraphNode;
                lastGraphNodeUpdate = System.currentTimeMillis();
            }
        }

        float distance = Vector3D.getDistanceBetween(position, GraphInformation.getPositionOf(currentGraphNode));
        if (distance > 0.1f) {
            System.out.println("found to big distance=" + distance + " position=" + position + " currentNode=" + currentGraphNode);
        }

        if (isStuck()) {
            // logger.log("stuck");
        }
    }

    public boolean isStuck() {
        if (lastGraphNodeUpdate < 0) {
            lastGraphNodeUpdate++;
        } else if (lastGraphNodeUpdate == 0) {
            lastGraphNodeUpdate = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - lastGraphNodeUpdate;

            boolean stuck = diff > MAX_STUCK_NOT_CHANGED_DURATION;

            /*
            if (ignoresObstacles() && stuck) {
                System.out.println("diff: " + diff);
                System.out.println("position: " + position);
                System.out.println("currentNode: " + GraphInformation.getPositionOf(currentGraphNode));
            }
             */

            return diff > MAX_STUCK_NOT_CHANGED_DURATION;
        }
        return false;
    }

    public void updateDirection(Vector3D direction) {
        this.direction = direction;
    }

    public float getDirectionUpdate() {
        if (behaviour.hasFinished(this))
            setDefaultBehaviour();

        return behaviour.getMoveDirectionUpdate(this);
    }

    private void setDefaultBehaviour() {
        this.behaviour = new StayBehaviour();
    }

    public Vector3D getPosition() {
        return position;
    }

    public Vector3D getDirection() {
        return direction;
    }

    /**
     * Returns the distance to the next supply center, ignoring obstacles
     */
    public float getDistanceToSupply() {
        Vector3D supplyPosition = MoveLogic.getNextPowerSupplyCenter(this.position);
        return Vector3D.getDistanceBetween(supplyPosition, position);
    }

    /**
     * @return the squared distance to the given graphNode
     */
    public float getDistanceSquaredTo(GraphNode graphNode) {
        return Vector3D.getDistanceSquaredBetween(position, GraphInformation.getPositionOf(graphNode));
    }

    /**
     * Returns the closest GraphNode that is owned by the given player.
     * @param playerNumber The number of the player, who should be the owner of the searched node
     * @return the closest GraphNode that is owned by the given player. If no node was found, null is returned.
     */
    public Float getDistanceToPlayerNode(int playerNumber) {
        GraphNode playerNode = GraphInformation.getClosestGraphWithPredicate(
                getCurrentGraphNode(),
                (GraphNode x) -> x.owner == playerNumber
        );

        if (playerNode == null)
            return null;

        return Vector3D.getDistanceBetween(GraphInformation.getPositionOf(playerNode), position);
    }

    /**
     * Returns the GraphNode this bot is currently standing on
     */
    public GraphNode getCurrentGraphNode() {
        return this.currentGraphNode;
    }

    /**
     * @return whether this bot ignores obstacles or not.
     */
    public boolean ignoresObstacles() {
        return botType == BotType.MOBILE;
    }

    /**
     * Returns the position of the given positions, that is closest to this bot
     * @param botPositions The array of botPositions in which to search
     * @return the position of the given positions, that is closest to this bot
     */
    public Vector3D getClosestBotPosition(@NotNull Vector3D[] botPositions) {
        Vector3D closestBotPosition = botPositions[0];
        float closestDistanceSquared = Vector3D.getDistanceSquaredBetween(closestBotPosition, position);

        // i = 1, because botPosition[0] is already assumed
        for (int i = 1; i < botPositions.length; i++) {
            Vector3D botPosition = botPositions[i];
            float distanceSquared = Vector3D.getDistanceSquaredBetween(botPosition, position);
            if (distanceSquared < closestDistanceSquared) {
                closestBotPosition = botPosition;
                closestDistanceSquared = distanceSquared;
            }
        }

        return closestBotPosition;
    }

    public boolean hasFinished() {
        return this.behaviour.hasFinished(this);
    }
}
