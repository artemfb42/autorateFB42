# Матриця покриття: BL-вимога → правило → тести

**Спринт 4, команда $Win (ФБ-42).**  
Відповідальна: Бурмай Юлія (ТА). Джерело правил — [SRS 1.1, розділ 5](../../../sprint2/SRS-1.1.md).  
Тест-класи — по одному на клас модуля (методичка, п. 4.8) у `docs/sprint4/qa-autorate/src/test/java/ua/kpi/tzyapz/autorate/`:
`RatingAggregatorTest` (BL-10, BL-20), `VoteValidatorTest` (BL-30…BL-50, BL-80, контракт розд. 5), `ModelSearchTest` (BL-60, BL-70).
Усередині класу тести кожного автора згруповано в окремий блок із коментарем-заголовком.

Позначення в колонці «v1» (результат `mvn clean test` на модулі v1), як у журналі виконання спринта 3:
**passed** — тест проходить; **failed** — тест падає, поруч ID звіту про дефект і номер Issue;
**skipped** — тест вимкнено з причиною (`@Disabled`).
Колонку «Рецензент» заповнює рецензент після схвалення pull request за чек-листом 5.3 (дата + «ок» або посилання на PR із зауваженнями).


## BL-10. `RatingAggregator.average` — тест-клас `RatingAggregatorTest` (автор блоку: Собко Артем)

