package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activity.MainActivity
import com.example.myapplication.classes.DataBase
import com.example.myapplication.classes.User
import com.example.myapplication.classes.UserAdapter
import com.example.myapplication.classes.UserDB
import com.example.myapplication.classes.UserDao
import kotlinx.coroutines.launch

class Login : Fragment() {

    private lateinit var dao: UserDao
    private lateinit var adapter: UserAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var emptyLayout: LinearLayout

    // состояние постраничной загрузки
    private var offset = 0
    private var isLoading = false
    private var endReached = false

    companion object {
        private const val PAGE_SIZE = 20
        private const val PRELOAD_DISTANCE = 5   // за сколько элементов до конца грузить следующую страницу
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = DataBase.getDatabase(requireContext()).userDao()

        recycler = view.findViewById(R.id.usersRv)
        emptyLayout = view.findViewById(R.id.emptyLayout)
        val regBtn = view.findViewById<Button>(R.id.buttonGoRegistration)

        val layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter { userDb -> loginAs(userDb) }
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter

        // Листание: когда дошли почти до конца списка, подгружаем следующую страницу
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (lastVisible >= adapter.itemCount - PRELOAD_DISTANCE) {
                    loadNextPage()
                }
            }
        })

        regBtn.setOnClickListener {
            (requireActivity() as MainActivity).showRegistration()
        }

        loadNextPage()   // первая страница
    }

    private fun loadNextPage() {
        if (isLoading || endReached) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            val page = dao.getUsersPage(PAGE_SIZE, offset)
            adapter.addAll(page)
            offset += page.size
            if (page.size < PAGE_SIZE) endReached = true
            isLoading = false
            updateEmptyState()
        }
    }

    private fun updateEmptyState() {
        val isEmpty = adapter.itemCount == 0
        emptyLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun loginAs(u: UserDB) {
        val activity = requireActivity() as MainActivity
        val user = User(
            name = u.name,
            course = u.course,
            difficulty = u.difficulty,
            birthDay = u.birthDay,
            birthMonth = u.birthMonth,
            birthYear = u.birthYear,
            gender = u.gender,
            zodiac = u.zodiac
        )
        activity.user = user
        activity.settings.userDifficulty = user.difficulty
        activity.showMenu()
    }
}