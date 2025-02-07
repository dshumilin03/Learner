package ru.itmo.learner.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import ru.itmo.learner.R;
import ru.itmo.learner.databinding.ActivityQuestionBinding;
import ru.itmo.learner.fragments.QuestionsFragment;
import ru.itmo.learner.fragments.ToolbarFragment;

public class QuestionActivity extends AppCompatActivity {

    private ActivityQuestionBinding binding;
    public static String topicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", true);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topicName = getIntent().getStringExtra("topicName");
        setTitle(topicName);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toolbar_container, new ToolbarFragment());
            ft.replace(R.id.content_container, new QuestionsFragment());
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
