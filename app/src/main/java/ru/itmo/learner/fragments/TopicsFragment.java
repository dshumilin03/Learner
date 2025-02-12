package ru.itmo.learner.fragments;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

import ru.itmo.learner.R;
import ru.itmo.learner.activities.QuestionActivity;
import ru.itmo.learner.factories.CardButtonFactory;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.model.Topic;
import ru.itmo.learner.databinding.FragmentCardsBinding;
import ru.itmo.learner.viewModels.TopicViewModel;

public class TopicsFragment extends Fragment {

    private FragmentCardsBinding binding;
    private TopicViewModel viewModel;
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

        viewModel = new ViewModelProvider(requireActivity()).get(TopicViewModel.class);
        viewModel.setContext(requireContext());

        viewModel.getFilteredCards().observe(getViewLifecycleOwner(), this::updateTopicsUI);
        viewModel.getCardsLiveData().observe(getViewLifecycleOwner(), this::updateTopicsUI);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddTopicDialog());
    }


    private void updateTopicsUI(List<Card> cards) {
        CardButtonFactory factory =  new CardButtonFactory(requireContext(), linearLayoutButtons, viewModel);
        linearLayoutButtons.removeAllViews();
        for (Card card : cards) {

            factory.createCardUI(card, v -> { // OnClickListener
                if (!viewModel.isSelectionMode()) {
                    android.content.Intent intent = new android.content.Intent(requireContext(), QuestionActivity.class);
                    intent.putExtra("topicName", card.getTitle());
                    startActivity(intent);
                } else {
                    viewModel.toggleCardSelection(card);
                }
            }, v -> { // OnLongClickListener
                viewModel.toggleCardSelection(card);

                return true;
            });
        }
    }

    private void showAddTopicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Введите название карточки");

        final EditText input = new EditText(requireContext());
        input.setHint("Название карточки");

        builder.setView(input);
        builder.setPositiveButton("ОК", (dialog, which) -> {

            String title = input.getText().toString().trim();
            if (!TextUtils.isEmpty(title)) {

                Card newTopic = new Topic(title);
                viewModel.addCard(newTopic);
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
