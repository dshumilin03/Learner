package ru.itmo.learner.model;

import android.widget.Button;

public class Question implements Card{
    private String title;
    private int backgroundColor;
    private int textColor;
    private boolean isSelected;
    private Button button;

    public Question(String title, int backgroundColor, int textColor) {
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
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
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
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

