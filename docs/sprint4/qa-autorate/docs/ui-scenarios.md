# UI-сценарії для автоматизації: TC → кроки, локатори, очікування, тест-метод

**Спринт 4, команда $Win (ФБ-42).**    
Відбір сценаріїв і локатори: Бурмай Юлія (ТА).    
Реалізація: Деркач Юрій (ІТ).     
Demo: https://buggy.justtestit.org.     
Тест-кейси — [test-cases.xlsx](../../../sprint3/test-cases.xlsx), журнал — [execution-log.xlsx](../../../sprint3/execution-log.xlsx).

## 1. Як відібрано сценарії

Критерії: (1) у спринті 3 кейс **пройшов** — автотест має стабільно проходити, а не фіксувати відомий дефект demo;
(2) пріоритет High (TC-018 — Medium, узятий як негативна пара до входу); (3) кейс не змінює спільні дані demo
(не створює облікових записів, не витрачає голоси), тому відтворюється необмежену кількість разів; (4) покрито вхід,
форму з валідацією і перегляд каталогу. Головну операцію — голосування — представлено кейсами TC-019 і TC-027
(доступність форми голосування); сам голос (TC-028) у спринті 3 не пройшов (BUG-009) і одноразовий для облікового запису.

Не автоматизовано: кейси зі статусом failed (TC-006, 009–011, 013, 015, 016, 020, 024, 026, 028–030, 033, 041, 044);
кейси, що створюють акаунти або змінюють профіль (TC-002, 007, 008, 012, 035–042); TC-044 (30 хвилин очікування).

## 2. Перелік сценаріїв (10 при нормативі ≥8)

| № | TC | Вимоги | Пріоритет | Run1 | Тест-клас | Тест-метод |
|---|---|---|---|---|---|---|
| 1 | TC-001 | F-20, GUI-20 | High | passed | `RegistrationUiTest` | `registrationFormHasAllFieldsAndDisabledRegister` |
| 2 | TC-004 | F-30, GUI-20 | High | passed | `RegistrationUiTest` | `emptyRequiredFieldShowsErrorAndKeepsRegisterDisabled` |
| 3 | TC-005 | F-30, GUI-20 | High | passed | `RegistrationUiTest` | `passwordMismatchShowsErrorAndKeepsRegisterDisabled` |
| 4 | TC-017 | GUI-30, U-20 | High | passed | `LoginUiTest` | `errorMessageDisappearsAfterSuccessfulLogin` |
| 5 | TC-018 | F-40 | Medium | passed | `LoginUiTest` | `unknownLoginShowsErrorAndDoesNotCreateSession` |
| 6 | TC-019 | F-50, S-10 | High | passed | `LoginUiTest` | `logoutEndsSessionAndHidesVoteForm` |
| 7 | TC-021 | F-10 | High | passed | `CatalogUiTest` | `homePageHasThreeBlocksThatOpenTheirPages` |
| 8 | TC-022 | F-60, F-130, GUI-50 | High | passed | `CatalogUiTest` | `makePageTableHasColumnsAndAtMostThreeComments` |
| 9 | TC-023 | F-70, F-130, GUI-50 | High | passed | `CatalogUiTest` | `overallRatingTableHasColumnsLinksAndAscendingRanks` |
| 10 | TC-027 | F-90, F-50 | High | passed | `CatalogUiTest` | `visitorSeesLoginHintInsteadOfVoteForm` |

## 3. Локатори

Розробники demo не передбачили атрибутів `data-test`, а `id` мають лише поля форм. Тому: поля — за `id` / `name`;
решта — CSS від імені Angular-компонента сторінки (`my-login`, `my-register`, `my-home`, `my-make`, `my-overall`,
`my-model`, `my-pager`) до елемента з атрибутом (`href`, `type`); XPath — лише відносний, за текстом. Абсолютних XPath немає.

Статус: **DOM** — локатор звірено з розміткою demo 10.11.2026; **⚠** — складено за описом сторінки, розмітку після входу
не знімали: ІТ перевіряє у DevTools під час першого запуску (`Ctrl+Shift+C` → елемент → `Ctrl+F` у Elements, має бути «1 of 1»).

