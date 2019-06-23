package bots;

import bots.behaviour.BotBehaviour;
import bots.behaviour.GotoNextSupplyBehaviour;
import bots.behaviour.MoveLogic;
import bots.behaviour.RandomBehaviour;
import com.sun.istack.internal.NotNull;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

public class Bot {
    BotType botType;
    Vector3D position;
    Vector3D direction;
    BotBehaviour behaviour;

    public Bot(BotType botType) {
        this.botType = botType;
        this.position = new Vector3D(1.f, 0.f, 0.f);
        this.direction = new Vector3D(1.f, 0.f, 0.f);
        this.behaviour = new GotoNextSupplyBehaviour();
    }

    public void setDefaultBehaviour() {
        this.behaviour = new RandomBehaviour();
    }

    public void updatePosition(Vector3D position) {
        this.position = position;
    }

    public void updateDirection(Vector3D direction) {
        this.direction = direction;
    }

    public float getDirectionUpdate() {
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
     * @param graphNodes An array of graphNodes to search in
     * @param playerNumber The number of the player, who should be the owner of the searched node
     * @return the closest GraphNode that is owned by the given player. If no node was found, null is returned.
     */
    public Float getDistanceToPlayerNode(GraphNode[] graphNodes, int playerNumber) {
        GraphNode currentNode = getCurrentGraphNode(graphNodes[0]);
        GraphNode playerNode = GraphInformation.getClosestGraphNodeOfPlayer(currentNode, playerNumber);

        if (playerNode == null)
            return null;

        return Vector3D.getDistanceBetween(GraphInformation.getPositionOf(playerNode), position);
    }

    /**
     * Returns the GraphNode you are standing on
     * @param graphNode The Graph in which to search
     */
    public GraphNode getCurrentGraphNode(GraphNode graphNode) {
        return GraphInformation.getClosestGraphNodeTo(graphNode, position);
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
}
