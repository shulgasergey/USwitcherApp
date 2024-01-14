package sergey.shulga.uswitcherapp.contollers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

@Service
public class CorrectWordPrinter {
    private static final Logger logger = LogManager.getLogger(CorrectWordPrinter.class);

    // Печатаем трансформированное слово
    public void printTransform(List<Integer> currentKeyCodes) {
        try {
            Robot robot = new Robot();
            for (int keyCode : currentKeyCodes) {
                robot.keyPress(getKeyEventKeyCode(keyCode));
            }

            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);

            currentKeyCodes.forEach(keyCode -> robot.keyRelease(getKeyEventKeyCode(keyCode)));
        } catch (AWTException e) {
            logger.error("AWTException in printTransform()! " + e);
        }
    }

    // Преобразовываем полученные кей-коды в коды для класса Robot
    private int getKeyEventKeyCode(int keyCode) {
        return Arrays.stream(KEYCODES).filter(mapping -> mapping[1] == keyCode).findFirst().map(mapping -> mapping[0]).orElse(-1);
    }

    // Таблица соответствия кей-кодов между классом Robot и jnativehook
    public final int[][] KEYCODES = {
            {KeyEvent.VK_Q, 16},
            {KeyEvent.VK_W, 17},
            {KeyEvent.VK_E, 18},
            {KeyEvent.VK_R, 19},
            {KeyEvent.VK_T, 20},
            {KeyEvent.VK_Y, 21},
            {KeyEvent.VK_U, 22},
            {KeyEvent.VK_I, 23},
            {KeyEvent.VK_O, 24},
            {KeyEvent.VK_P, 25},
            {KeyEvent.VK_OPEN_BRACKET, 27},
            {KeyEvent.VK_CLOSE_BRACKET, 28},
            {KeyEvent.VK_A, 30},
            {KeyEvent.VK_S, 31},
            {KeyEvent.VK_D, 32},
            {KeyEvent.VK_F, 33},
            {KeyEvent.VK_G, 34},
            {KeyEvent.VK_H, 35},
            {KeyEvent.VK_J, 36},
            {KeyEvent.VK_K, 37},
            {KeyEvent.VK_L, 38},
            {KeyEvent.VK_SEMICOLON, 39},
            {KeyEvent.VK_QUOTE, 40},
            {KeyEvent.VK_BACK_SLASH, 43},
            {KeyEvent.VK_Z, 44},
            {KeyEvent.VK_X, 45},
            {KeyEvent.VK_C, 46},
            {KeyEvent.VK_V, 47},
            {KeyEvent.VK_B, 48},
            {KeyEvent.VK_N, 49},
            {KeyEvent.VK_M, 50},
            {KeyEvent.VK_COMMA, 51},
            {KeyEvent.VK_PERIOD, 52}
    };
}