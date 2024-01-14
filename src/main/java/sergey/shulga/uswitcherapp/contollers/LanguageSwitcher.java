package sergey.shulga.uswitcherapp.contollers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LanguageSwitcher {
    @Autowired
    private KeyboardLayoutService keyboardLayoutService;
    private static final Logger logger = LogManager.getLogger(LanguageSwitcher.class);
    private static final String SYSTEM_SCRIPT_TO_CHANGE_LAYOUT = "tell application \"System Events\"\n" +
            "   keystroke tab using {control down}\n" +
            "end tell";

    public void mainSwitcher(String correctLanguage, String currentLanguage) throws InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", SYSTEM_SCRIPT_TO_CHANGE_LAYOUT);

        while (!correctLanguage.equals(currentLanguage)) {
            try {
                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                logger.error("IOException / InterruptedException in mainSwitcher()! ", e);
            }
            currentLanguage = keyboardLayoutService.getCurrentKeyboardLayout();
        }
        System.out.println("Language chosen to -> " + currentLanguage);
    }
}