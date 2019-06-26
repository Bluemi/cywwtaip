import bots.Bot;
import bots.BotType;
import lenz.htw.cywwtaip.net.NetworkClient;
import math.Vector3D;

public class Main {
    private class ClientRunner implements Runnable {
        NetworkClient client;
        String teamName;
        String winSlogan;
        int playerNumber;

        public ClientRunner(String teamName, String winSlogan) {
            this.teamName = teamName;
            this.winSlogan = winSlogan;
        }

        @Override
        public void run() {
            client = new NetworkClient(null, teamName, winSlogan);
            System.out.println("client " + teamName + " connected!");
            playerNumber = client.getMyPlayerNumber();

            Bot[] bots = new Bot[] {
                    new Bot(BotType.NORMAL, client.getGraph()[0]),
                    new Bot(BotType.MOBILE, client.getGraph()[0]),
                    new Bot(BotType.WIDE, client.getGraph()[0])
            };

            while (client.isAlive()) {
                for (int botIndex = 0; botIndex < 3; botIndex++) {
                    Bot bot = bots[botIndex];
                    bot.updatePosition(new Vector3D(client.getBotPosition(playerNumber, botIndex)));
                    bot.updateDirection(new Vector3D(client.getBotDirection(botIndex)));
                    float directionUpdate = bot.getDirectionUpdate();

                    if (directionUpdate != 0.f) {
                        client.changeMoveDirection(botIndex, directionUpdate);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
            }
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.start();
    }

    private void start() {
        Thread thread0 = new Thread(new ClientRunner("team 0", "team 0 win"));
        Thread thread1 = new Thread(new ClientRunner("team 1", "team 1 win"));
        Thread thread2 = new Thread(new ClientRunner("team 2", "team 2 win"));

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
