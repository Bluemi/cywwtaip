package bots.behaviour;

import bots.Bot;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.List;

public class PaintOverBehaviour implements BotBehaviour {
    private int playerToOverPaint;
    private DriveToPointBehaviour driveToStartPointBehaviour;
    private CompletePathBehaviour paintOverBehaviour;
    private boolean isOnPath;

    public PaintOverBehaviour(int playerToOverPaint) {
        this.playerToOverPaint = playerToOverPaint;
        this.driveToStartPointBehaviour = null;
        this.paintOverBehaviour = null;
        this.isOnPath = false;
    }

    @Override
    public void init(Bot bot) {
        GraphNode startNode = GraphInformation.getClosestGraphNodeWithPredicate(
                bot.getCurrentGraphNode(),
                g -> g.owner == playerToOverPaint
        );
        this.driveToStartPointBehaviour = new DriveToPointBehaviour(startNode);
        this.driveToStartPointBehaviour.init(bot);

        List<GraphNode> pathToOverwrite = GraphInformation.getPathWithPredicate(
                startNode,
                g -> g.owner == playerToOverPaint
        );

        this.paintOverBehaviour = new CompletePathBehaviour(pathToOverwrite);
        this.paintOverBehaviour.init(bot);
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        if (!isOnPath) {
            if (this.driveToStartPointBehaviour.hasFinished(bot)) {
                isOnPath = true;
            } else {
                return this.driveToStartPointBehaviour.getMoveDirectionUpdate(bot);
            }
        }

        return this.paintOverBehaviour.getMoveDirectionUpdate(bot);
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return isOnPath && this.paintOverBehaviour.hasFinished(bot);
    }
}
