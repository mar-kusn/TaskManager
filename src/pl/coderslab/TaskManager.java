package pl.coderslab;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TaskManager {
    static final String DB_FILE_NAME = "tasks.csv";
    static final String[] MENU_OPTIONS = {"add", "remove", "list", "exit"};

    static String[][] tasks;

    public static void main(String[] args) {
        tasks = readDbFile();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            menu(MENU_OPTIONS);
            if (scanner.hasNextLine()) {
                String option = scanner.nextLine();
                switch (option) {
                    case "add":
                        addTask();
                        break;
                    case "remove":
                        removeTask();
                        break;
                    case "list":
                        listTasks();
                        break;
                    case "exit":
                        saveAndExit();
                        break;
                    default:
                        warningMessage("Please select correct option.");
                        break;
                }
            }
        }
    }

    private static void warningMessage(String msg) {
        System.out.println(ConsoleColors.YELLOW_BOLD + msg + ConsoleColors.RESET);
    }

    private static void errorMessage(String msg) {
        System.out.println(ConsoleColors.RED_BOLD + msg + ConsoleColors.RESET);
    }

    public static String[][] readDbFile() {
        Path dbPath = Path.of(DB_FILE_NAME);

        String[][] result = null;
        // sprawdzamy czy plik istnieje
        if (!Files.exists(dbPath)) {
            errorMessage("File '" + dbPath.toAbsolutePath() + "' not exist!");
            System.exit(0);
        } else {
            // dodatkowa tablica do której będziemy wczytywać dane
            try {
                List<String> lines = Files.readAllLines(dbPath);
                // jeżeli plik nie jest pusty
                if (lines.size() > 0) {
                    // nowa tablica ma rozmiar [ilość wczytanych linii][ilość poszczególnych elementów wiersza]
                    result = new String[lines.size()][lines.get(0).split(",").length];

                    for (int i = 0; i < lines.size(); i++) {
                        String[] data = lines.get(i).split(",");
                        for (int j = 0; j < data.length; j++) {
                            result[i][j] = data[j];
                        }
                    }
                } else {
                    warningMessage("File '" + dbPath.toAbsolutePath() + "' is empty!");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void menu(String[] menuTab) {

        System.out.println(ConsoleColors.BLUE);
        System.out.println("Please select an option: " + ConsoleColors.RESET);
        for (String option : MENU_OPTIONS) {
            System.out.println(option);
        }
    }

    private static void addTask() {
        System.out.println("Please add task description");
        Scanner scanner = new Scanner(System.in);
        String taskDesc = " ";
        while (true) {
            if (!scanner.hasNextLine()) {
                scanner.next();
            } else {
                taskDesc = scanner.nextLine();
                if (taskDesc.length() > 0) {
                    break;
                } else {
                    System.out.println("Please add task description");
                }
            }
        }

        System.out.println("Please add task due date");
        String taskDueDate = " ";
        while (true) {
            if (!scanner.hasNextLine()) {
                scanner.next();
            } else {
                taskDueDate = scanner.nextLine();
                if (taskDueDate.length() > 4) {
                    break;
                } else {
                    System.out.println("Please add task due date");
                }
            }
        }

        System.out.println("Is your task is important: " + ConsoleColors.RED + "true" +
                ConsoleColors.RESET + "/" + ConsoleColors.RED + "false" + ConsoleColors.RESET);
        String important = " ";
        while (true) {
            if (!scanner.hasNextLine()) {
                scanner.next();
            } else {
                important = scanner.nextLine();
                if (important.equals("true") || important.equals("false")) {
                    break;
                } else {
                    System.out.println(ConsoleColors.RESET + "Is your task is important: " + ConsoleColors.RED + "true" +
                            ConsoleColors.RESET + "/" + ConsoleColors.RED + "false" + ConsoleColors.RESET);
                }
            }
        }

        // jeżeli lista zadań jest pusta / wyczyszczona
        if (tasks == null || tasks.length == 0) {
            // tworzymy jeden wiersz tabeli z 3 kolumnami
            tasks = new String[1][3];
        } else {
            // stwórz tabelę o 1 wiersz większą
            tasks = Arrays.copyOf(tasks, tasks.length + 1);
        }
        tasks[tasks.length - 1] = new String[tasks[0].length];

        // wprowadź parametry zadania do tabeli
        tasks[tasks.length - 1][0] = taskDesc;
        tasks[tasks.length - 1][1] = taskDueDate;
        tasks[tasks.length - 1][2] = important;

    }

    private static void removeTask() {
        // jeżeli w tabeli są zapisane jakieś zadania -> możemy coś usunąć
        if (tasks != null && tasks.length > 0) {

            // zmienna pomocnicza do wyświetlenia ilości zadań w zapytaniu
            String countStr = "";
            if (tasks.length > 1) {
                countStr = "(options: 0.." + (tasks.length - 1) + ")";
            } else {
                countStr = "(options: 0)";
            }
            System.out.println("Please select number to remove " +countStr);
            Scanner scanner = new Scanner(System.in);
            String toRemove = "";
            while (true) {
                toRemove = scanner.nextLine();
                if (NumberUtils.isParsable(toRemove) && Integer.parseInt(toRemove) >= 0) {
                    try {
                        tasks = ArrayUtils.remove(tasks, Integer.parseInt(toRemove));
                        break;
                    } catch (IndexOutOfBoundsException e) {
                        warningMessage("Element " + toRemove + " doesn't exist in list!");
                        warningMessage("Please select correct number to remove "+countStr);
                    }
                } else {
                    warningMessage("Please select correct number to remove "+countStr);
                }
            }
        } else {
            warningMessage("Tasks list is empty!");
        }
    }

    public static void listTasks() {
        // jeżeli tablica nie jest null'em i jej rozmiar jest > 0 to ją wyświetlamy
        if (tasks != null && tasks.length > 0) {
            for (int i = 0; i < tasks.length; i++) {
                System.out.print(i + ": ");
                for (int j = 0; j < tasks[i].length; j++) {
                    System.out.print(tasks[i][j] + " ");
                }
                System.out.println();
            }
        } else {
            warningMessage("Tasks list is empty!");
        }
    }

    private static void saveAndExit() {
        // save tasks to file
        saveTasksToDB();
        System.out.println(ConsoleColors.RED + "Bye, bye!" + ConsoleColors.RESET);
        System.exit(0);
    }

    private static void saveTasksToDB() {

        // jeżeli w tabeli są zapisane jakieś zadania
        if (tasks != null) {
            Path dbPath = Path.of(DB_FILE_NAME);
            if (!Files.exists(dbPath)) {
                errorMessage("Error while open file: " + dbPath.toAbsolutePath());
            } else {
                // lista zawierająca wszystkie linie pliku
                List<String> outList = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < tasks.length; i++) {
                    stringBuilder.setLength(0);
                    for (int j = 0; j < tasks[i].length; j++) {
                        stringBuilder.append(tasks[i][j]);
                        if (j < tasks[i].length - 1) {
                            stringBuilder.append(",");
                        }
                    }
                    outList.add(stringBuilder.toString());
                }
                try {
                    // zapisz linie do pliku
                    Files.write(dbPath, outList);
                } catch (IOException e) {
                    errorMessage("Error while writing a file: " + dbPath.toAbsolutePath());
                }
            }
        }
    }
}
