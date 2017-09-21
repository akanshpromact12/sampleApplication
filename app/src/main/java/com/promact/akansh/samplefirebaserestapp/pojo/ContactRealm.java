package com.promact.akansh.samplefirebaserestapp.pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Akansh on 19-09-2017.
 */

public class ContactRealm extends RealmObject {
    @PrimaryKey
    private String contact_id;
    private String contact_name;

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }
}
