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


## 4. Вимоги до робочого місця

| Що | Версія | Перевірка |
|---|---|---|
| JDK (Eclipse Temurin) | 17.0.21 | `java -version` → `openjdk version "17.…"` |
| Apache Maven | 3.9.16 | `mvn -v` → рядок `Java version: 17.…` |
| GitHub Desktop або Git | будь-яка | для клонування |
| Google Chrome | актуальна | лише для UI-тестів і Selenium IDE; chromedriver завантажує Selenium Manager |

Інтернет потрібен для першої збірки (Maven завантажує залежності в `%USERPROFILE%\.m2`) і для UI-тестів (публічний demo).
Якщо `java` або `mvn` «не розпізнано як команду»: Windows → «Змінити системні змінні середовища» → **Змінні середовища** →
створити `JAVA_HOME` = папка JDK (напр. `C:\Program Files\Eclipse Adoptium\jdk-17…`) → у `Path` додати `%JAVA_HOME%\bin`
і папку `bin` Maven (напр. `C:\tools\apache-maven-3.9.11\bin`) → відкрити **новий** PowerShell і повторити перевірку.

## 5. Запуск тестів

**1. Клонування.** GitHub Desktop → **File → Clone repository → GitHub.com** → `artemfb42/autorateFB42` →
Local path `C:\dev\autorateFB42` (шлях без кирилиці й пробілів, не в OneDrive) → **Clone**.

**2. Перехід у Maven-проєкт.** Проєкт лежить не в корені репозиторію, а в `docs\sprint4\qa-autorate` — усі команди нижче
виконуються з цієї папки (інакше Maven відповідає «there is no POM in this directory»):

```powershell
cd C:\dev\autorateFB42\docs\sprint4\qa-autorate
```

**3. Юніт-тести модуля.** Звичайний запуск виконує лише юніт-тести: UI-тести мають тег `ui` і виключені в `pom.xml` (`excludedGroups=ui`).

```powershell
mvn clean test
```

Очікуваний результат — модуль v1 містить дефекти, тому збірка завершується `BUILD FAILURE`; це правильний стан:

```
[ERROR] Tests run: 96, Failures: 12, Errors: 0, Skipped: 1
[INFO] BUILD FAILURE
```

12 Failures — це 6 тест-методів (параметризовані падають на кількох наборах даних), кожен відповідає звіту про дефект (розд. 6.5).
Skipped: 1 — тест порожнього коментаря, вимкнений до рішення замовника за RD-09. **Errors має бути 0.**

**4. UI-тести** (потрібні Chrome та інтернет; браузер працює у фоновому режимі):

```powershell
mvn test "-Dgroups=ui" "-DexcludedGroups=none"
```

Очікувано: `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`.
Попередження `Unable to find CDP implementation matching …` нешкідливе: тести не використовують DevTools-протокол.

**5. Окремий клас, метод, видимий браузер:**

```powershell
mvn test "-Dtest=VoteValidatorTest"                                   # один юніт-клас: 37 / 2 / 0 / 1
mvn test "-Dtest=RatingAggregatorTest#roundsHalfUpToOneDecimal"       # один метод
mvn test "-DexcludedGroups=none" "-Dtest=LoginUiTest" "-Dheadless=false"   # UI-клас із видимим вікном Chrome
mvn clean test "-DexcludedGroups=none"                                # юніт- і UI-тести разом: 106 / 12 / 0 / 1
```

Параметри `-D…` у PowerShell беруться в лапки, інакше PowerShell розриває аргумент на крапці.

**6. Параметри UI-тестів.** За замовчуванням вхід виконується тестовим обліковим записом `fb42_reg1` (журнал спринта 3).
Інший обліковий запис: `"-Dautorate.login=…" "-Dautorate.password=…" "-Dautorate.firstName=…"`.
Demo тимчасово блокує акаунт після кількох невдалих входів: якщо `LoginUiTest` дає Errors на очікуванні привітання,
зачекайте кілька хвилин, увійдіть вручну на сайті й повторіть запуск.

