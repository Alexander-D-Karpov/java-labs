package ru.akarpov.models.forms;

import ru.akarpov.client.util.PromptScan;

import java.io.BufferedReader;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Абстрактный класс для формирования объектов классов пользователем.
 *
 * @param <T> Класс формируемого объекта
 */
public abstract class Form<T> {
    private Scanner scanner;

    public Form() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Формирует объекта класса
     *
     * @return Объект класса
     */
    public abstract T build();

    /**
     * Запрашивает у пользователя строку
     *
     * @param fieldName    Название поля
     * @param restrictions Ограничения
     * @param validator    Предикат валидации
     * @return Строка
     */
    public String askString(String fieldName, String restrictions, Predicate<String> validator) {
        while (true) {
            System.out.println("Введите " + fieldName + restrictions + ":\n> ");
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            } else {
                if (input.isEmpty() && validator.test("")) {
                    return null;
                }
                System.err.println("Неверный формат ввода((");
            }
        }
    }

    /**
     * Запрашивает у пользователя Enum
     *
     * @param fieldName      Название поля
     * @param exceptedValues Допустимые значения
     * @param validator      Предикат валидации
     * @return Перечисление
     */
    public Enum<?> askEnum(String fieldName, Enum<?>[] exceptedValues, Predicate<Enum<?>> validator) {
        while (true) {
            System.out.println("Введите число для выбора " + fieldName + ":");
            for (int i = 0; i < exceptedValues.length; i++) {
                System.out.println((i + 1) + ": " + exceptedValues[i].name());
            }
            String input = scanner.nextLine().trim();
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < exceptedValues.length && validator.test(exceptedValues[index])) {
                    return exceptedValues[index];
                }
            } catch (NumberFormatException ignored) {}
            System.err.println("Некорректный ввод, попробуйте ещё раз.");
        }
    }

    /**
     * Запрашивает у пользователя Integer
     *
     * @param fieldName    Название поля
     * @param restrictions Ограничения
     * @param validator    Предикат валидации
     * @return Целое число
     */
    public Integer askInteger(String fieldName, String restrictions, Predicate<Integer> validator) {
        while (true) {
            System.out.println("Введите " + fieldName + restrictions + ":\n>>> ");
            String input = scanner.nextLine().trim();
            try {
                Integer number = Integer.parseInt(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    System.err.println("Ошибка валидации((");
                }
            } catch (NumberFormatException e) {
                if (input.isEmpty() && validator.test(null)) {
                    return null;
                }
                System.err.println("Неверный формат ввода((");
            }
        }
    }

    /**
     * Запрашивает у пользователя Long
     *
     * @param fieldName    Название поля
     * @param restrictions Ограничения
     * @param validator    Предикат валидации
     * @return Длинное число
     */
    public Long askLong(String fieldName, String restrictions, Predicate<Long> validator) {
        while (true) {
            System.out.println("Введите " + fieldName + restrictions + ":\n>>> ");
            String input = scanner.nextLine().trim();
            try {
                Long number = Long.parseLong(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    System.err.println("Ошибка валидации((");
                }
            } catch (NumberFormatException e) {
                if (input.isEmpty() && validator.test(null)) {
                    return null;
                }
                System.err.println("Неверный формат ввода((");
            }
        }
    }

    public void setUserScanner(BufferedReader bufferedReader) {
        this.scanner = new Scanner(bufferedReader);
    }
}