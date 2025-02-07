package ru.itmo.learner.fragments;

import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

import ru.itmo.learner.R;
import ru.itmo.learner.activities.QuestionActivity;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.model.Topic;
import ru.itmo.learner.databinding.FragmentCardsBinding;
import ru.itmo.learner.viewModels.SharedViewModel;

public class CardsFragment extends Fragment {

    private FragmentCardsBinding binding;
    private SharedViewModel viewModel;
    private LinearLayout linearLayoutButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             android.os.Bundle savedInstanceState) {
        binding = FragmentCardsBinding.inflate(inflater, container, false);
        linearLayoutButtons = binding.linearLayoutButtons;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, android.os.Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.setContext(requireContext());

        viewModel.getFilteredCards().observe(getViewLifecycleOwner(), cards -> updateCardsUI(cards));

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddCardDialog());
    }


    private void updateCardsUI(List<Card> cards) {
        linearLayoutButtons.removeAllViews();
        for (Card card : cards) {
            Button button = new Button(requireContext());
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

            button.setOnClickListener(v -> {

                if (!viewModel.isSelectionMode()) {
                    android.content.Intent intent = new android.content.Intent(requireContext(), QuestionActivity.class);
                    intent.putExtra("topicName", card.getTitle());
                    startActivity(intent);
                } else {
                    viewModel.toggleCardSelection(card);
                    updateCardsUI(viewModel.getAllCards());
                }
            });

            button.setOnLongClickListener(v -> {

                viewModel.toggleCardSelection(card);
                //todo сделать обновление выеделенных
                updateCardsUI(viewModel.getAllCards());

                return true;
            });

            linearLayoutButtons.addView(button);
            card.setButton(button);
        }
    }

    private void showAddCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Введите название карточки");

        final EditText input = new EditText(requireContext());
        input.setHint("Название карточки");

        builder.setView(input);
        builder.setPositiveButton("ОК", (dialog, which) -> {

            String title = input.getText().toString().trim();
            if (!TextUtils.isEmpty(title)) {

                Card newTopic = new Topic(title, viewModel.getDefaultBackgroundColor(), viewModel.getTopicTextColor());
                viewModel.addCard(newTopic);
                updateCardsUI(viewModel.getAllCards());
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
