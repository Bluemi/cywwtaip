package bots;

import math.Vector3D;

public class Bot {
    private BotType botType;
    private Vector3D position;

    public Bot(BotType botType, Vector3D position) {
        this.botType = botType;
        this.position = position;
    }

    public void updatePosition(Vector3D position) {
        this.position = position;
    }
}
