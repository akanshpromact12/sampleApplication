package com.promact.akansh.samplefirebaserestapp;

import android.util.Log;

import com.promact.akansh.samplefirebaserestapp.pojo.Chats;
import com.promact.akansh.samplefirebaserestapp.pojo.ChatsRealm;
import com.promact.akansh.samplefirebaserestapp.pojo.ContactRealm;
import com.promact.akansh.samplefirebaserestapp.pojo.Users;
import com.promact.akansh.samplefirebaserestapp.pojo.UsersRealm;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
    private String s="";
    private Boolean chatExists = false;
    private Random random = new Random();
    private String time = "";

    void addChats(ChatsRealm chats) {

        realm.beginTransaction();
        ChatsRealm cr = realm.createObject(ChatsRealm.class, UUID.randomUUID().toString());
        cr.setUserFrom(chats.getUserFrom());
        cr.setUserTo(chats.getUserTo());
        cr.setMsg(chats.getMsg());
        cr.setTime(chats.getTime());
        cr.setNetAvailable(chats.getNetAvailable());
        cr.setUploadCombo(chats.getUploadCombo());

        realm.commitTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                                .equalTo("netAvailable", true).findAll();
                Log.d(TAG, "chats size: " + realmResults.size());

                for (ChatsRealm obj : realmResults) {
                    Log.d(TAG, "execute: "+obj.getMsg());
                }

                //uploadChats();
                Log.d(TAG, "chats to upload -> size: " + realmResults.size());
                Log.d(TAG, "-----------Begin-----------");

                for (final ChatsRealm chatsRealm : realmResults) {
                    Log.d(TAG, "messages that haven't been delivered yet: " +
                            chatsRealm.getMsg() + " user from : " + chatsRealm
                            .getUserFrom() + " userTo: " + chatsRealm.getUserTo()
                            + "user String: " + chatsRealm.getUploadCombo());
                    s = "messages that haven't been delivered yet: " +
                            chatsRealm.getMsg() + " user from : " + chatsRealm
                            .getUserFrom() + " userTo: " + chatsRealm.getUserTo()
                            + "user String: " + chatsRealm.getUploadCombo();
                }

                Log.d(TAG, "------------End------------");
            }
        });
    }

    void getNumberOfChats(final String loggedInUser) {
        final Map<String, String> users = new HashMap<>();
        final Map<String, ChatsRealm> chats = new HashMap<>();

        Log.d(TAG, "Inside getNumberOfChats");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UsersRealm> usersList = realm.where(UsersRealm.class)
                        .findAll();

                for (UsersRealm usersRealm : usersList) {
                    users.put(usersRealm.getUserName(), "");
                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> chatsList = realm.where(ChatsRealm.class)
                        .findAll();

                for (String key : users.keySet()) {
                    for (ChatsRealm chatsRealm : chatsList) {
                        if ((chatsRealm.getUserTo().equals(loggedInUser) || chatsRealm
                                .getUserFrom().equals(key)) && (chatsRealm
                                .getUserTo().equals(key) || chatsRealm
                                .getUserFrom().equals(loggedInUser))) {
                            Log.d(TAG, "chats: " + chatsRealm.getMsg());
                            chats.put(key, chatsRealm);
                        }
                    }
                }
            }
        });
        Log.d(TAG, "Inside getNumberOfChats2");
    }

    Boolean checkIfChatExists(final String userFrom, final String userTo, final String msg, final String Time) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                        .findAll();

                for (ChatsRealm chatsRealm : realmResults) {
                    if (chatsRealm.getUserFrom().equals(userFrom) &&
                            chatsRealm.getUserTo().equals(userTo) &&
                            chatsRealm.getMsg().equals(msg) &&
                            chatsRealm.getTime().equals(Time)) {
                        chatExists = true;
                    }
                }
            }
        });

        return chatExists;
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

    int checkUserSize() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UsersRealm> usersRealm = realm.where(UsersRealm.class)
                        .findAll();

                size = usersRealm.size();
            }
        });

        return size;
    }

    int searchContactSize() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ContactRealm> realmResults = realm.where(ContactRealm.class)
                        .findAll();
                Log.d(TAG, "Total contacts: " + realmResults);
                size = realmResults.size();
            }
        });

        return size;
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
                        .equalTo("netAvailable", true).findAll();

                Log.d(TAG, "Realm chats size: " + realmResults.size());
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
                            str1 = str.getMsg() +","+ str.getTime();
                        } else {
                            str1 += "-" + str.getMsg() +","+ str.getTime();
                        }
                    }
                } else {
                    str1 = "EMPTY";
                }
            }
        });

        return str1;
    }

    String checkTime() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                /*RealmResults<ChatsRealm> realmResults = realm
                        .where(ChatsRealm.class).findAll();*/

                /*for (ChatsRealm cr : realmResults) {
                    if (time.equals("")) {
                        time = cr.getTime();
                    } else {
                        time += "-"+cr.getTime();
                    }
                }*/
                time = realm.where(ChatsRealm.class).findFirst().getTime();
            }
        });

        return time;
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
                                str += ":" + obj.names().get(i).toString();
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
        final int num = (random.nextInt(1081) + 2000);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(final Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                        .equalTo("netAvailable", false).findAll();

                Log.d(TAG, "chats to upload -> size: " + realmResults.size());

                for (final ChatsRealm chatsRealm : realmResults) {
                    Log.d(TAG, "-----------Begin unuploaded-----------");
                    Log.d(TAG, "messages that haven't been delivered yet: " +
                            chatsRealm.getMsg() + " user from : " + chatsRealm
                            .getUserFrom() + " userTo: " + chatsRealm
                            .getUserTo() + "user group: " + chatsRealm
                            .getUploadCombo());
                    s = "messages that haven't been delivered yet: " +
                            chatsRealm.getMsg() + " user from : " + chatsRealm
                            .getUserFrom() + " userTo: " + chatsRealm
                            .getUserTo() + " user group: " + chatsRealm
                            .getUploadCombo();

                    Chats chats = new Chats(chatsRealm.getUserFrom(),
                            chatsRealm.getUserTo(), chatsRealm.getMsg(),
                            chatsRealm.getTime(), chatsRealm
                            .getUploadCombo(), "true");
                    Call<Chats> call = apiInterface.registerChat(chatsRealm
                            .getUploadCombo().split("-")[0], chatsRealm
                                    .getUploadCombo().split("-")[1], num+"",
                            chats);
                    call.enqueue(new retrofit2.Callback<Chats>() {
                        @Override
                        public void onResponse(Call<Chats> call, Response<Chats> response) {
                            Log.d(TAG, "response code: " + response.code());
                            String displayRespUser;

                            Chats chats = response.body();

                            String userFrom = chats.userFrom;
                            String userTo = chats.userTo;
                            String Msg = chats.Msg;
                            String Time = chats.Time;

                            displayRespUser = "UserFromName: " + userFrom +
                                    " UserTo: " + userTo + " Msg: " + Msg +
                                    " Time: " + Time;

                            Log.d(TAG, displayRespUser);

                            Log.d(TAG, "Data successfully uploaded");
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    chatsRealm.setNetAvailable(true);
                                    realm.insertOrUpdate(chatsRealm);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Chats> call, Throwable t) {

                        }
                    });

                    Log.d(TAG, "------------End unuploaded------------");
                }
            }
        });
    }
}
