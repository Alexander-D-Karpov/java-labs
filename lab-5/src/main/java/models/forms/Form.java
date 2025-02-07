package models.forms;

import manager.ConsoleManager;
import manager.ScannerManager;

import java.util.Scanner;
import java.util.function.Predicate;


/**
 * Абстрактный класс для формирования объектов классов пользователем.
 *
 * @param <T> Класс формируемого объекта
 */
public abstract class Form<T> {
    private final ConsoleManager console;
    private final Scanner scanner = ScannerManager.getScanner();


    public Form(ConsoleManager console) {
        this.console = console;
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
            console.print("Введите " + fieldName + restrictions + ":\n>>> ");
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            } else {
                if (input.isEmpty() && validator.test("")) {
                    return null;
                }
                console.printError("Неверный формат ввода((");
            }
        }
    }

    /**
     * Запрашивает у пользователя Enum, принимая как числовой так и текстовый ввод.
     *
     * @param fieldName      Название поля
     * @param expectedValues Допустимые значения
     * @param validator      Предикат валидации
     * @return Перечисление
     */
    public Enum askEnum(String fieldName, Enum[] expectedValues, Predicate<String> validator) {
        while (true) {
            console.print("Доступные значения '" + fieldName + "':\n");
            for (int i = 0; i < expectedValues.length; i++) {
                console.println((i + 1) + ". " + expectedValues[i]);
            }
            console.print("Введите номер или название " + fieldName + ": \n>>> ");
            String input = scanner.nextLine().trim();
            try {
                int index = Integer.parseInt(input) - 1; // Adjust for 0-based index
                if (index >= 0 && index < expectedValues.length) {
                    return expectedValues[index];
                }
                console.printError("Введите номер из списка!");
            } catch (NumberFormatException e) {
                // Check if the input is a valid string for enum values
                for (Enum value : expectedValues) {
                    if (value.name().equalsIgnoreCase(input)) {
                        return value;
                    }
                }
                console.printError("Неверный формат ввода! Пожалуйста, введите номер или название.");
            }
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
            console.print("Введите " + fieldName + restrictions + ":\n>>> ");
            String input = scanner.nextLine().trim();
            try {
                Integer number = Integer.parseInt(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    console.printError("Ошибка валидации((");
                }
            } catch (NumberFormatException e) {
                if (input.isEmpty() && validator.test(null)) {
                    return null;
                }
                console.printError("Неверный формат ввода((");
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
            console.print("Введите " + fieldName + restrictions + ":\n>>> ");
            String input = scanner.nextLine().trim();

            try {
                Long number = Long.parseLong(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    console.printError("Ошибка валидации((");
                }
            } catch (NumberFormatException e) {
                if (input.isEmpty() && validator.test(null)) {
                    return null;
                }
                console.printError("Неверный формат ввода((");
            }
        }
    }

    /**
     * Запрашивает у пользователя Double
     *
     * @param fieldName    Название поля
     * @param restrictions Ограничения
     * @param validator    Предикат валидации
     * @return Дробное число
     */
    public Double askDouble(String fieldName, String restrictions, Predicate<Double> validator) {
        while (true) {
            console.print("Введите " + fieldName + restrictions + ":\n>>> ");
            String input = scanner.nextLine().trim();
            try {
                Double number = Double.parseDouble(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    console.printError("Ошибка валидации((");
                }
            } catch (NumberFormatException e) {
                if (input.isEmpty() && validator.test(null)) {
                    return null;
                }
                console.printError("Неверный формат ввода((");
            }
        }
    }

    /**
     * Запрашивает у пользователя Float
     *
     * @param fieldName    Название поля
     * @param restrictions Ограничения
     * @param validator    Предикат валидации
     * @return Дробное число
     */
    public Float askFloat(String fieldName, String restrictions, Predicate<Float> validator) {
        while (true) {
            console.print("Введите " + fieldName + restrictions + ":\n>>> ");
            String input = scanner.nextLine().trim();
            try {
                Float number = Float.parseFloat(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    console.printError("Ошибка валидации((");
                }
            } catch (NumberFormatException e) {
                if (input.isEmpty() && validator.test(null))
                    return null;
            }
            console.printError("Неверный формат ввода((");
        }
    }
}

