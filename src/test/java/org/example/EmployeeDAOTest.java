package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeDAOTest {

    private Properties createMockProperties() {
        Properties properties = new Properties();
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/test");
        properties.setProperty("db.username", "test");
        properties.setProperty("db.password", "test");
        return properties;
    }

    @Test
    @DisplayName("Должен вернуть корректный список при успешном выполнении запроса")
    void shouldReturnCorrectReportWhenQuerySucceeds() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        Connection mockConnection = mock(Connection.class);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("department_name"))
                .thenReturn("Разработка", "Маркетинг");
        when(mockResultSet.getString("role_name"))
                .thenReturn("Backend-разработчик", "Маркетолог");
        when(mockResultSet.getInt("employee_count"))
                .thenReturn(5, 2);

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        try (MockedStatic<ConfigManager> configMock = mockStatic(ConfigManager.class);
             MockedStatic<DriverManager> driverMock = mockStatic(DriverManager.class)) {

            configMock.when(ConfigManager::getConfig).thenReturn(createMockProperties());
            driverMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            EmployeeDAO dao = new EmployeeDAO();

            List<EmployeeReport> report = dao.getEmployeeReport();

            assertEquals(2, report.size(), "Должно вернуться 2 строки");

            assertEquals("Разработка", report.get(0).departmentName());
            assertEquals("Backend-разработчик", report.get(0).roleName());
            assertEquals(5, report.get(0).employeeCount());

            assertEquals("Маркетинг", report.get(1).departmentName());
            assertEquals("Маркетолог", report.get(1).roleName());
            assertEquals(2, report.get(1).employeeCount());

            verify(mockStatement, times(1)).executeQuery(anyString());
        }
    }

    @Test
    @DisplayName("Должен вернуть пустой список, если запрос вернул 0 строк")
    void shouldReturnEmptyListWhenNoRows() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        Connection mockConnection = mock(Connection.class);

        when(mockResultSet.next()).thenReturn(false);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        try (MockedStatic<ConfigManager> configMock = mockStatic(ConfigManager.class);
             MockedStatic<DriverManager> driverMock = mockStatic(DriverManager.class)) {

            configMock.when(ConfigManager::getConfig).thenReturn(createMockProperties());
            driverMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            EmployeeDAO dao = new EmployeeDAO();
            List<EmployeeReport> report = dao.getEmployeeReport();

            assertTrue(report.isEmpty());
        }
    }
}