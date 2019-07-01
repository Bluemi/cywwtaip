package manage;

import bots.Bot;
import bots.behaviour.GotoNextSupplyBehaviour;
import bots.behaviour.PaintBehaviour;
import bots.behaviour.PaintOverBehaviour;
import lenz.htw.cywwtaip.world.GraphNode;

public class GameManager {

    enum BotTask{
        ENERGY, PASSIVE, REPAINT
    }

    final int AMOUNT_OF_BOTTASKS = 3;

    private RateSystem evaluator;
    private Bot[] bots;

    private int[] scores;
    private int currentScore;

    private long energyUpdate = System.currentTimeMillis();
    private final long REFILL_ENERGY_PERIOD = 2000;

    private int playernumber;

    private final short BOTINDEX_NORMAL = 0;
    private final short BOTINDEX_MOBILE = 1;
    private final short BOTINDEX_WIDE = 2;

    private final short ENERGY_FIT_INDEX = 0;
    private final short PASSIVE_FIT_INDEX = 1;
    private final short REPAINT_FIT_INDEX = 2;

    private final long UPDATETIME = 0;

    private boolean energyOnTheWay = false;

    private long normalLastupdated = 0;
    private long mobileLastupdated = 0;
    private long wideLastupdated = 0;

    private BotTask currentTaskNormal;
    private BotTask currentTaskMobile;
    private BotTask currentTaskWide;

    private BotTask oldTaskNormal;
    private BotTask oldTaskMobile;
    private BotTask oldTaskWide;

    private boolean improveStrategy;

    public GameManager(Bot[] bots, int playernumber){
        this.bots = bots;
        this.evaluator = new RateSystem();
        this.improveStrategy = false;
        this.playernumber = playernumber;

        initBotTasks();
    }

    public void coordinateBots() {
        float[] normalFits = evaluator.generateRating(bots[BOTINDEX_NORMAL], playernumber);
        float[] mobileFits = evaluator.generateRating(bots[BOTINDEX_MOBILE], playernumber);
        float[] wideFits = evaluator.generateRating(bots[BOTINDEX_WIDE], playernumber);

        checkCurrentGameState(normalFits, mobileFits, wideFits);

        if (improveStrategy){
            updateStrategy();
        }
    }

    private void updateStrategy() {
        long currentTime = System.currentTimeMillis();

        if (oldTaskNormal != currentTaskNormal && currentTime-normalLastupdated > UPDATETIME) {
            applyBotTask(currentTaskNormal, bots[BOTINDEX_NORMAL], getBestOtherPlayer());
            oldTaskNormal = currentTaskNormal;
            normalLastupdated =currentTime;
        }

        if (oldTaskMobile != currentTaskMobile && currentTime - mobileLastupdated > UPDATETIME) {
            applyBotTask(currentTaskMobile, bots[BOTINDEX_MOBILE], getBestOtherPlayer());
            oldTaskMobile = currentTaskMobile;
            mobileLastupdated = currentTime;
        }

        if (oldTaskWide != currentTaskWide && currentTime - wideLastupdated > UPDATETIME){
            applyBotTask(currentTaskWide, bots[BOTINDEX_WIDE], getBestOtherPlayer());
            oldTaskWide = currentTaskWide;
            wideLastupdated = currentTime;
        }

        improveStrategy = false;
    }

    private void checkCurrentGameState(float[] normalFits, float[] mobileFits, float[] wideFits){
        checkTaskFinished();

        if (System.currentTimeMillis() - energyUpdate > REFILL_ENERGY_PERIOD && !energyOnTheWay) {
            float bestEnergyFit = Math.max(wideFits[ENERGY_FIT_INDEX], Math.max(normalFits[ENERGY_FIT_INDEX], mobileFits[ENERGY_FIT_INDEX]));

            if (wideFits[ENERGY_FIT_INDEX] == bestEnergyFit){
                currentTaskWide = BotTask.ENERGY;
            }
            else if (mobileFits[ENERGY_FIT_INDEX] == bestEnergyFit) {
                currentTaskMobile = BotTask.ENERGY;
            }
            else {
                currentTaskNormal = BotTask.ENERGY;
            }
            energyOnTheWay = true;
            improveStrategy = true;
        } else {

            // get currently the best task for every Unit
            BotTask normalsNewTask = getBestSuitedTask(normalFits);
            BotTask mobilesNewTask = getBestSuitedTask(mobileFits);
            BotTask widesNewTask = getBestSuitedTask(wideFits);

            if (currentTaskNormal != normalsNewTask && currentTaskNormal != BotTask.ENERGY){
                currentTaskNormal = normalsNewTask;
                improveStrategy = true;
            }

            if (currentTaskMobile != mobilesNewTask && currentTaskMobile != BotTask.ENERGY){
                currentTaskMobile = mobilesNewTask;
                improveStrategy = true;
            }

            if (currentTaskWide != widesNewTask && currentTaskWide != BotTask.ENERGY){
                currentTaskWide = widesNewTask;
                improveStrategy = true;
            }
        }
    }

    private void applyBotTask(BotTask task, Bot bot, int playernumber){
        switch(task){
            case ENERGY:
                bot.setBehaviour(new GotoNextSupplyBehaviour());
                break;
            case REPAINT:
                bot.setBehaviour(new PaintOverBehaviour(playernumber));
                break;
            case PASSIVE:
                bot.setBehaviour(new PaintBehaviour());
                break;
        }
    }

    public void updateGameState(int[] scores, float[] speeds){
        this.scores = scores;
        currentScore = scores[playernumber];

        for (Bot bot : bots){
            if(bot.isInSupply()){
                energyUpdate = System.currentTimeMillis();
                energyOnTheWay = false;
            }
        }
    }

    private void initBotTasks(){
        bots[BOTINDEX_NORMAL].setBehaviour(new GotoNextSupplyBehaviour());
        currentTaskNormal = BotTask.ENERGY;

        bots[BOTINDEX_MOBILE].setBehaviour(new PaintBehaviour());
        currentTaskMobile = BotTask.PASSIVE;

        bots[BOTINDEX_WIDE].setBehaviour(new PaintBehaviour());
        currentTaskWide = BotTask.PASSIVE;


    }

    private BotTask getBestSuitedTask(float[] fitValues){
        float bestValue = 0;
        int bestTask = 0;

        for (int i = 0; i < AMOUNT_OF_BOTTASKS; i++){
            if (fitValues[i] > bestValue) {
                bestValue = fitValues[i];
                bestTask = i;
            }
        }

        //if (bestTask == REPAINT_FIT_INDEX)
            return BotTask.REPAINT;

        //return BotTask.PASSIVE;

    }

    private int getBestOtherPlayer(){
        int bestEnemy = 0;
        int bestScore = 0;
        for (int i = 0; i < 3; i++){
            if (i!=playernumber){
                if (scores[i] > bestScore)
                    bestScore = scores[i];
                    bestEnemy = i;
            }
        }
        return bestEnemy;
    }

    private void checkTaskFinished(){
        for (Bot bot : bots){
            if (bot.hasFinished())
                bot.setBehaviour(new PaintOverBehaviour(getBestOtherPlayer()));
        }
    }

    private float getPointGainBalance(){
        return 0;
    }

    private GraphNode checkForCluster(){
        return null;
    }
}
