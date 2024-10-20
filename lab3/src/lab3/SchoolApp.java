package lab3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
* Класс создает экранную форму
* @author Vasilyev Timofey 3312
* @version 1.0
*/

public class SchoolApp extends JFrame {

    // Объявление графических компонентов
    private JTable subjectTable, classTable, studentTable;
    private JPanel tablePanel;
    private JComboBox<String> tableSelector, classSelector;
    private CardLayout cardLayout;
    private JTextField searchField;
    private JButton searchButton;

    // Данные для таблицы учеников и успеваемости
    private String[][] allStudentData = {
        {"Иванов", "5А", "Отличник"},
        {"Петров", "6Б", "Двоечник"},
        {"Сидоров", "7В", "Хорошист"},
        {"Кузнецов", "5А", "Хорошист"},
        {"Смирнов", "6Б", "Отличник"},
        {"Попов", "7В", "Двоечник"}
    };
    private String[] studentColumns = {"Фамилия", "Класс", "Успеваемость"};
    
    // Исходные данные
    private String[][] originalStudentData;

    // Конструктор приложения
    public SchoolApp() {
        super("Школьная информационная система");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Данные для таблицы предметов и преподавателей
        String[][] subjectData = {
            {"Математика", "Иванов"},
            {"Физика", "Петров"},
            {"Химия", "Сидоров"}
        };
        String[] subjectColumns = {"Предмет", "Преподаватель"};

        // Данные для таблицы классов и преподавателей
        String[][] classData = {
            {"Иванов", "5А, 6Б"},
            {"Петров", "6Б, 7В"},
            {"Сидоров", "7В, 8А"}
        };
        String[] classColumns = {"Преподаватель", "Классы"};

        // Копируем исходные данные для учеников
        originalStudentData = allStudentData.clone();

        // Создание таблиц
        subjectTable = new JTable(subjectData, subjectColumns);
        classTable = new JTable(classData, classColumns);
        studentTable = new JTable(allStudentData, studentColumns);

        // Создание панели с карточной компоновкой
        cardLayout = new CardLayout();
        tablePanel = new JPanel(cardLayout);
        tablePanel.add(new JScrollPane(subjectTable), "Предметы и преподаватели");
        tablePanel.add(new JScrollPane(classTable), "Преподаватели и классы");
        tablePanel.add(new JScrollPane(studentTable), "Ученики");

        // Выпадающий список для выбора таблицы
        tableSelector = new JComboBox<>(new String[]{"Предметы и преподаватели", "Преподаватели и классы", "Ученики"});
        
        /**
         * Слушатель для выбора таблицы.
         * Меняет отображаемую таблицу в зависимости от выбранного пункта.
         * Если выбрана таблица "Ученики", делает видимым выпадающий список с классами.
         * 
         */
        tableSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedTable = (String) tableSelector.getSelectedItem();
                cardLayout.show(tablePanel, selectedTable);

                if (selectedTable.equals("Ученики")) {
                    classSelector.setVisible(true);
                } else {
                    classSelector.setVisible(false);
                }
            }
        });

        // Выпадающий список для выбора класса
        classSelector = new JComboBox<>(new String[]{"Все классы", "5А", "6Б", "7В"});
        classSelector.setVisible(false);

        // Добавление панели с выпадающим меню File
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        // Пункты меню
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem saveItem = new JMenuItem("Save file");
        JMenuItem importItem = new JMenuItem("Import");
        JMenuItem exportItem = new JMenuItem("Export");

        // Добавление пунктов меню в "File"
        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(importItem);
        fileMenu.add(exportItem);

        // Добавление меню в панель меню
        menuBar.add(fileMenu);

        // Установка меню в окно
        setJMenuBar(menuBar);

        // Панель для поиска
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        JLabel searchLabel = new JLabel("Поиск:");
        searchField = new JTextField(20);
        searchButton = new JButton("Найти");

        // Добавление компонентов панели поиска
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Добавление компонентов в основное окно
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(tableSelector);
        topPanel.add(classSelector);

        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        // Добавление обработчика для поиска
        /**
         * Слушатель для кнопки поиска.
         * Осуществляет поиск учеников по введенному в поле поиска запросу.
         * Если строка поиска пустая, восстанавливает исходную таблицу.
         * 
         */
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().toLowerCase();
                if (!query.isEmpty()) {
                    searchStudents(query);
                } else {
                    resetStudentTable();
                }
            }
        });

        /**
         * Слушатель для пункта меню "Save file".
         * Открывает диалоговое окно для выбора файла и сохраняет данные в указанный файл.
         * 
         */
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showSaveDialog(SchoolApp.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    saveToFile(file);
                }
            }
        });
    }

    // Метод для поиска учеников
    private void searchStudents(String query) {
        ArrayList<String[]> filteredData = new ArrayList<>();
        for (String[] student : originalStudentData) {
            boolean matches = false;
            for (String field : student) {
                if (field.toLowerCase().contains(query)) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                filteredData.add(student);
            }
        }

        // Обновляем таблицу с результатами поиска
        String[][] newStudentData = filteredData.toArray(new String[0][]);
        studentTable.setModel(new javax.swing.table.DefaultTableModel(newStudentData, studentColumns));
    }

    // Метод для сброса таблицы учеников к исходным данным
    private void resetStudentTable() {
        studentTable.setModel(new javax.swing.table.DefaultTableModel(originalStudentData, studentColumns));
    }

    // Метод для сохранения данных в файл
    private void saveToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Записываем заголовки
            for (String column : studentColumns) {
                writer.write(column + "\t");
            }
            writer.write("\n");

            // Записываем данные
            for (String[] row : allStudentData) {
                for (String data : row) {
                    writer.write(data + "\t");
                }
                writer.write("\n");
            }

            JOptionPane.showMessageDialog(this, "Данные успешно сохранены.", "Сохранение", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении файла.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Метод для запуска приложения
    public static void main(String[] args) {
        SchoolApp app = new SchoolApp();
        app.setVisible(true);
    }
}


