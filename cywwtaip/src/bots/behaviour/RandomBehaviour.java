package bots.behaviour;

import bots.Bot;

import java.util.Random;

public class RandomBehaviour implements BotBehaviour {
    private Random random;

    public RandomBehaviour() {
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        if (random.nextInt(5) != 0) {
            return 0.f;
        }

        return (float) random.nextGaussian() * 0.4f;
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return false;
    }
}
