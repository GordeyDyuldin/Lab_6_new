package Request;

import Spam.IncorrectValueException;
import Spam.LabWork;
import Spam.Log;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

/**
 * Class to manage server
 */
public class Server implements Serializable {
    private static final int PORT = 50505;
    private static final ByteBuffer buffer = ByteBuffer.allocate(8096);
    private static String[] command;
    public static SocketAddress socketAddress;
    static Selector selector;
    static DatagramChannel dc;
    public static Log log;
    public static LinkedList<LabWork> linkedList = new LinkedList<>();
    public static String methodToContinue = "";

    public Server(DatagramChannel dc, Selector selector) throws IOException {
        this.dc = dc;
        this.selector = selector;
        log = new Log("log.txt");
    }

    /**
     * Method to run server
     */
    public void run() throws IOException
    {
        dc = DatagramChannel.open();
        dc.configureBlocking(false);
        selector = Selector.open();
        dc.register(selector, SelectionKey.OP_READ);
        InetSocketAddress address = new InetSocketAddress("localhost", PORT);
        try
        {
            dc.bind(address);
        }
        catch (BindException e)
        {
            log.logger.warning("Адрес уже используется. Завершение работы сервера");
            System.exit(0);
        }
    }

    /**
     * Method to receive commands from client
     */
    public void receiveCommands() throws IOException, ClassNotFoundException {
        selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        for (SelectionKey key : keys) {
            if (key.isReadable()) {
                buffer.clear();
                socketAddress = dc.receive(buffer);
                buffer.flip();
                int limit = buffer.limit();
                byte[] bytes = new byte[limit];
                buffer.get(bytes, 0, limit);
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
                String msg = (String) objectInputStream.readObject();
                command = msg.split(" ", 2);
                if (command.length == 2)
                    log.logger.info("Получена команда " + command[0] + " с аргументом " + command[1] + " от " + socketAddress);
                else log.logger.info("Получена команда " + msg + " от " + socketAddress);
            }
        }
    }

    /**
     * Method to start to manage commands
     */
    public void commandHandler(Collection collection) {
        if (command.length == 2) {
            collection.CommandServer(command[0], command[1]);
        } else if (command.length == 1) collection.CommandServer(command[0]);
    }

    /**
     * Method to send answer to client
     */
    public void sendToClient() throws IOException, IncorrectValueException {
        buffer.clear();
        buffer.flip();
        byte[] buff = Collection.execute().toByteArray();
        final int INCREMENT = 8096;
        for (int position = 0, limit = INCREMENT, capacity = 0; buff.length > capacity; position = limit,
                limit += INCREMENT) {
            byte[] window = Arrays.copyOfRange(buff, position, limit);
            capacity += limit - position;
            dc.send(ByteBuffer.wrap(window),socketAddress);
        }
    }
}
