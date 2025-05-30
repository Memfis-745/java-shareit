# java-shareit

## Технологии: 
Java + Spring Boot + Docker + PostgreSQL + Hibernate + Maven + JUnit5 + Mockito + RESTful API + MapStruct + Lombok

## Описание:
Вебсервис с использованием микросервисной архитектуры, позволяющий сдавать вещи в аренду. Сервис дает возможность бронировать вещи на определенные даты. Если вещь забронирована доступ к ней на время бронирования закрыт для прочих пользователей. Если вещь отсутствует, на сервисе есть возможность оставлять запросы, позволяющие другим пользователям, добавлять нужную вещь. Кроме того, есть возможность оставлять комментарии, по завершенной аренде.

## Микросервисная архитектура
Приложение состоит из 2 сервисов:
* Gateway. Принимает запросы от пользователей. Распределяет нагрузку, выполняет первичную проверку и направляет запросы дальше в основной сервис
* Server. Серверная часть приложения. Получает запросы, выполняет операции, отправляет данные клиенту

## Установка и запуск проекта
Приложение использует Docker для своей работы. Для запуска приложения, потребуется скачать его из текущего репозитория и запустить сборку проекта на своей машине из командной строки.
После ввода команды: $  docker-compose up, приложение запустится на порту 8080.

## Server

Классы моделей, контроллеров, сервисов и репозиториев располагаются в соответствующих каталогах:

* booking
* item
* request
* user

В каталоге request, расположены также классы комментариев Comments.

## user

Модель, User, позволяет создавать пользователей со следующими характеристиками:
Id – уникальный идентификатор, name – имя, email – электронный адрес.

### UserController

Это класс-контроллер предоставляющий REST API для управления пользователями, 
используя Spring Boot, Lombok, Bean Validation и DTO. Он обеспечивает логирование запросов и валидацию входящих данных, что делает его надежным и удобным в использовании. Класс имеет зависимость:

•   `UserService userService`: Сервис, предоставляющий бизнес-логику для работы с пользователями.

Технологии используемые в контроллерах: Spring Boot,  Lombok, Validation,  REST Controller,  Request Mapping, Request Header,  Request Body, Path Variable, Request Parameter, Service Layer.

Валидация входящих данных обеспечивается аннотацией @Valid, что помогает предотвратить ошибки и обеспечивает целостность данных.

#### Методы:

•   `addUser`:
•   Обрабатывает POST запросы к `/users` для создания нового пользователя, получая данные пользователя из тела запроса в формате JSON и десериализуя их в объект `UserDto`. Аннотация `@Valid` запускает процесс валидации объекта `UserDto` на основе заданных правил валидации (например, аннотации `javax.validation.constraints.*`). Возвращает созданного пользователя в формате `UserDto`.

•   `updateUser`:
Обрабатывает PATCH запросы к `/users/{userId}` для обновления существующего пользователя.  `PATCH` используется для частичного обновления данных. Контроллер получает данные пользователя из тела запроса, а id  из URL пути. Вызывает метод `userService.updateUser(userDto, userId)` для обновления пользователя. Возвращает обновленного пользователя в формате `UserDto`.

•   `getUserById`:
Обрабатывает GET запросы к `/users/{userId}` для получения пользователя по ID, которое получает из URL пути.
*   Вызывает метод `userService.getUserById(userId)` и возвращает пользователя в формате `UserDto`.

*   `getAllUsers()`:
    *   Обрабатывает GET запросы к `/users` для получения списка всех пользователей. Вызывает метод `userService.getAllUsers()` для получения списка всех пользователей. Возвращает список пользователей в формате `List<UserDto>`.

*   `deleteUser(@PathVariable long userId)`:
    *   Обрабатывает DELETE запросы к `/users/{userId}` для удаления пользователя по ID. Получает ID пользователя из URL пути. Вызывает метод `userService.deleteUser(userId)` для удаления пользователя.

Обращения раздела user к БД, не выходят за рамки стандартных операций, реализуемых с помощью интерфейс из Spring Data JPA. Дополнительный код в репозитории не требуется. Методы используемые в UserService: Save(user), findById(userId), findAll().

## item

Модель, Item, позволяет создавать вещи со следующими характеристиками:
itemId – уникальный идентификатор, name - имя, description – описание, available – доступность, owner – собственник, request – запрос.

