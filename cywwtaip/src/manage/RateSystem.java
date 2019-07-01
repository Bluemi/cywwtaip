package manage;

import bots.Bot;
import bots.BotType;

public class RateSystem {

    final float DISTANCE_RELATION = 10;
    final float VIEW_DISTANCE_RANGE = 5;

    private final float NORMAL_GET_ENERGY = 1.5f;
    private final float MOBILE_GET_ENERGY = 50f;
    private final float WIDE_GET_ENERGY = 50f;

    private final float NORMAL_REPAINT = 0.8f;
    private final float MOBILE_REPAINT = 1.6f;
    private final float WIDE_REPAINT = 1.8f;

    private final float NORMAL_KEEP_DISTANCE = 1.2f;
    private final float MOBILE_KEEP_DISTANCE = 0.2f;
    private final float WIDE_KEEP_DISTANCE = 0.7f;

    public float[] generateRating(Bot bot, int myPlayernumber){
        if (bot.botType== BotType.NORMAL)
            return generate(bot, myPlayernumber, NORMAL_GET_ENERGY, NORMAL_KEEP_DISTANCE, NORMAL_REPAINT);

        if (bot.botType== BotType.MOBILE)
            return generate(bot, myPlayernumber, MOBILE_GET_ENERGY, MOBILE_KEEP_DISTANCE, MOBILE_REPAINT);

        if (bot.botType== BotType.WIDE)
            return generate(bot, myPlayernumber, WIDE_GET_ENERGY, WIDE_KEEP_DISTANCE, WIDE_REPAINT);

        return null;
    }

    private float[] getDistancesToEnemies(Bot bot, int playernumber){
        float dis1 = 0;
        float dis2 = 0;
        boolean findSecond = false;

        for (int i = 0; i < 3; i++){
            if (i != playernumber){
                if(findSecond)
                    dis2= bot.getDistanceToPlayerNode(i);
                dis1 = bot.getDistanceToPlayerNode(i);
                findSecond = true;
            }

        }
        return new float[]{dis1, dis2};
    }

    private float[] generate(Bot bot, int myplayernumber, float multEnergy, float multPassive, float multRepaint){
        float distanceToPowerSupply = bot.getDistanceToSupply();
        float[] distanceToEnemyNodes = getDistancesToEnemies(bot, myplayernumber);
        return new float[]{
                (1/distanceToPowerSupply) * multEnergy,
                Math.min(distanceToEnemyNodes[0], distanceToEnemyNodes[1]) * multPassive,
                ((1/(distanceToEnemyNodes[0]+distanceToEnemyNodes[1])) /2 * multRepaint)
        };
    }
}
