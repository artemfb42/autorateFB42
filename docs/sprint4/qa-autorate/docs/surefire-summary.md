# Протокол запуску автотестів (звіт Surefire)

**Спринт 4, команда $Win (ФБ-42).** Відповідальний: Собко Артем (КТ). Повні звіти — `docs/sprint4/qa-autorate/target/surefire-reports/` (у репозиторій не комітяться). Усі команди виконуються з папки `docs/sprint4/qa-autorate`.


## 1. Юніт-набір

| Тест-клас | Tests run | Failures | Errors | Skipped | Червоні тести → дефект |
|---|---:|---:|---:|---:|---|
| `ExampleTest` (зразок із пакета модуля) | 5 | 0 | 0 | 0 | — |
| `ModelSearchTest` | 20 | 5 | 0 | 0 | `blankQueryGivesEmptyResult[1..4]` → BUG-018; `sortsByRatingDescending` → BUG-019 |
| `RatingAggregatorTest` | 34 | 5 | 0 | 0 | `roundsHalfUpToOneDecimal[1..4]` → BUG-014; `modelNeedsAtLeastThreeVotes[3]` → BUG-015 |
| `VoteValidatorTest` | 45 | 2 | 0 | 1 | `sameUserOtherModelIsAccepted` → BUG-016; `ratingWithinRangeIsAccepted[5]` → BUG-017; Skipped: `emptyCommentIsRejectedAsRequired` (RD-09) |
| **Разом** | **104** | **12** | **0** | **1** | 6 тест-методів → 6 звітів про дефекти |

Surefire рахує кожен набір даних параметризованого тесту окремо, тому 48 тест-методів дають 104 запуски,
а 6 червоних методів — 12 Failures. Підсумок збірки — `BUILD FAILURE`: це очікуваний стан для v1, бо кожен
червоний тест відповідає зареєстрованому дефекту модуля. Errors має бути 0.



## 2. Відповідність червоних тестів звітам про дефекти

| Тест | Очікувано / фактично | Вимога | Bug ID | Issue |
|---|---|---|---|---|
| `RatingAggregatorTest.roundsHalfUpToOneDecimal` | 4,7 / 4,6 (і ще три набори) | BL-10 | BUG-014 | #15 |
| `RatingAggregatorTest.modelNeedsAtLeastThreeVotes[3]` | у списку / не у списку | BL-20 | BUG-015 | #19 |
| `VoteValidatorTest.sameUserOtherModelIsAccepted` | прийнято / «повторний голос» | BL-30 | BUG-016 | #22 |
| `VoteValidatorTest.ratingWithinRangeIsAccepted[5]` | прийнято / «оцінка поза межами 1..5» | BL-40 | BUG-017 | #21 |
| `ModelSearchTest.blankQueryGivesEmptyResult` | порожній список / весь каталог | BL-60 | BUG-018 | #17 |
| `ModelSearchTest.sortsByRatingDescending` | за спаданням / за зростанням | BL-70 | BUG-019 | #18 |

## 3. UI-набір

| Параметр | Значення |
|---|---|
| Команда | `mvn test "-Dgroups=ui" "-DexcludedGroups=none"` |
| Дата | 12.11.2026 |
| Виконавець | Деркач Юрій |
| Браузер | Chrome 157.0.8103.51 (chromedriver 157.0.8103.78 через Selenium Manager), headless, 1280×900 |
| Demo | https://buggy.justtestit.org |
| Тестовий обліковий запис | `fb42_reg1` (First Name «Test») |

| Тест-клас | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| `CatalogUiTest` | 4 | 0 | 0 | 0 |
| `LoginUiTest` | 3 | 0 | 0 | 0 |
| `RegistrationUiTest` | 3 | 0 | 0 | 0 |
| **Разом** | **10** | **0** | **0** | **0** |