Модель Comment содержит характеристики комментария. Id – уникальный идентификатор, text – текст комментария, item – вещь к которой оставляется комментарий, author – автор комментария, LocalDateTime – время комментария.

### ItemController

Класс ItemController представляет собой REST контроллер, обрабатывающий запросы, связанные с управлением вещами (Item).

Основные возможности:

* Добавление вещи (POST /items): Позволяет пользователю добавить новую вещь.
* Обновление вещи (PATCH /items/{itemId}): Позволяет пользователю обновить информацию о существующей вещи.
* Получение информации о вещи по ID (GET /items/{itemId}): Возвращает информацию о вещи по ее ID, включая данные о бронировании.
* Получение списка вещей пользователя (GET /items): Возвращает список вещей, принадлежащих указанному пользователю (с пагинацией).
* Поиск вещей (GET /items/search): Выполняет поиск вещей по текстовому запросу.
* Добавление комментария к вещи (POST /items/{itemId}/comment): Позволяет пользователю оставить комментарий к вещи.

Зависимости:

•   ItemService:  Бизнес-логика для работы с вещами.

#### методы:

* addItem - Добавляет новую вещь.
ID пользователя, добавляющего вещь, извлекается из заголовка X-Sharer-User-Id,  DTO, содержащий информацию о вещи – из тела запроса.
Возвращает: ItemDto - добавленную вещь.


* updateItem - Обновляет информацию о существующей вещи.
ID пользователя, обновляющего вещь, извлекается из заголовка X-Sharer-User-Id, DTO, содержащий информацию для обновления вещи – из тела запроса.
ID вещи, которую необходимо обновить извлекается из URL.
Возвращает: ItemDto - обновленная вещь.


* getItemById - Получает информацию о вещи по ее ID.
ID пользователя, запрашивающего информацию извлекается из заголовка X-Sharer-User-Id, ID вещи, информацию о которой необходимо получить извлекается из URL. 
Возвращает: ItemDtoBooking - информация о вещи, включая данные о бронировании.


* getUserItems ( @RequestHeader(headers) long userId, @RequestParam(name = "from", defaultValue = "0") int from, @RequestParam(name = "size", defaultValue = "20") int size ):
Получает список вещей, принадлежащих указанному пользователю, с возможностью пагинации.
ID пользователя, вещи которого необходимо получить извлекается из заголовка X-Sharer-User-Id, from: Индекс первого элемента для постраничного вывода (по умолчанию 0), size: Количество элементов на одной странице (по умолчанию 20).
Возвращает: List<ItemDtoBooking> - список вещей пользователя.


* search - Выполняет поиск вещей по текстовому запросу.
Текст для поиска из тела запроса.
Возвращает: List<ItemDto> - список найденных вещей.

*  saveComment - Добавляет комментарий к вещи.
ID пользователя, добавляющего комментарий, извлекается из заголовка X-Sharer-User-Id, ID вещи, к которой добавляется комментарий (извлекается из URL). DTO, содержащий текст комментария извлекается из тела запроса. 
Возвращает: CommentDto - добавленный комментарий.


Обращения к таблице comments расширяют стандартные CRUD операции методом:
List<Comment> findByItemIn(List<Item> items, Sort created);
Метод извлекает из базы данных все комментарии, связанные с одной или несколькими вещами (Item).  Результаты сортируются.

### Репозиторий Item

помимо методов CRUD-операций, содержит следующие методы:

*     Page<Item> findAllByOwnerIdOrderByItemId(Long userId, Pageable pageable);

Находит все вещи, принадлежащие пользователю с указанным userId, с поддержкой пагинации и сортировкой по ID вещи.
Возвращает: Page<Item> - объект, содержащий список найденных вещей, информацию о количестве страниц, текущей странице и т.д. для реализации пагинации.

*     @Query(""" select it from Item as it where it.available = true and (it.name ilike concat('%', :text, '%') or it.description ilike concat('%', :text, '%')) """) List<Item> findByNameOrDescription(@Param("text") String text);

Находит все доступные вещи, в названии (name) или описании (description) которых содержится указанный текст.

*     List<Item> findAllByRequest_IdInOrderByItemId(List<Long> itemRequestId);

Находит все вещи, связанные с заявками на вещи (ItemRequest), ID которых содержатся в указанном списке.
Возвращает: List<Item> - список найденных вещей, связанных с указанными заявками.

