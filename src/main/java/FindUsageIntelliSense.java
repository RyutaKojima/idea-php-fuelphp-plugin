import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public class FindUsageIntelliSense extends AnAction {
    final String fuelViewPath = "/fuel/app/views";
    final String fuelViewModelPath = "/fuel/app/classes/view";
    final String fuelControllerPath = "/fuel/app/classes/controller";

    public FindUsageIntelliSense() {
        super("FindUsageCurrentViewModel");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        VirtualFile activeFile = this.getActiveFile(event);
        if (activeFile == null) {
            Messages.showWarningDialog("No file to be review", "Error");
            return;
        }

        String findingRegex = this.makeFindString(project, activeFile);
        if (findingRegex == null) {
            Messages.showWarningDialog("This file is not supported", "Error");
            return;
        }

        FindManager findManager = FindManager.getInstance(project);
        FindModel findModel = findManager.getFindInProjectModel().clone();
        findModel.setStringToFind(findingRegex);
        findModel.setRegularExpressions(true);
        FindInProjectManager.getInstance(project).startFindInProject(findModel);
    }

    @Nullable
    private VirtualFile getActiveFile(AnActionEvent event)
    {
        // Get active file
        final VirtualFile[] vFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (vFiles == null || vFiles.length == 0) {
            return null;
        }
        VirtualFile target = vFiles[0];
        return target;
    }

    private String getFuelFileType(VirtualFile activeFile)
    {
        if (activeFile.getCanonicalPath().contains("fuel/app/views/")) {
            return "views";
        }

        if (activeFile.getCanonicalPath().contains("fuel/app/classes/view/")) {
            return "viewmodel";
        }

        return "other";
    }

    @Nullable
    private String makeFindString(Project project, VirtualFile activeFile)
    {
        final String fileType = this.getFuelFileType(activeFile);

        String expectPath = "";
        switch (fileType) {
            case "views":
                expectPath = project.getBasePath() + this.fuelViewPath;
                break;
            case "viewmodel":
                expectPath = project.getBasePath() + this.fuelViewModelPath;
                break;
            default:
                return null;
        }

        String activeFileFullPath = activeFile.getCanonicalPath();
        String filePath = activeFileFullPath
                .replace(expectPath + "/", "")
                .replace("." + activeFile.getExtension(), "");

        switch (fileType) {
            case "views":
                return "\\View::forge\\(\\s*['\"]" + filePath + "['\"]";
            case "viewmodel":
                return "\\ViewModel::forge\\(\\s*['\"]" + filePath + "\\s*['\"]";
            default:
                return null;
        }
    }
}
