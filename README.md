# autorateFB42 — досьє якості продукту «АвтоРейт»

Дисципліна «Технології забезпечення якості програмних засобів», команда **$Win** (ФБ-42).
Репозиторій містить документи спринтів 1–3 і, починаючи зі спринта 4, **репозиторій автотестів**:
модуль бізнес-логіки `autorate-module` v1, юніт-тести JUnit 5 і UI-тести Selenium WebDriver для demo
[Buggy Cars Rating](https://buggy.justtestit.org/).

| Спринт | Інкремент | Документи |
|---|---|---|
| 1 | Статут, бізнес-процеси, беклог | [docs/team](docs/team/charter.md), [docs/sprint1](docs/sprint1/) |
| 2 | Аудит вимог, SRS 1.1 | [docs/sprint2](docs/sprint2/README.md) |
| 3 | Тест-кейси, журнал виконання, звіти про дефекти demo | [docs/sprint3](docs/sprint3/README.md) |
| 4 | Автотести, протокол запуску, звіти про дефекти модуля | [docs/sprint4](docs/sprint4/README.md) |

Трекер дефектів: [GitHub Issues](https://github.com/artemfb42/autorateFB42/issues) (дефекти модуля — мітка `module`).

## 1. Вимоги до робочого місця

| Що | Версія | Як перевірити |
|---|---|---|
| JDK (Eclipse Temurin) | 17 | `java -version` → `openjdk version "17.…"` |
| Apache Maven | 3.9 | `mvn -v` → рядок `Java version: 17.…` |
| Git | будь-яка | `git --version` |
| Google Chrome | актуальна | потрібен лише для UI-тестів; драйвер завантажує Selenium Manager |

Інтернет потрібен для першого збирання (Maven завантажує залежності у `~/.m2`) і для UI-тестів (відкривають публічний demo).

## 2. Збірка і запуск

**Клонування.** GitHub Desktop: **File → Clone repository… → GitHub.com** → `artemfb42/autorateFB42` →
**Local path** `C:\dev\autorateFB42` (шлях без кирилиці й пробілів, не в OneDrive) → **Clone**.
Або в терміналі: `git clone https://github.com/artemfb42/autorateFB42.git`.

**Термінал у папці проєкту.** GitHub Desktop: **Repository → Open in PowerShell** (або `cd C:\dev\autorateFB42`).
Усі команди нижче виконуються з кореня репозиторію — там, де лежить `pom.xml`.

```powershell
# 1) юніт-тести модуля (UI-тести з тегом "ui" за замовчуванням виключені)
mvn clean test

# 2) лише UI-тести (потрібен Chrome; браузер працює у headless-режимі)
mvn test "-Dgroups=ui" "-DexcludedGroups=none"

# 3) юніт- і UI-тести разом
mvn clean test "-DexcludedGroups=none"
```

Додаткові варіанти:

```powershell
mvn test "-Dtest=VoteValidatorTest"                               # один юніт-клас
mvn test "-Dtest=VoteValidatorTest#sameUserOtherModelIsAccepted"  # один метод
mvn test "-DexcludedGroups=none" "-Dtest=LoginUiTest"             # один UI-клас
mvn test "-DexcludedGroups=none" "-Dtest=LoginUiTest" "-Dheadless=false"   # UI-тест із видимим вікном браузера
```

Параметри `-D…` взято в лапки, бо PowerShell розриває аргумент на крапці (`-Dautorate.login=…` без лапок не спрацює).
У Git Bash, macOS і Linux лапки не потрібні, але й не заважають.

## 3. Очікуваний результат

**Команда 1 (`mvn clean test`)** — модуль v1 містить дефекти, тому збірка завершується `BUILD FAILURE`; це очікуваний стан:

```
[ERROR] Tests run: 96, Failures: 12, Errors: 0, Skipped: 1
[INFO] BUILD FAILURE
```

Падають 6 тест-методів (12 запусків з урахуванням наборів даних параметризованих тестів), і кожен відповідає
зареєстрованому звіту про дефект модуля:

| Тест | Вимога | Звіт про дефект |
|---|---|---|
| `RatingAggregatorTest.roundsHalfUpToOneDecimal` | BL-10 | BUG-014 |
| `RatingAggregatorTest.modelNeedsAtLeastThreeVotes` | BL-20 | BUG-015 |
| `VoteValidatorTest.sameUserOtherModelIsAccepted` | BL-30 | BUG-016 |
| `VoteValidatorTest.ratingWithinRangeIsAccepted` | BL-40 | BUG-017 |
| `ModelSearchTest.blankQueryGivesEmptyResult` | BL-60 | BUG-018 |
| `ModelSearchTest.sortsByRatingDescending` | BL-70 | BUG-019 |

`Errors` має дорівнювати 0. `Skipped: 1` — тест правила про порожній коментар, вимкнений до рішення замовника
щодо RD-09 (причина записана в `@Disabled`).

**Команда 2 (UI-набір)** — усі тести проходять:

```
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 4. Де лежать звіти

Після запуску — у `target/surefire-reports/`: `*.txt` — підсумок і трасування по класу, `TEST-*.xml` — усі тести
з назвами `@DisplayName` і часом. Папка `target/` у репозиторій не потрапляє; зафіксований протокол запуску —
[docs/surefire-summary.md](docs/surefire-summary.md).

## 5. Структура

```
autorateFB42/
├── README.md
├── .gitignore                                # target/, .idea/, *.iml, .DS_Store
├── pom.xml                                   # JDK 17, JUnit 5.10.2, Selenium 4.21.0, Surefire 3.2.5
├── docs/
│   ├── coverage-matrix.md                    # BL-вимога → правило → тести → рецензент
│   ├── ui-scenarios.md                       # TC → кроки, локатори, очікування, тест-метод; Selenium IDE
│   ├── surefire-summary.md                   # протокол запуску
│   ├── team/, sprint1/, sprint2/, sprint3/   # попередні інкременти
│   └── sprint4/                              # розділ «Автоматизація» досьє, дошка задач, протокол аудиту, ретро
├── selenium-ide/
│   ├── autorate.side                         # проєкт Selenium IDE (TC-005, TC-027)
│   └── export/                               # експорт Java JUnit (чернетки, не збираються)
└── src/
    ├── main/java/ua/kpi/tzyapz/autorate/     # модуль v1: RatingAggregator, VoteValidator, ModelSearch (+ записи Vote, CarModel)
    └── test/java/ua/kpi/tzyapz/autorate/
        ├── ExampleTest.java                  # зразок із пакета модуля
        ├── RatingAggregatorTest.java         # BL-10, BL-20
        ├── VoteValidatorTest.java            # BL-30, BL-40, BL-50, BL-80
        ├── ModelSearchTest.java              # BL-60, BL-70
        └── ui/
            ├── RegistrationUiTest.java       # TC-001, TC-004, TC-005
            ├── LoginUiTest.java              # TC-017, TC-018, TC-019
            ├── CatalogUiTest.java            # TC-021, TC-022, TC-023, TC-027
            ├── UiTestBase.java               # спільний життєвий цикл браузера
            └── pages/                        # Page Object: TopBar, RegisterPage
```

Один тест-клас на один клас модуля; усередині класу тести кожного автора згруповано в окремий блок.

## 6. Команда і хто що написав (спринт 4)

| Учасник | Роль | Юніт-тести | Інше |
|---|---|---|---|
| Собко Артем (@artemfb42) | Керівник тестування | `RatingAggregatorTest`, блок BL-10 — 8 тестів | README, протокол запуску, розділ досьє |
| Бурмай Юлія (@b-yuliia) | Тест-аналітик | `RatingAggregatorTest`, блок BL-20 — 8 тестів | матриця покриття, перелік UI-сценаріїв |
| Деркач Юрій (@ShiftSSS3) | Інженер з тестування | `ModelSearchTest`, BL-60, BL-70 — 8 тестів | Maven-проєкт, Selenium IDE, 10 WebDriver-тестів |
| Фощій Дмитро (@fb42dima) | Скрам-майстер / рецензент | `VoteValidatorTest`, блок BL-30, BL-40 — 8 тестів | дошка задач, code review, ретро |
| Найда Владислав (@vladnaj-ipt28-cmd) | Аудитор-опонент | `VoteValidatorTest`, блок BL-50, BL-80 — 8 тестів | протокол відтворення «з нуля» |

Перевірка за історією: https://github.com/artemfb42/autorateFB42/commits/main/src/test (автор кожного коміту видно у списку).

### Робочі місця (перший коміт кожного учасника)

Кожен учасник власним комітом заповнює свій рядок: результат `mvn -v` і `mvn clean test` на своїй машині.

- **Собко Артем** — JDK: … · Maven: … · ОС: … · `mvn clean test`: … · дата: …

- **Бурмай Юлія** — JDK: … · Maven: … · ОС: … · `mvn clean test`: … · дата: …

- **Деркач Юрій** — JDK: … · Maven: … · ОС: … · `mvn clean test`: … · дата: …

- **Фощій Дмитро** — JDK: … · Maven: … · ОС: … · `mvn clean test`: … · дата: …

- **Найда Владислав** — JDK: … · Maven: … · ОС: … · `mvn clean test`: … · дата: …

## 7. Параметри UI-тестів

| Параметр | За замовчуванням | Призначення |
|---|---|---|
| `"-Dautorate.baseUrl=…"` | `https://buggy.justtestit.org` | адреса demo |
| `"-Dheadless=false"` | headless увімкнено | показати вікно браузера |
| `"-Dautorate.login=…"`, `"-Dautorate.password=…"`, `"-Dautorate.firstName=…"` | обліковий запис `fb42_reg1` зі спринта 3 | тестовий користувач для `LoginUiTest` |

Тестовий обліковий запис містить лише вигадані дані й існує тільки на публічному demo-стенді.

## 8. Типові помилки

| Симптом | Причина і дія |
|---|---|
| `invalid target release: 17` | Maven бачить інший JDK: `mvn -v`; задати `JAVA_HOME` на JDK 17 і відкрити новий термінал |
| `mvn: command not found` | Maven не в `PATH`: додати папку `bin` Maven до `PATH` |
| `Tests run: 0` для UI-класу | тег `ui` виключено: додати `"-DexcludedGroups=none"` |
| `Unknown lifecycle phase ".baseUrl=…"` або схоже | PowerShell розірвав параметр на крапці: взяти `-D…` у лапки |
| `java` / `mvn` «is not recognized» | не задано `JAVA_HOME` або `Path`; після змін відкрити новий термінал |
| `SessionNotCreatedException`, `Unable to obtain driver` | немає інтернету або Chrome: перевірити мережу, оновити Chrome, очистити `~/.cache/selenium` |
| `TimeoutException` в UI-тесті | demo відповідає повільно або змінилася розмітка: повторити з `-Dheadless=false` і подивитися, на якому кроці зупинка |
| `LoginUiTest` падає на вході | обліковий запис `fb42_reg1` зник після скидання demo: створити його заново (First Name «Test») |
| Кирилиця у консолі Windows як `????` | виконати `chcp 65001` перед `mvn` або запускати тести з IntelliJ IDEA |