*     @Query(""" select i from Item i where i.request.id = id """) List<Item> findAllByRequestId(@Param("id") Long id);
Находит все вещи, связанные с заявкой на вещь (ItemRequest) с указанным ID.  Эта реализация использует пользовательский запрос JPQL.

*     List<Item>  findAllByRequest_Requestor_IdNotAndRequest_IdInOrderByItemId(Long userId, List<Long> requestsIds);
Находит все вещи, связанные с заявками, которые не были созданы пользователем с userId, и ID заявок содержатся в списке requestsIds.

## request

Модель ItemRequest содержит основные параметры запроса: id – уникальный идентификатор, description – описание, requestor – пользователь создавший запрос, created – дата создания запроса, items – список требуемых вещей.

### ItemRequestController 
представляет собой REST контроллер, обрабатывающий запросы, связанные с заявками на вещи (ItemRequest).

Основные возможности:

•   Создание заявки на вещь (POST /requests): Позволяет пользователю создать новую заявку на вещь, указав описание необходимого предмета.
•   Получение списка заявок пользователя (GET /requests): Возвращает список всех заявок, созданных конкретным пользователем.
•   Получение списка заявок других пользователей (GET /requests/all): Возвращает список заявок, созданных другими пользователями (с пагинацией).
•   Получение заявки по ID (GET /requests/{requestId}): Возвращает подробную информацию о заявке на вещь по ее уникальному идентификатору.

Зависимости: RequestService.

#### Методы:

•   **saveItemRequest()** - Создает новую заявку на вещь.
ID пользователя, создающего заявку извлекается из заголовка X-Sharer-User-Id,
DTO, содержащий описание необходимой вещи – из тела запроса. Вызывает метод createdItemRequest(userId, requestDto).
Возвращает: ItemRequestDto - созданная заявка.

•   **getItemRequests** - Получает список всех заявок, созданных указанным пользователем.
ID пользователя, заявки которого необходимо получить извлекается из заголовка X-Sharer-User-Id.
Возвращает: List<ItemRequestDto> - список заявок пользователя.

•   **getItemRequestsFromOtherUsers** ( @RequestHeader(HEADER) long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size ):

Получает список заявок, созданных другими пользователями, с возможностью пагинации.
ID пользователя, для которого не нужно возвращать заявки извлекается из заголовка X-Sharer-User-Id. from: Индекс первого элемента для постраничного вывода (по умолчанию 0).  size: Количество элементов на одной странице (по умолчанию 10).
Возвращает: List<ItemRequestDto> - список заявок других пользователей.

•   **getOneItemRequest** - Получает заявку на вещь по ее ID.
userId: ID пользователя, выполняющего запрос извлекается из заголовка X-Sharer-User-Id, ID заявки, которую необходимо получить (извлекается из URL).
Возвращает: ItemRequestDto - запрошенная заявка.

#### Обращения к таблице requests расширяют стандартные CRUD операции методами:

*     List<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(Long userId);

Метод извлекает из базы данных все заявки на вещи, созданные пользователем с указанным userId и возвращает список всех заявок, созданных указанным пользователем. Заявки отсортированы по дате создания (Created) в порядке убывания.

*     Page<ItemRequest> findAllByRequestor_IdNot(Long userId, Pageable page);

Метод извлекает из базы данных все заявки на вещи, созданные другими пользователями кроме указанного с userId и поддерживает пагинацию результатов.    Возвращает: Объект Page<ItemRequest>, который содержит список (List<ItemRequest>) заявок, созданных другими пользователями (не userId). Общую информацию о пагинации. 

## booking
Модель Booking содержит поля: id – уникальный идентификатор, startBooking – начало аренды, finishBooking – завершение аренды, item – вещь, booker – арендатор, bookingStatus – статус бронирования

### BookingController
- это REST контроллер, обрабатывающий запросы, связанные с управлением бронированиями вещей. Он позволяет пользователям создавать бронирования, подтверждать/отклонять их (владельцам вещей), а также получать информацию о бронированиях.

Основные возможности:

* Создание бронирования (POST /bookings): Позволяет пользователю создать новое бронирование вещи.
* Подтверждение/отклонение бронирования (PATCH /bookings/{bookingId}): Позволяет владельцу вещи подтвердить или отклонить бронирование.
* Получение информации о бронировании по ID (GET /bookings/{bookingId}): Возвращает информацию о конкретном бронировании по его ID.
* Получение списка бронирований пользователя (GET /bookings): Возвращает список бронирований, созданных указанным пользователем, с возможностью фильтрации по статусу и пагинации.
* Получение списка бронирований для владельца вещи (GET /bookings/owner): Возвращает список бронирований вещей, принадлежащих указанному пользователю (владельцу), с возможностью фильтрации по статусу и пагинации.

