package sergey.shulga.uswitcherapp.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sergey.shulga.uswitcherapp.contollers.KeyboardListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class Frame {
    private final KeyboardListener keyboardListener;

    @Autowired
    public Frame(KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }

    private static final Logger logger = LogManager.getLogger(Frame.class);
    private TrayIcon trayIcon;
    private static boolean isProgramRunning = true;
    private boolean isLanguageAlreadySelected = false;

    private static String selectedLanguage1 = null;
    private static String selectedLanguage2 = null;

    public static String getSelectedLanguage1() {
        return selectedLanguage1;
    }

    public static String getSelectedLanguage2() {
        return selectedLanguage2;
    }

    private String[] availableLanguages;

    public void setAvailableLanguages(String[] availableLanguages) {
        this.availableLanguages = availableLanguages;
        createTrayWindow();
    }

    private void createTrayWindow() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported on this platform.");
            return;
        }

        // Создаем выпадающее меню для трея
        PopupMenu popup = new PopupMenu("Tray menu");

        MenuItem buttonToSelectLanguages = getButtonToSelectLanguages();
        popup.add(buttonToSelectLanguages);

        // LAUNCH HERE
        MenuItem buttonLaunch = new MenuItem("Запуск програми");
        buttonLaunch.addActionListener(e -> {
            if (!isLanguageAlreadySelected) {
                JOptionPane.showMessageDialog(null, "Для початку роботи оберіть дві мови");
            } else {
                System.out.println("Program has been started");
                setRunningProgram(true);

                keyboardListener.mainListener();
            }
        });
        popup.add(buttonLaunch);

        MenuItem buttonPause = new MenuItem("Пауза");
        buttonPause.addActionListener(e -> {
            if (!isProgramRunning)
                JOptionPane.showMessageDialog(null, "Робота програми вже призупинена");
            // Действия при нажатии кнопки "Пауза"
            System.out.println("Program paused");
            setRunningProgram(false);
        });
        popup.add(buttonPause);

        MenuItem buttonContinue = new MenuItem("Продовжити");
        buttonContinue.addActionListener(e -> {
            if (isProgramRunning)
                JOptionPane.showMessageDialog(null, "Робота програми не була призупинена");
            // Действия при нажатии кнопки "Продолжить"
            System.out.println("Program resumed");
            setRunningProgram(true);
        });
        popup.add(buttonContinue);

        MenuItem buttonFeedback = new MenuItem("Зв'язок з розробником");
        buttonFeedback.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://t.me/userfromworld"));
            } catch (IOException | URISyntaxException ex) {
                logger.error("Error: " + ex);
            }
        });
        popup.add(buttonFeedback);

        MenuItem buttonInstructions = new MenuItem("Інструкції з використання");
        buttonInstructions.addActionListener(e -> {
            // Создаем окошко с текстом инструкций
            JFrame instructionsFrame = new JFrame("Інструкції");
            instructionsFrame.setSize(400, 300);
            instructionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            instructionsFrame.setLocationRelativeTo(null);
            instructionsFrame.setResizable(false);

            JTextArea instructionsText = new JTextArea();
            instructionsText.setFont(new Font("Verdana", Font.PLAIN, 14));
            instructionsText.setEditable(false);
            instructionsText.setLineWrap(true);
            instructionsText.setWrapStyleWord(true);
            instructionsText.setText("""
                    Для запуску програми натисніть кнопку "Запуск"

                    Якщо Вам потрібно на певний час зупинити програму, натисніть кнопку "Пауза"

                    Якщо Вам потрібно відновити роботу програми, натисніть кнопку "Продовжити"

                    Для виходу з програми натисніть кнопку "Вихід"

                    Дякую за використання uSwitcher. Все буде Україна!""");

            instructionsText.setBorder(new EmptyBorder(10, 10, 10, 10)); // Добавляем отступы текста от краев окна

            instructionsFrame.add(instructionsText);
            instructionsFrame.setVisible(true);
        });
        popup.add(buttonInstructions);

        MenuItem buttonExit = new MenuItem("Вихід");
        buttonExit.addActionListener(e -> {
            SystemTray.getSystemTray().remove(trayIcon);
            System.exit(0);
        });
        popup.add(buttonExit);

        Image trapPicture = Toolkit.getDefaultToolkit().getImage("uSwitcherLogo.png");

        // Создаем трей
        SystemTray tray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(trapPicture, "uSwitcher", popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error("Error: " + e);
        }
    }

    private MenuItem getButtonToSelectLanguages() {
        MenuItem buttonToSelectLanguages = new MenuItem("Обрати мови для зміни");
        buttonToSelectLanguages.addActionListener(e -> {

            JFrame frame = new JFrame("Доступні мови");
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(5, 5, 5, 5);

            Font font = new Font("Tahoma", Font.PLAIN, 14);

            final int[] selectedCount = {0};

            for (String language : availableLanguages) {
                JCheckBox checkBox = new JCheckBox(language);
                checkBox.setFont(font);
                gbc.gridy++;
                panel.add(checkBox, gbc);

                checkBox.addActionListener(e1 -> {
                    if (checkBox.isSelected()) {
                        if (selectedCount[0] == 0)
                            selectedLanguage1 = checkBox.getText();
                        else if (selectedCount[0] == 1)
                            selectedLanguage2 = checkBox.getText();

                        selectedCount[0]++;
                    } else {
                        if (selectedCount[0] == 1 && checkBox.getText().equals(selectedLanguage1))
                            selectedLanguage1 = null;
                        else if (selectedCount[0] == 1 && checkBox.getText().equals(selectedLanguage2))
                            selectedLanguage2 = null;
                        else if (selectedCount[0] == 2 && checkBox.getText().equals(selectedLanguage1)) {
                            selectedLanguage1 = selectedLanguage2;
                            selectedLanguage2 = null;
                        }
                        selectedCount[0]--;
                    }
                });
            }

            JButton okButton = new JButton("Зберегти");
            okButton.setFont(font);
            okButton.addActionListener(e12 -> {
                frame.dispose();
                if (selectedCount[0] != 2) {
                    JOptionPane.showMessageDialog(null, "Будь-ласка, оберіть дві мови");
                } else if (isLanguageAlreadySelected) {
                    JOptionPane.showMessageDialog(null, "Ви вже обрали мови");
                } else {
                    JOptionPane.showMessageDialog(null, "Обрані мови: " + selectedLanguage1 + ", " + selectedLanguage2);
                    System.out.println("Selected Language 1: " + selectedLanguage1);
                    System.out.println("Selected Language 2: " + selectedLanguage2);
                    isLanguageAlreadySelected = true;
                }
            });

            gbc.gridy++;
            panel.add(okButton, gbc);

            frame.add(panel);
            frame.setVisible(true);
        });
        return buttonToSelectLanguages;
    }

    public void setRunningProgram(boolean runningProgram) {
        Frame.isProgramRunning = runningProgram;
    }

    public static boolean isProgramRunning() {
        return isProgramRunning;
    }
}