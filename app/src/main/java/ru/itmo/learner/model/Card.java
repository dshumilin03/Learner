package ru.itmo.learner.model;

import android.widget.Button;

public interface Card {
    String getTitle();
    boolean isSelected();
    void setSelected(boolean selected);
    Button getButton();
    void setButton(Button button);
}
