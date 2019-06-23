package bots;

import bots.behaviour.BotBehaviour;
import bots.behaviour.GotoNextSupplyBehaviour;
import bots.behaviour.RandomBehaviour;
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
}
