package ru.akarpov.client.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class ScriptManager {
    private static final Stack<String> pathStack = new Stack<>();
    private static final Stack<Scanner> scanners = new Stack<>();

    public static void addFile(String path) throws FileNotFoundException {
        pathStack.push(new File(path).getAbsolutePath());
        scanners.push(new Scanner(new File(path)));
    }

    public static boolean isRecursive(String path) {
        return pathStack.contains(new File(path).getAbsolutePath());
    }

    public static void removeFile() {
        scanners.pop();
        pathStack.pop();
    }

    public static Scanner getLastScanner() {
        return scanners.peek();
    }
}