Зависимости:

* BookingService: Бизнес-логика для работы с бронированиями.

Описание методов:

•   **addBooking** - Создает новое бронирование вещи.
userId: ID пользователя, создающего бронирование извлекается из заголовка X-Sharer-User-Id, DTO, содержащий информацию о бронировании ID вещи, время начала и окончания.  
Возвращает: BookingDtoOut - созданное бронирование.

•   **approveBooking** - подтверждает или отклоняет бронирование.  Этот метод доступен только владельцу вещи.
userId: ID пользователя, подтверждающего/отклоняющего бронирование извлекается из заголовка X-Sharer-User-Id,  ID бронирования, которое необходимо подтвердить или отклонить извлекается из URL, Булевое значение, указывает, следует ли подтвердить (true) или отклонить (false) бронирование.
Возвращает: BookingDtoOut - обновленное бронирование с новым статусом.

•   **getBookingId** - получает информацию о бронировании по его ID. 
ID пользователя, запрашивающего информацию извлекается из заголовка X-Sharer-User-Id, ID бронирования, информацию о котором необходимо получить извлекается из URL.
Возвращает: BookingDtoOut - запрошенное бронирование.

•   **getAllBookingByUser** - получает список бронирований, созданных указанным пользователем, с возможностью фильтрации по статусу и пагинации.
ID пользователя, бронирования которого необходимо получить, извлекается из заголовка X-Sharer-User-Id Статус бронирования для фильтрации (например, "WAITING", "APPROVED", "REJECTED", "ALL").  Значение по умолчанию - "ALL" (получить все бронирования). 
from: Индекс первого элемента для постраничного вывода (по умолчанию 0). size: Количество элементов на одной странице (по умолчанию 20).
Возвращает: List<BookingDtoOut> - список бронирований пользователя.

•   **getAllBookingByOwner** - получает список бронирований вещей, принадлежащих указанному пользователю (владельцу), с возможностью фильтрации по статусу и пагинации.
ID пользователя (владельца вещи), бронирования которых необходимо получить извлекается из заголовка X-Sharer-User-Id,  Статус бронирования для фильтрации (например, "WAITING", "APPROVED", "REJECTED", "ALL"). Значение по умолчанию - "ALL". Индекс первого элемента для постраничного вывода (по умолчанию 0). Количество элементов на одной странице (по умолчанию 20).
Возвращает: List<BookingDtoOut> - список бронирований вещей владельца.

### BookingRepository

BookingRepository - это интерфейс, предоставляющий методы для доступа к данным бронирований (Booking) в базе данных. Он расширяет JpaRepository, предоставляя стандартные CRUD операции, и содержит кастомные методы для выполнения специфических запросов к данным бронирований.

Примеры методов:

*     List<Booking> findByItemInAndBookingStatus(List<Item> items, BookingStatus status, Sort created);

Находит все бронирования для заданных вещей (items) с указанным статусом (status) и сортирует результаты. items:  Список объектов Item, для которых нужно найти бронирования.
status: Значение перечисления BookingStatus, представляющее статус бронирования (например, APPROVED, REJECTED, WAITING).  created: Объект Sort, определяющий порядок сортировки результатов (например, по дате создания бронирования).
Возвращает:  Список объектов Booking, удовлетворяющих критериям поиска, отсортированных заданным образом.

  *      @Query(""" select b from Booking b where b.item.itemId = :itemId """) List<Booking> findAllBookingsByItem(@Param("itemId") Long itemId);

Находит все бронирования для вещи с указанным ID (itemId).
itemId: ID вещи, для которой нужно найти бронирования. 
Возвращает:  Список всех объектов Booking, связанных с указанной вещью.

  *     @Query(value = """ select new java.lang.Boolean(COUNT(b) > 0) from Booking b where (b.item.itemId = :itemId and b.bookingStatus = :status and b.finishBooking = :end or b.finishBooking < :end) and b.booker.id = :userId """) Boolean checkValidateBookingsFromItemAndStatus(@Param("itemId") Long itemId, @Param("userId") Long userId, @Param("status") BookingStatus status, @Param("end") LocalDateTime end);

