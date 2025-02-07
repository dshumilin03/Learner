package ru.itmo.learner.viewModels;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.itmo.learner.model.Card;

public interface CardViewModel {
    LiveData<List<Card>> getFilteredCards();
    void setSearchQuery(String query);
    void addCard(Card card);
    void toggleCardSelection(Card card);
    void deleteSelectedCards();
    LiveData<List<Card>> getSelectedCards();
    boolean isSelectionMode();
}
