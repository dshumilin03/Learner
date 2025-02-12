package ru.itmo.learner.viewModels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import ru.itmo.learner.R;
import ru.itmo.learner.managers.QuestionDataManager;
import ru.itmo.learner.model.Card;

public class QuestionsViewModel extends AndroidViewModel implements CardViewModel {

    private final MutableLiveData<List<Card>> cardsLiveData;
    private final MutableLiveData<String> searchQuery;
    private final MutableLiveData<List<Card>> selectedCardsLiveData;
    private MutableLiveData<Context> contextLiveData = new MutableLiveData<>();
    private int defaultBackgroundColor;
    private int selectedBackgroundColor;
    private int topicTextColor;
    private boolean selectionMode;
    private final QuestionDataManager dataManager;

    public QuestionsViewModel(Application application) {
        super(application);
        cardsLiveData = new MutableLiveData<>(new ArrayList<>());
        searchQuery = new MutableLiveData<>("");
        selectedCardsLiveData = new MutableLiveData<>(new ArrayList<>());
        selectionMode = false;
        dataManager = new QuestionDataManager(application);
    }

    public void init(Context context) {
        contextLiveData.setValue(context);
        initColors();
        cardsLiveData.setValue(dataManager.load());
    }

    public List<Card> getAllCards() {
        return cardsLiveData.getValue();
    }

    public void saveAnswerForQuestion(String questionTitle, String answer) {
        dataManager.saveAnswerForQuestion(questionTitle, answer);
    }

    public String loadAnswerForQuestion(String questionTitle) {
        return dataManager.loadAnswerForQuestion(questionTitle);
    }
    public MutableLiveData<List<Card>> getCardsLiveData() {
        return cardsLiveData;
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
            List<Card> cardsToDelete = new ArrayList<>();

            for (Card card : allCards) {

                if (card.isSelected()) {
                    cardsToDelete.add(card);
                }
            }

            allCards.removeAll(cardsToDelete);
            dataManager.delete(cardsToDelete);

            cardsLiveData.setValue(allCards);
            selectionMode = false;
            updateSelectedCards();
        }
    }

    @Override
    public int getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    @Override

    public int getSelectedBackgroundColor() {
        return selectedBackgroundColor;
    }

    @Override

    public int getTopicTextColor() {
        return topicTextColor;
    }
}
