import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorIllustrationAction extends AnAction {
    @Override
    public void update(@NotNull final AnActionEvent e) {
        //Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        //Set visibility only in case of existing project and editor and if a selection exists
        e.getPresentation().setEnabledAndVisible( project != null
                && editor != null
                && editor.getSelectionModel().hasSelection() );

//        e.getPresentation().setEnabledAndVisible( project != null
//                && editor != null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();

        // Work off of the primary caret to get the selection info
        final Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        final int start = primaryCaret.getSelectionStart();
        final int end = primaryCaret.getSelectionEnd();
        final String selectionText = document.getText().substring(start, end);

        // for View::forge
        Pattern patternView = Pattern.compile("View::forge\\(\\s*['\"]([^'\"]+)['\"]");
        Matcher matcher = patternView.matcher(selectionText);
        if(matcher.find()){
            String matchText = matcher.group(1);
            String targetFilePath = project.getBasePath() + "/fuel/app/views/" + matchText + ".php";

            VirtualFile file = file = LocalFileSystem.getInstance().findFileByPath(targetFilePath);
//            file = LocalFileSystem.getInstance().refreshAndFindFileByPath(targetFilePath);
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
            FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
            return;
        }

        // for ViewModel::forge
        Pattern patternViewModel = Pattern.compile("ViewModel::forge\\(\\s*['\"]([^'\"]+)['\"]");
        matcher = patternViewModel.matcher(selectionText);
        if(matcher.find()){
            String matchText = matcher.group(1);
            String targetFilePath = project.getBasePath() + "/fuel/app/classes/view/" + matchText + ".php";

            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(targetFilePath);
//            file = LocalFileSystem.getInstance().refreshAndFindFileByPath(targetFilePath);
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
            FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
        }
    }
}