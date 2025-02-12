package ru.itmo.learner.viewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.ContextThemeWrapper;

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
import ru.itmo.learner.managers.TopicDataManager;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.model.Topic;

public class TopicViewModel extends AndroidViewModel implements CardViewModel {

    private final MutableLiveData<List<Card>> cardsLiveData;
    private MutableLiveData<Context> contextLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery;
    private final MutableLiveData<List<Card>> selectedCardsLiveData;
    private int defaultBackgroundColor;
    private int selectedBackgroundColor;
    private int topicTextColor;
    private boolean selectionMode;
    private final TopicDataManager dataManager;

    public TopicViewModel(Application application) {
        super(application);
        cardsLiveData = new MutableLiveData<>(new ArrayList<>());
        searchQuery = new MutableLiveData<>("");
        selectedCardsLiveData = new MutableLiveData<>(new ArrayList<>());
        selectionMode = false;
        this.dataManager = new TopicDataManager(application);
    }

    public void setContext(Context context) {
        contextLiveData.setValue(context);
        initColors();
        cardsLiveData.setValue(dataManager.load());
    }

    public LiveData<Context> getContextLiveData() {
        return contextLiveData;
    }

    public void initColors() {

        Context context = contextLiveData.getValue();
        Context themedContext = new ContextThemeWrapper(context, R.style.Theme_Learner);

        TypedArray a = themedContext.obtainStyledAttributes(new int[]{
                R.attr.cardsBackgroundColor,
                R.attr.cardsBackgroundColorSelected,
                R.attr.cardsTextColor
        });

        defaultBackgroundColor = a.getColor(0, -3);
        selectedBackgroundColor = a.getColor(1, -3);
        topicTextColor = a.getColor(2, -3);

        a.recycle();
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
            dataManager.save(current);
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

                if (card.isSelected()) {
                    selected.add(card);
                }
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

    public List<Card> getAllCards() {
        return cardsLiveData.getValue();
    }

    public MutableLiveData<List<Card>> getCardsLiveData() {
        return cardsLiveData;
    }

    private void updateSelectedCards() {
        List<Card> selected = new ArrayList<>();

        if (cardsLiveData.getValue() != null) {

            for (Card card : cardsLiveData.getValue()) {

                if (card.isSelected()) {
                    selected.add(card);
                }
            }
        }

        selectedCardsLiveData.setValue(selected);
    }

    @Override
    public void deleteSelectedCards() {

        List<Card> allCards = cardsLiveData.getValue();

        if (allCards != null) {
            List<Card> cardsToDelete = new ArrayList<>();

            for (Card card : allCards) {

                if (card.isSelected()) {
                    cardsToDelete.add(card);
                }
            }

            dataManager.delete(cardsToDelete);
            allCards.removeAll(cardsToDelete);
            cardsLiveData.setValue(allCards);
            selectionMode = false;
            updateSelectedCards();
        }
    }

    @Override
    public int getDefaultBackgroundColor() { return defaultBackgroundColor; }

    @Override
    public int getSelectedBackgroundColor() { return selectedBackgroundColor; }

    @Override
    public int getTopicTextColor() { return topicTextColor; }
}
