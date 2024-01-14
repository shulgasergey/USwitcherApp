package sergey.shulga.uswitcherapp.contollers;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sergey.shulga.uswitcherapp.view.Frame;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailableLanguageGetter {
    private final Frame frame;

    @Autowired
    public AvailableLanguageGetter(Frame frame) {
        this.frame = frame;
    }

    private static final Logger logger = LogManager.getLogger(AvailableLanguageGetter.class);

    @PostConstruct
    public void starter() {
        // Получение доступных языков раскладки в текущий момент
        String[] availableLanguages = getAvailableLanguages();

        System.out.print("Available languages: ");

        // Форматирование списка к нужному формату
        if (availableLanguages.length > 0) {
            String firstLanguage = availableLanguages[0];
            if (firstLanguage.length() > 4) {
                availableLanguages[0] = firstLanguage.substring(4);
            }
        }

        availableLanguages = removeLanguage(availableLanguages, "ua");

        // Вывод всех языков в консоль
        for (String language : availableLanguages) {
            System.out.print(language + " ");
        }

        // Выключение headless режима для будущей работы в Tray-окне
        System.setProperty("java.awt.headless", "false");
        frame.setAvailableLanguages(availableLanguages);
    }

    // Получение доступных языков раскладки в текущий момент
    private String[] getAvailableLanguages() {
        List<String> allAvailableLanguages = new ArrayList<>();

        // Получение списка языков в формате Apple
        String appleLanguages = getAppleLanguages();

        // Раздел списка на отдельные языковые коды
        String[] languageCodes = appleLanguages.split(",");

        // Обход каждого языкового кода
        for (int i = 0; i < languageCodes.length; i++) {
            String languageCode = languageCodes[i].trim();
            if (!languageCode.isEmpty()) {
                // Получение основного языка из кода
                String language = languageCode.split("-")[0].toLowerCase();

                // Если это последний языковой код, получение второго языка и добавление обоих в список
                if (i == languageCodes.length - 1) {
                    String lastLanguage = languageCode.split("-")[1].toLowerCase();
                    allAvailableLanguages.add(stripQuotes(stripParentheses(language)));
                    allAvailableLanguages.add(stripQuotes(stripParentheses(lastLanguage)));
                } else {
                    // Добавление последнего языка в список
                    allAvailableLanguages.add(stripQuotes(stripParentheses(language)));
                }
            }
        }
        // Превращение списка в массив
        return allAvailableLanguages.toArray(new String[0]);
    }

    // Получение списка языков в формате Apple
    private String getAppleLanguages() {
        StringBuilder appleLanguages = new StringBuilder();
        try {
            // Запуск команда для получения списка языков
            Process processToGetAppleLanguages = Runtime.getRuntime().exec("defaults read -g AppleLanguages");
            BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(processToGetAppleLanguages.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Чтение каждого ряда вывода и добавления в ряд языков
                appleLanguages.append(line);
            }
            reader.close();
        } catch (Exception e) {
            logger.error("Error in method getAppleLanguages(): " + e);
        }
        return appleLanguages.toString();
    }

    // Удаление кавычек из ряда
    private String stripQuotes(String str) {
        return str.replace("\"", "");
    }

    // Удаление скобок из ряда
    private String stripParentheses(String str) {
        return str.replace("(", "").replace(")", "");
    }

    // Удаление второго украинского языка (особенность ОС)
    public String[] removeLanguage(String[] availableLanguages, String languageToRemove) {
        int index = -1;

        for (int i = 0; i < availableLanguages.length; i++) {
            if (availableLanguages[i].equals(languageToRemove)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            String[] updatedLanguages = new String[availableLanguages.length - 1];
            System.arraycopy(availableLanguages, 0, updatedLanguages, 0, index);
            System.arraycopy(availableLanguages, index + 1, updatedLanguages, index, availableLanguages.length - index - 1);
            return updatedLanguages;
        }
        return availableLanguages;
    }
}