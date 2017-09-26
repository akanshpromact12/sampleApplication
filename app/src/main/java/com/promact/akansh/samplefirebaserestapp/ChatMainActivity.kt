package com.promact.akansh.samplefirebaserestapp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.promact.akansh.samplefirebaserestapp.pojo.Chats
import com.promact.akansh.samplefirebaserestapp.pojo.ContactRealm
import com.promact.akansh.samplefirebaserestapp.pojo.UsersRealm
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ChatMainActivity : BaseActivity() {
    lateinit var contactsRecyclerView: RecyclerView
    lateinit var apiInterface: APIInterface
    lateinit var name: String
    val TAG: String = "ChatMainActivity"
    var str: String = ""
    lateinit var contactsList: MutableList<String>
    lateinit var contactRealm: ContactRealm
    lateinit var usersRealm: UsersRealm
    lateinit var middleware: Middleware
    lateinit var networkStatus1: NetworkStatus
    lateinit var contactsAdapter: ContactsAdapter
    var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        contactRealm = ContactRealm()
        usersRealm = UsersRealm()
        middleware = Middleware()
        networkStatus1 = NetworkStatus()

        val intent: Intent = intent
        if (SaveSharedPrefs.getName(this@ChatMainActivity).isEmpty()) {
            name = intent.getStringExtra("name")
        } else {
            name = SaveSharedPrefs.getName(this@ChatMainActivity)
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.sender_name) + name

        contactsRecyclerView = findViewById(R.id.contacts_recycler_view) as RecyclerView
        Log.d(TAG, "start")
        val users = synchronousGetUsers()
        val time = synchronousGetChats(users)
        Log.d(TAG, "time $time")
        Log.d(TAG, " end ")
        getAllContacts(time)
        getAllUsers()
    }

    fun synchronousGetUsers(): String {
        val call = apiInterface.fetchAllUsers()
        val resp = call.execute()
        var users = ""

        if (resp.body().contentLength() > 4) {
            val jsonObj = JSONObject(resp.body().string())

            for (i in 0..(jsonObj.length()-1)) {
                if (users.equals("")) {
                    users = jsonObj.names().get(i) as String
                } else {
                    users += "-" + jsonObj.names().get(i) as String
                }
            }
        }
        Log.d(TAG, "names: $users\n")

        return users
    }

    fun synchronousGetChats(users: String): String {
        var time = ""
        for (i in 0..(users.split("-").size-1)) {
            val call = apiInterface.ReceiveChats(users.split("-")[i], name)
            val call1 = apiInterface.ReceiveChats(name, users.split("-")[i])
            val resp = call.execute()
            val resp1 = call1.execute()

            if (resp.body().contentLength() > 4) {
                val jsonObj = JSONObject(resp.body().string())
                val jsonArr = jsonObj.names()
                Log.d(TAG, "Outside1")

                for (i in 0..(jsonArr.length() - 1)) {
                    val jsonObj1 = jsonObj.getJSONObject(jsonArr.get(i).toString())
                    Log.d(TAG, "time: ${jsonObj1.getString("Time")}")

                    if (time.equals("")) {
                        time = users.split("-")[i] + "-" + name + "~" + jsonObj1
                                .getString("Time")
                    } else {
                        time += "_" + users.split("-")[i] + "-" + name + "~" + jsonObj1
                                .getString("Time")
                    }
                    Log.d(TAG, "Inside1")
                }
                Log.d(TAG, "Outside1\n$time")
            }

            if (resp1.body().contentLength() > 4) {
                val jsonObj2 = JSONObject(resp1.body().string())
                val jsonArr2 = jsonObj2.names()
                Log.d(TAG, "Outside2")

                for (i in 0..(jsonArr2.length() - 1)) {
                    val jsonObj2 = jsonObj2.getJSONObject(jsonArr2.get(i).toString())
                    Log.d(TAG, "time: ${jsonObj2.getString("Time")}")

                    if (time.equals("")) {
                        time = users.split("-")[i] + "-" + name + "~" + jsonObj2
                                .getString("Time")
                    } else {
                        time += "_" + users.split("-")[i] + "-" + name + "~" + jsonObj2
                                .getString("Time")
                    }
                    Log.d(TAG, "Inside2")
                }
                Log.d(TAG, "Outside2\n$time")
            }
        }

        return time
    }

    fun getAllUsers() {
        if (networkStatus1.checkInternet(applicationContext)) {
            val call = apiInterface.fetchAllUsers()
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    try {
                        if (response?.body()?.contentLength()!! > 4) {
                            val jsonObj = JSONObject(response.body().string())
                            val usersSize = middleware.checkUserSize()

                            for (i in 0..(jsonObj.length()-1)) {
                                val user = jsonObj.names().get(i) as String
                                Log.d(TAG, "names: $user\n")

                                if (usersSize == 0) {
                                    usersRealm.userName = user
                                    middleware.addUsers(usersRealm)

                                    Toast.makeText(this@ChatMainActivity,
                                            "users added", Toast.LENGTH_SHORT)
                                            .show()
                                    Log.d(TAG, "users added to realm")
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                }
            })
        }
    }

    fun getAllContacts(timeStr: String) {
        contactsList = ArrayList()
        Log.d(TAG, "In getAllContacts() -> $timeStr")
        for (i in 0..(timeStr.split("_").size-1)) {
            Log.d(TAG, "timeStr: ${timeStr.split("_")[i].split("~")[1]
                    .replace(("/"+ Calendar.getInstance()
                            .get(Calendar.YEAR)), "")}")
        }
        if (networkStatus1.checkInternet(applicationContext)) {
            val call: Call<ResponseBody> = apiInterface.fetchAllUsers()
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    try {
                        Log.d(TAG, "hello")
                        if (response?.body()?.contentLength()!! > 4) {
                            val jsonObj: JSONObject = JSONObject(response.body()
                                    .string())
                            val contactRealmSizeCheck = middleware
                                    .searchContactSize()

                            for (i in 0..(jsonObj.length()-1)) {
                                val user: String = jsonObj.names().get(i) as String
                                Log.d(TAG, "names: $user\n")

                                if (user != name) {
                                    contactsList.add(user)
                                }
                                if (contactRealmSizeCheck == 0) {
                                    contactRealm.contact_name = user
                                    middleware.addContacts(contactRealm)

                                    Toast.makeText(this@ChatMainActivity,
                                            "contacts added", Toast.LENGTH_SHORT)
                                            .show()
                                    Log.d(TAG, "contacts added to realm")
                                }/*

                                val call1 = apiInterface.ReceiveChats(user, name)
                                val call2 = apiInterface.ReceiveChats(name, user)

                                call1.enqueue(object : Callback<ResponseBody> {
                                    override fun onResponse(call1: Call<ResponseBody>?, response1: Response<ResponseBody>?) {
                                        Log.d(TAG, "receive response code: " + response1?.code())

                                        try {
                                            if (response1?.body()?.contentLength()!! > 4) {
                                                val jsonObj1 = JSONObject(response1.body().string())
                                                val jsonArr1 = jsonObj1.names()

                                                val jsonObject1 = jsonObj1.getJSONObject(jsonArr1.get(0).toString())
                                                if (time == "") {
                                                    time = jsonObject1.getString("Time")
                                                    Log.d(TAG, "time1: $time")
                                                } else {
                                                    time += "-"+jsonObject1.getString("Time")

                                                }
                                            }
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call1: Call<ResponseBody>?, t1: Throwable?) {
                                        Log.d(TAG, "unsuccessful")
                                    }
                                })

                                call2.enqueue(object : Callback<ResponseBody> {
                                    override fun onResponse(call2: Call<ResponseBody>?, response2: Response<ResponseBody>?) {
                                        Log.d(TAG, "receive response code: " + response2?.code())

                                        try {
                                            if (response2?.body()?.contentLength()!! > 4) {
                                                Log.d(TAG, "response: ${response2.body().string()}")
                                                val jsonObj1 = JSONObject(response2.body().string())
                                                val jsonArr1 = jsonObj1.names()

                                                val jsonObject1 = jsonObj1.getJSONObject(jsonArr1.get(0).toString())
                                                if (time == "") {
                                                    time = jsonObject1.getString("Time")
                                                    Log.d(TAG, "time2: $time")
                                                } else {
                                                    time += "-"+jsonObject1.getString("Time")
                                                }
                                                if (contactsRecyclerView.adapter == null) {
                                                    contactsAdapter = ContactsAdapter(contactsList,
                                                            applicationContext, name, str, time)
                                                    contactsRecyclerView.layoutManager = LinearLayoutManager(this@ChatMainActivity)
                                                    contactsRecyclerView.adapter = contactsAdapter
                                                } else {
                                                    contactsAdapter.notifyDataSetChanged()
                                                }
                                                *//*
                                                contactsAdapter = ContactsAdapter(contactsList,
                                                        applicationContext, name, str, time)
                                                contactsRecyclerView.layoutManager = LinearLayoutManager(this@ChatMainActivity)
                                                contactsRecyclerView.adapter = contactsAdapter*//*
                                            }
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call2: Call<ResponseBody>?, t2: Throwable?) {

                                    }
                                })*/
                            }

                            Log.d(TAG, "final time: $timeStr")
                            contactsAdapter = ContactsAdapter(contactsList,
                                    applicationContext, name, str, timeStr)
                            contactsRecyclerView.layoutManager = LinearLayoutManager(this@ChatMainActivity)
                            contactsRecyclerView.adapter = contactsAdapter
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                }
            })
            /*val call: Call<ResponseBody> = apiInterface.fetchAllUsers()
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    Log.d(TAG, "response code: ${response?.code()}")

                    try {
                        if (response?.body()?.contentLength()!! > 4) {
                            val jsonObj: JSONObject = JSONObject(response.body().string())
                            val jsonArr: JSONArray = jsonObj.names()
                            val contactRealmSizeCheck = middleware.checkContactsRealmSize()

                            Log.d(TAG, "json obj size: ${jsonObj.length()}")
                            for (i in 0..(jsonArr.length()-1)) {
                                val jsonObj1: JSONObject = jsonObj
                                        .getJSONObject(jsonArr.get(i).toString())
                                val user: String = jsonObj1.getString("user")

                                if (user != name) {
                                    contactsList.add(user)
                                }
                                if (contactRealmSizeCheck == 0) {
                                    contactRealm.contact_name = user
                                    middleware.addContacts(contactRealm)

                                    Log.d(TAG, "contacts added to realm")
                                }
                            }

                            val contactsAdapter: ContactsAdapter = ContactsAdapter(contactsList,
                                    applicationContext, name, str)
                            contactsRecyclerView.layoutManager = LinearLayoutManager(this@ChatMainActivity)
                            contactsRecyclerView.adapter = contactsAdapter
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                }
            })*/
        } else {
            if (middleware.searchContactSize() != 0) {
                val contactNames: String = middleware.searchAllContacts()
                for (users in contactNames.split(":")) {
                    Log.d(TAG, "users: ${users.trim()}\nName: $name")
                    if (users.trim() != name) {
                        contactsList.add(users)
                    }
                }
            }

            val contactsAdapter: ContactsAdapter = ContactsAdapter(contactsList,
                    applicationContext, name, str, middleware.checkTime())
            contactsRecyclerView.layoutManager = LinearLayoutManager(this@ChatMainActivity)
            contactsRecyclerView.adapter = contactsAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId

        if (id == R.id.logoutMenuOption) {
            Toast.makeText(this@ChatMainActivity, "You just logged out!!",
                    Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ChatMainActivity, MainActivity::class.java)
            //SaveSharedPrefs.clearAllPrefs(applicationContext)
            val editor = SaveSharedPrefs.getSharedPrefs(applicationContext).edit()
            editor.clear()
            editor.apply()
            finish()

            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("EXIT", true)

        startActivity(intent)
    }
}
