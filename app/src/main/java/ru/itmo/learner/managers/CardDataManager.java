package ru.itmo.learner.managers;

import java.util.List;

import ru.itmo.learner.model.Card;

public interface CardDataManager {

    void save(List<Card> cards);

    void delete(List<Card> cards);

    List<Card> load();
}
