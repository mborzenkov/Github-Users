# Github-Users
Ознакомительное приложение для поиска пользователей на GitHub.

Функциональность:
- Авторизация пользователя локально с помощью Вконтакте/Facebook/Google.
- Поиск пользователей на GitHub с помощью GitHub API.
- Просмотр дополнительной информации о позователе.

Используемые технологии:
- Kotlin, RxJava, Retrofit, Picasso, Dagger, JUnit, Mockito, Robolectric, Espresso

## Описание составляющих
### MainActivity
- Выполняет управление фрагментами, передает информацию между фрагментами

### UserListFragment
- Фрагмент со списком пользователей (в RecyclerView)
- Отображает текущего пользователя и его фотографию в CollapsingToolbarLayout
- Для наполнения RecyclerView используется адаптер UserListAdapter, реализованный по шаблону Delegete Adapters (от слова "делегировать" - один общий адаптер обладает информацией о типах данных и вызывает соответствующий вложенный адаптер, в свою очередь каждый вложенный адаптер сам занимается инфлейтом и биндингом данных). Адаптеры располагаются в пакете adapters.
- Для асинхронной загрузки данных из сети используется RxJava. Подписки сохраняются и в onPause выполняется отписка от всего.
- Также выполняется кэширование запросов, т.е. повторно запросы не выполняются, а сохраняются как страницы. Поэтому, передвижение вперед по страницам выполняется с загрузкой данных, а назад почти мнгновенно, т.к. данные загружаются локально
- Для вызова принудительного обновления данных добавлен SwipeRefreshLayout

### UserInfoFragment
- При нажатии на элемент списка открывается фрагмент с подробной информацией о пользователе
- Данные загружаются асинхронно отдельным запросом по API
- При переходе в фрагмент используется SharedElement и анимация, чтобы все было красиво

### LoginFragment
- Фрагмент с кнопками авторизации, для непосредственной авторизации использует SocialAuthHelper
- SocialAuthHelper - помощник для создания Intent, открывающего авторизацию в соответствующей соцсети, а также реализующий функции для парсинга полученных данных в формат UserItem. Фотография профиля сохраняется локально и используется при повторном входе.

### Commons
- Содержит расширения для работы с файлами, фрагментами и прочие

### LoginManager
- Вспомогательный класс, отвечающий за локальную авторизацию (деавторизацию)
- Сохраняет список локальных пользователей в формате JSON
- Предоставляет Observer для оповещений об изменении статуса авторизации типа BehaviorSubject

### ADT (Model)
- UserItem - представляет элемент списка пользователей, реализует Parcelable, отдельно реализован класс для конвертации JSON
- UsersPage - страница пользователей, хранит номер, общее число страниц и список пользователей на этой странице, реализует Parcelable
- PaginatorItem - объект для преедачи данных в пагинатор
- LoginItem - представляет данные локального пользователя (авторизация работает с ним)

## Тесты
В качестве демонстрации реализованы следующие тесты:
- UserItemJsonAdapterTest и UserItemParcelableTest - unit тесты для тестирования конвертаций в JSON и Parcelable
- GithubApiTest - тест работы с GitHub API с использованием Fake-сервера. Проверяются различные варианты взаимодействия с сервером, включая некорректные ответы, отсутствие ожидаемых данных и т.д. Fake-сервер реализован в GithubApiMockDispatcher. Для подключения используются иньекции Dagger'а.
- UserListFragmentTest - Espresso UI тест, проверяет: что окно авторизации действительно показывается, из него можно выйти, список показывается, элементы списка открываются в отельном фрагменте и из него можно вернуться
