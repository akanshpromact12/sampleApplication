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
import com.promact.akansh.samplefirebaserestapp.pojo.Users
import com.promact.akansh.samplefirebaserestapp.pojo.UsersRealm
import io.realm.Realm
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : BaseActivity() {
    val TAG: String = "MainActivity";
    lateinit var middleware: Middleware
    lateinit var apiInterface: APIInterface
    val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                if (middleware.checkUserSize() >= 0 && middleware
                        .checkIfUserExists(name.text.toString()) == false) {
                    val userRealm: UsersRealm = UsersRealm()

                    userRealm.userName = name.text.toString()
                    middleware.addUsers(userRealm)
                }

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
                /*val userSize: Call<ResponseBody> = apiInterface.fetchAllUsers()
                userSize.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                        try {
                            if (response?.body()?.contentLength()!! > 4) {
                                val jsonObj: JSONObject = JSONObject(response.body()?.string())
                                val jsonArr: JSONArray = jsonObj.names()

                                var checkUsers = false
                                for (i in 0..(jsonArr.length()-1)) {
                                    val jsonObj1: JSONObject = jsonObj
                                            .getJSONObject(jsonArr.get(i).toString())
                                    val user: String = jsonObj1.getString("user")

                                    if (user == name.text.toString()) {
                                        checkUsers = false
                                        Log.d(TAG, "false")
                                    } else {
                                        checkUsers = true
                                        Log.d(TAG, "true")
                                    }
                                }

                                Log.d(TAG, "users Check -> " + checkUsers)
                                if (checkUsers) {
                                    val call: Call<Users> = apiInterface
                                            .registerUsers(name.text.toString())
                                    call.enqueue(object : Callback<Users> {
                                        override fun onResponse(call: Call<Users>?, response: Response<Users>?) {
                                            Log.d(TAG, "response code: " + response?.code())

                                            Log.d(TAG, "username: ${users.userName}")
                                        }

                                        override fun onFailure(call: Call<Users>?, t: Throwable?) {

                                        }
                                    })
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                    }
                })*/
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
            SaveSharedPrefs.setPrefs(applicationContext, name.text.toString());
            val intent: Intent = Intent(applicationContext, ChatMainActivity::class.java)
            intent.putExtra("name", name.text.toString())
            startActivity(intent)
        }
    }
}
