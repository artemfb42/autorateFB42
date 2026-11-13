# АвтоРейт — Спринт 4: автоматизація тестування та контроль якості

**Дисципліна:** Технології забезпечення якості програмних засобів  
**Продукт:** «АвтоРейт» — каталог і рейтинг автомобілів  
**Команда:** $Win, ФБ-42  
**Керівник тестування:** Собко Артем (`@artemfb42`)  
**Базова специфікація:** [SRS 1.1](../sprint2/SRS-1.1.md), розділ 5  
**Дата актуалізації:** 13.11.2026  
**Статус:** у процесі інтеграції та рецензування.

## 1. Мета спринту

Мета четвертого спринту — автоматизувати перевірку бізнес-логіки Java-модуля та UI-сценаріїв, забезпечити простежуваність вимог, тестів, результатів і дефектів.

Основні завдання:
1. Підготувати Java 17 та Maven.
2. Реалізувати JUnit 5 тести для BL-10–BL-80.
3. Зареєструвати дефекти в GitHub Issues.
4. Підготувати матрицю покриття вимог.
5. Автоматизувати UI-сценарії через Selenium IDE та WebDriver.
6. Провести review, аудит та підсумкове тестування.

## 2. Команда

| Учасник | Роль | Відповідальність |
|---|---|---|
| Собко Артем (`@artemfb42`) | Керівник тестування | Координація, BL-10, BUG-014, звіт |
| Бурмай Юлія (`@b-yuliia`) | Тест-аналітик | Матриця покриття, сценарії, BL-20 |
| Деркач Юрій (`@ShiftSSS3`) | Інженер з тестування | ModelSearchTest, BUG-018/019, Selenium |
| Фощій Дмитро (`@fb42dima`) | Скрам-майстер | Review, VoteValidatorTest |
| Найда Владислав (`@vladnaj-ipt28-cmd`) | Аудитор-опонент | Незалежне відтворення та аудит |

## 3. Структура та документація

