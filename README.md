## Проект тестирования backend "DomRF"

### Требования:

- Java 8
- Maven 3.6.3+

### Команда запуска
mvn clean test

### Генерация отчета
mvn allure:serve

### Параметризация
Параметризация отсутствует, вариативность проверок опирается на получение данных из системы

### Известные проблемы

Тест 'Калькулятор ипотеки' заканчивается с ошибкой проверки (вероятно ошибка в тестируемом приложении)


### Описание сценариев

#### Проверка ипотечного калькулятора
- Параметры (все параметры имеют значение по умолчанию):

- Шаги
  - Инициализация сессии
  - Получение данных по ипотечным программам
  - Генерация данных для расчета параметров кредита
  - Запрос расчета параметров кредита
  - Проверка параметров кредита

#### Проверка расчета кэшбэка по карте

- Шаги
  - Инициализация сессии
  - Генерация данных для расчета курсов обмена
  - Запрос расчета курсов обмена
  - Валидация схемы ответа