package pl.coderslab;

import com.sun.security.jgss.GSSUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    static final String DB_FILE_NAME = "tasks.csv";
    static final String[] MENU_OPTIONS = {"add", "remove", "list", "exit"};

    static String[][] tasks;

    public static void main(String[] args) {
        tasks = readDbFile();

        String option = "";
        while (true) {
            menu(MENU_OPTIONS);
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                option = scanner.next();
                switch (option) {
                    case "add":
                        System.out.println("ADD");
                        addTask();
                        break;
                    case "remove":
                        System.out.println("REMOVE");
                        break;
                    case "list":
                        listTasks();
                        break;
                    case "exit":
                        saveAndExit();
                    default:
                        menu(MENU_OPTIONS);
                        break;
                }
            }
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
        System.out.println(taskDesc);


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
        System.out.println(taskDueDate);

        System.out.println("Is your task is important: " + ConsoleColors.RED + "true" +
                ConsoleColors.RESET + "/" + ConsoleColors.RED + "false");
        String important = " ";
        while (true) {
            if (!scanner.hasNextLine()) {
                scanner.next();
            } else {
                important = scanner.nextLine();
                if (important.equals("true") || important.equals("false")) {
                    break;
                } else {
                    System.out.println(ConsoleColors.RESET +"Is your task is important: " + ConsoleColors.RED + "true" +
                            ConsoleColors.RESET + "/" + ConsoleColors.RED + "false");
                }
            }
        }


    }

    private static void saveAndExit() {
        // save tasks to file
        saveTasksToDB();
    //    listTasks();
        System.exit(0);
    }

    private static void saveTasksToDB() {
        Path dbPath = Path.of(DB_FILE_NAME);
        if (!Files.exists(dbPath)) {
            System.out.println("Error while open file: " + dbPath.toAbsolutePath());
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
                Files.write(dbPath, outList);
            } catch (IOException e) {
                System.out.println("Error while writing a file: " + dbPath.toAbsolutePath());
            }

            //    System.out.println(outList.toString());
        }
    }

    public static void menu(String[] menuTab) {

        System.out.println(ConsoleColors.BLUE);
        System.out.println("Please select an option: " + ConsoleColors.RESET);
        for (String option : MENU_OPTIONS) {
            System.out.println(option);
        }
    }

    public static void listTasks() {
        for (int i = 0; i < tasks.length; i++) {
            for (int j = 0; j < tasks[i].length; j++) {
                System.out.print(i + " : " + tasks[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static String[][] readDbFile() {
        Path dbPath = Path.of(DB_FILE_NAME);

        String[][] result = null;
        // sprawdzamy czy plik istnieje
        if (!Files.exists(dbPath)) {
            System.out.println("File '" + dbPath.toAbsolutePath() + "' not exist!");
        } else {
            // dodatkowa tablica do której będziemy wczytywać dane
            try {
                List<String> lines = Files.readAllLines(dbPath);
                // nowa tablica ma rozmiar [ilość wczytanych linii][ilość poszczególnych elementów wiersza]
                result = new String[lines.size()][lines.get(0).split(",").length];

                for (int i = 0; i < lines.size(); i++) {
                    String[] data = lines.get(i).split(",");
                    for (int j = 0; j < data.length; j++) {
                        result[i][j] = data[j];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
