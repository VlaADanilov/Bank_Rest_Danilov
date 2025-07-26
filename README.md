<strong>Привет! Это выполненное ТЗ (Bank_Rest) для компании Effective Mobile. </strong>
Я разработал Backend-приложение для управления банковскими картами.

<h1>Запуск приложения</h1>
<p>Достаточно находится в корне проекта и ввести команду
docker-compose up --build</p>
<strong>Запустите поднятие заранее, 
так как проект билдится при создании image,
из-за чего первое поднятие будет долгое (около 5 минут)</strong>

<h1>Предлагаю пройтись по ТЗ:</h1>

<h2>Сущности</h2>
<ul>
<li>Пользователь - имеет атрибуты: id, username, 
пароль, имя, фамилия, отчество, роль (User, Admin)</li>
<li>Карта - имеет атрибуты: id, номер карты, дата истечения, 
статус(Active, Blocked, Expired), баланс, ссылка на владельца</li>
<li>Заявка на блокировку карты - имеет атрибуты: id, ссылка на карту</li>
</ul>

<h2>✅ Аутентификация и авторизация</h2>
<p>Реализована с помощью Spring Security и JWT. JWT выдаётся при логине в системе.
Срок жизни - 10 минут, но в пропертях можно поставить любое время.</p>
<p>Проверяет на валидность токена самописный филтьтр для Spring Security.</p>
<p><strong>Я создал аккаунты для вас: 
1)>✅Role: ADMIN | username: admin | password: 1234
2)>✅Role: USER  | username: 1234 | password: 1234
3)>✅Role: USER | username: test | password: 1234</strong></p>

<h2>✅ Пройдёмся по возможностям </h2>
<strong>Администратор:</strong>
  <ul>
    <li>Создаёт карту - POST /api/v1/admin/card</li>
    <li>Блокирует карту - POST /api/v1/admin/block/{cardId}</li>
    <li>Активирует карту - POST /api/v1/admin/active/{cardId}</li>
    <li>Удаляет карту - DELETE /api/v1/card/{cardId}</li>
    <li>Просматривает все карты с фильтрацией по статусам и по пользователю с пагинацией- GET /api/v1/admin/card</li>
    <li>Просматривает заявки на блокировку карты с пагинацией - GET /api/v1/admin/requestToBlock</li>
    <li>Просматривает всех пользователей с фильтрацией по username с пагинацией - GET /api/v1/user</li>
    <li>Удаляет аккаунт пользователя - DELETE /api/v1/user/{userId}</li>
    <li>Просматривает информацию о любой карте - GET /api/v1/card/{cardId}</li>
  </ul>

<strong>Пользователь:</strong>
  <ul>
    <li>Просматривает свои карты с фильтрацией по статусу с пагинацией - GET /api/v1/card</li>
    <li>Просматривает подробную информацию о своей карте (тут же смотрит баланс) - GET /api/v1/card/{cardId}</li>
    <li>Запрашивает блокировку карты - POST /api/v1/card/block/{cardId}</li>
    <li>Делает переводы между своими картами - POST /api/v1/card/transfer</li>
  </ul>

<strong>Любой человек:</strong>
  <ul>
    <li>Регистрируется в системе - POST /api/v1/user</li>
    <li>Авторизуется в системе - POST /api/v1/user/login</li>
  </ul>

<h2>✅ Безопасность</h2>
  <ul>
    <li>Шифрование пароля в базе данных</li>
    <li>Ролевой доступ - есть две роли: USER и ADMIN</li>
    <li>Маскирование номеров карт - Есть!</li>
  </ul>

<h2>✅ Работа с БД</h2>
  <ul>
    <li>СУБД - PostgreSQL</li>
    <li>Миграции - Liquibase (Создание таблиц и заполнение тестовыми данными)</li>
  </ul>

<h2>✅ Документация </h2>
  <ul>
    <li>Я использовал OpenAPI и Swagger для документации. 
Вы сможете увидеть openAPi в папке docs или после запуска приложения
можете перейти по http://localhost:8080/swagger-ui.html, где
сможете потыкаться в систему</li>
  </ul>

<h2>✅ Тесты </h2>
  <p>Я написал интеграционные и модульные тесты.</p>
    <p><strong>Тестовое покрытие: свыше 90%</strong></p>

<h2>✅ Логирование и Scheduler </h2>
  <ul>
    <li>Приложение логирует любые действия с помощью Spring logback. 
Посмотреть их можно в volume app_logs_data</li>
    <li>Ещё есть Scheduler задача, которая запускается каждый день в 00:01
и меняет статус карт, у которых истёк срок, на EXPIRED</li>
  </ul>

<h2>💡 Технологии</h2>
  <p>
    Java 21, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Liquibase, Docker, JWT, Swagger (OpenAPI), Spring Scheduler
  </p>
