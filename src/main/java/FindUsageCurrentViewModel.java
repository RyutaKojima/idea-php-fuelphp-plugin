import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

public class FindUsageCurrentViewModel extends AnAction {
    public FindUsageCurrentViewModel() {
        super("FindUsageCurrentViewModel");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        // NOTE: 作業ディレクトリからの相対パス
        final String viewRelativePath = "/fuel/app/classes/view";

        String viewBasePath = project.getBasePath() + viewRelativePath;

        // NOTE: アクティブになっているファイルを取得
        final VirtualFile[] vFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (vFiles == null || vFiles.length == 0) {
            Messages.showWarningDialog("No file to be review", "Error");
            return;
        }
        VirtualFile target = vFiles[0];

        /*
         * viewのフルパス
         *   {projectBasePath}/application/views/manager/report/network.php
         * コントローラ側の呼び出し
         *   $this->setTemplate('manager/report/network');
         * 検索に使いたい文字列
         *   manager/report/network
         */
        String filePath = target.getCanonicalPath()
                .replace(viewBasePath + "/", "")
                .replace("." + target.getExtension(), "");

        // 検索実行
        FindManager findManager = FindManager.getInstance(project);
        FindModel findModel = findManager.getFindInProjectModel().clone();
        findModel.setStringToFind("\\ViewModel::forge\\(\\s*['\"]" + filePath + "\\s*['\"]");
        findModel.setRegularExpressions(true);
        FindInProjectManager.getInstance(project).startFindInProject(findModel);
    }
}
