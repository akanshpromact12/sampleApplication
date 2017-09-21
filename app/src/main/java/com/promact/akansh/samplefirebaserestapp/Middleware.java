package com.promact.akansh.samplefirebaserestapp;

import android.util.Log;

import com.promact.akansh.samplefirebaserestapp.pojo.Chats;
import com.promact.akansh.samplefirebaserestapp.pojo.ChatsRealm;
import com.promact.akansh.samplefirebaserestapp.pojo.ContactRealm;
import com.promact.akansh.samplefirebaserestapp.pojo.Users;
import com.promact.akansh.samplefirebaserestapp.pojo.UsersRealm;

import org.json.JSONObject;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Akansh on 16-09-2017.
 */

public class Middleware {
    public Realm realm = Realm.getDefaultInstance();
    private String str1 = "";
    private String str2 = "";
    private int size = 0;
    private Boolean userExists = false;
    private APIInterface apiInterface;
    private String str = "";

    void addChats(ChatsRealm chats) {

        realm.beginTransaction();
        ChatsRealm cr = realm.createObject(ChatsRealm.class, UUID.randomUUID().toString());
        cr.setUserFrom(chats.getUserFrom());
        cr.setUserTo(chats.getUserTo());
        cr.setMsg(chats.getMsg());
        cr.setTime(chats.getTime());
        cr.setNetAvailable(chats.getNetAvailable());

        realm.commitTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                                .findAll();

                for (ChatsRealm obj : realmResults) {
                    Log.d(TAG, "execute: "+obj.getMsg());
                }

            }
        });
    }

    void addUsers(UsersRealm usersRealm) {
        realm.beginTransaction();
        UsersRealm users = realm.createObject(UsersRealm.class,
                UUID.randomUUID().toString());
        users.setUserName(usersRealm.getUserName());

        realm.commitTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UsersRealm> realmResults = realm.where(UsersRealm.class)
                        .findAll();

                for (UsersRealm usr : realmResults) {
                    Log.d(TAG, "execute users: " + usr + "\nUsers added.");
                }
            }
        });
    }

    void addContacts(ContactRealm contacts) {
        realm.beginTransaction();
        ContactRealm contactRealm = realm.createObject(ContactRealm.class, UUID
                .randomUUID().toString());
        contactRealm.setContact_name(contacts.getContact_name());

        realm.commitTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ContactRealm> realmResults = realm
                        .where(ContactRealm.class).findAll();

                for (ContactRealm obj : realmResults) {
                    Log.d(TAG, "execute contacts: " + obj.getContact_name());
                }
            }
        });
    }

    void searchFinallyUploadedMessages() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                        .equalTo("netAvailable", true).findAll();
            }
        });
    }

    int checkChatRealmSize() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                        .findAll();

                size = realmResults.size();
            }
        });

        return size;
    }

    int checkContactsRealmSize() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UsersRealm> realmResults = realm
                        .where(UsersRealm.class).findAll();

                size = realmResults.size();
            }
        });

        return size;
    }

    String searchAllContacts() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ContactRealm> realmResults = realm
                        .where(ContactRealm.class).findAll();
                Log.d(TAG, "realm contacts size: " + realmResults.size());

                for (ContactRealm cr : realmResults) {
                    if (str2.equals("")) {
                        str2 = cr.getContact_name();
                    } else {
                        str2 += ":" + cr.getContact_name();
                    }
                }
            }
        });

        return str2;
    }

    String searchAllUsers() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UsersRealm> realmResults = realm
                        .where(UsersRealm.class).findAll();
                Log.d(TAG, "realm contacts size: " + realmResults.size());

                for (UsersRealm cr : realmResults) {
                    if (str2.equals("")) {
                        str2 = cr.getUserName();
                    } else {
                        str2 += ":" + cr.getUserName();
                    }
                }
            }
        });

        return str2;
    }

    String searchUnuploadedMessages() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> checkSize = realm.where(ChatsRealm.class)
                        .findAll();
                if (!checkSize.isEmpty()) {
                    RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                            .equalTo("netAvailable", true).findAll();
                    Log.d(TAG, "realm results size: " + realmResults.size());

                    for (ChatsRealm str : realmResults) {
                        Chats chats = new Chats(str.getUserFrom(), str.getUserTo(),
                                str.getMsg(), str.getTime());

                        /*Call<ChatsRealm>*/
                    }
                } else {
                    str1 = "EMPTY";
                }
            }
        });

        return str1;
    }

    Boolean checkIfUserExists(final String userName) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UsersRealm> checkUserExists = realm.where(UsersRealm.class)
                        .equalTo("userName", userName).findAll();

                if (checkUserExists.size() == 0) {
                    userExists = false;
                } else {
                    userExists = true;
                }
            }
        });

        return userExists;
    }

    String receiveMessages(final String receiver) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> checkSize = realm.where(ChatsRealm.class)
                        .equalTo("userTo", receiver).or()
                        .equalTo("userFrom", receiver).findAll();
                if (!checkSize.isEmpty()) {
                    RealmResults<ChatsRealm> realmResults = realm
                            .where(ChatsRealm.class).findAll();
                    Log.d(TAG, "realm results size: " + realmResults.size());

                    for (ChatsRealm str : realmResults) {
                        Log.d(TAG, "msgs: " + str);
                        if (str1.equals("")) {
                            str1 = str.getMsg();
                        } else {
                            str1 += "-" + str.getMsg();
                        }
                    }
                } else {
                    str1 = "EMPTY";
                }
            }
        });

        return str1;
    }

    String checkNames() {
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<ResponseBody> call = apiInterface.fetchAllContactNames();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "contacts response code");

                try {
                    if (response.body().contentLength() > 4) {
                        JSONObject obj = new JSONObject(response.body().string());

                        for (int i=0; i<obj.length(); i++) {
                            if (str.equals("")) {
                                str = obj.names().get(i).toString();
                            } else {
                                str += " - " + obj.names().get(i).toString();
                            }
                        }
                        Log.d(TAG, "contact names: " + str);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return str;
    }

    void uploadChats() {
        apiInterface = APIClient.getClient().create(APIInterface.class);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                        .equalTo("netAvailable", false).findAll();

                for (ChatsRealm chatsRealm : realmResults) {
                    for (String cr : checkNames().split("-")) {
                        if (cr.trim().equals(chatsRealm.getUserFrom()+"-"))
                    }
                }
            }
        });
    }
}