| Елемент | Локатор | Де у коді | Статус |
|---|---|---|---|
| Поле логіна (верхня панель) | `By.name("login")` | `TopBar.LOGIN_INPUT` | DOM |
| Поле пароля (верхня панель) | `By.name("password")` | `TopBar.PASSWORD_INPUT` | DOM |
| Кнопка Login | `my-login button[type='submit']` | `TopBar.LOGIN_BUTTON` | DOM |
| Кнопка Register (панель) | `my-login a[href='/register']` | `TopBar.REGISTER_LINK` | DOM |
| Повідомлення про невдалий вхід | `my-login .label-warning` | `TopBar.ERROR_LABEL` | DOM |
| Привітання «Hi, …» | `my-login span.nav-link` | `TopBar.GREETING` | DOM |
| Посилання Profile | `my-login a[href='/profile']` | `TopBar.PROFILE_LINK` | DOM |
| Кнопка Logout | `By.linkText("Logout")` | `TopBar.LOGOUT_LINK` | DOM |
| Поля реєстрації | `By.id("username")`, `firstName`, `lastName`, `password`, `confirmPassword` | `RegisterPage` | DOM |
| Кнопка Register (форма) | `my-register button[type='submit']` | `RegisterPage.REGISTER_BUTTON` | DOM |
| Посилання Cancel | `my-register a[role='button']` | `RegisterPage.CANCEL_LINK` | DOM |
| Помилка поля Login | `#username + div.alert-danger` | `RegisterPage.USERNAME_ERROR` | DOM |
| Помилка Confirm Password | `#confirmPassword + div.alert-danger` | `RegisterPage.CONFIRM_PASSWORD_ERROR` | DOM |
| Заголовки блоків головної | `my-home h2.card-header` | `CatalogUiTest` | DOM |
| Зображення Popular Make / Model / Overall | `my-home a[href^='/make/']`, `my-home a[href^='/model/']`, `my-home a[href='/overall']` | `CatalogUiTest` | DOM |
| Таблиця марки: заголовки, рядки | `my-make table.cars thead th`, `my-make table.cars tbody tr` | `CatalogUiTest` | DOM |
| Таблиця рейтингу: заголовки, рядки, ранг | `my-overall table.cars thead th`, `… tbody tr`, `… tbody tr td:nth-child(4)` | `CatalogUiTest` | DOM |
| Коментарі у рядку, View more | `p.comment`, `By.linkText("View more")` (усередині рядка) | `CatalogUiTest` | DOM |
| Пагінація | `my-pager` | `CatalogUiTest.PAGER` | DOM |
| Заголовок Specification | `//my-model//h4[normalize-space()='Specification']` | `CatalogUiTest` | DOM |
| Текст для відвідувача у блоці Votes | `my-model p.card-text` | `CatalogUiTest`, `LoginUiTest` | DOM |
| Поле Comment | `By.id("comment")` (перевіряється відсутність) | `CatalogUiTest`, `LoginUiTest` | DOM |
| Кнопка Vote! | `//my-model//button[normalize-space()='Vote!']` (перевіряється відсутність) | `CatalogUiTest` | DOM |

Для елементів «перевіряється відсутність» помилковий локатор дав би хибно-зелений результат, тому ІТ один раз
увійти вручну, відкрити сторінку моделі, за яку ще не голосували, і переконатися, що обидва локатори знаходять елемент.

## 4. Сценарії: кроки → очікування → перевірка

Передумова для всіх: новий браузер Chrome (headless, 1280×900), без входу; тайм-аут явних очікувань 15 с.

### TC-001. Сторінка реєстрації: поля, Cancel, неактивна Register
| Крок | Дія → локатор | Явне очікування |
|---|---|---|
| 1 | `get("/")` | — |
| 2 | клік `my-login a[href='/register']` | `urlContains("/register")`, видимість `#username` |

