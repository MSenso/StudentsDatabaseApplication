package run;

import db.Students;

import java.text.MessageFormat;
import java.util.Scanner;

public class Run {
    private static final String OPTIONS = MessageFormat.format("1. Добавить студента {0}2. Удалить студента по ID {1}" +
                    "3. Вывести список студентов {2}4. Выход",
            System.lineSeparator(), System.lineSeparator(), System.lineSeparator());

    private static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateInputNumber(int leftLimit, int rightLimit, String input) {
        if (isInteger(input)) {
            var number = Integer.parseInt(input);
            if (number >= leftLimit && number <= rightLimit) return true;
            else {
                System.out.println(MessageFormat.format("Число должно быть в диапазоне от {0} до {1} включительно", leftLimit, rightLimit));
                return false;
            }
        } else {
            System.out.println("Введено не число");
            return false;
        }
    }

    private static int readNumber(int leftLimit, int rightLimit, String message) {
        var input = 0;
        var isCorrect = true;
        var in = new Scanner(System.in);
        do {
            if (message != null) System.out.println(message);
            var temp = in.nextLine();
            isCorrect = validateInputNumber(leftLimit, rightLimit, temp);
            if (isCorrect) {
                input = Integer.parseInt(temp);
            }
        } while (!isCorrect);
        return input;
    }

    private static void switchOptions(int index) {
        switch (index) {
            case 1 -> Students.addStudent();
            case 2 -> Students.deleteStudent(readNumber(0, Integer.MAX_VALUE, "Введите ID студента для удаления:"));
            case 3 -> Students.showStudents();
            case 4 -> System.exit(0);
        }
    }

    private static void run() {
        var input = 0;
        Students.init();
        do {
            System.out.println(OPTIONS);
            input = readNumber(1, 4, null);
            switchOptions(input);
        } while (input != 4);
    }

    public static void main(String[] args) {
        run();
    }
}
