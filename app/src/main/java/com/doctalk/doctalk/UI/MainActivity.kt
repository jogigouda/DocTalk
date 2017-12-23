package com.doctalk.doctalk.UI

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.doctalk.doctalk.R
import kotlinx.android.synthetic.main.activity_main.*
 import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.request.Request
import com.doctalk.doctalk.Network.ServiceCall
import com.doctalk.doctalk.adapter.UsersAdapter
import com.doctalk.doctalk.model.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.lang.reflect.Method
import android.R.attr.delay
import android.os.Handler
import java.util.*


class MainActivity : AppCompatActivity() {
    internal lateinit var usersList: MutableList<User>
    private var searchMenuItem: MenuItem? = null
    private var mSearchView: SearchView? = null
    val serverDownloadObservable = Observable.create<List<User>> { emitter ->
        emitter.onNext(
                getUserList())
        emitter.onComplete();
    }

    var query: String = ""

    //To detect type delay
    var delay: Long = 1000 // 1 seconds after user stops typing
    var last_text_edit: Long = 0
    var handler = Handler()

    private val input_finish_checker = Runnable {
        if (System.currentTimeMillis() > last_text_edit + delay - 500) {
            callAPi(query)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        usersList = ArrayList<User>()
        userList.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

        callAPi("user")

//        serverDownloadObservable.
//                observeOn(AndroidSchedulers.mainThread()).
//                subscribeOn(Schedulers.io()).
////                subscribe(integer -> {
////            updateTheUserInterface(integer); // this methods updates the ui
////            view.setEnabled(true); // enables it again
//        });
//    }
//    .subscribeOn(Schedulers.io())
//    .observeOn(AndroidSchedulers.mainThread());
    }

    private fun getUserList(): List<User> {
        return usersList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val b = super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.search, menu)
        searchMenuItem = menu!!.findItem(R.id.action_search)

        mSearchView = menu.findItem(R.id.action_search).actionView as SearchView?
        mSearchView!!.setOnQueryTextListener(listener)
        return b
    }




    internal var listener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            handler.removeCallbacks(input_finish_checker);
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            //avoid triggering event when text is empty
            if (newText.length > 0) {
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
                query = newText
            }

            return false
        }
    }



    fun callAPi(name: String) {
        val url = String.format(ServiceCall.USER_FETCH_URL, name)
        Log.d("url", url)
        progressBar.visibility = View.VISIBLE
        val jsonObjectRequest = JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                Response.Listener { response ->
                    progressBar.visibility = View.GONE
                    Log.d("Response", response.toString())
                    val items = response.optJSONArray("items")
                    if (items != null && items.length() > 0) {
                        usersList.clear()

                        var i = 0
                        while (i < items.length()) {
                            val user = User(items.optJSONObject(i))
                            usersList.add(user)
                            i++
                        }
                        userList.adapter = UsersAdapter(usersList)
                        tvNoUser.visibility = View.GONE
                        userList.visibility = View.VISIBLE
                    } else {
                        tvNoUser.visibility = View.VISIBLE
                        userList.visibility = View.GONE
                    }
                }, ServiceCall().errorListener)
        val serviceCall = ServiceCall(jsonObjectRequest, this)
        serviceCall.callAPI()
    }
}