Перевірка (м'яка, `assertAll`): видимі п'ять полів; кнопка має текст «Register» і `isEnabled() == false`; посилання «Cancel».

### TC-004. Порожнє обов'язкове поле Login
| Крок | Дія → локатор | Явне очікування |
|---|---|---|
| 1–2 | як у TC-001 | — |
| 3 | `#username` ← «a» | — |
| 4 | `#username` ← Backspace | видимість `#username + div.alert-danger` |

Перевірка (`assertAll`): текст «Login is required»; поле порожнє; Register неактивна.

### TC-005. Розбіжність Password і Confirm Password
| Крок | Дія → локатор | Явне очікування |
|---|---|---|
| 1–2 | як у TC-001 | — |
| 3 | Login «fb42_tc005», First Name «Test», Last Name «Tester», Password «Pass1234a@», Confirm «Pass1235a@» | видимість `#confirmPassword + div.alert-danger` |

Перевірка (`assertAll`): текст «Passwords do not match»; Register неактивна; URL містить `/register`. Форма не надсилається.

### TC-018. Невідомий логін
| Крок | Дія → локатор | Явне очікування |
|---|---|---|
| 1 | `get("/")` | видимість `name=login` |
| 2 | логін «nouser_zz9», пароль «Abcdef12», клік Login | видимість `my-login .label-warning` |

Перевірка (`assertAll`): текст починається з «Invalid»; немає Logout і Profile; форма входу на місці.
Точний текст повідомлення у SRS 1.1 («Invalid login or password.») очікує погодження RD-05, demo показує інший варіант —
тому перевіряється спільна частина.

### TC-017. Повідомлення зникає після успішного входу
| Крок | Дія → локатор | Явне очікування |
|---|---|---|
| 1 | `get("/")` | видимість `name=login` |
| 2 | логін fb42_reg1, пароль «Wrong1234@», Login | видимість повідомлення (жорстка перевірка-передумова) |
| 3 | очистити поля, логін fb42_reg1 з правильним паролем, Login | видимість `my-login span.nav-link` |

Перевірка (`assertAll`): привітання «Hi, Test»; видимі Profile і Logout; повідомлення про помилку відсутнє.

### TC-019. Logout завершує сеанс
| Крок | Дія → локатор | Явне очікування |
|---|---|---|
| 1–2 | `get("/")`, вхід fb42_reg1 | клікабельність Profile |
| 3 | клік Profile | `urlContains("/profile")` |
| 4 | клік Logout | видимість `name=login` |
| 5 | `get("/")`, клік зображення Popular Model | видимість `my-model p.card-text` |

Перевірка 1 (`assertAll`): поля входу і Register видимі; Logout і Profile відсутні.
Перевірка 2 (`assertAll`): текст «You need to be logged in to vote.»; поля Comment немає.
Крок 6 тест-кейсу (відкрити збережену адресу профілю після виходу) лишається ручним: очікуваний вміст сторінки
у кейсі описано словами, стійкого елемента для перевірки немає.

### TC-021. Головна сторінка: три блоки і переходи
| Крок | Дія → локатор | Явне очікування |
|---|---|---|
| 1 | `get("/")` | клікабельність `my-home a[href^='/make/']` |
| 2 | клік зображення Popular Make | `urlContains("/make/")`, рядки таблиці непорожні (умова-лямбда) |
| 3 | `get("/")`, клік зображення Popular Model | `urlContains("/model/")`, видимість «Specification» |
| 4 | `get("/")`, клік зображення Overall Rating | `urlContains("/overall")`, рядки таблиці непорожні |

Перевірка: заголовки блоків = [Popular Make, Popular Model, Overall Rating] (жорстко); три переходи — `assertAll`.
Напис блоку Overall Rating не перевіряється: його текст очікує погодження RD-26.

### TC-022. Сторінка марки
Кроки: `get("/")` → клік Popular Make → очікування рядків таблиці.
Перевірка (`assertAll`): заголовки = [Model, Rank, Votes, Comments]; у кожному рядку ≤3 `p.comment`; у кожному рядку є «View more».

### TC-023. Overall Rating
Кроки: `get("/")` → клік Overall Rating → очікування рядків таблиці.
Перевірка (`assertAll`): заголовки = [Make, Model, Rank, Votes, Engine, Comments]; ранги на сторінці впорядковано за зростанням;
у кожному рядку посилання на марку і модель; `my-pager` містить «page 1 of».

### TC-027. Відвідувач не бачить форми голосування
Кроки: `get("/")` → клік Popular Model → `urlContains("/model/")` → видимість `my-model p.card-text`.
Перевірка (`assertAll`): текст «You need to be logged in to vote.»; немає поля Comment; немає кнопки Vote!.

## 5. Тестові дані

| Дані | Значення | Джерело |
|---|---|---|
| Обліковий запис для входу | логін `fb42_reg1`, First Name «Test» | аркуш Accounts журналу спринта 3 |
| Інший обліковий запис | `-Dautorate.login=… -Dautorate.password=… -Dautorate.firstName=…` | параметри запуску |
| Неіснуючий логін | `nouser_zz9` | TC-018 |

Якщо demo скине дані й `fb42_reg1` зникне, ІТ створює його заново вручну через форму реєстрації (First Name «Test»).

## 6. Selenium IDE

Проєкт `selenium-ide/autorate.side` (базовий URL `https://buggy.justtestit.org`), записує і відтворює ІТ.

| Тест у проєкті | Тест-кейс | Команд | Експорт Java JUnit | WebDriver-аналог |
|---|---|---:|---|---|
| TC-005 Розбіжність Password і Confirm Password блокує реєстрацію | TC-005 | 12 | `selenium-ide/export/TC005PasswordConfirmPasswordTest.java` | `RegistrationUiTest.passwordMismatchShowsErrorAndKeepsRegisterDisabled` |
| TC-027 Відвідувач не бачить форми голосування | TC-027 | 8 | `selenium-ide/export/TC027Test.java` | `CatalogUiTest.visitorSeesLoginHintInsteadOfVoteForm` |

Обидва сценарії не потребують входу і не змінюють дані demo. Команди: дії `open`, `set window size`, `click`, `type`;
очікування `wait for element visible`; жорстка перевірка `assert text`; м'які `verify not editable`, `verify element not present`.
Експорт `Export → Java JUnit` — у `selenium-ide/export/` (чернетки у стилі JUnit 4, поза `src/`, не збираються).

| Дата запису і відтворення | Версія Selenium IDE | Браузер | Результат трьох відтворень |
|---|---|---|---|
| 12.11.2026 | 3.17.0 (розширення для Chrome) | Google Chrome 157 (стабільний канал, остання версія на дату запуску) | 3 × 2 passed (TC-005, TC-027) — `Runs: 2, Failures: 0` |

### Як записано TC-005

1. Сценарій записано кнопкою **REC**: Register у верхній панелі → заповнення п'яти полів форми (кнопку Register не натискали).
2. Після запису прибрано зайві команди: `close` (закриття вкладок під час запису), `click` перед кожним `type`, клік по формі
   `css=form:nth-child(2)` і дубль `click linkText=Register`.
3. Локатор кнопки Register `linkText=Register` замінено на стійкий `css=my-login a[href='/register']`
   (той самий, що в Page Object `TopBar.REGISTER_LINK`).
4. Додано `open /` і `set window size 1280x900` на початок, `wait for element visible id=username` після переходу на форму
   (SPA домальовує форму після кліку) і три перевірки в кінці: очікування повідомлення, `assert text`, `verify not editable`.

### TC-027

Складено вручну з кроків тест-кейсу; головна сторінка і сторінка моделі домальовуються після завантаження, тому перед кліком
і перед перевіркою стоїть `wait for element visible`.

### Обмеження середовища

Перший запуск 12.11.2026 у **Mozilla Firefox** завершився помилками: команда `type` падала з повідомленням
`n.createEvent(...).initKeyEvent is not a function`. Причина — Firefox 147+ прибрав застарілий API `initKeyEvent`, яким
розширення Selenium IDE імітує введення з клавіатури (SeleniumHQ/selenium#16915). Сценарій при цьому коректний:
у Google Chrome обидва тести проходять. Тому запис і відтворення виконано в Chrome; WebDriver-тести також працюють через Chrome.
