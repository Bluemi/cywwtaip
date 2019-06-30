package bots.behaviour;

import bots.Bot;

public class PaintOverBehaviour implements BotBehaviour {
    private int playerToOverPaint;
    private CompletePathBehaviour pathBehaviour;

    public PaintOverBehaviour(int playerToOverPaint) {
        this.playerToOverPaint = playerToOverPaint;
        this.pathBehaviour = null;
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        return 0.f;
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return false;
    }
}
