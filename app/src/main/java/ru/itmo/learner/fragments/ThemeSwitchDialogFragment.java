package ru.itmo.learner.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import ru.itmo.learner.R;

public class ThemeSwitchDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.theme_switch_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SwitchCompat themeSwitch = view.findViewById(R.id.theme_switch);
        ImageView sunIcon = view.findViewById(R.id.sun_icon);
        ImageView moonIcon = view.findViewById(R.id.moon_icon);

        boolean isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        themeSwitch.setChecked(isNightMode);
        updateIcons(isNightMode, sunIcon, moonIcon);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            updateIcons(isChecked, sunIcon, moonIcon);

            SharedPreferences prefs = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();
            if (getActivity() != null) {
                getActivity().recreate();
            }
            dismiss();
        });
    }

    private void updateIcons(boolean isNightMode, ImageView sunIcon, ImageView moonIcon) {
        if (isNightMode) {
            sunIcon.setVisibility(View.GONE);
            moonIcon.setVisibility(View.VISIBLE);
        } else {
            sunIcon.setVisibility(View.VISIBLE);
            moonIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.END | Gravity.TOP;
        params.y = 100;
        window.setAttributes(params);
    }
}
