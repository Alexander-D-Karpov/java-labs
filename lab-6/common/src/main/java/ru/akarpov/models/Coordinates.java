package ru.akarpov.models;

import lombok.*;

import java.io.Serializable;

/**
 * Класс Координат.
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Coordinates implements Serializable {
    private int x; //Максимальное значение поля: 794
    private int y;
}
