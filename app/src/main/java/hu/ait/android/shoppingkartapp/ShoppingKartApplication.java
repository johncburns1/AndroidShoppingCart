package hu.ait.android.shoppingkartapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by johnc on 11/7/2017.
 */

public class ShoppingKartApplication extends Application {

    private Realm realmKart;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realmKart = Realm.getInstance(config);
    }

    public void closeRealm() {
        realmKart.close();
    }

    public Realm getRealmKart() {
        return realmKart;
    }
}