- [SRS 1.1](../sprint2/SRS-1.1.md)
- [Статут команди](../team/charter.md)
- [Матриця покриття](qa-autorate/docs/coverage-matrix.md)
- [UI-сценарії](qa-autorate/docs/ui-scenarios.md)
- [Selenium IDE](qa-autorate/selenium-ide/autorate.side)
- [JUnit-тести](qa-autorate/src/test/java/ua/kpi/tzyapz/autorate/)
- [GitHub Issues](https://github.com/artemfb42/autorateFB42/issues)
- [Pull Requests](https://github.com/artemfb42/autorateFB42/pulls)

## 4. Технічне середовище

| Компонент | Версія |
|---|---|
| ОС | Windows 11 |
| Java | JDK 17 |
| Maven | 3.9.x |
| JUnit Jupiter | 5.10.2 |
| Selenium Java | 4.21.0 |
| Maven Surefire | 3.2.5 |
| Maven Compiler | 3.13.0 |

Перевірка середовища:

```powershell
java -version
mvn -v
```

## 5. Запуск тестів

Виконувати в каталозі з відповідним `pom.xml`.

```powershell
mvn test
mvn test "-Dtest=RatingAggregatorTest"
mvn test "-Dtest=VoteValidatorTest"
mvn test "-Dtest=ModelSearchTest"
```

Запуск окремих методів:

```powershell
mvn test "-Dtest=RatingAggregatorTest#roundsHalfUpToOneDecimal"
mvn test "-Dtest=ModelSearchTest#sortsByRatingDescending"
```

Звіти створюються в `target/surefire-reports/`.

**Примітка:** падіння тестів через відомі дефекти продукту не приховуються та не виправляються зміною очікуваних результатів.

## 6. Модульне тестування

| Тестовий клас | Вимоги | Перевірки |
|---|---|---|
| `RatingAggregatorTest` | BL-10, BL-20 | Середній рейтинг, округлення, topN |
| `VoteValidatorTest` | BL-30–BL-50, BL-80 | Валідація голосів, дублікати, межі |
| `ModelSearchTest` | BL-60, BL-70 | Пошук і сортування |
| `ExampleTest` | Технічна | Перевірка тестового середовища |

Підходи: граничні значення, класи еквівалентності, порожні та null-дані, перевірка сортування, параметризовані тести.

## 7. Зареєстровані дефекти

| ID | Issue | Опис | Severity |
|---|---|---|---|
| BUG-014 | [#15](https://github.com/artemfb42/autorateFB42/issues/15) | Некоректне округлення рейтингу | Normal |
| BUG-015 | [#19](https://github.com/artemfb42/autorateFB42/issues/19) | topN не включає модель із 3 голосами | Major |
| BUG-016 | [#22](https://github.com/artemfb42/autorateFB42/issues/22) | Голос за іншу модель вважається повторним | Critical |
| BUG-017 | [#21](https://github.com/artemfb42/autorateFB42/issues/21) | Відхилення оцінки 5 | Major |
| BUG-018 | [#17](https://github.com/artemfb42/autorateFB42/issues/17) | Порожній пошук повертає всі моделі | Normal |
| BUG-019 | [#18](https://github.com/artemfb42/autorateFB42/issues/18) | Сортування рейтингу за зростанням | Major |

Усі дефекти мають пріоритет P3. Відкритий Issue не означає, що дефект виправлено.

### BUG-014

Метод `RatingAggregator.average` некоректно округлює значення до одного десяткового знака. Для перевірених граничних випадків спостерігалися:

| Очікувано | Фактично |
|---|---|
| 4.7 | 4.6 |
| 3.3 | 3.2 |
| 3.8 | 3.7 |
| 4.8 | 4.7 |

### BUG-019

Метод `ModelSearch.sortByRating` сортує моделі за зростанням, хоча BL-70 вимагає спадання.

Очікуваний порядок: Lamborghini Aventador → Lamborghini Diablo → Ferrari F40 → Ford Mustang.

Фактичний порядок: Ford Mustang → Ferrari F40 → Lamborghini Diablo → Lamborghini Aventador.

## 8. UI-автоматизація

Використовується демонстраційний вебсайт [Buggy Cars Rating](https://buggy.justtestit.org/).

| Група | Тестовий клас | Сценарії |
|---|---|---|
| Реєстрація | `RegistrationUiTest` | TC-001, TC-004, TC-005 |
| Авторизація | `LoginUiTest` | TC-017, TC-018, TC-019 |
| Каталог | `CatalogUiTest` | TC-021, TC-022, TC-023, TC-027 |

Підготовлено Selenium IDE сценарії та Java-класи WebDriver. Успішність запусків потрібно підтвердити окремими протоколами.

## 9. Гілки та Pull Requests

| Учасник | Гілка | PR |
|---|---|---|
| Собко Артем | `feature/S4-sobko` | [#14](https://github.com/artemfb42/autorateFB42/pull/14) |
| Деркач Юрій | `feature/S4-derkach` | [#20](https://github.com/artemfb42/autorateFB42/pull/20) |
| Фощій Дмитро | `feature/S4-foshchii` | [#23](https://github.com/artemfb42/autorateFB42/pull/23) |
| Бурмай Юлія | `feature/S4-files-Burmai` | [#16](https://github.com/artemfb42/autorateFB42/pull/16) |

Правила: окремі робочі гілки, PR до main, рецензування перед злиттям, відсутність target/ у комітах.

## 10. Простежуваність

`SRS 1.1 → BL-вимога → JUnit-тест → результат → GitHub Issue → review → аудит`

Для UI:

`TC → Selenium IDE → WebDriver → протокол запуску → підсумковий статус`

Основні докази: звіти Surefire, повідомлення assertion error, скриншоти, Issues, PR, результати UI-прогонів та аудиторські записи.

## 11. Критерії завершення

- [ ] Усі заплановані тести інтегровані.
- [ ] Maven-проєкт перевірено в main.
- [ ] Результати JUnit задокументовано.
- [ ] Дефекти оформлено в GitHub Issues.
- [ ] Матрицю покриття актуалізовано.
- [ ] Selenium IDE та WebDriver перевірено.
- [ ] Результати UI-прогонів підтверджено.
- [ ] PR пройшли review.
- [ ] Незалежний аудит завершено.
- [ ] Керівник тестування затвердив підсумок.

## 12. Підсумок керівника тестування

У четвертому спринті команда підготувала JUnit-тести для бізнес-логіки, UI-сценарії Selenium, матрицю покриття та звіти про дефекти BUG-014–BUG-019.

Виявлені проблеми стосуються округлення рейтингу, відбору моделей, перевірки голосів, порожнього пошуку та сортування.

Остаточний статус спринту буде визначено після інтеграції командних PR, повторного запуску тестів і завершення рецензування та аудиту.

---

**Підготував:** Собко Артем, керівник тестування спринту 4. 
