import bots.Bot;
import bots.BotType;
import bots.behaviour.MoveLogic;
import math.Vector3D;

import java.util.ArrayDeque;

public class Test {
    public static void main(String[] args) {
        test5();
    }

    private static void test1() {
        Bot bot = new Bot(BotType.NORMAL);
        bot.updatePosition(new Vector3D(0, 1, 0));
        bot.updateDirection(new Vector3D(1, 0, 0));

        Vector3D target = new Vector3D(0, 0, 1);
        float update = MoveLogic.getDirectionUpdateToPosition(bot, target);
        System.out.println("update: " + update);
    }

    private static void test2() {
        Bot bot = new Bot(BotType.NORMAL);
        bot.updatePosition(new Vector3D(0.007999489f, 0.0f, (float)-6.3995925E-5));
        bot.updateDirection(new Vector3D(0.4265745f, -0.61627996f, -0.66199195f));

        Vector3D target = new Vector3D(0.f, 0.f, -1.f);
        float update = MoveLogic.getDirectionUpdateToPosition(bot, target);
        System.out.println("update: " + update);
    }

    private static void test3() {
        Vector3D a = new Vector3D(0.007999489f, 0.0f, (float)-6.3995925E-5);
        Vector3D b = new Vector3D(-0.99996805f, (float)1.1180514E-7, 0.007999744f);
        float angle = Vector3D.getAngleBetween(a, b);
        System.out.println("angle: " + angle);

    }

    private static void test4() {
        Vector3D a = new Vector3D(1.f/(float)Math.sqrt(2.f), 1.0f/(float)Math.sqrt(2.f), 0.f);
        Vector3D b = a.normalized();
        System.out.println(a.getLength());
        System.out.println(b.getLength());
        System.out.println(a);
        System.out.println(b);
    }

    private static void test5() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.add(1);
        deque.add(2);
        deque.add(3);

        while (!deque.isEmpty()) {
            System.out.println(deque.poll());
        }
    }
}