| Правило | Вид перевірки | Вхідні дані → очікуване (обчислено вручну) | Тест-метод | v1 | Рецензент |
|---|---|---|---|---|---|
| Зважене середнє: сума(оцінка × вага) / сума(ваг) | нормальний випадок | (5×3 + 4×1 + 2×2) / 6 = 3,833 → 3,8 | `weightedAverageUsesReputationAsWeight` | passed | Фощій Дмитро |
| Порожній список → 0,0 | окремий клас даних | `[]` → 0,0 | `emptyVotesGiveZero` | passed | Фощій Дмитро |
| Оцінка 1..5, вага 1..3 — межі допустимого | межі зсередини | 1:1 → 1,0; 5:3 → 5,0; 1:3+1:1 → 1,0; 5:1+5:2+5:3 → 5,0; 4:2 → 4,0 | `boundaryRatingsAndReputationsAreAccepted` (5 наборів) | passed | Фощій Дмитро |
| Округлення до 0,1 — друга цифра < 5 | межа округлення знизу | 13/3 = 4,33 → 4,3; 18/5 = 3,6; 7/3 = 2,33 → 2,3 | `roundsDownWhenNextDigitBelowFive` (3 набори) | passed | Фощій Дмитро |
| Округлення до 0,1 HALF_UP — друга цифра ≥ 5 | межа округлення (рівно 5 і вище) | 14/3 = 4,67 → 4,7; 13/4 = 3,25 → 3,3; 15/4 = 3,75 → 3,8; 19/4 = 4,75 → 4,8 | `roundsHalfUpToOneDecimal` (4 набори) | failed — BUG-014 (#15) | Фощій Дмитро |
| Оцінка поза 1..5 → IllegalArgumentException | заборонений вхід, обидва боки | 0, 6, −1 | `ratingOutOfRangeThrows` (3 набори) | passed | Фощій Дмитро |
| Репутація поза 1..3 → IllegalArgumentException | заборонений вхід, обидва боки | 0, 4, −1 | `reputationOutOfRangeThrows` (3 набори) | passed | Фощій Дмитро |
| `null` замість колекції → IllegalArgumentException | заборонений вхід | `null` | `nullVotesThrow` | passed | Фощій Дмитро |

## BL-20. `RatingAggregator.topN` — тест-клас `RatingAggregatorTest` (автор блоку: Бурмай Юлія)

| Правило | Вид перевірки | Вхідні дані → очікуване | Тест-метод | v1 | Рецензент |
|---|---|---|---|---|---|
| Порядок за рейтингом за спаданням | нормальний випадок | 4,2 / 4,9 / 3,5 → Diablo, F40, Mustang | `ordersByRatingDescending` | passed | Фощій Дмитро |
| Рівні рейтинги — за повною назвою без урахування регістру | окреме правило | audi R8, BMW M1, BMW M3, Ferrari F40 (усі 4,5) | `tiesAreOrderedByFullNameIgnoringCase` | passed | Фощій Дмитро |
| Модель у списку лише з ≥3 голосами | межа: 2 / **3** / 4 (і 0) | 0 → ні; 2 → ні; 3 → так; 4 → так | `modelNeedsAtLeastThreeVotes` (4 набори) | failed — BUG-015 (#19) | Фощій Дмитро |
| Не більше ніж n моделей | нормальний випадок | 4 моделі, n = 2 → Diablo, R8 | `returnsAtMostNModels` | passed | Фощій Дмитро |
| n більше за розмір каталогу | межа зверху | n = 100 → усі, що пройшли поріг | `nGreaterThanCatalogReturnsAllEligible` | passed | Фощій Дмитро |
| n ≤ 0 → порожній список | межа: 0 і нижче | 0, −1, −100 → `[]` | `nonPositiveNGivesEmptyList` (3 набори) | passed | Фощій Дмитро |
| n = 1 | межа: перше допустиме значення | n = 1 → одна найкраща модель | `nEqualToOneReturnsSingleBestModel` | passed | Фощій Дмитро |
| `null` замість колекції → IllegalArgumentException | заборонений вхід | `null` | `nullModelsThrow` | passed | Фощій Дмитро |

## BL-30, BL-40. `VoteValidator.validate` — тест-клас `VoteValidatorTest` (автор блоку: Фощій Дмитро)

| Вимога | Правило | Вид перевірки | Вхідні дані → очікуване | Тест-метод | v1 | Рецензент |
|---|---|---|---|---|---|---|
| BL-30 | Коректний голос → порожній перелік | нормальний випадок | u1, m1, 4, без попередніх голосів → `[]`, `isValid` = true | `validFirstVoteIsAccepted` | passed | Собко Артем |
| BL-30 | Та сама пара (userId, modelId) → «повторний голос» | порушення правила | є (u1, m1); новий (u1, m1) | `sameUserSameModelIsRejectedAsDuplicate` | passed | Собко Артем |
| BL-30 | Той самий користувач, інша модель — дозволено | межа правила: збігається лише userId | є (u1, m2); новий (u1, m1) → без «повторний голос» | `sameUserOtherModelIsAccepted` | failed — BUG-016 (#22) | Собко Артем |
| BL-30 | Інший користувач, та сама модель — дозволено | межа правила: збігається лише modelId | є (u2, m1); новий (u1, m1) | `otherUserSameModelIsAccepted` | passed | Собко Артем |
| BL-40 | Оцінка 1..5 включно приймається | межі зсередини: 1 і 5 | 1, 2, 3, 4, 5 → без повідомлення | `ratingWithinRangeIsAccepted` (5 наборів) | failed — BUG-017 (#21) — набір з оцінкою 5 | Собко Артем |
| BL-40 | Оцінка поза 1..5 → «оцінка поза межами 1..5» | межі ззовні: 0 і 6 | 0, 6, −1, 100 | `ratingOutsideRangeIsRejected` (4 набори) | passed | Собко Артем |
| BL-30 + BL-40 | Перелік містить усі порушення | пов'язані властивості | повторний голос + оцінка 0 → 2 повідомлення | `severalViolationsAreReportedTogether` | passed | Собко Артем |
| розд. 5 | `null` замість колекції → IllegalArgumentException | заборонений вхід | existingVotes = `null` | `nullExistingVotesThrow` | passed | Собко Артем |

## BL-50, BL-80. `VoteValidator` — тест-клас `VoteValidatorTest` (автор блоку: Бурмай Юлія)

| Вимога | Правило | Вид перевірки | Вхідні дані → очікуване | Тест-метод | v1 | Рецензент |
|---|---|---|---|---|---|---|
| BL-50 | Коментар до 200 символів приймається | межі зсередини: 1, 199, **200** | довжина 1, 199, 200 → `[]` | `commentUpToTwoHundredCharactersIsAccepted` (3 набори) | passed | Фощій Дмитро |
| BL-50 | Довший коментар → «коментар задовгий» | межа ззовні: **201** | довжина 201, 202, 1000 | `commentLongerThanTwoHundredCharactersIsRejected` (3 набори) | passed | Фощій Дмитро |
| BL-50 | Довжина рахується в символах | окремий клас даних (кирилиця) | 200 × «я» → без «коментар задовгий» | `twoHundredCyrillicCharactersAreAccepted` | passed | Фощій Дмитро |
| BL-50 (RD-09) | Порожній коментар → «коментар обов'язковий» | заборонений вхід | `""`, `null`, пробіли | `emptyCommentIsRejectedAsRequired` | skipped | Фощій Дмитро |
| BL-80 | Окреме стоп-слово → «недопустиме слово», регістр не враховується | порушення правила | «this is spam», «SCAM!», «Best Casino ever», «це казино, не рейтинг», «КАЗИНО», «anti-spam», «spam» | `commentWithStopWordIsRejected` (7 наборів) | passed | Фощій Дмитро |
| BL-80 | Стоп-слово як частина іншого слова — дозволено | межа правила «ціле слово» | «spammer», «scammers everywhere», «casinos», «казинове місто», звичайний коментар | `stopWordInsideAnotherWordIsAccepted` (5 наборів) | passed | Фощій Дмитро |
| BL-80 | Власний стоп-список через конструктор | альтернативна конфігурація | список [Lada]: «стара lada» відхилено, «this is spam» прийнято | `customStopListReplacesDefault` | passed | Фощій Дмитро |
| розд. 5 | `null` замість колекції → IllegalArgumentException | заборонений вхід | `new VoteValidator(null)` | `nullStopListThrows` | passed | Фощій Дмитро |

**Примітка до BL-50.** SRS 1.0 дозволяє порожній коментар, SRS 1.1 (дефект вимог RD-09) — забороняє; RD-09 має статус
«Очікує погодження» (SRS 1.1, розділ 7). Модуль v1 реалізує SRS 1.0. До рішення замовника тест на порожній коментар
вимкнено з поясненням (`@Disabled("RD-09 …")`, у звіті — `Skipped: 1`); довжину 200/201 обидві редакції описують однаково,
тому ці тести активні. Після погодження RD-09 тест вмикається; якщо v2 не реалізує правило — оформлюється звіт.

## Розд. 5 і `containsStopWord`. Контракт методів `VoteValidator` — тест-клас `VoteValidatorTest` (автор блоку: Бурмай Юлія)

| Вимога | Правило | Вид перевірки | Вхідні дані → очікуване | Тест-метод | v1 | Рецензент |
|---|---|---|---|---|---|---|
| розд. 5 | `userId = null` → IllegalArgumentException | заборонений вхід | `validate(null, "m1", 3, …)` | `nullUserIdThrows` | passed | Фощій Дмитро |
| розд. 5 | `modelId = null` → IllegalArgumentException | заборонений вхід | `validate("u1", null, 3, …)` | `nullModelIdThrows` | passed | Фощій Дмитро |
| BL-80 | Немає слів — немає стоп-слів | окремий клас даних (`assertAll`) | `containsStopWord(null)`, `("")`, `("   ")` → false | `containsStopWordIsFalseForNullOrBlank` | passed | Фощій Дмитро |
| BL-80 | Стоп-слово між розділовими знаками — окреме слово | межа правила «ціле слово» | «(spam)», «spam.», «КАЗИНО?», «ok,scam,ok» → true | `containsStopWordFindsWordBetweenPunctuation` (4 набори) | passed | Фощій Дмитро |
| BL-50 + BL-80 | Два незалежні порушення в одному коментарі | пов'язані властивості | «spam » + 200 літер (205 символів) → «коментар задовгий» і «недопустиме слово» | `longCommentWithStopWordReportsBothViolations` | passed | Фощій Дмитро |

## BL-60, BL-70. `ModelSearch` — тест-клас `ModelSearchTest` (автор: Деркач Юрій)

| Вимога | Правило | Вид перевірки | Вхідні дані → очікуване | Тест-метод | v1 | Рецензент |
|---|---|---|---|---|---|---|
| BL-60 | Префікс марки або моделі, без урахування регістру, порядок вхідного списку | нормальні випадки | «lam», «LAMBO» → Diablo, Aventador; «f» → F40, Mustang; «must»; «AVENT»; «f4» | `findsByMakeOrModelPrefixIgnoringCaseInInputOrder` (6 наборів) | passed | Фощій Дмитро |
| BL-60 | Збіг усередині назви — не префікс | межа правила «префікс» | «iablo», «orghini», «40», «stang», «zzz» → `[]` | `substringThatIsNotPrefixFindsNothing` (5 наборів) | passed | Фощій Дмитро |
| BL-60 | Порожній або пробільний запит → порожній результат | заборонений вхід | `""`, пробіл, три пробіли, табуляція → `[]` | `blankQueryGivesEmptyResult` (4 набори) | failed — BUG-018 (#17) | Фощій Дмитро |
| розд. 5 | `null` замість колекції → IllegalArgumentException | заборонений вхід | models = `null` | `searchWithNullModelsThrows` | passed | Фощій Дмитро |
| BL-70 | За рейтингом за спаданням | нормальний випадок | 4,6 / 4,2 / 4,8 / 3,9 → Aventador, Diablo, F40, Mustang | `sortsByRatingDescending` | failed — BUG-019 (#18) | Фощій Дмитро |
| BL-70 | Рівні рейтинги — за повною назвою без урахування регістру | окреме правило | audi R8, BMW M3, Ford Mustang (усі 4,0) | `equalRatingsAreOrderedByFullNameIgnoringCase` | passed | Фощій Дмитро |
| BL-70 | Новий список, вхідний не змінюється | пов'язані властивості (`assertAll`) | інший об'єкт; вхідний порядок збережено; розмір той самий | `sortReturnsNewListAndKeepsInputUntouched` | passed | Фощій Дмитро |
| розд. 5 | `null` замість колекції → IllegalArgumentException | заборонений вхід | models = `null` | `sortWithNullModelsThrows` | passed | Фощій Дмитро |

## Підсумок

| Тест-клас | Блок тестів | Автор блоку | Вимоги | Тест-методів | з них параметризованих | Червоних на v1 | Рецензент |
|---|---|---|---|---:|---:|---:|---|
| `RatingAggregatorTest` | average | Собко Артем (КТ) | BL-10 | 8 | 5 | 1 (BUG-014 #15) | Фощій Дмитро |
| `RatingAggregatorTest` | topN | Бурмай Юлія (ТА) | BL-20 | 8 | 2 | 1 (BUG-015 #19) | Фощій Дмитро |
| `VoteValidatorTest` | повторний голос, оцінка | Фощій Дмитро (СМ) | BL-30, BL-40 | 8 | 2 | 2 (BUG-016 #22, BUG-017 #21) | Собко Артем |
| `VoteValidatorTest` | коментар, стоп-список | Бурмай Юлія (ТА) | BL-50, BL-80 | 8 (1 вимкнено) | 4 | 0 | Фощій Дмитро |
| `VoteValidatorTest` | контракт, containsStopWord | Бурмай Юлія (ТА) | розд. 5, BL-50, BL-80 | 5 | 1 | 0 | Фощій Дмитро |
| `ModelSearchTest` | search, sortByRating | Деркач Юрій (ІТ) | BL-60, BL-70 | 8 | 3 | 2 (BUG-018 #17, BUG-019 #18) | Фощій Дмитро |
| **Разом** | | | BL-10…BL-80 | **45** | 17 | 6 методів → 6 дефектів | |

Норматив для 5 осіб: ≥20 юніт-тестів (факт 45), ≥3 на особу (Собко 8, Бурмай 16, Фощій 8, Найда 5, Деркач 8),
≥5 дефектів із 6 (факт 6). `ExampleTest` із пакета модуля (3 тест-методи, 5 запусків) лишається у проєкті,
але в норматив не зараховується. Тривіальні елементи без тестів: записи `Vote` і `CarModel` (без логіки);
`CarModel.fullName()` перевіряється опосередковано через сортування; `VoteValidator.isValid` — у `validFirstVoteIsAccepted`
і `sameUserSameModelIsRejectedAsDuplicate`.

