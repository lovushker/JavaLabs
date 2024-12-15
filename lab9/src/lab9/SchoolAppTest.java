package lab9;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

public class SchoolAppTest {

    private SchoolApp app;

    @BeforeEach
    public void setup() {
        app = new SchoolApp();
    }

    @Test
    public void testSearchStudents() {
        
        String query = "Иванов";

        app.searchStudents(query);
        
        JTable studentTable = app.getStudentTable();
        
        
        String firstStudent = (String) studentTable.getValueAt(0, 0);
        
        
        assertEquals("Иванов", firstStudent);
        
        
        String secondStudent = (String) studentTable.getValueAt(1, 0);
        assertNotEquals("Иванов", secondStudent);
    }

    @Test
    public void testResetStudentTable() {
        
        app.searchStudents("Иванов");
        
        
        JTable studentTable = app.getStudentTable();
        String firstStudentAfterSearch = (String) studentTable.getValueAt(0, 0);
        assertEquals("Иванов", firstStudentAfterSearch);
        
        app.resetStudentTable();
             
        String firstStudentAfterReset = (String) studentTable.getValueAt(0, 0);
        assertEquals("Иванов", firstStudentAfterReset);  
    }

    @Test
    public void testTableSelectorVisibility() {
        JComboBox<String> tableSelector = app.getTableSelector();
        
        
        tableSelector.setSelectedItem("Предметы и преподаватели");
        assertFalse(app.getClassSelector().isVisible(), "Class selector should be hidden for Subjects table.");
        
        tableSelector.setSelectedItem("Преподаватели и классы");
        assertFalse(app.getClassSelector().isVisible(), "Class selector should be hidden for Teachers and Classes table.");

        
        tableSelector.setSelectedItem("Ученики");
        assertTrue(app.getClassSelector().isVisible(), "Class selector should be visible for Students table.");
    }

    @Test
    public void testSearchButtonFunctionality() {
        
        JTextField searchField = app.getSearchField();
        searchField.setText("Смирнов");
        
        
        JButton searchButton = app.getSearchButton();
        searchButton.doClick();
        
        
        JTable studentTable = app.getStudentTable();
        
        
        String firstStudent = (String) studentTable.getValueAt(0, 0);
        assertEquals("Смирнов", firstStudent);
        
        
        String secondStudent = (String) studentTable.getValueAt(1, 0);
        assertNotEquals("Смирнов", secondStudent);
    }
}

