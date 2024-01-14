package sergey.shulga.uswitcherapp.contollers;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sergey.shulga.uswitcherapp.repositories.EnglishWordRepository;
import sergey.shulga.uswitcherapp.repositories.RussianWordRepository;
import sergey.shulga.uswitcherapp.repositories.UkrainianWordRepository;
import sergey.shulga.uswitcherapp.view.Frame;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DictionaryChecker {

    @Autowired
    private RussianWordRepository russianWordRepository;
    @Autowired
    private EnglishWordRepository englishWordRepository;
    @Autowired
    private UkrainianWordRepository ukrainianWordRepository;
    @Autowired
    private KeyboardLayoutService keyboardLayoutService;
    @Autowired
    private LastWrongWordDeleter lastWrongWordDeleter;
    @Autowired
    private LanguageSwitcher languageSwitcher;
    @Autowired
    private CorrectWordPrinter correctWordPrinter;
    private static final Logger logger = LogManager.getLogger(DictionaryChecker.class);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public String correctLanguage = "";

    public void launchDictionaryChecker(String currentTypedWord) {
        findWordInDictionary(currentTypedWord, keyboardLayoutService.getCurrentKeyboardLayout());
    }

    private void findWordInDictionary(String wordToSearch, String currentKeyboardLayout) {
        // Поиск текущего слова в словаре языка, который сейчас установлен
        List<?> foundWords = switch (currentKeyboardLayout) {
            case "en" -> englishWordRepository.findByWord(wordToSearch);
            case "ua" -> ukrainianWordRepository.findByWord(wordToSearch);
            case "ru" -> russianWordRepository.findByWord(wordToSearch);
            default -> throw new IllegalArgumentException("Unsupported layout: " + currentKeyboardLayout);
        };


        // Установка правильного языка раскладки, в случае если слово
        // не найдено в словаре
        try {
            if (foundWords.isEmpty()) {
                String firstLanguage = Frame.getSelectedLanguage1();
                String secondLanguage = Frame.getSelectedLanguage2();
                setCorrectLanguage(keyboardLayoutService.getCurrentKeyboardLayout().equals(firstLanguage) ? secondLanguage : firstLanguage);

                executorService.submit(() -> lastWrongWordDeleter.mainDeleter(wordToSearch));
                languageSwitcher.mainSwitcher(getCorrectLanguage(), keyboardLayoutService.getCurrentKeyboardLayout());
                correctWordPrinter.printTransform(KeyboardListener.getCurrentKeyCodes());
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException in findWordInDictionary()! " + e);
        }

        // Форматирование для вывода в консоль
        String language = switch (currentKeyboardLayout) {
            case "en" -> "English";
            case "ua" -> "Ukrainian";
            case "ru" -> "Russian";
            default -> throw new IllegalArgumentException("Unsupported layout: " + currentKeyboardLayout);
        };

        // Вывод информации в консоль
        if (foundWords.isEmpty())
            System.out.println("Слово '" + wordToSearch + "' не найдено в базе данных " + language + ". Корректный язык: " + getCorrectLanguage());
        else
            System.out.println("Слово '" + wordToSearch + "' найдено в базе данных " + language + ". Корректный язык: " + getCorrectLanguage());

        setCorrectLanguage("");
    }

    public String getCorrectLanguage() {
        return correctLanguage;
    }
    public void setCorrectLanguage(String correctLanguage) {
        this.correctLanguage = correctLanguage;
    }

    public void shutdownExecutorService() {
        executorService.shutdown();
    }

    @PreDestroy
    public void destroy() {
        shutdownExecutorService();
    }
}