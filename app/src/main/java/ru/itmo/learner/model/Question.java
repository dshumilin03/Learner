package ru.itmo.learner.model;

import android.widget.Button;

public class Question implements Card{
    private String title;
    private boolean isSelected;
    private Button button;

    public Question(String title) {
        this.title = title;
        this.isSelected = false;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public Button getButton() {
        return button;
    }

    @Override
    public void setButton(Button button) {
        this.button = button;
    }
}

