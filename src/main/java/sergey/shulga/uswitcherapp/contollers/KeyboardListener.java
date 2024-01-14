package sergey.shulga.uswitcherapp.contollers;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sergey.shulga.uswitcherapp.view.Frame;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class KeyboardListener implements NativeKeyListener {
    private DictionaryChecker dictionaryChecker;

    public KeyboardListener() {
    }

    @Autowired
    public KeyboardListener(DictionaryChecker dictionaryChecker) {
        this.dictionaryChecker = dictionaryChecker;
    }

    @Autowired
    public void setDictionaryChecker(DictionaryChecker dictionaryChecker) {
        this.dictionaryChecker = dictionaryChecker;
    }

    private String currentWord = "";
    private static final List<Integer> currentKeyCodes = new ArrayList<>();
    private final List<Integer> previousKeyCodes = new ArrayList<>();
    private int currentKeyCode;
    private final char[] extraSymbols = {'[', ']', ';', '\'', '\\', ',', '.'}; // Массив с символами, которые не являются буквами на английском раскладке

    // Метод, который вызывается при нажатии клавиши на клавиатуре
    public void nativeKeyPressed(NativeKeyEvent e) {
        currentKeyCode = e.getKeyCode();
        // Если нажат Backspace - удаляем последний символ слова
        if (e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE) {
            if (!currentWord.isEmpty()) {
                currentWord = currentWord.substring(0, currentWord.length() - 1);
                currentKeyCodes.remove(currentKeyCodes.size() - 1);
            }
        }
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    // Метод, который вызывается при отпускании клавиши на клавиатуре
    public void nativeKeyReleased(NativeKeyEvent e) {
        //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    // Метод, который вызывается при наборе символа на клавиатуре
    public void nativeKeyTyped(NativeKeyEvent e) {
        if (Frame.isProgramRunning()) {
            char actualSymbolTyped = e.getKeyChar();
            if (actualSymbolTyped == ' ') { // Если нажат пробел
                if (!currentKeyCodes.equals(previousKeyCodes)) { // Если последних два слова имеют разные кей-коды (разные слова)
                    if (!currentWord.isEmpty()) { // Если переменная "currentWord" не пустая

                        System.out.println("Actual word typed -> " + currentWord);
                        System.out.println("Actual key codes -> " + currentKeyCodes);

                        dictionaryChecker.launchDictionaryChecker(currentWord);

                        currentWord = "";
                        previousKeyCodes.clear();
                        previousKeyCodes.addAll(currentKeyCodes);
                        currentKeyCodes.clear();

                        System.out.println("------------------------------");
                        System.out.println(" ");
                    }
                } else { // Если предыдущее слово = текущее слово
                    System.out.println("Word has been processed already");
                    currentWord = "";
                    currentKeyCodes.clear();
                    System.out.println("------------------------------");
                }
            } else if (Character.isLetter(actualSymbolTyped) || isContainsExtraSymbol(actualSymbolTyped, extraSymbols)) {
                currentWord += actualSymbolTyped;
                currentKeyCodes.add(currentKeyCode);
            }

        }
    }

    // Метод для проверки наличия extra-символа в массиве
    private boolean isContainsExtraSymbol(char symbol, char[] symbols) {
        for (char s : symbols) {
            if (s == symbol)
                return true;
        }
        return false;
    }

    public void mainListener() {
        // Получаем логер для библиотеки jnativehook и выключаем вывод логов
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        try {
            // Регистрируем глобальный слушатель клавиатуры
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            // Обработка исключения при ошибке регистрации слушателя
            logger.warning("Error in method mainListener(): " + ex);

            // Выходим из программы с кодом 1
            System.exit(1);
        }
        // Добавляем экземпляр класса KeyboardListener в список слушателей
        GlobalScreen.addNativeKeyListener(this);
    }

    public static List<Integer> getCurrentKeyCodes() {
        return currentKeyCodes;
    }
}