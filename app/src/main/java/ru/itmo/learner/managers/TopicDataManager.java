package ru.itmo.learner.managers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.itmo.learner.activities.MainActivity;
import ru.itmo.learner.activities.QuestionActivity;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.model.CardDataPrefs;
import ru.itmo.learner.model.Question;
import ru.itmo.learner.model.Topic;

public class TopicDataManager implements CardDataManager {

    CardDataPrefs cardDataPrefs;
    Application application;

    public TopicDataManager(Application application) {
        SharedPreferences prefs = application.getSharedPreferences("TopicPrefs", Context.MODE_PRIVATE);
        String key = "topics";
        this.cardDataPrefs = new CardDataPrefs(prefs, key);
        this.application = application;
    }

    @Override
    public void save(List<Card> cards) {
        SharedPreferences prefs = cardDataPrefs.getPrefs();
        SharedPreferences.Editor editor = prefs.edit();
        cards.forEach(card -> {
            editor.putString(cardDataPrefs.getKey(card.getTitle()), card.getTitle());
            editor.apply();
        });
    }

    @Override
    public void delete(List<Card> cards) {
        cards.forEach(card -> {
            QuestionDataManager questionDataManager = new QuestionDataManager(application, card.getTitle());
            questionDataManager.deleteAll();
            cardDataPrefs.getPrefs().edit().remove(cardDataPrefs.getKey(card.getTitle())).apply();
        });
    }

    @Override
    public List<Card> load() {
        List<Card> list = new ArrayList<>();

        Map<String, ?> dataMap = cardDataPrefs.getPrefs().getAll();
        for (Map.Entry<String, ?> data : dataMap.entrySet()) {
            list.add(new Topic((String) data.getValue()));
        }
        return list;

    }
}
