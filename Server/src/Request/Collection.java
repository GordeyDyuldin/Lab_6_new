package Request;

import Spam.*;
import org.apache.commons.csv.*;

import java.io.*;
import java.util.*;
import java.time.ZonedDateTime;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

/**
 * Класс для работы с коллекцией LinkedList
 */

public class Collection {
    private static String command;
    private static String argument;
    public static String[] history = new String[15];
    private static ByteArrayOutputStream byteArrayOutputStream;
    private static ObjectOutputStream objectOutputStream;
    private static int i;
    private static int IdForUpdate;
    private static int progressTrek = 0;
    private static int progressTrekExScr = 0;
    private static String fileAdress = "";


    static LinkedList<LabWork> collection = new LinkedList<>();
    static ZonedDateTime collCreation = ZonedDateTime.now();

    /**
     * Конструктор класса для работы с коллекцией LinkedList ", который инициализирует коллекцию
     *
     * @param path путь к CSV файлу коллекции
     */

    public Collection(String path) throws IncorrectValueException, IOException {
        load(path);
        Sortir();
        fileAdress = path;
    }

    /**
     * Метод для стандартной сортировки коллекции
     */
    public static void Sortir() {
        collection.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
    }

    public void CommandServer(String command, String argument) {
        Collection.command = command;
        Collection.argument = argument;
    }

    public void CommandServer(String command) {
        Collection.command = command;
    }

    /**
     * Метод для выхода из программы
     * Имеет возможности по выводу информации о доступных командах, информации о коллекции, информации о элементах коллекции и др.
     */

    public static ByteArrayOutputStream execute() throws IOException, IncorrectValueException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        String temporaryCom = command;
        String temporaryArg = argument;
        argument = null;
        try {
            updateHistory(temporaryCom);
            switch (Server.methodToContinue.isEmpty() ? temporaryCom : Server.methodToContinue) {
                case "":
                    break;
                case "help":
                    return help();
                case "info":
                    return info();
                case "show":
                    return show();
                case "add":
                    if(Server.methodToContinue.isEmpty())
                        return add();
                case "update_id":
                    if (Server.methodToContinue.isEmpty())
                        return update_id(parseInt(temporaryArg));
                    else {
                        Server.methodToContinue = "";
                        return update_id2(temporaryCom, temporaryArg);
                    }
                case "remove_by_id":
                    return remove_by_id(parseInt(temporaryArg));
                case "clear":
                    return clear();
                case "save":
                    return save(fileAdress);
                case "execute_script":
                    objectOutputStream.writeObject("Переход в скриптовый режим");
                    objectOutputStream.flush();
                    return byteArrayOutputStream;
                case "remove_first":
                    return remove_first();
                case "remove_greater":
                    return remove_greater(parseInt(temporaryArg));
                case "history":
                    return history();
                case "filter_by_personal_qualities_maximum":
                    return filter_by_personal_qualities_maximum(parseInt(temporaryArg));
                case "filter_contains_name":
                    return filter_contains_name(temporaryArg);
                case "print_field_descending_discipline":
                    return print_field_descending_discipline();
                default:
                    printer("Несуществующая команда. Введите help для справки.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            printer("Отсутствует аргумент команды.");
        } catch (IOException e) {
            printer("Ошибка при записи файла!");
        }
        return null;
    }

    public static ByteArrayOutputStream help() throws IOException {
        StringBuilder data = new StringBuilder();
        data.append("help - вывести справку по доступным командам\n" +
                "info - вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show - вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add \"element\" - добавить новый элемент в коллекцию\n" +
                "update_id \"element\" - обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_by_id \"id\" - удалить элемент из коллекции по его id\n" +
                "clear - очистить коллекцию\n" +
                "save - сохранить коллекцию в файл\n" +
                "execute_script \"File_name\" - считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit - завершить программу (без сохранения в файл)\n" +
                "remove_first : удалить первый элемент из коллекции\n" +
                "remove_greater \"element\" : удалить из коллекции все элементы, превышающие заданный\n" +
                "history : вывести последние 14 команд (без их аргументов)\n" +
                "help - вывести справку по доступным командам\n" +
                "filter_contains_name \"name\" : вывести элементы, значение поля name которых содержит заданную подстроку\n" +
                "filter_by_personal_qualities_maximum personalQualitiesMaximum : вывести элементы, значение поля personalQualitiesMaximum которых равно заданному\n" +
                "print_field_descending \"discipline\" : вывести значения поля discipline всех элементов в порядке убывания\n");
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        println(data.substring(0));
        return byteArrayOutputStream;
    }

    public static ByteArrayOutputStream load(String path) throws IncorrectValueException, IOException {
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            StringBuilder data = new StringBuilder();
            try {
                FileReader in = new FileReader(path);
                CSVParser records = CSVFormat.RFC4180.withHeader
                        ("name",
                                "cordX",
                                "cordY",
                                "minimalPoint",
                                "maximumPoint",
                                "personalQualitiesMaximum",
                                "difficulty",
                                "disciplineName",
                                "disciplinePracticeHours").parse(in);
                int i = 0;
                for (CSVRecord record : records) {
                    if (i == 0) {
                        i++;
                        continue;
                    }
                    try {
                        String name = record.get("name");
                        Integer cordX = Integer.parseInt(record.get("cordX"));
                        Double cordY = Double.parseDouble(record.get("cordY"));
                        Double minimalPoint = Double.parseDouble(record.get("minimalPoint"));
                        Integer maximumPoint = Integer.parseInt(record.get("maximumPoint"));
                        float personalQualitiesMaximum = Float.parseFloat(record.get("personalQualitiesMaximum"));
                        Difficulty difficulty = Difficulty.valueOf(record.get("difficulty"));
                        String disciplineName = record.get("disciplineName");
                        Integer disciplinePracticeHours = Integer.parseInt(record.get("disciplinePracticeHours"));

                        LabWork newCollectionElement = new LabWork(name, new Coordinates(cordX, cordY), minimalPoint, maximumPoint,
                                personalQualitiesMaximum, difficulty, new Discipline(disciplineName, disciplinePracticeHours));
                        collection.add(newCollectionElement);

                    } catch (IllegalArgumentException e) {
                        data.append("Неверный синтаксис CSV!");
                    }
                }
                if (collection.size() > 0) {
                    data.append("Коллекция успешно подгружена из файла!");
                } else {
                    data.append("Файл пуст!");
                }
                in.close();
            } catch (IOException e) {
                data.append("Неправильно указан путь к файлу!");
            }

            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            printer(data.substring(0));
            return byteArrayOutputStream;
        } catch (NullPointerException e) {
            printer("Возникла ошибка при загрузке файла: " + e);
        }
        return null;
    }

