package Request;

import java.io.*;
import java.net.*;

/**
 * Class to client management
 */
public class Client implements Serializable {
    public final static int PORT = 50505;
    private final static int TIMEOUT = 3000;
    private final static int MAX_TIMEOUT_ATTEMPTS = 7;
    private static int timeout_attempts = 0;
    private static DatagramPacket sendPacket;
    public final static InetSocketAddress addr = new InetSocketAddress("localhost", PORT);
    public static DatagramSocket socket;

    static {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
        }
    }

    /**
     * Method to send command to server
     *
     * @param T - message(command) to send
     * @param t - flag
     * @throws IOException
     * @throws ClassNotFoundException
     */
    //t = 1 => common command; t != 1 => add (or same) command
    public static void sendCommand(Object T, int t) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        if (t == 1) objectOutputStream.writeObject(T.toString());
        else objectOutputStream.writeObject(T);
        objectOutputStream.flush();
        byte[] buff = byteArrayOutputStream.toByteArray();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, addr);
        sendPacket = packet;
        socket.send(packet);
    }

    /**
     * Method to receive answer from server
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SocketException
     */
    public static void receiveAnswer() throws IOException, ClassNotFoundException, SocketException {
        byte[] buff = new byte[8096];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            if (timeout_attempts == MAX_TIMEOUT_ATTEMPTS) {
                System.out.println("Превышено кол-во попыток переподключения.");
                exit();
            } else {
                System.out.println("Сервер временно недоступен, попытка переподключения...");
                timeout_attempts++;
                socket.send(sendPacket);
                receiveAnswer();
            }
        }
        byte[] data = packet.getData();
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
            System.out.println(objectInputStream.readObject());
            //System.out.println("Answer is  up");
        } catch (StreamCorruptedException ignored) {
            timeout_attempts = 0;
            //System.out.println(ignored);
        }
    }

    /**
     * Method to exit from client
     */
    public static void exit() {
        System.out.println("Клиент заканчивает свою работу...");
        socket.close();
        MainClient.flag = false;
    }
}
