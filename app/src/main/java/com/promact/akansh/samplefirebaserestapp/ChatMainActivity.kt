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
import com.promact.akansh.samplefirebaserestapp.pojo.ContactsBean
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatMainActivity : AppCompatActivity() {
    lateinit var contactsRecyclerView: RecyclerView
    lateinit var apiInterface: APIInterface
    lateinit var name: String
    val TAG: String = "ChatMainActivity"
    var count: Int = 0;
    var str: String = ""
    lateinit var contactsList: MutableList<ContactsBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        apiInterface = APIClient.getClient().create(APIInterface::class.java)

        val intent: Intent = intent
        name = intent.getStringExtra("name")

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.sender_name) + name

        contactsRecyclerView = findViewById(R.id.contacts_recycler_view) as RecyclerView
        getAllContacts()
        getMessageCount(name, contactsList);
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
        var contacts: ContactsBean

        val contentResolver: ContentResolver = contentResolver
        val cursor: Cursor = contentResolver.query(ContactsContract
                .Contacts.CONTENT_URI, null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " ASC")

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val hasPhoneNumber: Int = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt()

                if (hasPhoneNumber > 0) {
                    val id: String = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID))
                    val name: String = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    contacts = ContactsBean()
                    contacts.contactName = name

                    val phoneCursor: Cursor = contentResolver.query(ContactsContract
                            .CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                            arrayOf(id), null)

                    phoneCursor.close()

                    val emailCursor: Cursor = contentResolver.query(ContactsContract
                            .CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                            arrayOf(id), null)
                    while (emailCursor.moveToNext()) {
                        val emailId: String = emailCursor.getString(emailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds
                                        .Email.DATA))
                    }
                    contactsList.add(contacts)
                }
            }



            val contactsAdapter: ContactsAdapter = ContactsAdapter(contactsList,
                    applicationContext, name, str)
            contactsRecyclerView.layoutManager = LinearLayoutManager(this)
            contactsRecyclerView.adapter = contactsAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent: Intent = Intent(Intent.ACTION_MAIN)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("EXIT", true)

        startActivity(intent)
    }
}
