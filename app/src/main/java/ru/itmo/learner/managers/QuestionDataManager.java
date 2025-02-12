package ru.itmo.learner.managers;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.itmo.learner.activities.QuestionActivity;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.model.CardDataPrefs;
import ru.itmo.learner.model.Question;

public class QuestionDataManager implements CardDataManager {
    CardDataPrefs questionDataPrefs;
    CardDataPrefs answerDataPrefs;
    Application application;

    public QuestionDataManager(Application application) {
        SharedPreferences prefs = application.getSharedPreferences("QuestionPrefs-" + QuestionActivity.topicName, Context.MODE_PRIVATE);
        SharedPreferences answerPrefs = application.getSharedPreferences("AnswerPrefs-" + QuestionActivity.topicName, Context.MODE_PRIVATE);

        String questionsKey = "questions-" + QuestionActivity.topicName + "-";
        String answerPrefixKey ="answers-" + QuestionActivity.topicName + "-";

        this.answerDataPrefs = new CardDataPrefs(answerPrefs, answerPrefixKey);
        this.questionDataPrefs = new CardDataPrefs(prefs, questionsKey);
        this.application = application;
    }

    public QuestionDataManager(Application application, String topicName) {
        SharedPreferences prefs = application.getSharedPreferences("QuestionPrefs-" + topicName, Context.MODE_PRIVATE);
        SharedPreferences answerPrefs = application.getSharedPreferences("AnswerPrefs-" + QuestionActivity.topicName, Context.MODE_PRIVATE);


        String questionsKey = "questions-" + topicName + "-";
        String answerPrefixKey ="answers-" + QuestionActivity.topicName + "-";

        this.answerDataPrefs = new CardDataPrefs(answerPrefs, answerPrefixKey);
        this.questionDataPrefs = new CardDataPrefs(prefs, questionsKey);
        this.application = application;
    }

    @Override
    public void save(List<Card> cards) {
        SharedPreferences prefs = questionDataPrefs.getPrefs();
        SharedPreferences.Editor editor = prefs.edit();
        cards.forEach(card -> {
            editor.putString(questionDataPrefs.getKey(card.getTitle()), card.getTitle());
            editor.apply();
        });
    }

    @Override
    public void delete(List<Card> cards) {
        cards.forEach(card -> {
            answerDataPrefs.getPrefs().edit().remove(answerDataPrefs.getKey(card.getTitle())).apply();
            questionDataPrefs.getPrefs().edit().remove(questionDataPrefs.getKey(card.getTitle())).apply();
        });
    }

    @Override
    public List<Card> load() {
        List<Card> list = new ArrayList<>();

        Map<String, ?> dataMap = questionDataPrefs.getPrefs().getAll();
        for (Map.Entry<String, ?> data : dataMap.entrySet()) {
            list.add(new Question((String) data.getValue()));
        }
        return list;
    }

    public void deleteAll() {
        delete(load());
    }

    public String loadAnswerForQuestion(String questionTitle) {
        return answerDataPrefs.getPrefsDataByKey(questionTitle);
    }

    public void saveAnswerForQuestion(String questionTitle, String answer) {
        SharedPreferences prefs = application.getApplicationContext()
                .getSharedPreferences("AnswerPrefs-" + QuestionActivity.topicName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(answerDataPrefs.getKey(questionTitle), answer);
        editor.apply();
    }
}
