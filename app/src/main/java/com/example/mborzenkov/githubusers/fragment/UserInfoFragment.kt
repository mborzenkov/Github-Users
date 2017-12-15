package com.example.mborzenkov.githubusers.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.example.mborzenkov.githubusers.MyApplication
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.activity.MainActivity
import com.example.mborzenkov.githubusers.adt.UserItem
import com.example.mborzenkov.githubusers.commons.*
import com.example.mborzenkov.githubusers.networking.GithubApiImpl
import com.example.mborzenkov.githubusers.networking.UsersManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_userinfo.*

/** Фрагмент с подробной информацией о пользователе. */
class UserInfoFragment : Fragment() {

    companion object {

        /** Layout фрагмента. */
        private const val FRAGMENT_LAYOUT = R.layout.fragment_userinfo

        /** Ключ для userdata в saved instance state. */
        private const val DATA_KEY = "userdata"

        /** TAG фрагмента для фрагмент менеджера. */
        const val TAG = "fragment_userinfo"

        /** Возвращает уже созданный ранее объект UserInfoFragment или создает новый, если такого нет.
         * Для создания объектов следует всегда использовать этот метод.
         * Не помещает объект в FragmentManager.
         * При помещении объекта в FragmentManager, следует использовать тэг TAG.
         *
         * @param manager менеджер для поиска фрагментов по тэгу
         *
         * @return новый объект UserInfoFragment
         */
        fun getInstance(manager: FragmentManager): UserInfoFragment {
            return manager.findFragmentByTag(TAG) as UserInfoFragment? ?: UserInfoFragment()
        }

    }


    /////////////////////////
    // Поля объекта

    /** Менеджер для доступа к сети. */
    private val mUserManager by lazy {
        val app = activity!!.application as MyApplication
        UsersManager(GithubApiImpl(app.getGithubApiComponent()))
    }

    /** Данные для отображения в фрагменте. */
    private var mUserData: UserItem? = null

    /** Подписки. */
    private var mSubscriptions: CompositeDisposable? = null


    /////////////////////////
    // Колбеки Fragment

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return container?.inflate(FRAGMENT_LAYOUT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewCompat.setTransitionName(iv_userinfo_image, MainActivity.SHARED_ELEMENT_IMAGE_TRANSITION_NAME)

        val act = activity as AppCompatActivity
        act.setNewToolbar(toolbar_userinfo)

        savedInstanceState?.let {
            mUserData = it.getParcelable(DATA_KEY)
        }

        mUserData?.let {
            act.setAppBarTitle(it.username)
            iv_userinfo_image.loadImg(it.imageUrl)
            tv_userinfo_username.text = it.username
            tv_userinfo_fullname.text = ""
            tv_userinfo_location.text = ""
            iv_userinfo_location.setVisibility(false)
            tv_userinfo_location.setVisibility(false)
            tv_userinfo_email.text = ""
            iv_userinfo_email.setVisibility(false)
            tv_userinfo_email.setVisibility(false)
            tv_userinfo_blog.text = ""
            iv_userinfo_blog.setVisibility( false )
            tv_userinfo_blog.setVisibility( false )
            tv_userinfo_followers.text = ""
            tv_userinfo_bio.text = ""
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        mSubscriptions = CompositeDisposable()
    }

    override fun onPause() {
        super.onPause()
        mSubscriptions?.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(DATA_KEY, mUserData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setHasOptionsMenu(false)
    }


    /////////////////////////
    // Колбеки Menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_userinfo, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_userinfo_opengithub -> {
                mUserData?.let {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.profileUrl)))
                }
                return true
            }
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /////////////////////////
    // Обновление фрагмента

    /** Обновляет отображаемые данные в фрагменте.
     *
     * @param newData новые данные, которые необходимо отобразить
     */
    fun updateUserData(newData: UserItem) {
        mUserData = newData
    }

    /** Загружает полные данные пользователя из сети и отображает их. */
    private fun loadData() {
        mUserData?.let {
            val subscription = mUserManager.getSingleUser(it.username)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->
                                tv_userinfo_fullname?.let {
                                    tv_userinfo_fullname.text = response.fullname
                                    tv_userinfo_location.text = response.location
                                    iv_userinfo_location.setVisibility(response.location.isNotEmpty())
                                    tv_userinfo_location.setVisibility(response.location.isNotEmpty())
                                    tv_userinfo_email.text = response.email
                                    iv_userinfo_email.setVisibility(response.email.isNotEmpty())
                                    tv_userinfo_email.setVisibility(response.email.isNotEmpty())
                                    tv_userinfo_blog.text = response.blog
                                    iv_userinfo_blog.setVisibility(response.blog.isNotEmpty())
                                    tv_userinfo_blog.setVisibility(response.blog.isNotEmpty())
                                    tv_userinfo_followers.text = response.followers.toString()
                                    tv_userinfo_bio.text = response.bio
                                }
                            },
                            { e ->
                                Snackbar.make(
                                        constraintlayout_userinfo,
                                        e.message ?: "",
                                        Snackbar.LENGTH_LONG).show()
                            }
                    )
            mSubscriptions?.add(subscription)
        }
    }

}