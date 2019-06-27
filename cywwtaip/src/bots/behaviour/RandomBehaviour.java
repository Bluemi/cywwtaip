package bots.behaviour;

import bots.Bot;

import java.util.Random;

public class RandomBehaviour implements BotBehaviour {
    private Random random;
    private int counter;

    public RandomBehaviour() {
        random = new Random(System.currentTimeMillis());
        counter = 0;
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        counter = (counter+1) % 20;
        if (counter == 0) {
            return (float) random.nextGaussian() * 0.7f;
        }

        return 0.f;
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return false;
    }
}
