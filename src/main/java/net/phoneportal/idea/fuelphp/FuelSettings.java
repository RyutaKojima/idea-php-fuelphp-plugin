package net.phoneportal.idea.fuelphp;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "FuelPluginSettings",
        storages = {
                @Storage("fuel-plugin.xml")
        }
)
public class FuelSettings implements PersistentStateComponent<FuelSettings> {

    public String mainLanguage;
    public String fuelEnv;

    public boolean dismissEnableNotification = false;

    public static FuelSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, FuelSettings.class);
    }

    public String getMainLanguage() {
        return !StringUtils.isBlank(mainLanguage) ? mainLanguage : "en";
    }
    public String getFuelEnv() {
        return !StringUtils.isBlank(fuelEnv) ? fuelEnv : "";
    }

    @Nullable
    @Override
    public FuelSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull FuelSettings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }

}
