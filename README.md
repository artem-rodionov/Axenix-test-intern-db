# Тестовое задание Axenix — Часть 2 (Программа для работы с БД)

Консольное приложение на Java, демонстрирующее работу с базой данных учета персонала из Части 1.

## Требования

- **Язык:** Java 26 (OpenJDK 26.0.2)
- **Сборка:** Maven
- **СУБД:** PostgreSQL 14+
- **Драйвер:** `org.postgresql:postgresql:42.7.5`
- **Логирование:** Log4j2 (консоль + файл `logs/app.log`)
- **Тестирование:** JUnit 5 + Mockito

## Структура проекта

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── Main.java              — точка входа, вывод таблицы
│   │   ├── EmployeeDAO.java       — выполнение SQL-запроса
│   │   ├── ConfigManager.java     — чтение db.properties
│   │   └── EmployeeReport.java    — DTO (record)
│   └── resources/
│       ├── db.properties          — настройки подключения
│       └── log4j2.xml             — конфигурация логирования
└── test/
    └── java/org/example/
        └── EmployeeDAOTest.java   — тесты с моками JDBC
```

## Подготовка базы данных

1. Убедитесь, что PostgreSQL запущен.
```bash
docker run --name postgres-db -e POSTGRES_PASSWORD=123456 -p 5432:5432 -d postgres:18
```
2. Выполните DDL-скрипт и seed-скрипт из Части 1.
3. Проверьте параметры подключения в `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/postgres
db.username=postgres
db.password=123456
```
## Запуск

### Вариант 1: через IDE
Запустите класс `org.example.Main`.

### Вариант 2: через Maven
```bash
mvn clean compile exec:java -Dexec.mainClass="org.example.Main"
```

### Запуск тестов
```bash
mvn clean test
```

## Выполняемый запрос

Программа выполняет **Запрос №2** из Части 2 — распределение сотрудников по должностям внутри каждого департамента:

```sql
SELECT d.name AS department_name,
       r.name AS role_name,
       COUNT(*) AS employee_count
FROM employees_departments ed
JOIN employees e ON e.id = ed.employee_id
JOIN departments d ON d.id = ed.department_id
JOIN roles r ON r.id = e.role_id
GROUP BY d.name, r.name
ORDER BY d.name, COUNT(*) DESC;
```

### Пример вывода:

```
---------------------------------------------------------------------------------
| Департамент          | Должность                      | Кол-во сотрудников |
---------------------------------------------------------------------------------
| HR                   | HR-специалист                  | 2               |
| Маркетинг            | Менеджер проектов              | 1               |
| Маркетинг            | Маркетолог                     | 1               |
| Продажи              | Менеджер по продажам           | 3               |
| Разработка           | Backend-разработчик            | 5               |
...
---------------------------------------------------------------------------------
```

## Особенности реализации

- **Конфигурационный файл** для настроек подключения
- **Логирование** через Log4j2 в консоль и файл
- **Unit-тесты** с моками `Connection`, `Statement`, `ResultSet`
- **try-with-resources** для гарантированного закрытия ресурсов