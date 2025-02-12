package ru.itmo.learner.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

import ru.itmo.learner.R;
import ru.itmo.learner.activities.QuestionActivity;
import ru.itmo.learner.factories.CardButtonFactory;
import ru.itmo.learner.model.Card;
import ru.itmo.learner.model.CardDataPrefs;
import ru.itmo.learner.model.Question;
import ru.itmo.learner.databinding.FragmentCardsBinding;
import ru.itmo.learner.viewModels.QuestionsViewModel;

public class QuestionsFragment extends Fragment {

    private FragmentCardsBinding binding;
    private QuestionsViewModel viewModel;
    private LinearLayout linearLayoutButtons;
    private CardDataPrefs questionDataPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             android.os.Bundle savedInstanceState) {
        binding = FragmentCardsBinding.inflate(inflater, container, false);
        linearLayoutButtons = binding.linearLayoutButtons;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, android.os.Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsViewModel.class);
        viewModel.init(requireContext());
        viewModel.getFilteredCards().observe(getViewLifecycleOwner(), this::updateQuestionsUI);
        viewModel.getCardsLiveData().observe(getViewLifecycleOwner(), this::updateQuestionsUI);

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddQuestionDialog());

        SharedPreferences prefs = requireContext().getSharedPreferences("QuestionPrefs-" + QuestionActivity.topicName, MODE_PRIVATE);
        String key = "answers-" + QuestionActivity.topicName + "-";
        questionDataPrefs = new CardDataPrefs(prefs, key);
    }

    private void updateQuestionsUI(List<Card> cards) {
        linearLayoutButtons.removeAllViews();
        CardButtonFactory factory = new CardButtonFactory(requireContext(), linearLayoutButtons, viewModel);
        for (Card card : cards) {

            factory.createCardUI(card, v -> { // OnClickListener
                if (!viewModel.isSelectionMode()) {
                    showAnswerDialog((Question) card);
                } else {
                    viewModel.toggleCardSelection(card);
                }
            }, v -> { // OnLongClickListener
                viewModel.toggleCardSelection(card);
                return true;
            });
        }
    }

    private void showAddQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Введите ваш вопрос");

        final EditText input = new EditText(requireContext());
        input.setHint("сдал лабу?");
        builder.setView(input);

        builder.setPositiveButton("ОК", (dialog, which) -> {

            String questionText = input.getText().toString().trim();
            if (!TextUtils.isEmpty(questionText)) {
                Question newQuestion = new Question(questionText);

                viewModel.addCard(newQuestion);
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showAnswerDialog(Question question) {
        String savedAnswer = viewModel.loadAnswerForQuestion(question.getTitle());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_answer, null);

        TextView textView = dialogView.findViewById(R.id.text_answer);
        Button buttonBack = dialogView.findViewById(R.id.button_back);
        Button buttonEdit = dialogView.findViewById(R.id.button_edit);

        if (savedAnswer.isEmpty()) {
            textView.setText("Нажмите \"Редактировать\"");
        } else {
            textView.setText(savedAnswer);
        }

        AlertDialog answerDialog = builder.setView(dialogView).create();

        answerDialog.getWindow().setBackgroundDrawableResource(R.drawable.answer_dialog_background);

        buttonBack.setOnClickListener(v -> answerDialog.dismiss());

        buttonEdit.setOnClickListener(v -> {

            answerDialog.dismiss();
            showEditAnswerDialog(question);
        });

        answerDialog.show();
    }


    private void showEditAnswerDialog(Question question) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Редактирование ответа");

        EditText input = new EditText(requireContext());
        input.setHint("Введите ответ");

        String savedAnswer = viewModel.loadAnswerForQuestion(question.getTitle());
        input.setText(savedAnswer);

        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {

            String newAnswer = input.getText().toString().trim();
            if (!newAnswer.isEmpty()) {
                viewModel.saveAnswerForQuestion(question.getTitle(), newAnswer);
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
