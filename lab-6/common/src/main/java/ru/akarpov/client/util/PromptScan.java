package ru.akarpov.client.util;

import lombok.Getter;

import java.util.Scanner;

public class PromptScan {
    @Getter
    private static Scanner userScanner;

    public static void setUserScanner(Scanner userScanner) {
        PromptScan.userScanner = userScanner;
    }
}
