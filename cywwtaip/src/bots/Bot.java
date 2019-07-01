package bots;

import bots.behaviour.*;
import com.sun.istack.internal.NotNull;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.ArrayList;

public class Bot {
    private static final int MAX_STUCK_NOT_CHANGED_DURATION = 150;


    public BotType botType;
    Vector3D position;
    Vector3D direction;
    BotBehaviour behaviour;
    GraphNode currentGraphNode;
    long lastPositionUpdate;
    public String teamName;

    int playerNumber;
    private boolean random;

    /**
     * Creates a new Bot
     * @param botType The type of this bot (normal, mobile, wide)
     * @param graphNode A random graph node to get access to the graph
     */
    public Bot(@NotNull BotType botType, @NotNull GraphNode graphNode, @NotNull BotBehaviour behaviour, String teamName, int playerNumber, boolean random) {
        this.botType = botType;
        this.position = new Vector3D(1.f, 0.f, 0.f);
        this.direction = new Vector3D(1.f, 0.f, 0.f);
        this.currentGraphNode = graphNode;
        this.lastPositionUpdate = -1;
        this.teamName = teamName;
        setBehaviour(behaviour);
        this.playerNumber = playerNumber;
        this.random = random;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setBehaviour(BotBehaviour behaviour) {
        /*
        if (!random) {
            System.out.println("set behaviour " + botType + ": " + behaviour);
        }
         */
        this.behaviour = behaviour;
        this.behaviour.init(this);
        /*
        if (playerNumber == 0 && botType == BotType.NORMAL)
            System.out.println(behaviour);
         */
    }

    public void updatePosition(Vector3D position) {
        if (!this.position.equals(position)) {
            this.position = position;
            currentGraphNode = GraphInformation.getClosestGraphNodeTo(currentGraphNode, position);

            lastPositionUpdate = System.currentTimeMillis();
        }

        float distance = Vector3D.getDistanceSquaredBetween(position, GraphInformation.getPositionOf(currentGraphNode));
        if (distance > 0.01f) {
            System.out.println("found to big distance=" + distance + " position=" + position + " currentNode=" + currentGraphNode);
        }
    }

    public Vector3D getLeftDirection() {
        return Vector3D.crossProduct(position, direction).normalized();
    }

    public boolean isStuck() {
        if (lastPositionUpdate < 0) {
            lastPositionUpdate++;
        } else if (lastPositionUpdate == 0) {
            lastPositionUpdate = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - lastPositionUpdate;

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

    public void setDefaultBehaviour() {
        setBehaviour(new PaintBehaviour());
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
        GraphNode playerNode = GraphInformation.getClosestGraphNodeWithPredicate(
                getCurrentGraphNode(),
                (GraphNode x) -> x.owner-1 == playerNumber
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

    public void driveTo(GraphNode node){
        //TODO
        // hat updateDirection die selbe wirkung? nur das eine richtung angegeben wird, anstatt das ziel? wie wird das fahren zum ziel geregelt? indem direction anhhand bom ziel ermittelt wird?
        setBehaviour(new DriveToPointBehaviour(node));
    }

    public ArrayList<GraphNode> getPathToSupply(){
        //TODO
        // länge des weges zur energy
        // wahrscheinlich ähnlich zu getDistanceToSupply oder? sollte aber schluchten mit einbeziehen
        // returned ein node array das schluchten umgeht(sollte ignoreObstacles() == false sein), das abgefahren werden kann
        // gibt es ein behavior, das mehrere Nodes bekommen kann und nach erreichen des ziels nicht ins default schaltet sondern danach zu dem nächsten node fährt? das würde
        // der manager dann an das behavior übergeben

        Vector3D nextPowerSupplyCenter = MoveLogic.getNextPowerSupplyCenter(position);
        GraphNode supplyTargetNode = GraphInformation.getClosestGraphNodeTo(currentGraphNode, nextPowerSupplyCenter);
        return GraphInformation.getPathTo(
                currentGraphNode,
                supplyTargetNode,
                playerNumber
        );
    }

    /**
     * Gets the path to the nearest node owned by otherPlayerNumber and optimizes the path for myPlayerNumber
     * @param otherPlayerNumber The player number whose node should be found
     * @return A path to the player with playerNumber optimized for myPlayerNumber
     */
    public ArrayList<GraphNode> getPathToPlayerNode(int otherPlayerNumber){
        //TODO
        // so wie getPathToSupply
        GraphNode targetNode = GraphInformation.getClosestGraphNodeWithPredicate(
                currentGraphNode,
                (GraphNode g) -> g.owner-1 == otherPlayerNumber
        );
        return GraphInformation.getPathTo(
                currentGraphNode,
                targetNode,
                getPlayerNumber()
        );
    }

    public GraphNode[] getClusterPosition(){
        // TODO nicht zwingend für einen Bot implementiert sondern soll später wahrscheinlich eher im Manager laufen
        // Idee dahinter --> Wo kann man am meisten Punkte klauen auch wenn man anhand von lag/latenz auch mal einen schlenker zur seite macht?
        // --> da wo regional die meisten enemy nodes zu finden sind
        return null;
    }

    public boolean isInSupply() {
        return Math.abs(position.absMax()) > MoveLogic.SUPPLY_BORDER;
    }
}