Очікуваний підсумок — `BUILD SUCCESS`. Попередження `Unable to find CDP implementation matching 157` виводить Selenium 4.21.0, бо не знає DevTools-протоколу Chrome 157; тести CDP не використовують, на результат воно не впливає. Стабільність: набір запускається тричі поспіль, результати трьох запусків:


### 3.1. Запуски під час налагодження (12.11.2026)

Дані — з виводу Maven, збереженого на скриншотах. Показують, як було усунено помилки UI-набору.

| Завершено | Команда | Tests run / Failures / Errors | Тривалість | Причина помилок | Що зроблено |
|---|---|---|---|---|---|
| 12.11.2026 17:16:39 | `LoginUiTest` | 3 / 0 / 2 | 44,954 s | у `UiTestBase` пароль `fb42_reg1` за замовчуванням не збігався з фактичним — TC-017, TC-019 не входили (`TimeoutException` на `span.nav-link`, `a[href='/profile']`) | правильний пароль передано параметром `-Dautorate.password` |
| 12.11.2026 17:18:54 | UI-набір | 10 / 0 / 2 | 1:41 min | те саме | — |
| 12.11.2026 17:24:34 | `LoginUiTest` з правильним паролем | 3 / 0 / 1 | 40,233 s | TC-019 пройшов; TC-017 не дочекався привітання після невдалої спроби — стенд тимчасово блокує акаунт після серії невдалих входів | пауза, ручний вхід для скидання лічильника |
| 12.11.2026 17:40:34 | UI-набір | 10 / 0 / 2 | 1:40 min | локальна копія `UiTestBase` ще зі старим паролем (зміну зроблено на сайті GitHub, Pull не виконано) | `Pull origin`; пароль за замовчуванням виправлено (коміт `cc8edf5`) |

Попередження `Unable to find CDP implementation matching 157` у всіх запусках не впливало на результат.

## 4. Повний набір

Команда `mvn clean test "-DexcludedGroups=none"` запускає юніт- і UI-тести разом: очікувано `Tests run: 114, Failures: 12, Errors: 0, Skipped: 1`
(ті самі 12 Failures юніт-набору; усі 10 UI-тестів зелені).


## 5. Як отримати дані для таблиць (журнал запусків)

З папки `C:\dev\autorateFB42\docs\sprint4\qa-autorate`, PowerShell. Команда сама записує дату, час, підсумок і тривалість
кожного запуску у файл `C:\dev\surefire-runs.md` у вигляді рядків таблиці.

```powershell
chcp 65001
$log = "C:\dev\surefire-runs.md"
"Коміт: $(git rev-parse --short HEAD)" | Tee-Object -FilePath $log -Append
function Run-Suite([string]$name, [string[]]$mvnArgs) {
  $out = & mvn @mvnArgs 2>&1 | ForEach-Object { "$_" }
  $tests = ($out | Select-String "Tests run: \d+, Failures: \d+, Errors: \d+, Skipped: \d+$" | Select-Object -Last 1).Line -replace '^.*(Tests run.*)$','$1'
  $build = ($out | Select-String "BUILD (SUCCESS|FAILURE)" | Select-Object -Last 1).Matches.Value
  $fin   = ($out | Select-String "Finished at: (.*)$" | Select-Object -Last 1).Matches.Groups[1].Value
  $time  = ($out | Select-String "Total time:\s+(.*)$" | Select-Object -Last 1).Matches.Groups[1].Value
  "| $name | $fin | $tests | $build | $time |" | Tee-Object -FilePath $log -Append
}
Run-Suite "Юніт-набір" @("clean","test")
1..3 | ForEach-Object { Run-Suite "UI, запуск $_" @("test","-Dgroups=ui","-DexcludedGroups=none"); Start-Sleep -Seconds 60 }
```

Рядок «Юніт-набір» → розділ 1 (дата, тривалість), рядки «UI, запуск 1–3» → таблиця розділу 3, коміт — у розділи 1 і 3.