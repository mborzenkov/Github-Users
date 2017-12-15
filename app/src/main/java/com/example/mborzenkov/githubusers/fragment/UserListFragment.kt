package com.example.mborzenkov.githubusers.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseArray
import android.view.*
import android.widget.ImageView
import com.example.mborzenkov.githubusers.MyApplication
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.adapter.userlist.UserListAdapter
import com.example.mborzenkov.githubusers.adt.LoginItem
import com.example.mborzenkov.githubusers.adt.UserItem
import com.example.mborzenkov.githubusers.adt.UsersPage
import com.example.mborzenkov.githubusers.commons.inflate
import com.example.mborzenkov.githubusers.commons.loadImg
import com.example.mborzenkov.githubusers.commons.setAppBarTitle
import com.example.mborzenkov.githubusers.commons.setNewToolbar
import com.example.mborzenkov.githubusers.login.LoginManager
import com.example.mborzenkov.githubusers.networking.GithubApi
import com.example.mborzenkov.githubusers.networking.GithubApiImpl
import com.example.mborzenkov.githubusers.networking.UsersManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_userlist_search.*
import kotlinx.android.synthetic.main.fragment_userlist.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil


/** Фрагмент со списком UserItem.
 * Activity, использующая фрагмент, должна реализовывать интерфейс UserListCallbacks.
 */
class UserListFragment : Fragment(), UserListAdapter.UserListAdapterEventHandler {


    /////////////////////////
    // Static

    /** Интерфейс для оповещений о событиях в фрагменте. */
    interface UserListCallbacks{
        /** Вызывается при нажатии на кнопку "авторизоваться". */
        fun onSignInPressed()
        /** Вызывается при нажатии на кнопку "выйти". */
        fun onLogOffPressed()
        /** Вызывается при завершении загрузки данных. */
        fun onDataLoaded()
        /** Вызывается при нажатии на элемент списка.
         *
         * @param user элемент, на который нажали, содержит неполные данные о пользователе
         * @param sharedElement Shared Element для перемещения в другой фрагмент
         */
        fun onUserClick(user: UserItem, sharedElement: ImageView?)
    }

    companion object {

        /** Layout фрагмента. */
        private const val FRAGMENT_LAYOUT = R.layout.fragment_userlist
        /** Задержка ввода в поле запроса в мс. */
        private const val SEARCH_DEBOUNCE = 600L
        /** Максимальная длительность значка обновления в мс. */
        private const val REFRESH_TIMEOUT = 6000L

        /** Ключ для сохранения загруженной информации в saved instance state. */
        private const val KEY_DATA = "userlistdata"
        /** Ключ для сохранения номера текущей страницы в saved instance state. */
        private const val KEY_PAGE = "curpage"
        /** Ключ для сохранения текущего запроса в saved instance state. */
        private const val KEY_QUERY = "curquery"

        /** TAG фрагмента для фрагмент менеджера. */
        const val TAG = "fragment_userlist"

        /** Возвращает уже созданный ранее объект UserListFragment или создает новый, если такого нет.
         * Для создания объектов следует всегда использовать этот метод.
         * Не помещает объект в FragmentManager.
         * При помещении объекта в FragmentManager, следует использовать тэг TAG.
         *
         * @param manager менеджер для поиска фрагментов по тэгу
         * @return новый объект UserListFragment
         */
        fun getInstance(manager: FragmentManager): UserListFragment {
            return manager.findFragmentByTag(TAG) as UserListFragment? ?: UserListFragment()
        }

    }


    /////////////////////////
    // Поля объекта

    /** Объект для колбеков о событиях во фрагменте.  */
    private var mCallbacks: UserListCallbacks? = null
    /** Адаптер для списка. */
    private var mItemListAdapter: UserListAdapter? = null
    /** Менеджер для доступа к сети. */
    private val mUserManager by lazy {
        val app = activity!!.application as MyApplication
        UsersManager(GithubApiImpl(app.getGithubApiComponent()))
    }
    /** Подписки. */
    private var mSubscriptions: CompositeDisposable? = null

    /** Текущий запрос. */
    private var mCurQuery = ""
    /** Номер текущей страницы. */
    private var mCurPage = 1
    /** Все загруженные страницы. */
    private var mAllPages = SparseArray<UsersPage?>()
    /** Логин текущего пользователя. */
    private var mCurLogin: LoginItem = LoginItem.EMPTY


