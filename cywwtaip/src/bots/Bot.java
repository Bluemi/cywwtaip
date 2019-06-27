package bots;

import bots.behaviour.*;
import com.sun.istack.internal.NotNull;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

public class Bot {
    BotType botType;
    Vector3D position;
    Vector3D direction;
    BotBehaviour behaviour;
    GraphNode currentGraphNode;
    public boolean debug;
    int counter;

    /**
     * Creates a new Bot
     * @param botType The type of this bot (normal, mobile, wide)
     * @param graphNode A random graph node to get access to the graph
     */
    public Bot(@NotNull BotType botType, @NotNull GraphNode graphNode) {
        this.botType = botType;
        this.position = new Vector3D(1.f, 0.f, 0.f);
        this.direction = new Vector3D(1.f, 0.f, 0.f);
        this.behaviour = new StayBehaviour();
        this.currentGraphNode = graphNode;
        this.counter = 0;
        this.debug = false;
    }

    public void setDefaultBehaviour() {
        this.behaviour = new RandomBehaviour();
        System.out.println("random");
    }

    public void setBehaviour(BotBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public void updatePosition(Vector3D position) {
        this.position = position;
        if (debug) {
            System.out.println("start get closest");
            System.out.flush();
        }
        this.currentGraphNode = GraphInformation.getClosestGraphNodeTo(currentGraphNode, position);
        if (debug) {
            System.out.println("end get closest");
            System.out.flush();
        }

        if (debug) {
            float distance = Vector3D.getDistanceBetween(GraphInformation.getPositionOf(currentGraphNode), position);
            if (distance > 0.05f) {
                System.out.println("updatePosition.position: " + position);
                System.out.println("updatePosition.currentGraphNode: " + currentGraphNode);
                System.out.println("distance: " + distance);
            }
        }
    }

    public void updateDirection(Vector3D direction) {
        this.direction = direction;
    }

    public float getDirectionUpdate() {
        if (debug && !(behaviour instanceof GotoPointBehaviour)) {
            counter++;
            System.out.println("counter: " + counter);
            if (counter >= 200) {
                counter = 0;
                GraphNode supplyNode = GraphInformation.getClosestGraphNodeTo(
                        getCurrentGraphNode(),
                        MoveLogic.getNextPowerSupplyCenter(position)
                );
                System.out.println("Supply Center: " + MoveLogic.getNextPowerSupplyCenter(position));
                System.out.println("SupplyNode: " + supplyNode);
                this.behaviour = new GotoPointBehaviour(supplyNode);
                System.out.println("goto supply");
            }
        }
        if (behaviour.hasFinished(this))
            setDefaultBehaviour();

        return behaviour.getMoveDirectionUpdate(this);
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

    public void doDebug() {
        this.debug = true;
        System.out.println("do debug");
    }
}