    /**
     * Вывод информации о коллекции в консоль
     */

    public static ByteArrayOutputStream info() throws IOException {
        StringBuilder data = new StringBuilder();
        data.append("Тип коллекции: ").append
                (collection.getClass().getSimpleName()).append
                (", дата инициализации: ").append(collCreation).append
                (", количество элементов ").append(collection.size());
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Вывод всех элементов коллекции, если коллекция не пуста
     */

    public static ByteArrayOutputStream show() throws IOException {
        StringBuilder data = new StringBuilder();
        if (!collection.isEmpty()) {
            for (LabWork o : collection) {
                data.append(o);
            }
        } else {
            data.append("Невозможно вывести элементы, так как коллекция пуста!");
        }
        data.append("Конец коллекции");
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        println(data.substring(0));
        return byteArrayOutputStream;
    }

    /**
     * Контекстное меню для добавления нового элемента в коллекцию (с заполнением полей) и вывода информации о новом элементе
     */

    private static String rawName;
    private static Coordinates coord;
    private static Double rawMin;
    private static Integer rawMax;
    private static Float rawPersQualMax;
    private static Difficulty dific;
    private static Discipline dicsip;

    public static ByteArrayOutputStream add() throws IOException, IncorrectValueException {
        StringBuilder data = new StringBuilder();
        Server.methodToContinue = "";
        switch (progressTrek) {
            case (0):
                data.append("Для добавления нового элемента в коллекцию заполните значения полей:\n");
                data.append("Введите имя: ");
                progressTrek++;
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
            case (1):
                //установка имени
                rawName = command;
                data.append("Введите координату X и Y (через знак ','): ");
                progressTrek++;
                progressTrek++;
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
            case (3):
                //установка Cord X and Y
                var rawCoords = command.split(",");
                coord = new Coordinates(Integer.parseInt( rawCoords[0]), Double.parseDouble(rawCoords[1]));
                data.append("Введите минимальное значение: ");
                progressTrek++;
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
            case (4):
                //установка минимальное значение
                rawMin = Double.parseDouble(command);
                data.append("Введите максимальное значение: ");
                progressTrek++;
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
            case (5):
                //установка максимальное значение
                rawMax = Integer.parseInt(command);
                data.append("Введите количество личных качеств: ");
                progressTrek++;
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
            case (6):
                //установка количество личных качеств
                rawPersQualMax = Float.parseFloat(command);
                data.append("Доступные уровень сложности: ").append(Stream.of(Difficulty.values()));
                data.append("Введите уровень сложности: ");
                progressTrek++;
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
            case (7):
                //установка уровень сложности
                dific.valueOf(command);
                data.append("Введите наименование дисциплины и кол-во часов практики (через знак ','): ");
                progressTrek++;
                progressTrek++;
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
            case (9):
                //установка getDiscipline
                var rawDicsip = command.split(",");
                dicsip = new Discipline( rawDicsip[0], Integer.parseInt(rawDicsip[1]));
                progressTrek = 0;
                if(rawName.isEmpty() && coord != null && rawMin != null && rawMax != null && rawPersQualMax != null && dific != null){
                try {
                    LabWork lab = new LabWork(
                            rawName,
                            coord,
                            rawMin,
                            rawMax,
                            rawPersQualMax,
                            dific,
                            dicsip);
                    collection.add(lab);
                    data.append("Коллекция добавлена:");
                    data.append(lab);
                    Server.methodToContinue = command;
                } catch (Exception e) {
                    data.append(e.getMessage());
                }
                }

                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                return byteArrayOutputStream;
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Вызов контекстного меню для обновление поля, которое задается пользователем
     *
     * @param id ID организации (должен быть больше 0)
     */

    public static ByteArrayOutputStream update_id(int id) throws IOException, IncorrectValueException // НУЖНО ПЕРЕДЕЛАТЬ
    {
        StringBuilder data = new StringBuilder();
        if (!collection.isEmpty()) {
            if (id > 0 && id < collection.size()) {
                for (LabWork o : collection) {
                    if (o.getId() == id) {
                        data.append("Укажите имя поля, которое вы хотите изменить, а также значение которое хотите установить через запятую:\n" +
                                "\"name\" \n" +
                                "\"coordinates\" (укажите 2 значение через запятую)\n" +
                                "\"minimalpoint\" \n" +
                                "\"maximumpoint\" \n" +
                                "\"personalqualitiesmaximum\" \n" +
                                "\"difficulty\" \n" +
                                "\"discipline\" (укажите 2 значение через запятую.1) Название 2) Кол-во часов практики)\n");
                        Server.methodToContinue = command;
                    }
                }
            } else {
                data.append("ID должен быть > 0! Id должен быть меньше ").append(collection.size());
            }
        } else {
            data.append("Невозможно обновить элементы, так как коллекция пуста!");
        }
        IdForUpdate = id;
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    public static ByteArrayOutputStream update_id2(String command11, String argument11) throws IOException, IncorrectValueException {
        StringBuilder data = new StringBuilder();
        println(command11 + " " + argument11);
        for (LabWork o : collection) {
            if (o.getId() == IdForUpdate) {
                switch (command11) {
                    case "name":
                        try {
                            o.setName(argument11);
                        } catch (IncorrectValueException e) {
                            data.append("При изменении элемента \"name\" произошла ошибка. Повторите попытку заново");
                        }
                        data.append("Поле имени обновлено!");
                        break;
                    case "coordinates":
                        try {
                            o.setCoordinates(argument11);
                        } catch (IncorrectValueException e) {
                            data.append("При изменении элемента \"coordinates\" произошла ошибка. Повторите попытку заново");
                        }
                        data.append("Поле координат обновлено!");
                        break;

                    case "minimalpoint":
                        try {
                            o.setMinimalPoint(Double.parseDouble(argument11));
                        } catch (IncorrectValueException e) {
                            data.append("При изменении элемента \"minimalpoint\" произошла ошибка. Повторите попытку заново");
                        }
                        data.append("Поле минимальных баллов обновлено!");
                        break;
                    case "maximumpoint":
                        try {
                            o.setMaximumPoint(Integer.parseInt(argument11));
                        } catch (IncorrectValueException e) {
                            data.append("При изменении элемента \"maximumpoint\" произошла ошибка. Повторите попытку заново");
                        }
                        data.append("Поле максимальных баллов обновлено!");
                        break;
                    case "personalqualitiesmaximum":
                        try {
                            o.setPersonalQualitiesMaximum(Float.parseFloat(argument11));
                        } catch (IncorrectValueException e) {
                            data.append("При изменении элемента \"personalqualitiesmaximum\" произошла ошибка. Повторите попытку заново");
                        }
                        data.append("Поле максимальных личных качеств обновлено!");
                        break;
                    case "difficulty":
                        o.setDifficulty(argument11);
                        data.append("Поле уровня сложности обновлено!");
                        break;
                    case "discipline":
                        o.setDiscipline(argument11);
                        data.append("Поле дисциплины обновлено!");
                        break;
                    case "exit":
                        data.append("Обновление поля было отменено пользователем!");
                        break;
                    default:
                        data.append("Произошла ошибка при обновлении поля!");
                        break;
                }
            }
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Удаление элемента из коллекции с указанным ID организации
     *
     * @param id ID организации (должен быть больше 0)
     */

    public static ByteArrayOutputStream remove_by_id(int id) throws IOException {
        StringBuilder data = new StringBuilder();
        int temporaryVal = 0;
        if (!collection.isEmpty()) {
            if (id > 0) {
                for (LabWork o : collection) {
                    if (o.getId() != id) {
                        temporaryVal++;
                    }
                }
                collection.remove(temporaryVal - 1);
                data.append("Элемент коллекции успешно удален!");
            } else {
                data.append("ID должен быть > 0!");
            }
            //data.append("Элемент с указанным ID (").append(id).append(") не найден! Попробуйте ввести команду еще раз!");
        } else {
            data.append("Невозможно вывести элементы, так как коллекция пуста!");
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Очистка коллекции и вывод сообщения о том, что коллекция очищена
     */

    public static ByteArrayOutputStream clear() throws IOException {
        StringBuilder data = new StringBuilder();
        collection.clear();
        data.append("Коллекция очищена!");

        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Сохранение коллекции по указанному пользователю пути
     *
     * @param path путь к CSV, в котором будет сохраанена коллекция
     * @throws IOException если произошла ошибка при попытке сохранить файл
     */

    public static ByteArrayOutputStream save(String path) throws IOException {
        StringBuilder data = new StringBuilder();
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw); CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader
                ("name",
                        "cordX",
                        "cordY",
                        "minimalPoint",
                        "maximumPoint",
                        "personalQualitiesMaximum",
                        "difficulty",
                        "disciplineName",
                        "practiceHours"))) {
            for (LabWork o : collection) {
                csvPrinter.printRecord(o.getName(),
                        o.getCoordinates().getX(),
                        o.getCoordinates().getY(),
                        o.getMinimalPoint(),
                        o.getMaximumPoint(),
                        o.getPersonalQualitiesMaximum(),
                        o.getDifficulty(),
                        o.getDiscipline().getName(),
                        o.getDiscipline().getPracticeHours());
            }
            csvPrinter.flush();
            data.append("CSV-коллекция сохранена: ").append(path);
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Команда завершения программ
     */

    public void exit() {
        System.out.println("Вызвана команда выхода из программы без сохранения!");
        System.exit(0);
    }

    /**
     * Команда удаления первого элемента из коллекции
     */

    public static ByteArrayOutputStream remove_first() throws IOException {
        StringBuilder data = new StringBuilder();
        if (!collection.isEmpty()) {
            collection.remove(0);
            data.append("Первый элемент коллекции успешно удалён!");
        } else {
            data.append("Невозможно удалить первый элемент, так как коллекция пуста!");
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Удаление элемента коллекции, превышающее указанное пользователем значение
     */

    public static ByteArrayOutputStream remove_greater(int someshitperemenn) throws IOException {
        StringBuilder data = new StringBuilder();
        if (!collection.isEmpty()) {
            int sizeBefore = collection.size();
            collection.removeIf(p -> p.comparePersonalQualitiesMaximum((long) someshitperemenn) == 1);
            int calc = sizeBefore - collection.size();
            data.append("Из коллекции удалено " + calc + " элементов, превышающиих максимальное значение " + someshitperemenn);
        } else {
            data.append("Невозможно удалить элементы, так как коллекция пуста!");
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Вывод последних 14 команд введенных пользователем
     * <p>
     * Если объект в коллекции равен null, то он не выводится
     */

    private static void updateHistory(String command11) {
        if (i < 14) {
            history[i] = command11;
            i++;
        } else {
            history[14] = command11;
            for (i = 0; i < 14; i++) {
                history[i] = history[i + 1];
            }
        }
    }

    public static ByteArrayOutputStream history() throws IOException {
        StringBuilder data = new StringBuilder();
        int normalI;
        ArrayList<String> time = new ArrayList<>(Arrays.asList(GetHistory()).subList(0, 14));
        int timVal = time.size();

        for (int i = 0; i < timVal; i++) {
            if (time.get(i) != null) {
                normalI = i + 1;
                data.append("Команда №" + normalI + ": " + time.get(i) + "\n");
            }
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Команда вывода кол-ва максимальных качеств, которые совпадают с заданным
     */

    public static ByteArrayOutputStream filter_by_personal_qualities_maximum(int abvg) throws IOException {
        StringBuilder data = new StringBuilder();
        if (!collection.isEmpty()) {
            for (LabWork o : collection) {
                if (o.getPersonalQualitiesMaximum() == abvg) {
                    data.append("ID элемента равного заданному количеству максимальных качеств: ").append(o.getId() + "\n");
                }
            }
        } else {
            data.append("Невозможно удалить элементы, так как коллекция пуста!");
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Команда вывода ID объекта, чьё имя совпало с заданным
     *
     * @param argument2
     */

    public static ByteArrayOutputStream filter_contains_name(String argument2) throws IOException {
        StringBuilder data = new StringBuilder();
        int ivi = 0;
        int bibi = 0;
        try {
            for (LabWork o : collection) {
                ivi++;
                if (o.getName().equals(argument2)) {
                    data.append("ID элемента: " + o.getId());
                    bibi++;
                }
                if (bibi == 0 && ivi == collection.size())
                    data.append("Элемента с таким именем не найдено");
            }
        } catch (Exception e) {
            data.append(e.getMessage());
            data.append("Введены неправильные данные!");
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Команда вывода объектов типа "discipline" в порядке убывания
     */

    public static ByteArrayOutputStream print_field_descending_discipline() throws IOException {
        StringBuilder data = new StringBuilder();
        if (!collection.isEmpty()) {
            int i = 0;
            for (LabWork o : collection) {
                i++;
                data.append("№" + i + ": " + o.getDiscipline().getName() + " " + o.getDiscipline().getPracticeHours());
            }
        } else {
            data.append("Невозможно вывести \"discipline\", так как коллекция пуста!");
        }
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Выполнение консольных команд из файла
     */

    private static ByteArrayOutputStream execute_script() throws IOException   //   ././src/newFile.csv
    {
        StringBuilder data = new StringBuilder();
            try {
                Scanner input = new Scanner(System.in);
                data.append("Введи имя файла, который хотите запустить!");

                FileReader in1 = new FileReader(input.nextLine());
                CSVParser records = CSVFormat.RFC4180.withHeader("command").parse(in1);

                for (CSVRecord record : records) {
                    try {
                        String[] fileCommand = new String[1000];
                        fileCommand[0] = record.get("command");
                        if (fileCommand[0].equals("command"))
                            continue;
                        else if (fileCommand[0].equals("execute_script"))
                            continue;
                        System.out.println("\n" + fileCommand[0]);
                        //execute(fileCommand);  ----------------------------------------------------

                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверный синтаксис CSV! " + e.getMessage());
                    }
                }
                in1.close();
            } catch (IOException e) {
                printer(e.getMessage());
                printer("Введены неправильные данные!");
            }
        return null;
    }

    /**
     * Возвращает последние 14 команд вводимых в консоль
     *
     * @return history
     */

    public static String[] GetHistory() {
        return history;
    }

    /**
     * Вывод текста в консоль
     *
     * @param text текст, который будет выведен в консоль
     */

    public static void println(String text) {
        System.out.println(text);
    }

    /**
     * Вывод ошибки в консоль
     *
     * @param error ошибка, которая будет выведена в консоль
     */

    public static void printer(String error) {
        System.err.println(error);
    }

}
