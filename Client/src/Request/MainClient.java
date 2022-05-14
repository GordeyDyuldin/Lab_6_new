package Request;
import java.io.IOException;
import java.net.SocketException;

/**
 * Класс для запуска программы с методом main
 *
 * @author Dyuldin Gordey P3118
 */

public class MainClient {
    public static boolean flag = true;

    /**
     * Стандартный метод для запуска программы
     */

    public static void main(String[] args) throws IOException
    {
        System.out.println("Клиент запущен");
        while (flag) {
            try {
                new CommandHandlerClient();
                Client.receiveAnswer();
            } catch (SocketException | ClassNotFoundException e) {
                //System.out.println("Произошла следующая ошибка: \n" + e);
            }
        }
    }
}
