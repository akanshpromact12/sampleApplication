package com.promact.akansh.samplefirebaserestapp

import android.content.ContentResolver
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.promact.akansh.samplefirebaserestapp.pojo.ContactsBean

class MainActivity : AppCompatActivity() {
    lateinit var contactsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        contactsRecyclerView = findViewById(R.id.contacts_recycler_view) as RecyclerView
        getAllContacts()
        loadData()
    }

    fun loadData() {

    }

    fun getAllContacts() {
        val contactsList: MutableList<ContactsBean> = ArrayList()
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
                    applicationContext)
            contactsRecyclerView.layoutManager = LinearLayoutManager(this)
            contactsRecyclerView.adapter = contactsAdapter
        }
    }
}
