package ru.itmo.learner.viewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ru.itmo.learner.R;
import ru.itmo.learner.activities.QuestionActivity;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.model.Question;

public class QuestionsViewModel extends AndroidViewModel implements CardViewModel {

    private final MutableLiveData<List<Card>> cardsLiveData;
    private final MutableLiveData<String> searchQuery;
    private final MutableLiveData<List<Card>> selectedCardsLiveData;
    private MutableLiveData<Context> contextLiveData = new MutableLiveData<>();
    private int defaultBackgroundColor;
    private int selectedBackgroundColor;
    private int topicTextColor;
    private boolean selectionMode;

    public QuestionsViewModel(Application application) {
        super(application);
        cardsLiveData = new MutableLiveData<>(new ArrayList<>());
        searchQuery = new MutableLiveData<>("");
        selectedCardsLiveData = new MutableLiveData<>(new ArrayList<>());
        selectionMode = false;
    }

    public void setContext(Context context) {
        Log.d("SharedViewModel", "Контекст в ViewModel: " + context);
        contextLiveData.setValue(context);
        initColors();
        loadCards();
    }

    public List<Card> getAllCards() {
        return cardsLiveData.getValue();
    }

    private void initColors() {
        TypedValue value = new TypedValue();
        Context context = contextLiveData.getValue();
        context.getTheme().resolveAttribute(R.attr.cardsBackgroundColor, value, true);
        defaultBackgroundColor = value.data;
        context.getTheme().resolveAttribute(R.attr.cardsBackgroundColorSelected, value, true);
        selectedBackgroundColor = value.data;
        context.getTheme().resolveAttribute(R.attr.cardsTextColor, value, true);
        topicTextColor = value.data;
    }

    private void loadCards() {

        SharedPreferences prefs = getApplication().getSharedPreferences("QuestionPrefs-" + QuestionActivity.topicName, Context.MODE_PRIVATE);
        String json = prefs.getString("questions-" + QuestionActivity.topicName, null);
        List<Card> list = new ArrayList<>();

        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    String title = array.getString(i);
                    list.add(new Question(title, defaultBackgroundColor, topicTextColor));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cardsLiveData.setValue(list);
    }

    private void saveCards() {

        SharedPreferences prefs = getApplication().getSharedPreferences("QuestionPrefs-" + QuestionActivity.topicName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        List<Card> list = cardsLiveData.getValue();

        if (list != null) {
            JSONArray array = new JSONArray();

            for (Card card : list) {
                array.put(card.getTitle());
            }
            editor.putString("questions-" + QuestionActivity.topicName, array.toString());
            editor.apply();
        }
    }

    @Override
    public LiveData<List<Card>> getFilteredCards() {

        return Transformations.map(searchQuery, query -> {

            List<Card> allCards = cardsLiveData.getValue();

            if (allCards == null) return new ArrayList<>();

            if (query == null || query.isEmpty()) return new ArrayList<>(allCards);
            List<Card> filtered = new ArrayList<>();

            for (Card card : allCards) {
                if (card.getTitle().toLowerCase().startsWith(query.toLowerCase()))
                    filtered.add(card);
            }

            return filtered;
        });
    }

    @Override
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    @Override
    public void addCard(Card card) {
        List<Card> current = cardsLiveData.getValue();

        if (current != null) {
            current.add(card);
            cardsLiveData.setValue(current);
            saveCards();
        }
    }

    @Override
    public void toggleCardSelection(Card card) {
        card.setSelected(!card.isSelected());
        updateSelectionMode();
        updateSelectedCards();

        cardsLiveData.setValue(cardsLiveData.getValue());
    }

    private void updateSelectionMode() {
        List<Card> selected = new ArrayList<>();

        if (cardsLiveData.getValue() != null) {

            for (Card card : cardsLiveData.getValue()) {
                if (card.isSelected()) selected.add(card);
            }
        }
        selectionMode = !selected.isEmpty();
    }

    @Override
    public boolean isSelectionMode() {
        return selectionMode;
    }

    @Override
    public LiveData<List<Card>> getSelectedCards() {
        return selectedCardsLiveData;
    }

    private void updateSelectedCards() {
        List<Card> selected = new ArrayList<>();

        if (cardsLiveData.getValue() != null) {

            for (Card card : cardsLiveData.getValue()) {

                if (card.isSelected()) selected.add(card);
            }
        }
        selectedCardsLiveData.setValue(selected);
    }

    @Override
    public void deleteSelectedCards() {
        List<Card> allCards = cardsLiveData.getValue();

        if (allCards != null) {
            List<Card> remaining = new ArrayList<>();

            for (Card card : allCards) {

                if (!card.isSelected()) {
                    remaining.add(card);
                } else {
                    deleteCardData(card.getTitle(), card);
                }
            }

            cardsLiveData.setValue(remaining);
            selectionMode = false;
            updateSelectedCards();
            saveCards();
        }
    }

    private void deleteCardData(String cardTitle, Card card) {
        SharedPreferences answerPrefs = getApplication().getSharedPreferences("AnswerPrefs-" + cardTitle, Context.MODE_PRIVATE);
        answerPrefs.edit().clear().apply();
    }

    public int getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    public int getSelectedBackgroundColor() {
        return selectedBackgroundColor;
    }

    public int getTopicTextColor() {
        return topicTextColor;
    }
}
