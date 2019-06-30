package manage;

import bots.Bot;
import lenz.htw.cywwtaip.world.GraphNode;

public class GameManager {

    enum BotTasks{
        REPAINT, ENERGY, PASSIVE
    }

    private RateSystem evaluator;
    private Bot[] bots;

    private int currentScore;
    private int lastScore;

    private final short BOTINDEX_NOMAL = 0;
    private final short BOTINDEX_MOBILE = 1;
    private final short BOTINDEX_WIDE = 2;

    private boolean improveStrategy;

    public GameManager(Bot[] bots){
        this.bots = bots;
        this.evaluator = new RateSystem();
        this.improveStrategy = false;
    }

    public void coordinateBots() {
        float[] tasksFitValues = new float[12];

        for(Bot bot : bots){
            tasksFitValues = evaluator.generateRating(bot);
        }

        improveStrategy = checkCurrentGameState(tasksFitValues);

        if (improveStrategy){
            updateStrategy();
        }
    }

    private void updateStrategy(){
        //TODO redefines a bot's behavior
    }

    private boolean checkCurrentGameState(float[] fitValues){
        //TODO returns true if a bot need to change considering rating, current scores (+ ?).


        return false;
    }

    /*
    private float getPointGainBalance(){
        return 0;
    }
     */

    private void applyBotTask(BotTasks task, Bot bot){

    }

    private GraphNode checkForCluster(){
        return null;
    }
}
