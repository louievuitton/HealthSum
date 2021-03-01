package com.stevenlouie.healthsum;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Utils {

    private static Utils instance;
    private static FirebaseAuth auth;
    private static DatabaseReference database;

    private Utils() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }

        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
    }

    public static synchronized Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }

        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getDatabase() {
        return database;
    }
}