Проверяет, существует ли бронирование, соответствующее заданным критериям (itemId, userId, status, end).  Используется для валидации возможности бронирования.
itemId: ID вещи.
userId: ID пользователя, который пытается забронировать вещь.
status: Статус бронирования.
end: Дата и время окончания бронирования.
Возвращает:  Boolean - true, если существует бронирование, удовлетворяющее критериям, false в противном случае.

  *     @Query(""" select b from Booking b where b.item.itemId = :itemId and :bookingDtoStartBookingIsBeforeOrAfter between b.startBooking and b.finishBooking """) List<Booking> checkValidateBookings(@Param("itemId") Long itemId, @Param("bookingDtoStartBookingIsBeforeOrAfter") LocalDateTime bookingDtoStartBookingIsBeforeOrAfter);

Описание: Проверяет, пересекается ли заданная дата и время (bookingDtoStartBookingIsB eforeOrAfter) с существующими бронированиями для указанной вещи (itemId). Используется для валидации возможности бронирования.
itemId: ID вещи.
bookingDtoStartBookingIsBeforeOrAfter: Дата и время, которое нужно проверить на пересечение с существующими бронированиями.
Возвращает: Список объектов Booking, которые пересекаются с заданной датой и временем. Если список пуст, пересечений нет.

  *     @Query(""" select b from Booking b where b.booker.id = :userId order by b.startBooking DESC """) Page<Booking> findAllBookingsByBooker(@Param("userId") Long userId, Pageable pageable);

Находит все бронирования, созданные пользователем с указанным ID (userId), с поддержкой пагинации и сортировкой по дате начала бронирования (в порядке убывания).
userId: ID пользователя, бронирования которого нужно найти.
pageable: Объект Pageable, содержащий информацию о пагинации (номер страницы, размер страницы).
Возвращает: Объект Page<Booking>, содержащий страницу бронирований, удовлетворяющих критериям, а также информацию о пагинации.

  *     @Query(""" select b from Booking b where b.booker.id = :userId and :now between b.startBooking and b.finishBooking order by b.startBooking DESC """) Page<Booking> findAllCurrentBookingsByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

Описание: Находит все текущие бронирования (те, которые активны на момент времени now), созданные пользователем с указанным ID (userId), с поддержкой пагинации и сортировкой по дате начала бронирования (в порядке убывания).
userId: ID пользователя, бронирования которого нужно найти.
now: Текущая дата и время.
pageable: Объект Pageable, содержащий информацию о пагинации.
Возвращает: Объект Page<Booking>, содержащий страницу текущих бронирований, удовлетворяющих критериям, а также информацию о пагинации.

Аналогичные методы и запросы, используются для собственника вещи (owner).


## Описание БД

### Описание таблиц:

1. users - Представляет пользователей приложения.
    *  id (BIGINT, PRIMARY KEY): Уникальный идентификатор пользователя.
    *  name (VARCHAR(255)): Имя пользователя.
    *  email (VARCHAR(255), UNIQUE): Адрес электронной почты пользователя. Должен быть уникальным для каждого пользователя.
       •  Ограничения:
    *  pk_user: Первичный ключ (id).
    *  UQ_USER_EMAIL: Уникальное ограничение на поле email.

2. requests:  Представляет запросы на вещи (описания вещей, которые пользователи хотят арендовать).
    *  id (BIGINT, PRIMARY KEY): Уникальный идентификатор запроса.
    *  description (VARCHAR(512)): Описание необходимой вещи.
    *  user_id (BIGINT, FOREIGN KEY): ID пользователя, создавшего запрос (ссылается на таблицу users).
    *  created (TIMESTAMP WITHOUT TIME ZONE): Дата и время создания запроса.
       •  Ограничения:
    *  pk_requests: Первичный ключ (id).
    *  user_id REFERENCES users (id) ON DELETE CASCADE: Внешний ключ, ссылающийся на таблицу users. При удалении пользователя все его запросы также удаляются.

