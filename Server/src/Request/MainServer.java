package Request;

import Spam.IncorrectValueException;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.Scanner;

/**
 * Класс для запуска программы с методом main
 *
 * @author Dyuldin Gordey P3118
 */

public class MainServer {
    public static boolean flag = true;

    /**
     * Стандартный метод для запуска программы
     */

    public static void main(String[] args) throws IOException, ClassNotFoundException, IncorrectValueException {
        DatagramChannel dc = null;
        Selector selector = null;
        Server server = new Server(dc, selector);
        server.run();
        String path = "C:\\Users\\Boss\\IdeaProjects\\new proj\\Lab_6_new\\data.csv";
        Collection collection = new Collection(path);
        //String path1 = "/home/s312187/data.csv";
        Collection.println("Server is open for u ^)");

        while (flag) {
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                String input;
                while (true) {
                    input = scanner.nextLine().toLowerCase();
                    if (input.equals("exit")) {
                        collection.exit();
                    } else if (input.equals("save")) {
                        try {
                            collection.save(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            try {
                server.receiveCommands();
                server.commandHandler(collection);
                server.sendToClient();
            }  catch (NullPointerException e) {
                Collection.printer("Произошла следующая ошибка: \n" + e);
            }
        }
    }
}

