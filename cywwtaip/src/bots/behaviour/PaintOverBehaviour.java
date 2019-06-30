package bots.behaviour;

import bots.Bot;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.List;
import java.util.function.Predicate;

public class PaintOverBehaviour implements BotBehaviour {
    private static final int MAX_PATH_LENGTH = 50;

    int playerToOverPaint;
    private DriveToPointBehaviour driveToStartPointBehaviour;
    private CompletePathBehaviour paintOverBehaviour;
    private boolean isOnPath;
    private boolean initialized;

    public PaintOverBehaviour(int playerToOverPaint) {
        this.playerToOverPaint = playerToOverPaint;
        this.driveToStartPointBehaviour = null;
        this.paintOverBehaviour = null;
        this.isOnPath = false;
        this.initialized = false;
    }

    @Override
    public void init(Bot bot) {
        initialized = false;

        GraphNode startNode = GraphInformation.getClosestGraphNodeWithPredicate(
                bot.getCurrentGraphNode(),
                g -> g.owner-1 == playerToOverPaint
        );

        if (startNode == null) {
            return;
        }

        this.driveToStartPointBehaviour = new DriveToPointBehaviour(startNode);
        this.driveToStartPointBehaviour.init(bot);
        Predicate<GraphNode> pathPredicate;

        if (bot.ignoresObstacles()) {
            pathPredicate = graphNode -> graphNode.owner-1 == playerToOverPaint;
        } else {
            pathPredicate = graphNode -> graphNode.owner-1 == playerToOverPaint && !graphNode.blocked;
        }

        List<GraphNode> pathToOverwrite = GraphInformation.getPathWithPredicate(
                startNode,
                pathPredicate,
                MAX_PATH_LENGTH
        );

        this.paintOverBehaviour = new CompletePathBehaviour(pathToOverwrite);
        this.paintOverBehaviour.init(bot);
        this.initialized = true;
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        if (hasFinished(bot)) {
            init(bot);
        }

        if (!initialized) {
            init(bot);
            return 0.f;
        }
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
