package sergey.shulga.uswitcherapp.contollers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LastWrongWordDeleter {
    private static final Logger logger = LogManager.getLogger(LastWrongWordDeleter.class);

    // Выполнение команды osascript для выполнения скрипта AppleScript
    public void mainDeleter(String currentWord) {
        int numberOfBackspaces = currentWord.length() + 1; // Количество нажатий клавиши Backspace (=длинна последнего слова)

        try {
            String[] cmd = getCmd(numberOfBackspaces);
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            logger.error("IOException in mainDeleter()! " + e);
        }
        System.out.println("Word " + currentWord + " has been deleted.");
    }

    // AppleScript-код для нажатия клавиши Backspace заданное количество раз
    private static String[] getCmd(int numberOfBackspaces) {
        String scriptBuilder = "tell application \"System Events\"\n" +
                "    key code 51\n".repeat(Math.max(0, numberOfBackspaces)) +
                "end tell";

        return new String[]{"osascript", "-e", scriptBuilder};
    }
}