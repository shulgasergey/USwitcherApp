package sergey.shulga.uswitcherapp.contollers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class KeyboardLayoutService {
    private static final Logger logger = LogManager.getLogger(KeyboardLayoutService.class);

    // Преобразуем полученный код в более простой вид
    public String getCurrentKeyboardLayout() {
        return switch (getCurrentLayoutCode()) {
            case "\"com.apple.keylayout.US\"" -> "en";
            case "\"com.apple.keylayout.Russian\"" -> "ru";
            case "\"com.apple.keylayout.Ukrainian\"" -> "ua";
            default -> "";
        };
    }

    // Получаем код текущей раскладки
    private String getCurrentLayoutCode() {
        String finalCode = "";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "defaults read ~/Library/Preferences/com.apple.HIToolbox.plist | grep -A 3 AppleCurrentKeyboardLayoutInputSourceID | grep -oE '(\"(.*)\")|(\\w+\\.\\w+)'");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            finalCode = result.toString();
        } catch (IOException e) {
            logger.error("IOException в методе getCurrentLayoutCode! ", e);
        }
        return finalCode;
    }
}