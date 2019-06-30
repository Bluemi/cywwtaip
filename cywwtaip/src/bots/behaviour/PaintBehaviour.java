package bots.behaviour;

import bots.Bot;
import bots.BotType;
import graphInformation.GraphInformation;
import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.Objects;

public class PaintBehaviour implements BotBehaviour {
    public static final float MAX_ENEMY_DISTANCE = GraphInformation.AVERAGE_NEIGHBOR_DISTANCE * 12.f;
    public static final float MAX_ENEMY_DISTANCE_SQUARED = MAX_ENEMY_DISTANCE * MAX_ENEMY_DISTANCE;
    private BotBehaviour currentBehaviour;

    private GraphNode getClosestEnemyStartNode(Bot bot) {
        GraphNode closestEnemyStartNode = null;

        for (Integer enemyPlayer : MoveLogic.getEnemyPlayerNumbers(bot.getPlayerNumber())) {
            GraphNode enemyStartNode = GraphInformation.getClosestGraphNodeWithPredicate(
                    bot.getCurrentGraphNode(),
                    g -> g.owner-1 == enemyPlayer
            );

            if (enemyStartNode == null)
                continue;

            float distanceSquaredToEnemy = GraphInformation.getDistanceSquaredBetween(bot.getCurrentGraphNode(), enemyStartNode);
            if (distanceSquaredToEnemy > MAX_ENEMY_DISTANCE_SQUARED)
                continue;

            if (closestEnemyStartNode == null) {
                closestEnemyStartNode = enemyStartNode;
                continue;
            }

            float otherEnemySquaredDistance = GraphInformation.getDistanceSquaredBetween(bot.getCurrentGraphNode(), closestEnemyStartNode);
            if (distanceSquaredToEnemy < otherEnemySquaredDistance) {
                closestEnemyStartNode = enemyStartNode;
            }
        }

        return closestEnemyStartNode;
    }

    @Override
    public void init(Bot bot) {
        chooseBehaviour(bot);
    }

    private void chooseBehaviour(Bot bot) {
        GraphNode closestEnemyStartNode = getClosestEnemyStartNode(bot);
        if (closestEnemyStartNode != null) {
            this.currentBehaviour = new PaintOverBehaviour(closestEnemyStartNode.owner-1);
        } else {
            GraphNode randomNode = GraphInformation.getRandomNode(bot.getCurrentGraphNode());
            this.currentBehaviour = new DriveToPointBehaviour(randomNode);
        }
        this.currentBehaviour.init(bot);
    }

    @Override
    public float getMoveDirectionUpdate(Bot bot) {
        if (this.currentBehaviour.hasFinished(bot)) {
            chooseBehaviour(bot);
        }

        if (currentBehaviour instanceof PaintOverBehaviour) {
            PaintOverBehaviour p = (PaintOverBehaviour) currentBehaviour;
            if (bot.getPlayerNumber() == 0 && bot.botType == BotType.WIDE) {
                System.out.println("playerToOverPaint: " + p.playerToOverPaint);
            }
        }

        return this.currentBehaviour.getMoveDirectionUpdate(bot);
    }

    @Override
    public boolean hasFinished(Bot bot) {
        return false;
    }
}
