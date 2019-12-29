package net.phoneportal.idea.fuelphp.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public class FilesystemUtil {

    @Nullable
    public static VirtualFile getProjectBaseDir(Project project) {
        if (project.getBasePath() == null) {
            return null;
        }

        return LocalFileSystem.getInstance().findFileByPath(project.getBasePath());
    }
}
