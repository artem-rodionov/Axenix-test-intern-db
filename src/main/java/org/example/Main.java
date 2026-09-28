package org.example;

import java.util.List;

public class Main {
    static void main(String[] args) {
        EmployeeDAO dao = new EmployeeDAO();
        List<EmployeeReport> report = dao.getEmployeeReport();

        if (report.isEmpty()) {
            System.out.println("Данные не найдены или произошла ошибка.");
            return;
        }

        // Красивый табличный вывод
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("| %-20s | %-30s | %-15s |%n", "Департамент", "Должность", "Кол-во сотрудников");
        System.out.println("---------------------------------------------------------------------------------");

        for (EmployeeReport line : report) {
            System.out.printf("| %-20s | %-30s | %-15d |%n",
                    line.departmentName(),
                    line.roleName(),
                    line.employeeCount());
        }
        System.out.println("---------------------------------------------------------------------------------");
    }
}