3. items:  Представляет вещи, которые можно арендовать.
    *  id (BIGINT, PRIMARY KEY): Уникальный идентификатор вещи.
    *  name (VARCHAR(255)): Название вещи.
    *  description (VARCHAR(512)): Описание вещи.
    *  available (BOOLEAN): Указывает, доступна ли вещь для аренды.
    *  user_id (BIGINT, FOREIGN KEY): ID пользователя, владеющего вещью (ссылается на таблицу users).
    *  request_id (BIGINT, FOREIGN KEY, NULLABLE): ID запроса, для которого была добавлена эта вещь (ссылается на таблицу requests). Может быть NULL, если вещь не связана с запросом.
       •  Ограничения:
    *  pk_item: Первичный ключ (id).
    *  user_id REFERENCES users (id) ON DELETE CASCADE: Внешний ключ, ссылающийся на таблицу users. При удалении пользователя все его вещи также удаляются.
    *  request_id REFERENCES requests (id) ON DELETE CASCADE: Внешний ключ, ссы лающийся на таблицу requests. При удалении запроса все связанные с ним вещи также удаляются.

4. bookings: Представляет бронирования вещей.
    *  id (BIGINT, PRIMARY KEY): Уникальный идентификатор бронирования.
    *  start_date (TIMESTAMP WITHOUT TIME ZONE): Дата и время начала бронирования.
    *  end_date (TIMESTAMP WITHOUT TIME ZONE): Дата и время окончания бронирования.
    *  item_id (BIGINT, FOREIGN KEY): ID вещи, забронированной (ссылается на таблицу items).
    *  booker_id (BIGINT, FOREIGN KEY): ID пользователя, забронировавшего вещь (ссылается на таблицу users).
    *  status (VARCHAR(50)): Статус бронирования (например, "WAITING", "APPROVED", "REJECTED").
       •  Ограничения:
    *  pk_booking: Первичный ключ (id).
    *  item_id REFERENCES items (id) ON DELETE CASCADE: Внешний ключ, ссылающийся на таблицу items. При удалении вещи все ее бронирования также удаляются.
    *  booker_id REFERENCES users (id) ON DELETE CASCADE: Внешний ключ, ссылающийся на таблицу users. При удалении пользователя все его бронирования также удаляются.

5. comments: Представляет комментарии к вещам.
    *  id (BIGINT, PRIMARY KEY): Уникальный идентификатор комментария.
    *  text (VARCHAR(600)): Текст комментария.
    *  item_id (BIGINT, FOREIGN KEY): ID вещи, к которой относится комментарий (ссылается на таблицу items).
    *  author_id (BIGINT, FOREIGN KEY): ID пользователя, оставившего комментарий (ссылается на таблицу users).
    *  created (TIMESTAMP WITHOUT TIME ZONE): Дата и время создания комментария.
       •  Ограничения:
    *  pk_comm: Первичный ключ (id).
    *  item_id REFERENCES items (id) ON DELETE CASCADE: Внешний ключ, ссылающийся на таблицу items. При удалении вещи все ее комментарии также удаляются.
    *  author_id REFERENCES users (id) ON DELETE CASCADE: Внешний ключ, ссылающийся на таблицу users. При удалении пользователя все его комментарии также удаляются.

Связи между таблицами:

•  users и requests: Один-ко-многим (один пользователь может создать несколько запросов).
•  users и items: Один-ко-многим (один пользователь может владеть несколькими вещами).
•  requests и items: Один-ко-многим (один запрос может быть связан с несколькими вещами). Необязательная связь.
•  items и bookings: Один-ко-многим (одна вещь может быть забронирована несколько раз).
•  users и bookings: Один-ко-многим (один пользователь может создать несколько бронирований).
•  items и comments: Один-ко-многим (одна вещь может иметь несколько комментариев).
•  users и comments: Один-ко-многим (один пользователь может оставить несколько комментариев).

## Тестирование
Тесты показывают, что программа работает на граничных условиях.

## Gateway
Повторяет общую структуру приложения, созданную в части Server, однако не содержит классы бизнес-логики и обращения к БД. Каждый раздел Gateway содержит контроллер, принимающий запросы от внешних сайтов (например BookingController) и клиент, с помощью которого происходит взаимодействие с Сервером (BookingClient).

BookingClient, расширяет интерфейс BaseClient. BookingClient использует BaseClient для фактической отправки HTTP-запроса на API-сервер бронирований. BaseClient отвечает за установку заголовков (например, Content-Type, X-Sharer-User-Id), обработку ошибок и т.д. Он использует RestTemplate для отправки запроса и получения ответа. BaseClient получает HTTP-ответ от API-сервера бронирований. BookingClient получает результат от BaseClient и может выполнить дополнительную обработку ответа, если необходимо. BookingController получает результат от BookingClient и формирует HTTP-ответ для внешнего клиента.

Аналогично с остальными разделами.
