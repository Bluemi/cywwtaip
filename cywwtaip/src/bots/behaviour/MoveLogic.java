package bots.behaviour;

import bots.Bot;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.Arrays;
import java.util.List;

public class MoveLogic {
    private MoveLogic() {}

    public static final float EPSILON = 0.000001f;
    public static final float SUPPLY_BORDER = 0.94f;

    /**
     * Returns the direction update for the given bot, to navigate it to the given position
     * @param bot The bot to maneuver
     * @param targetPoint The position to be navigated to
     * @return The direction update
     */
    public static float getDirectionUpdateToPosition(Bot bot, Vector3D targetPoint) {
        Vector3D z = Vector3D.fromTo(bot.getPosition(), targetPoint);

        // if we are already at the target point
        if (z.getLengthSquared() < EPSILON)
            return 0.f;

        Vector3D projectedOnPosition = z.projectOn(bot.getPosition());
        Vector3D positionToZOnPlane = Vector3D.sub(z, projectedOnPosition);
        float angle = Vector3D.getAngleBetween(positionToZOnPlane, bot.getDirection());

        // give back negative angle, if positionToZOnPlane is on the left side of direction
        Vector3D left = Vector3D.crossProduct(bot.getPosition(), bot.getDirection()).normalized();
        float leftDotProduct = Vector3D.dotProduct(left, positionToZOnPlane);
        if (leftDotProduct < 0.f)
            angle = -angle;

        return angle;
    }

    public static List<Integer> getEnemyPlayerNumbers(int playerNumber) {
        switch (playerNumber) {
            case 0:
                return Arrays.asList(1, 2);
            case 1:
                return Arrays.asList(0, 2);
            case 2:
                return Arrays.asList(0, 1);
        }
        throw new IllegalArgumentException("got player number: " + playerNumber);
    }

    /**
     * Returns the center of power supply closest to the given position
     */
    public static Vector3D getNextPowerSupplyCenter(Vector3D position) {
        int coordinateIndex = position.absArgMax();
        float value = position.get(coordinateIndex);
        Vector3D result = new Vector3D();
        return result.set(Math.signum(value), coordinateIndex);
    }

    /**
     * Searches a target node that is free for passing
     * @param bot The bot for which to search a unstuck route.
     * @return The position to go to, to unstuck the given bot
     */
    public static Vector3D getUnstuckTarget(Bot bot) {
        GraphNode g = bot.getCurrentGraphNode();
        Vector3D gPos = GraphInformation.getPositionOf(g);

        Vector3D toNeighbors = new Vector3D();
        int counter = 0;
        for (GraphNode n : g.neighbors) {
            if (n.blocked) {
                Vector3D g2n = Vector3D.fromTo(gPos, GraphInformation.getPositionOf(n));
                toNeighbors = Vector3D.add(toNeighbors, g2n);
                counter++;
            }
        }

        if (counter == 0) {
            System.out.println("random unstuck");
            return Vector3D.getRandomNormalized();
        }

        Vector3D awayFromNeighbors = toNeighbors.scale(-1.f / counter);

        return Vector3D.add(gPos, awayFromNeighbors);
    }

    /**
     * Returns whether the given bot collides with the next neighbor graphNode if it would move in the direction of the
     * bot. If the bot ignores obstacles this function always returns false.
     * @param bot Checks whether this bot is colliding
     * @return true, if the given bot collides with a neighbor graphNode, otherwise false
     */
    public static boolean isBotColliding(Bot bot) {
        if (bot.ignoresObstacles()) {
            return false;
        }
        Vector3D scaledDirection = bot.getDirection().withLength(GraphInformation.AVERAGE_NEIGHBOR_DISTANCE);
        Vector3D nextPosition = Vector3D.add(bot.getPosition(), scaledDirection);

        GraphNode[] neighbors = bot.getCurrentGraphNode().neighbors.clone();
        Arrays.sort(neighbors, new GraphInformation.GraphNodePositionComparator(nextPosition));

        return neighbors[neighbors.length-2].blocked || neighbors[neighbors.length-1].blocked;
    }
}