    /////////////////////////
    // Колбеки Fragment

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let {
            mItemListAdapter = UserListAdapter(this, context.getString(R.string.pagination_page))
            mCallbacks = context as UserListCallbacks
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return container?.inflate(FRAGMENT_LAYOUT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        // Настройка RecyclerView
        recyclerview_userlist.setHasFixedSize(true)
        recyclerview_userlist.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Слушаем о потягивании refresh
        swiperefreshlayout_userlist.setOnRefreshListener {
            mAllPages.remove(mCurPage)
            loadUsers()
        }

        // Подключаем адаптер к RecyclerView
        recyclerview_userlist.adapter = mItemListAdapter

        // Устанавливаем тулбар
        val act = activity as AppCompatActivity
        toolbar_userlist.title = mCurLogin.fullname
        act.setNewToolbar(toolbar_userlist)

        // Отключаем открытие клавиатуры по умолчанию
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        if (savedInstanceState != null
                && savedInstanceState.containsKey(KEY_DATA)
                && savedInstanceState.containsKey(KEY_PAGE)) {
            mAllPages = savedInstanceState.get(KEY_DATA) as SparseArray<UsersPage?>
            mCurPage = savedInstanceState.getInt(KEY_PAGE)
            mCurQuery = savedInstanceState.getString(KEY_QUERY)
        }

    }

    override fun onResume() {
        super.onResume()

        // Управление подписками
        mSubscriptions = CompositeDisposable()

        // Подписка об изменении статуса авторизации
        val loginStateSubscription = LoginManager.stateObserver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    newLogin ->
                    mCurLogin = newLogin
                    updateLoginBar()
                })
        mSubscriptions?.add(loginStateSubscription)

        // Подписка о вводе текста в строку поиска
        val textChangedObservable: Observable<String> = Observable.create({
            subscriber ->
            et_userlist_search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun afterTextChanged(p0: Editable?) {
                    subscriber.onNext(et_userlist_search.text.toString())
                }
            })
        })
        val textChangeSubscription = textChangedObservable
                .debounce(SEARCH_DEBOUNCE, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ query ->
                    if (query != mCurQuery) {
                        mCurQuery = query.trim()
                        mCurPage = 1
                        mAllPages.clear()
                        loadUsers()
                    }
                })
        mSubscriptions?.add(textChangeSubscription)

        loadUsers()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSparseParcelableArray(KEY_DATA, mAllPages)
        outState.putInt(KEY_PAGE, mCurPage)
        outState.putString(KEY_QUERY, mCurQuery)
    }

    override fun onPause() {
        super.onPause()
        // Очистка подписок (с отпиской)
        mSubscriptions?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setHasOptionsMenu(false)
    }

    override fun onDetach() {
        super.onDetach()
        // Предотвращение утечек
        mCallbacks = null
        mItemListAdapter = null
    }


    /////////////////////////
    // Колбеки Menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Создание меню
        inflater.inflate(R.menu.menu_userlist, menu)
        val loggedIn = mCurLogin != LoginItem.EMPTY
        menu.findItem(R.id.action_userlist_signin)?.isVisible = !loggedIn
        menu.findItem(R.id.action_userlist_logoff)?.isVisible = loggedIn
        val act = activity as AppCompatActivity
        act.setAppBarTitle(mCurLogin.fullname)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_userlist_signin -> {
                mCallbacks?.onSignInPressed()
                return true
            }
            R.id.action_userlist_logoff -> {
                mCallbacks?.onLogOffPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /////////////////////////
    // Колбеки UserListAdapter

    override fun onPrevClick() {
        mCurPage--
        loadUsers()
    }

    override fun onNextClick() {
        mCurPage++
        loadUsers()
    }

    override fun onUserClick(user: UserItem, sharedElement: ImageView?) {
        mCallbacks?.onUserClick(user, sharedElement)
    }


    /////////////////////////
    // Вспомогательные

    /** Обновляет AppBarLayout информацией об авторизованном пользователе. */
    private fun updateLoginBar() {
        context?.let {
            if (mCurLogin != LoginItem.EMPTY) {
                iv_loginbar_image.loadImg(mCurLogin.imageUrl)
                appbar_userlist.layoutParams.height = it.resources
                        .getDimension(R.dimen.height_loginbar_expanded).toInt()
            } else {
                iv_loginbar_image.setImageResource(0)
                appbar_userlist.layoutParams.height = it.resources
                        .getDimension(R.dimen.height_loginbar_collapsed).toInt()
            }
            activity?.invalidateOptionsMenu()
        }
    }

    /** Загружает список пользователей из сети и отображает. */
    private fun loadUsers() {

        mAllPages.get(mCurPage)?.let {
            mItemListAdapter?.replaceUsers(it)
            // recyclerview_userlist.smoothScrollToPosition(0)
            return
        }

        val observer: Single<GithubApi.UsersResponse> = if (mCurQuery.isEmpty()) {
            mUserManager.topUsers(mCurPage)
        } else {
            mUserManager.searchUsers(mCurQuery, mCurPage)
        }
        swiperefreshlayout_userlist.isRefreshing = true
        swiperefreshlayout_userlist.postDelayed(
                { swiperefreshlayout_userlist?.isRefreshing = false }, REFRESH_TIMEOUT)
        val subscription = observer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            response.items?.let {
                                mAllPages.put(mCurPage, UsersPage(mCurPage, ceil(response.total_count.toFloat() / GithubApiImpl.PER_PAGE).toInt(), it))
                            }
                            mItemListAdapter?.replaceUsers(mAllPages.get(mCurPage) ?: UsersPage.EMPTY)
                            swiperefreshlayout_userlist?.isRefreshing = false
                            // recyclerview_userlist.smoothScrollToPosition(0)
                            mCallbacks?.onDataLoaded()
                        },
                        { e ->
                            Snackbar.make(
                                    recyclerview_userlist,
                                    e.message ?: "",
                                    Snackbar.LENGTH_LONG).show()
                        }
                )
        mSubscriptions?.add(subscription)

    }

}