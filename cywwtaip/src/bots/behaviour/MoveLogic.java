package bots.behaviour;

import bots.Bot;
import math.Vector3D;

public class MoveLogic {
    private MoveLogic() {}

    public static final float EPSILON = 0.000001f;

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

    /**
     * Returns the center of power supply closest to the given position
     */
    public static Vector3D getNextPowerSupplyCenter(Vector3D position) {
        int coordinateIndex = position.absArgMax();
        float value = position.get(coordinateIndex);
        Vector3D result = new Vector3D();
        return result.set(Math.signum(value), coordinateIndex);
    }
}
