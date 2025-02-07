package ru.itmo.learner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import ru.itmo.learner.R;
import ru.itmo.learner.activities.MainActivity;
import ru.itmo.learner.databinding.FragmentToolbarBinding;
import ru.itmo.learner.viewModels.CardViewModel;
import ru.itmo.learner.viewModels.QuestionsViewModel;
import ru.itmo.learner.viewModels.SharedViewModel;

public class ToolbarFragment extends Fragment {

    private FragmentToolbarBinding binding;
    private CardViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentToolbarBinding.inflate(inflater, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(binding.toolbar);

            if (activity.getSupportActionBar() != null && !(activity instanceof MainActivity)) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle(getActivity().getIntent().getStringExtra("topicName"));
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        if (getActivity() instanceof MainActivity) {
            viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            Log.d(ToolbarFragment.class.toString(), "Поставился SharedViewModel");
        } else {
            viewModel = new ViewModelProvider(requireActivity()).get(QuestionsViewModel.class);
            Log.d(ToolbarFragment.class.toString(), "Поставился QuestionsViewModel");

        }

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
                MenuItem searchItem = menu.findItem(R.id.search_view);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setIconified(false);
                searchView.clearFocus();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        viewModel.setSearchQuery(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewModel.setSearchQuery(newText);
                        return false;
                    }
                });

                MenuItem themeSwitchItem = menu.findItem(R.id.action_theme_switch);
                themeSwitchItem.setOnMenuItemClickListener(item -> {
                    ThemeSwitchDialogFragment dialog = new ThemeSwitchDialogFragment();
                    dialog.show(getParentFragmentManager(), "ThemeSwitchDialog");
                    return true;
                });

                MenuItem deleteItem = menu.findItem(R.id.action_delete);
                viewModel.getSelectedCards().observe(getViewLifecycleOwner(), selectedCards -> {
                    if (selectedCards != null && !selectedCards.isEmpty()) {
                        deleteItem.setVisible(true);
                        searchItem.setVisible(false);
                    } else {
                        deleteItem.setVisible(false);
                        searchItem.setVisible(true);
                    }
                });

                deleteItem.setOnMenuItemClickListener(item -> {
                    viewModel.deleteSelectedCards();
                    return true;
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}
