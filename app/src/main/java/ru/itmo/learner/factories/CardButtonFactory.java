package ru.itmo.learner.factories;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.viewModels.CardViewModel;

public class CardButtonFactory {

    private final LinearLayout linearLayoutButtons;
    private final Context context;
    private final CardViewModel viewModel;

    public CardButtonFactory(Context context, LinearLayout linearLayoutButtons, CardViewModel viewModel) {
        this.linearLayoutButtons = linearLayoutButtons;
        this.context = context;
        this.viewModel = viewModel;
    }

    public void createCardUI(Card card, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        Button button = new Button(context);
        button.setText(card.getTitle());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 150);
        params.setMargins(64, 16, 64, 16);
        button.setLayoutParams(params);

        int backgroundColor = card.isSelected() ? viewModel.getSelectedBackgroundColor()
                : viewModel.getDefaultBackgroundColor();

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(30f);
        drawable.setColor(backgroundColor);
        button.setBackground(drawable);
        button.setTextColor(viewModel.getTopicTextColor());

        button.setOnClickListener(v -> onClickListener.onClick(button));
        button.setOnLongClickListener(v -> onLongClickListener.onLongClick(button));

        linearLayoutButtons.addView(button);
        card.setButton(button);
    }
}
