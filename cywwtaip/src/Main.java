import bots.Bot;
import bots.BotType;
import lenz.htw.cywwtaip.net.NetworkClient;
import math.Vector3D;

public class Main {
    private class ClientRunner implements Runnable {
        String teamName;
        String winSlogan;
        Bot bot;

        public ClientRunner(String teamName, String winSlogan, Bot bot) {
            this.teamName = teamName;
            this.winSlogan = winSlogan;
            this.bot = bot;
        }

        @Override
        public void run() {
            System.out.println("client " + teamName + " connected");

            NetworkClient client = new NetworkClient(null, teamName, winSlogan);
            int playerNumber = client.getMyPlayerNumber();
            while (client.isAlive()) {
                for (int botIndex = 0; botIndex < 3; botIndex++) {
                    bot.updatePosition(new Vector3D(client.getBotPosition(playerNumber, botIndex)));
                    float directionUpdate = bot.getDirectionUpdate();
                    if (directionUpdate != 0.f) {
                        client.changeMoveDirection(botIndex, directionUpdate);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
            }

            System.out.println("client " + teamName + " is out");
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.start();
    }

    private void start() {
        Bot bot0 = new Bot(BotType.NORMAL, new Vector3D());
        Thread thread0 = new Thread(new ClientRunner("team 0", "team 0 win", bot0));
        Bot bot1 = new Bot(BotType.NORMAL, new Vector3D());
        Thread thread1 = new Thread(new ClientRunner("team 1", "team 1 win", bot1));
        Bot bot2 = new Bot(BotType.NORMAL, new Vector3D());
        Thread thread2 = new Thread(new ClientRunner("team 2", "team 2 win", bot2));

        thread0.start();
        thread1.start();
        thread2.start();

        try {
            thread0.join();
            thread1.join();
            thread2.join();
        } catch (InterruptedException ignored) { }
    }
}
