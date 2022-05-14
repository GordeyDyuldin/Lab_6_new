package Request;

import Spam.IncorrectValueException;
import Spam.LabWork;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Command controller client class
 */
public class CommandHandlerClient {

    Scanner scanner = new Scanner(System.in);
    public static String argument;
    public static String command;
    public static String error = "";
    private static boolean True = true;
    private static String strochka = "";
    public static boolean flag = true;
    public static boolean flag1 = true;
    private static boolean isScriptFile = true;
    public static LinkedList<String> line = new LinkedList<>();


    /**
     * Full command manager client
     */

    public CommandHandlerClient() throws IOException, ClassNotFoundException {
        try {
            try {
                if (flag) {
                    String[] temp = scanner.nextLine().toLowerCase(Locale.ROOT).split(" ");
                    command = temp[0];
                    argument = temp[1];
                } else {
                    String[] temp = strochka.toLowerCase(Locale.ROOT).split(" ");
                    command = temp[0];
                    System.out.println("Выполнение команды " + "\"" + " \"...");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ignored) {
                    }
                    argument = temp[1];
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
            if(flag1) {
                switch (command) {
                    case ("help"):
                    case ("info"):
                    case ("show"):
                    case ("save"):
                    case ("clear"):
                    case ("history"):
                    case ("remove_first"):
                    case ("print_field_descending_discipline"):
                        Client.sendCommand(new CommandSerialize(command), 1);
                        break;
                    case ("add"):
                        Client.sendCommand(new CommandSerialize(command), 1);
                        flag1 =false;
                        break;
                    case ("filter_contains_name"):
                        try {
                            if (!argument.isEmpty()) {
                                Client.sendCommand(new CommandSerialize(command, argument), 1);
                            } else {
                                System.out.println("Вы не ввели имя. Повторите попытку! ");
                                new CommandHandlerClient();
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Введен неверный аргумент.");
                            new CommandHandlerClient();
                        }
                        break;
                    case ("update_id"):
                        try {
                            if (Integer.parseInt(argument) <= 0)
                                throw new IncorrectValueException("Некоректное значение! ");
                            Client.sendCommand(new CommandSerialize(command, argument), 1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println("Ошибка. Вы не ввели аргумент команды.");
                            new CommandHandlerClient();
                        } catch (NumberFormatException e) {
                            System.err.println("Ошибка. Ввёден неправильный аргумент команды");
                            new CommandHandlerClient();
                        } catch (IncorrectValueException e) {
                            System.err.println("Ошибка. Id должен быть больше нуля.");
                            new CommandHandlerClient();
                        }
                        break;
                    case ("remove_by_id"):
                    case ("remove_greater"):
                    case ("replace_if_lower"):
                    case ("filter_by_personal_qualities_maximum"):
                        try {
                            if (argument == null) {
                                System.err.println("Ошибка. Вы не ввели аргумент команды.");
                                new CommandHandlerClient();
                            }
                            if (Integer.parseInt(argument) <= 0)
                                throw new IncorrectValueException("Некоректное значение! ");
                            Client.sendCommand(new CommandSerialize(command, argument), 1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println("Ошибка. Вы не ввели аргумент команды.");
                            new CommandHandlerClient();
                        } catch (NumberFormatException e) {
                            System.err.println("Ошибка. Ввёден неправильный аргумент команды");
                            new CommandHandlerClient();
                        } catch (IncorrectValueException e) {
                            System.err.println("Ошибка. Id должен быть больше нуля.");
                            new CommandHandlerClient();
                        }
                        break;
                    case ("execute_script"):
                        try {
                            readScriptFile();
                            if (isScriptFile) {
                                Client.sendCommand(new CommandSerialize(command, argument), 1);
                                flag = false;
                                executeScript();
                            } else new CommandHandlerClient();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println("Ошибка. Вы не ввели название файла");
                            new CommandHandlerClient();
                        }
                        break;
                    case ("exit"):
                        Client.sendCommand(new CommandSerialize(command), 1);
                        Client.exit();
                        break;
                    case ("name"):
                    case ("coordinates"):
                    case ("minimalpoint"):
                    case ("maximumpoint"):
                    case ("personalqualitiesmaximum"):
                    case ("difficulty"):
                    case ("discipline"):
                        Client.sendCommand(new CommandSerialize(command, argument), 1);
                        break;
                    default:
                        //Client.sendCommand(new CommandSerialize(command), 1);
                        System.err.println("Такой команды не найдено!Введите \"help\" для получения списка команд");
                        new CommandHandlerClient();
                }
            }
            else {
                for(int i =0; i < 7; i++) Client.sendCommand(new CommandSerialize(command), 1);
                flag1 = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Ошибка. Вы не ввели команду");
            if (flag) {
                try {
                    line.removeFirst();
                    strochka = line.getFirst();
                } catch (NoSuchElementException ignored) {
                    True = true;
                    System.out.println("Переход в консольный режим...");
                }
            }
            new CommandHandlerClient();
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }

    /**
     * Method to read script file for execute_script command
     */
    public void readScriptFile() throws IOException, ClassNotFoundException {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("Введи имя файла, который хотите запустить!"); //C:\Users\Boss\IdeaProjects\new proj\Lab_6_new

            FileReader in1 = new FileReader(input.nextLine());
            CSVParser records = CSVFormat.RFC4180.withHeader("command").parse(in1);

            label:
            for (CSVRecord record : records) {
                try {
                    String[] fileCommand = new String[1000];
                    fileCommand[0] = record.get("command");
                    switch (fileCommand[0]) {
                        case "command":
                        case "execute_script":
                            continue;
                        case "stop":
                            in1.close();
                            break label;
                    }
                    line.add(fileCommand[0]);


                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный синтаксис CSV! " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Введены неправильные данные!");
        }
    }

    /**
     * Method to execute script
     */

    public void executeScript() throws IOException, ClassNotFoundException {
        boolean temp = false;
        while (line.size() != 0) {
            strochka = line.getFirst();
            Client.receiveAnswer();
            new CommandHandlerClient();
            try {
                line.removeFirst();
            } catch (NoSuchElementException ignored) {
                temp = true;
            }
        }
        if (!temp) {
            flag = true;
            System.out.println("Переход в консольный режим...");
        }
    }
}