**7. Selenium IDE.** Розширення Selenium IDE для **Chrome** → Open project → `selenium-ide\autorate.side` → Run all tests
(TC-005, TC-027). У Firefox 147+ команда `type` не працює (`initKeyEvent is not a function`).

Звіти Surefire — у `target\surefire-reports\` (`*.txt`, `TEST-*.xml`); папка `target/` у репозиторій не комітиться.

## 6. Автоматизація (розділ досьє)

### 6.1. Норматив: план і факт (команда з 5 осіб)

| Показник | Норматив | План | Факт | Доказ |
|---|---:|---:|---:|---|
| Юніт-тестів JUnit 5 (параметризований метод = 1 тест) | ≥20 | 40 | 40 (91 запуск) | [матриця](qa-autorate/docs/coverage-matrix.md) |
| Юніт-тестів на особу | ≥3 | 8 | Собко 8, Бурмай 8, Фощій 8, Деркач 8, Найда 8 | матриця, історія комітів |
| UI-сценаріїв WebDriver | ≥8 | 10 | 10 | [UI-сценарії](qa-autorate/docs/ui-scenarios.md) |
| Сценаріїв Selenium IDE | 1–2 | 2 | 2 (TC-005, TC-027) | `selenium-ide/autorate.side` |
| Дефектів модуля з 6 закладених | ≥5 | 6 | 6 | розд. 6.5 |

### 6.2. Матриця покриття BL

| Вимога | Метод модуля | Тест-клас | Методів | Червоних на v1 | Автор блоку |
|---|---|---|---:|---|---|
| BL-10 | `RatingAggregator.average` | `RatingAggregatorTest` | 8 | 1 (BUG-014) | Собко Артем |
| BL-20 | `RatingAggregator.topN` | `RatingAggregatorTest` | 8 | 1 (BUG-015) | Бурмай Юлія |
| BL-30, BL-40 | `VoteValidator.validate` | `VoteValidatorTest` | 8 | 2 (BUG-016, BUG-017) | Фощій Дмитро |
| BL-50, BL-80 | `VoteValidator.validate`, `containsStopWord` | `VoteValidatorTest` | 8 | 0 (1 skipped, RD-09) | Бурмай Юлія |
| BL-60, BL-70 | `ModelSearch.search`, `sortByRating` | `ModelSearchTest` | 8 | 2 (BUG-018, BUG-019) | Деркач Юрій |

Правила, межі, очікувані значення й рецензенти для кожного тесту — у [матриці покриття](qa-autorate/docs/coverage-matrix.md).

### 6.3. UI-сценарії

| Тест-клас | Сценарії | Вимоги |
|---|---|---|
| `RegistrationUiTest` | TC-001, TC-004, TC-005 | F-20, F-30, GUI-20 |
| `LoginUiTest` | TC-017, TC-018, TC-019 | GUI-30, U-20, F-40, F-50, S-10 |
| `CatalogUiTest` | TC-021, TC-022, TC-023, TC-027 | F-10, F-60, F-70, F-90, F-130, GUI-50 |

Page Object: `TopBar`, `RegisterPage`; лише явні очікування `WebDriverWait`; локатори — `id`, `name`, CSS, відносний XPath.
Кроки, локатори й очікування — у [UI-сценаріях](qa-autorate/docs/ui-scenarios.md).

### 6.4. Підсумок Surefire

| Набір | Команда | Tests run | Failures | Errors | Skipped |
|---|---|---:|---:|---:|---:|
| Юніт | `mvn clean test` | 96 | 12 | 0 | 1 |
| UI | `mvn test "-Dgroups=ui" "-DexcludedGroups=none"` | 10 | 0 | 0 | 0 |

Дати, коміт, тривалість і три запуски UI-набору поспіль — у [протоколі Surefire](qa-autorate/docs/surefire-summary.md).

### 6.5. Висновок про стан модуля v1

Модуль v1 **не готовий до випуску**: 6 із 8 правил бізнес-логіки (BL-10, BL-20, BL-30, BL-40, BL-60, BL-70) порушено,
серед них Critical — після першого голосу користувач не може проголосувати за жодну іншу модель (BUG-016).
Правила BL-50 і BL-80 у межах, однакових для SRS 1.0 і 1.1, виконуються. Порожній коментар не перевірено до рішення
замовника за RD-09. Усі 6 червоних тестів лишаються в наборі до виправлення у v2; після виправлення очікується
`Tests run: 96, Failures: 0`. UI-набір на demo проходить повністю (10 / 10).

## 7. Зареєстровані дефекти

| ID | Вимога | Тест, що фіксує дефект | Що не так у v1 | Severity | Priority | Issue |
|---|---|---|---|---|---|---|
| BUG-014 | BL-10 | `RatingAggregatorTest.roundsHalfUpToOneDecimal` | округлення вниз замість HALF_UP: 4,6 замість 4,7 | Normal | P3 | [#15](https://github.com/artemfb42/autorateFB42/issues/15) |
| BUG-015 | BL-20 | `RatingAggregatorTest.modelNeedsAtLeastThreeVotes` | модель рівно з 3 голосами не потрапляє в topN | Major | P3 | [#19](https://github.com/artemfb42/autorateFB42/issues/19) |
| BUG-016 | BL-30 | `VoteValidatorTest.sameUserOtherModelIsAccepted` | голос за іншу модель відхилено як повторний | Critical | P3 | [#22](https://github.com/artemfb42/autorateFB42/issues/22) |
| BUG-017 | BL-40 | `VoteValidatorTest.ratingWithinRangeIsAccepted` | оцінку 5 відхилено як «поза межами 1..5» | Major | P3 | [#21](https://github.com/artemfb42/autorateFB42/issues/21) |
| BUG-018 | BL-60 | `ModelSearchTest.blankQueryGivesEmptyResult` | порожній запит повертає весь каталог | Normal | P3 | [#17](https://github.com/artemfb42/autorateFB42/issues/17) |
| BUG-019 | BL-70 | `ModelSearchTest.sortsByRatingDescending` | сортування за зростанням замість спадання | Major | P3 | [#18](https://github.com/artemfb42/autorateFB42/issues/18) |


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

## 8. Простежуваність

`SRS 1.1 → BL-вимога → тест-метод (@DisplayName з ID) → результат Surefire → GitHub Issue → review → аудит`

`TC спринта 3 → сценарій у ui-scenarios.md → Selenium IDE / WebDriver-тест → протокол запуску`

## 9. Критерії завершення

- [x] Maven-проєкт і модуль v1 у `main`, `target/` не комітиться.
- [x] Юніт-тести BL-10…BL-80: 40 при нормативі ≥20.
- [x] UI-тести WebDriver: 10 при нормативі ≥8; Selenium IDE: 2 сценарії з експортом.
- [x] 6 дефектів модуля в GitHub Issues, кожен із червоним тестом.
- [x] Матриця покриття і UI-сценарії відповідають коду.
- [x] README: каталог запуску, команди юніт- і UI-набору, очікуваний результат.
- [ ] Протокол Surefire: дата, коміт і три запуски UI-набору поспіль.
- [ ] Code review кожного блоку з датою і результатом.
- [ ] Повторний аудит за [audit.md](audit.md), розд. 7.
- [ ] Керівник тестування затвердив підсумок.

## 10. Підсумок керівника тестування

Команда автоматизувала перевірку всіх правил BL-10…BL-80 (40 юніт-тестів) і 10 UI-сценаріїв спринта 3, знайшла всі 6 закладених
дефектів модуля v1 і оформила їх звітами в трекері. Висновок про стан модуля — розд. 6.6.

---

**Підготував:** Собко Артем, керівник тестування спринту 4.
