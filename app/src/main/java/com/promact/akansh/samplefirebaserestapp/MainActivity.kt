package com.promact.akansh.samplefirebaserestapp

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.promact.akansh.samplefirebaserestapp.pojo.ContactsBean

class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name: TextInputEditText = findViewById(R.id.txtBoxUname) as TextInputEditText
        val signIn: Button = findViewById(R.id.btnSignIn) as Button

        //signIn.isEnabled = name.text.toString() != ""

        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //Do something
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Do something
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signIn.isEnabled = true
            }
        })

        signIn.setOnClickListener {
            Log.d(TAG, "name (as logged in): ${name.text}")

            val intent: Intent = Intent(applicationContext, ChatMainActivity::class.java)
            intent.putExtra("name", name.text.toString())
            startActivity(intent)
        }
    }
}
