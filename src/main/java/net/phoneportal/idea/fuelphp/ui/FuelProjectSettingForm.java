package net.phoneportal.idea.fuelphp.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import net.phoneportal.idea.fuelphp.FuelSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FuelProjectSettingForm implements Configurable {

    private Project project;

    private JPanel mainPanel;
    private JTextField textMainLang;
    private JTextField textFuelEnv;

    public FuelProjectSettingForm(@NotNull final Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "FuelPHP Plugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return !textMainLang.getText().equals(getSettings().getMainLanguage()) ||
                !textFuelEnv.getText().equals(getSettings().getFuelEnv());
    }

    @Override
    public void apply() throws ConfigurationException {
        getSettings().mainLanguage = textMainLang.getText();
        getSettings().fuelEnv = textFuelEnv.getText();
    }

    @Override
    public void reset() {
        updateUIFromSettings();
    }

    @Override
    public void disposeUIResources() {

    }

    private void updateUIFromSettings() {
        textMainLang.setText(getSettings().getMainLanguage());
        textFuelEnv.setText(getSettings().getFuelEnv());
    }

    private FuelSettings getSettings() {
        return FuelSettings.getInstance(this.project);
    }

}
