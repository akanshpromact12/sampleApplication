package com.promact.akansh.samplefirebaserestapp

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.promact.akansh.samplefirebaserestapp.pojo.ContactRealm
import com.promact.akansh.samplefirebaserestapp.pojo.ContactsBean
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ChatMainActivity : AppCompatActivity() {
    lateinit var contactsRecyclerView: RecyclerView
    lateinit var apiInterface: APIInterface
    lateinit var name: String
    val TAG: String = "ChatMainActivity"
    var count: Int = 0;
    var str: String = ""
    lateinit var contactsList: MutableList<String>
    lateinit var contactRealm: ContactRealm
    lateinit var middleware: Middleware

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        contactRealm = ContactRealm()
        middleware = Middleware()

        val intent: Intent = intent
        if (SaveSharedPrefs.getName(this@ChatMainActivity).isEmpty()) {
            name = intent.getStringExtra("name")
        } else {
            name = SaveSharedPrefs.getName(this@ChatMainActivity);
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.sender_name) + name

        contactsRecyclerView = findViewById(R.id.contacts_recycler_view) as RecyclerView
        getAllContacts()
    }

    fun getMessageCount(name: String, contactsList: MutableList<ContactsBean>) {
        for (contacts in contactsList) {
            val call = apiInterface.ReceiveChats(name, contacts.contactName)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d(TAG, "response code: " + response.code())

                    try {
                        if (response.body().contentLength() > 4) {
                            val jsonObject = JSONObject(response.body().string())
                            val jsonArray = jsonObject.names()
                            var sender: String
                            var user: String

                            for (i in 0..jsonArray.length() - 1) {
                                val jsonObject1 = jsonObject.getJSONObject(jsonArray.get(i).toString())
                                user = jsonObject1.getString("userTo")
                                sender = jsonObject1.getString("userFrom")

                                if (contacts.contactName == user) {
                                    count++
                                }
                            }

                            Log.d(TAG, "count: " + count)
                            str += contacts.contactName + count
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }
            })
        }
    }

    fun getAllContacts() {
        contactsList = ArrayList()

        if (NetworkStatus.isNetworkAvailable(applicationContext)) {
            val call: Call<ResponseBody> = apiInterface.fetchAllUsers()
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    try {
                        if (response?.body()?.contentLength()!! > 4) {
                            val jsonObj: JSONObject = JSONObject(response.body()
                                    .string())
                            val contactRealmSizeCheck = middleware
                                    .checkContactsRealmSize()

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
            val contactRealmSizeCheck = middleware
                    .checkContactsRealmSize()
            if (contactRealmSizeCheck != 0) {
                val contactNames: String = middleware.searchAllUsers()
                for (users in contactNames.split(":")) {
                    Log.d(TAG, "users: ${users.trim()}\nName: $name");
                    if (users.trim() != name) {
                        contactsList.add(users)
                    }
                }
            }

            val contactsAdapter: ContactsAdapter = ContactsAdapter(contactsList,
                    applicationContext, name, str)
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
            SaveSharedPrefs.clearAllPrefs(applicationContext)
            val intent = Intent(this@ChatMainActivity, MainActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent: Intent = Intent(Intent.ACTION_MAIN)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("EXIT", true)

        startActivity(intent)
    }
}
