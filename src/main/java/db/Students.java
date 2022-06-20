package db;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;

public class Students {

    private static int rowsCount = 0;

    private static void insertRow(String name, String surname, String patronymic, String birthday, String groupName, Connection c) throws SQLException {
        PreparedStatement prep = c.prepareStatement("INSERT INTO STUDENTS (NAME,SURNAME,PATRONYMIC, BIRTHDAY,GROUP_NAME) VALUES(?, ?, ?, date(?), ?)");
        prep.setString(1, name);
        prep.setString(2, surname);
        prep.setString(3, patronymic);
        prep.setString(4, birthday);
        prep.setString(5, groupName);
        prep.executeUpdate();
        prep.close();
        rowsCount++;
    }

    private static void insertData(Connection c) throws SQLException {
        insertRow("Михаил", "Дорохин", "Михеевич", "2001-07-12", "БПИ192", c);
        insertRow("Анастасия", "Снаткина", "Тимуровна", "2000-04-22", "БПИ193", c);
        insertRow("Борис", "Капралов", "Валерьянович", "2001-11-16", "БПИ191", c);
        insertRow("Степан", "Чугунов", "Богданович", "2001-02-02", "БПИ191", c);
        insertRow("Игорь", "Ефремов", "Леонтиевич", "2000-10-28", "БПИ192", c);
    }

    private static int getRowsCount(Connection c) throws SQLException {
        var stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as rows_count FROM STUDENTS;");
        var count = rs.getInt("rows_count");
        stmt.close();
        rs.close();
        return count;
    }

    public static void deleteStudent(int id) {
        Connection c;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:students.db");
            c.setAutoCommit(false);
            PreparedStatement prep = c.prepareStatement("DELETE FROM STUDENTS WHERE ID = ?");
            prep.setString(1, String.valueOf(id));
            prep.executeUpdate();
            c.commit();
            var currentCount = getRowsCount(c);
            if (currentCount != rowsCount) {
                System.out.println("Студент успешно удален из таблицы");
                rowsCount -= 1;
            } else System.out.println("Студента с таким ID нет в таблице");
            c.close();
        } catch (Exception e) {
            System.err.println("Произошла ошибка при попытке удаления студента из таблицы");
            System.exit(0);
        }
    }

    private static String readString(String message, boolean isData) {
        var in = new Scanner(System.in);
        var flag = true;
        var output = "";
        do {
            System.out.println(message);
            output = in.nextLine();
            if (output == null || output.isEmpty() || output.isBlank()) {
                flag = false;
                System.out.println("Строка не должна быть пустой");
            } else if (isData && !validateData(output)) {
                flag = false;
                System.out.println("Дата должна быть введена в формате yyyy-MM-dd и иметь корректные год, месяц и день");
            } else flag = true;
        } while (!flag);
        return output;
    }

    private static boolean validateData(String data) {
        var format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            format.parse(data);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static void addStudent() {
        Connection c;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:students.db");
            c.setAutoCommit(false);
            var name = readString("Введите имя:", false);
            var surname = readString("Введите фамилию:", false);
            var patronymic = readString("Введите отчество:", false);
            var birthday = readString("Введите дату рождения:", true);
            var groupName = readString("Введите группу:", false);
            insertRow(name, surname, patronymic, birthday, groupName, c);
            c.commit();
            c.close();
            System.out.println("Студент успешно добавлен");
        } catch (Exception e) {
            System.err.println("Произошла ошибка при попытке добавления студента в таблицу");
            System.exit(0);
        }
    }

    public static void showStudents() {
        Connection c;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:students.db");
            c.setAutoCommit(false);
            if (rowsCount == 0) System.out.println("Таблица пуста");
            else {
                var stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT ID, NAME, SURNAME, PATRONYMIC, BIRTHDAY, GROUP_NAME FROM STUDENTS;");

                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("ID"));
                    System.out.println("Фамилия: " + rs.getString("SURNAME"));
                    System.out.println("Имя: " + rs.getString("NAME"));
                    System.out.println("Отчество: " + rs.getString("PATRONYMIC"));
                    System.out.println("Дата рождения: " + LocalDate.parse(rs.getString("BIRTHDAY")));
                    System.out.println("Группа: " + rs.getString("GROUP_NAME"));
                    System.out.println();
                }
                rs.close();
            }
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println("Произошла ошибка при попытке вывода списка студентов");
            System.exit(0);
        }
    }

    public static void init() {
        Connection c;
        Statement stmt;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:students.db");
            stmt = c.createStatement();
            var sql = "DROP TABLE IF EXISTS STUDENTS";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE STUDENTS " +
                    "(ID INTEGER PRIMARY KEY   AUTOINCREMENT  NOT NULL, " +
                    " NAME           TEXT    NOT NULL, " +
                    " SURNAME        TEXT    NOT NULL, " +
                    " PATRONYMIC     TEXT    NOT NULL, " +
                    " BIRTHDAY       DATE    NOT NULL, " +
                    " GROUP_NAME      TEXT    NOT NULL)";
            stmt.executeUpdate(sql);
            c.setAutoCommit(false);
            System.out.println("Инициализация данных...");
            insertData(c);
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println("Произошла ошибка при попытке инициализации таблицы");
            System.exit(0);
        }
        System.out.println("Таблица успешно создана");
    }
}
