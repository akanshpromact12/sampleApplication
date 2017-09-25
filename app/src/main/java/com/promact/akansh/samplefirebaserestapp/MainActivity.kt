package com.promact.akansh.samplefirebaserestapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import com.promact.akansh.samplefirebaserestapp.pojo.Users
import com.promact.akansh.samplefirebaserestapp.pojo.UsersRealm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {
    val TAG: String = "MainActivity"
    lateinit var middleware: Middleware
    lateinit var apiInterface: APIInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SaveSharedPrefs.getName(this@MainActivity).isEmpty()) {
            val name: TextInputEditText = findViewById(R.id.txtBoxUname) as TextInputEditText
            val signIn: Button = findViewById(R.id.btnSignIn) as Button
            middleware = Middleware()
            apiInterface = APIClient.getClient().create(APIInterface::class.java)

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
                val users = Users(name.text.toString())

                if (NetworkStatus.isNetworkAvailable(applicationContext)) {
                    //val num = random.nextInt(1081) + 2000
                    val call: Call<Users> = apiInterface
                            .registerUsers(name.text.toString(), "")
                    call.enqueue(object : Callback<Users> {
                        override fun onResponse(call: Call<Users>?, response: Response<Users>?) {
                            Log.d(TAG, "response code: " + response?.code())

                            Log.d(TAG, "username: ${users.userName}")
                        }

                        override fun onFailure(call: Call<Users>?, t: Throwable?) {

                        }
                    })
                } else {
                    if (middleware.checkIfUserExists(name.text.toString()) == false) {
                        val userRealm: UsersRealm = UsersRealm()

                        userRealm.userName = name.text.toString()
                        middleware.addUsers(userRealm)
                    }
                }

                if (!SaveSharedPrefs.getName(applicationContext).isEmpty()) {
                    SaveSharedPrefs.clearAllPrefs(applicationContext)
                }
                SaveSharedPrefs.setPrefs(applicationContext, name.text.toString())
                val intent: Intent = Intent(applicationContext, ChatMainActivity::class.java)
                intent.putExtra("name", name.text.toString())
                startActivity(intent)
            }
        } else {
            val intent = Intent(applicationContext, ChatMainActivity::class.java)
            startActivity(intent)
        }

        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }
    }
}
