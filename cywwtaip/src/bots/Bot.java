package bots;

import math.Vector3D;

public class Bot {
    private BotType botType;
    private Vector3D position;
    private BotBehaviour behaviour;

    public Bot(BotType botType, Vector3D position) {
        this.botType = botType;
        this.position = position;
        this.behaviour = new RandomBehaviour();
    }

    public void updatePosition(Vector3D position) {
        this.position = position;
    }

    public float getDirectionUpdate() {
        return behaviour.getMoveDirectionUpdate(this);
    }
}
