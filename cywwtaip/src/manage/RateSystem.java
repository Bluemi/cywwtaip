package manage;

import bots.Bot;

public class RateSystem {

    final float NORMAL_GET_ENERGY = 1.0f;
    final float MOBILE_GET_ENERGY = 1.0f;
    final float WIDE_GET_ENERGY = 1.0f;

    final float NORMAL_DRAW_OVER = 1.0f;
    final float MOBILE_DRAW_OVER = 1.0f;
    final float WIDE_DRAW_OVER = 1.0f;

    final float NORMAL_KEEP_DISTANCE = 1.0f;
    final float MOBILE_KEEP_DISTANCE = 1.0f;
    final float WIDE_KEEP_DISTANCE = 1.0f;

    public float[] generateRating(Bot bot){
        float goToEnergySupply;
        float attackOtherPlayer;
        float keepDistance;

        bot.getDistanceToSupply();

        return null;
    }
}
