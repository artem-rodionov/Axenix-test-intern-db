package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeDAO {
    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private static final String SELECT_SQL = "SELECT d.\"name\" AS department_name, \n" +
            "   \t\t  r.\"name\" AS role_name, \n" +
            "   \t\t  COUNT(*) AS employee_count\n" +
            "     FROM employees_departments ed\n" +
            "     JOIN employees e \n" +
            "       ON e.id = ed.employee_id \n" +
            "     JOIN departments d \n" +
            "       ON d.id = ed.department_id \n" +
            "     JOIN roles r \n" +
            "       ON r.id = e.role_id\n" +
            " GROUP BY d.\"name\", r.\"name\"\n" +
            " ORDER BY d.\"name\", COUNT(*) desc";

    public List<EmployeeReport> getEmployeeReport() {
        List<EmployeeReport> report = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_SQL)) {
            while (rs.next()) {
                report.add(new EmployeeReport(
                        rs.getString("department_name"),
                        rs.getString("role_name"),
                        rs.getInt("employee_count")
                ));
            }
            log.info("{} строк получено", report.size());
        } catch (SQLException e) {
            log.error("Ошибка при выполнении запроса: ", e);
        }
        return report;
    }


    private Connection getConnection() throws SQLException {
        Properties properties = ConfigManager.getConfig();
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        Connection conn = DriverManager.getConnection(url, username, password);
        log.info("Успешное подключение к базе данных: {}", url);
        return conn;
    }
}
