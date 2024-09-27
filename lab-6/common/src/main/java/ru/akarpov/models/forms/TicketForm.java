package ru.akarpov.models.forms;

import lombok.Setter;
import ru.akarpov.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Форма для создания объекта класса {@link Ticket}.
 */
public class TicketForm extends Form<Ticket> {
    private BufferedReader reader;
    @Setter
    private boolean scriptMode = false;

    public TicketForm() {
        super();
    }

    public void setUserScanner(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Ticket build() {
        return new Ticket(
                -1,
                askString("название билета", " (строка, поле не может быть пустым)", s -> !s.isEmpty()),
                askCoordinates(),
                new Date(),
                askLong("цена билета", " (целое число, не может быть пустым, значение должно быть больше нуля)", x -> (x != null && x > 0)),
                askLong("скидка", " (целое число, не может быть пустым, значение должно быть больше 0 и не больше 100)", x -> (x != null && x > 0 && x <= 100)),
                askTicketType(),
                askEvent()
        );
    }

    private String readLine() throws IOException {
        String line = reader.readLine();
        if (scriptMode) {
            System.out.println(line); // Выводим считанную строку для отладки
        }
        return line;
    }

    @Override
    public String askString(String fieldName, String restrictions, Predicate<String> validator) {
        while (true) {
            if (!scriptMode) {
                System.out.println("Введите " + fieldName + restrictions + ":");
                System.out.print("> ");
            }
            try {
                String input = readLine();
                if (validator.test(input)) {
                    return input;
                } else {
                    if (!scriptMode) {
                        System.err.println("Неверный формат ввода.");
                    } else {
                        throw new IllegalArgumentException("Неверный формат ввода в скрипте для поля: " + fieldName);
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении ввода: " + e.getMessage());
                if (scriptMode) {
                    throw new IllegalStateException("Ошибка при чтении из скрипта", e);
                }
            }
        }
    }

    @Override
    public Integer askInteger(String fieldName, String restrictions, Predicate<Integer> validator) {
        while (true) {
            if (!scriptMode) {
                System.out.println("Введите " + fieldName + restrictions + ":");
                System.out.print(">>> ");
            }
            try {
                String input = readLine();
                Integer number = Integer.parseInt(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    if (!scriptMode) {
                        System.err.println("Ошибка валидации.");
                    } else {
                        throw new IllegalArgumentException("Ошибка валидации в скрипте для поля: " + fieldName);
                    }
                }
            } catch (NumberFormatException e) {
                if (!scriptMode) {
                    System.err.println("Неверный формат ввода.");
                } else {
                    throw new IllegalArgumentException("Неверный формат числа в скрипте для поля: " + fieldName);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении ввода: " + e.getMessage());
                if (scriptMode) {
                    throw new IllegalStateException("Ошибка при чтении из скрипта", e);
                }
            }
        }
    }

    @Override
    public Long askLong(String fieldName, String restrictions, Predicate<Long> validator) {
        while (true) {
            if (!scriptMode) {
                System.out.println("Введите " + fieldName + restrictions + ":");
                System.out.print(">>> ");
            }
            try {
                String input = readLine();
                Long number = Long.parseLong(input);
                if (validator.test(number)) {
                    return number;
                } else {
                    if (!scriptMode) {
                        System.err.println("Ошибка валидации.");
                    } else {
                        throw new IllegalArgumentException("Ошибка валидации в скрипте для поля: " + fieldName);
                    }
                }
            } catch (NumberFormatException e) {
                if (!scriptMode) {
                    System.err.println("Неверный формат ввода.");
                } else {
                    throw new IllegalArgumentException("Неверный формат числа в скрипте для поля: " + fieldName);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении ввода: " + e.getMessage());
                if (scriptMode) {
                    throw new IllegalStateException("Ошибка при чтении из скрипта", e);
                }
            }
        }
    }

    @Override
    public Enum<?> askEnum(String fieldName, Enum<?>[] values, Predicate<Enum<?>> validator) {
        while (true) {
            if (!scriptMode) {
                System.out.println("Введите число или название для выбора " + fieldName + ":");
                for (int i = 0; i < values.length; i++) {
                    System.out.println((i + 1) + ": " + values[i].name());
                }
            }
            try {
                String input = readLine();
                Enum<?> result = null;

                try {
                    int index = Integer.parseInt(input) - 1;
                    if (index >= 0 && index < values.length) {
                        result = values[index];
                    }
                } catch (NumberFormatException ignored) {
                    for (Enum<?> value : values) {
                        if (value.name().equalsIgnoreCase(input)) {
                            result = value;
                            break;
                        }
                    }
                }

                if (result != null && validator.test(result)) {
                    return result;
                } else {
                    if (!scriptMode) {
                        System.err.println("Некорректный ввод, попробуйте ещё раз.");
                    } else {
                        throw new IllegalArgumentException("Некорректный выбор в скрипте для поля: " + fieldName);
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении ввода: " + e.getMessage());
                if (scriptMode) {
                    throw new IllegalStateException("Ошибка при чтении из скрипта", e);
                }
            }
        }
    }

    private Coordinates askCoordinates() {
        return new Coordinates(
                askInteger("координата x", " (целое число, максимальное значение = 794)", x -> (x <= 794)),
                askInteger("координата y", " (целое число)", x -> true)
        );
    }

    private TicketType askTicketType() {
        return (TicketType) askEnum("тип билета", TicketType.values(), Objects::nonNull);
    }

    private Event askEvent() {
        return new Event(
                -1,
                askString("название события", " (строка, поле не может быть пустым)", s -> !s.isEmpty()),
                askLong("количество билетов", " (целое число, значение должно быть больше нуля)", x -> (x > 0)),
                askString("описание события", " (строка, поле не может быть пустым)", s -> !s.isEmpty()),
                askEventType()
        );
    }

    private EventType askEventType() {
        return (EventType) askEnum("тип события", EventType.values(), Objects::nonNull);
    }
}
