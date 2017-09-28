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
import com.promact.akansh.samplefirebaserestapp.pojo.ChatsRealm
import com.promact.akansh.samplefirebaserestapp.pojo.ContactRealm
import com.promact.akansh.samplefirebaserestapp.pojo.UsersRealm
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        Log.d(TAG, "before")
        middleware.getNumberOfChats(name)
        Log.d(TAG, "after")
        val users = synchronousGetUsers()
        val time = synchronousGetChats(users)
        val chatCount = syncGetChatCount(users)
        Log.d(TAG, "chatCount: ${chatCount.size}")
        for (i in 0..(chatCount.size)) {
            if (chatCount[users[i]]?.split("~")?.size == 4) {
                Log.d(TAG, "chatCount items: " + chatCount[users[i]])
            }
        }

        for (i in 0..(users.size-1)) {
            if (!time[users[i]].isNullOrBlank()) {
                Log.d(TAG, "Array elements: " + time[users[i]])
            }
        }
        Log.d(TAG, " end ")
        getAllContacts(time, users, chatCount)
        getAllUsers()
    }

    fun syncGetChatCount(users: ArrayList<String>): MutableMap<String, String> {
        val chatsCount: MutableMap<String, String> = HashMap()

        for (i in 0..(users.size-1)) {
            val call = apiInterface.ReceiveChats(users[i], name)
            val call1 = apiInterface.ReceiveChats(name, users[i])
            val resp = call.execute()
            val resp1 = call1.execute()

            if (resp.body().contentLength() > 4) {
                val jsonObj = JSONObject(resp.body().string())
                val jsonArr = jsonObj.names()
                Log.d(TAG, "users: " + users)
                Log.d(TAG, "Outside1")
                var count: Int = 0

                for (i in 0..(jsonArr.length() - 1)) {
                    val jsonObj1 = jsonObj.getJSONObject(jsonArr.get(i).toString())
                    Log.d(TAG, "time: ${jsonObj1.getString("Time")}")
                    var s: String = ""

                    if ((jsonObj1.getString("userTo").equals(name)
                            && jsonObj1.getString("userFrom").equals(users[i]))
                            && jsonObj1.getString("unread").equals("true")) {
                        Log.d(TAG, "counts")
                        s = jsonObj1.getString("sendRecvPair")+"~"+jsonObj1
                                .getString("Msg")+"~"+jsonObj1.getString("Time")
                        count++
                        Log.d(TAG, "counts value: $count")
                    }

                    if (count != 0) {
                        Log.d(TAG, "s: $s~$count")
                        chatsCount.put(users[i], s + "~" + count)
                    }

                    Log.d(TAG, "Inside1")
                }
                Log.d(TAG, "Outside1\n$time")
            }

            if (resp1.body().contentLength() > 4) {
                val jsonObj2 = JSONObject(resp1.body().string())
                val jsonArr2 = jsonObj2.names()
                Log.d(TAG, "Outside2")
                var count: Int = 0

                for (i in 0..(jsonArr2.length() - 1)) {
                    val jsonObj2 = jsonObj2.getJSONObject(jsonArr2.get(i).toString())
                    Log.d(TAG, "time: ${jsonObj2.getString("Time")}")

                    var s: String = ""

                    if ((jsonObj2.getString("userTo").equals(name)
                            && jsonObj2.getString("userFrom").equals(users[i]))
                            && jsonObj2.getString("unread").equals("false")) {
                        s = jsonObj2.getString("sendRecvPair")+"~"+jsonObj2
                                .getString("Msg")+"~"+jsonObj2.getString("Time")
                        count++
                    }

                    if (count != 0) {
                        Log.d(TAG, "s: $s~$count")
                        chatsCount.put(users[i], s + "~" + count)
                    }
                    Log.d(TAG, "Inside2")
                }
                //Log.d(TAG, "Outside2\n$time")
                Log.d(TAG, "Outside2")
            }
            Log.d(TAG, "map size: ${chatsCount.size}")
        }

        return chatsCount
    }

    fun synchronousGetUsers(): ArrayList<String> {
        val call = apiInterface.fetchAllUsers()
        val resp = call.execute()
        var users = ""
        var userList = ArrayList<String>()

        if (resp.body().contentLength() > 4) {
            val jsonObj = JSONObject(resp.body().string())

            for (i in 0..(jsonObj.length()-1)) {
                /*if (users.equals("")) {
                    users = jsonObj.names().get(i) as String
                } else {
                    users += "-" + jsonObj.names().get(i) as String
                }*/
                userList.add(jsonObj.names().get(i).toString())
            }
        }
        Log.d(TAG, "names: ${userList.size}\n")

        return userList
    }

    fun synchronousGetChats(users: ArrayList<String>): MutableMap<String, String> {
        val time = ""
        val chatTimes: MutableMap<String, String> = HashMap()

        for (i in 0..(users.size-1)) {
            val call = apiInterface.ReceiveChats(users[i], name)
            val call1 = apiInterface.ReceiveChats(name, users[i])
            val resp = call.execute()
            val resp1 = call1.execute()

            if (resp.body().contentLength() > 4) {
                val jsonObj = JSONObject(resp.body().string())
                val jsonArr = jsonObj.names()
                Log.d(TAG, "users: " + users)
                Log.d(TAG, "Outside1")

                for (i in 0..(jsonArr.length() - 1)) {
                    val jsonObj1 = jsonObj.getJSONObject(jsonArr.get(i).toString())
                    Log.d(TAG, "time: ${jsonObj1.getString("Time")}")

                   /* val s = users[i] + "-" + name + "~" + jsonObj1
                            .getString("Time")*/
                    val s = jsonObj1.getString("sendRecvPair") + "~" + jsonObj1
                            .getString("Time")
                    Log.d(TAG, "s: " + s)
                    chatTimes.put(users[i], s)
                    /*if (time == "") {
                        time = users.split("-")[i] + "-" + name + "~" + jsonObj1
                                .getString("Time")
                    } else {
                        time += "_" + users.split("-")[i] + "-" + name + "~" + jsonObj1
                                .getString("Time")
                    }*/
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

                    /*val s = users[i] + "-" + name + "~" + jsonObj2
                            .getString("Time")*/
                    val s = jsonObj2.getString("sendRecvPair") + "~" + jsonObj2
                            .getString("Time")
                    Log.d(TAG, "s: " + s)
                    chatTimes.put(users[i], s)
                    /*if (time.equals("")) {
                        time = users.split("-")[i] + "-" + name + "~" + jsonObj2
                                .getString("Time")
                    } else {
                        time += "_" + users.split("-")[i] + "-" + name + "~" + jsonObj2
                                .getString("Time")
                    }*/
                    Log.d(TAG, "Inside2")
                }
                //Log.d(TAG, "Outside2\n$time")
                Log.d(TAG, "Outside2")
            }
            Log.d(TAG, "map size: ${chatTimes.size}")
        }

        return chatTimes
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

    fun getAllContacts(timeStr: MutableMap<String, String>, users: ArrayList<String>, chatCount: MutableMap<String, String>) {
        contactsList = ArrayList()
        val timeList: ArrayList<String> = ArrayList()
        val offlineList: ArrayList<String> = ArrayList()
        val chatCountsList: ArrayList<String> = ArrayList()
        Log.d(TAG, "In getAllContacts() -> $timeStr")
        for (i in 0..(users.size-1)) {
            if (!timeStr[users[i]].isNullOrBlank()) {
                timeList.add(timeStr[users[i]]!!)
            }
        }

        for (i in 0..(chatCount.size)) {
            if (chatCount[users[i]]?.split("~")?.size == 4) {
                chatCountsList.add(chatCount[users[i]]!!)
            }
        }
        Log.d(TAG, "timeList size: " + timeList.size)
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

                                    Log.d(TAG, "contacts added to realm")
                                }
                            }

                            //Log.d(TAG, "final time: $timeStr")
                            contactsAdapter = ContactsAdapter(contactsList,
                                    applicationContext, name, str, timeList,
                                    chatCountsList)
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
            for (i in 0..(users.size-1)) {
                if (!timeStr[users[i]].isNullOrBlank()) {
                    offlineList.add(timeStr[users[i]]!!)
                }
            }

            chatCountsList.add("")

            val contactsAdapter: ContactsAdapter = ContactsAdapter(contactsList,
                    applicationContext, name, str, offlineList, chatCountsList)
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
