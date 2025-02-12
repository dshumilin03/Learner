package ru.itmo.learner.model;

import android.content.SharedPreferences;

public class CardDataPrefs {
    private final String key;
    private final SharedPreferences prefs;

    public CardDataPrefs(SharedPreferences prefs, String stringKey) {
        this.prefs = prefs;
        this.key = stringKey;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public String getPrefsDataByKey(String uniqueKey) {
        return prefs.getString(key + uniqueKey, "");
    }
    public String getPrefsDataByKey() {
        return prefs.getString(key, "");
    }

    public String getKey() {
        return key;
    }

    public String getKey(String uniqueKey) {
        return key + uniqueKey;
    }
}
