
---
name: "Звіт про дефект — АвтоРейт"   
about: "Повідомити про дефект Buggy Cars Rating із кроками відтворення, зв'язком із SRS 1.1 і тест-кейсом"  
title: "[BUG-XXX] Коротко: проблема та умова виникнення"   
labels: ["status:new", "sprint:3"]   
assignees: []   
---

<!--
Шаблон команди $Win (ФБ-42), спринт 3. Замініть усі XXX та інструкції фактичними даними.
Один дефект — один Issue. Внутрішній BUG-ID і номер GitHub Issue можуть відрізнятися.
Мітки status:new та sprint:3 потрібно попередньо створити у репозиторії.
Додайте також одну мітку severity:... та одну priority:... згідно з ухваленою класифікацією.
Не публікуйте паролі, токени або реальні персональні дані.
-->

**Bug ID:** BUG-XXX  
**Severity:** Blocker | Critical | Major | Normal | Minor | Trivial — **залишити один**  
**Priority:** P1 | P2 | P3 — **залишити один**  
**Linked:** test case TC-XXX; requirement F-XXX / GUI-XXX / BL-XXX / інший ID із SRS 1.1  
**Reporter:** ПІБ  
**Date:** РРРР-ММ-ДД  
**Status:** New (за потреби змінити відповідно до фактичного життєвого циклу)

### Description

**Суть і вплив:** Коротко пояснити, що саме не відповідає вимозі та як це впливає на користувача або функцію. Послатися на конкретний пункт [SRS 1.1](https://github.com/artemfb42/autorateFB42/blob/main/docs/sprint2/SRS-1.1.md).

**Обґрунтування Severity:** Пояснити обраний рівень впливу (не плутати з терміновістю виправлення).

**Обґрунтування Priority:** P1 — блокує подальше тестування загалом; P2 — блокує поточний або кілька тест-кейсів (назвати заблоковані TC-ID); P3 — не блокує подальші тести.

**Підтвердження вимоги:** Confirmed / Check requirement / Check moderation — залишити актуальне; якщо є спірне правило, послатися на рішення команди.

### Preconditions

- Стан до першої дії (гість / авторизований користувач, тестова роль, попередні голосування, дані профілю тощо): ...
- Тестовий акаунт (лише логін або умовне позначення, **без пароля**): ...
- Тестові дані та перевірювані граничні значення: ...

### Steps to reproduce

1. Відкрити https://buggy.justtestit.org/ у вказаному браузері в новій сесії.
2. ...
3. ...

### Actual result

На кроці № ... фактично спостерігалося: ... (точний текст повідомлення, значення лічильника, URL, стан кнопки тощо).

### Expected result

На тому самому кроці за вимогою **F-XXX / GUI-XXX / ...** система повинна: ... (конкретний перевірюваний результат).

### Environment

- **Demo / дата перевірки:** https://buggy.justtestit.org/; РРРР-ММ-ДД (за наявності — версія збірки)
- **Браузер і версія:** ...
- **ОС:** ...
- **Сесія / роль:** інкогніто або звичайна; гість або тестовий користувач
- **Дані:** вигадані тестові дані; необхідний початковий стан (без секретів)

### Attachments

<!-- Називайте нові вкладення за Bug ID: BUG-XXX-1.png, BUG-XXX-2.png. Старі докази з TC-назвами можна залишати, але чітко поясніть зв'язок. -->

- [BUG-XXX-1.png](ПОСИЛАННЯ_НА_ФАЙЛ) — що саме видно на скриншоті та який крок він підтверджує.
- [BUG-XXX-2.png](ПОСИЛАННЯ_НА_ФАЙЛ) — за потреби.

Файли доказів: [docs/sprint3/attachments/](https://github.com/artemfb42/autorateFB42/tree/main/docs/sprint3/attachments).

### Traceability

- **Test case:** TC-XXX у [test-cases.xlsx](https://github.com/artemfb42/autorateFB42/blob/main/docs/sprint3/test-cases.xlsx).
- **Execution:** Run1 / TC-XXX у [execution-log.xlsx](https://github.com/artemfb42/autorateFB42/blob/main/docs/sprint3/execution-log.xlsx).
- **Requirement:** ID із [SRS 1.1](https://github.com/artemfb42/autorateFB42/blob/main/docs/sprint2/SRS-1.1.md).
- **Related Issues:** #... / немає.

### Review

<!-- Рецензування звіту ≠ незалежне відтворення дефекту. Автор не рецензує власний звіт. -->

- **Reviewer:** ПІБ
- **Review date:** РРРР-ММ-ДД
- **Checklist:** опис зрозумілий без вкладень; один дефект; достатні передумови; кроки незалежні та точні; фактичний/очікуваний результати перевірювані; немає описок і сленгу; посилання та Severity/Priority обґрунтовані.
- **Comments:** Зауважень немає / перелік зауважень.
- **Decision:** approved / needs revision.
- **Corrections:** Не застосовується / що і коли виправлено.

### Independent reproduction (лише якщо проводилося)

- **Performed:** ні / так
- **Auditor:** ...
- **Date and environment:** ...
- **Result:** reproduced / not reproduced / blocked / not performed
- **Evidence / audit record:** посилання на конкретний розділ [audit.md](https://github.com/artemfb42/autorateFB42/blob/main/docs/sprint3/audit.md) або опис причини невідтворення.

<!--
ПЕРЕД ПУБЛІКАЦІЄЮ: переконайтеся, що назва містить прояв та умову, кроки можна відтворити без автора, а усі XXX/плейсхолдери замінено.
ПІСЛЯ ПУБЛІКАЦІЇ: зв'яжіть номер Issue з Bug ID та TC-ID у журналі; додайте мітки severity:<level>, priority:P1|P2|P3, sprint:3 і відповідну status:*.
Для спринту 3 Issue може залишатися Open; закривайте його за реальним станом виправлення та перевірки.
-->
