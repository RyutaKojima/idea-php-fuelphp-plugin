package net.phoneportal.idea.fuelphp.codeInsight.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import net.phoneportal.idea.fuelphp.config.AppConfigReferences;
import net.phoneportal.idea.fuelphp.lang.LanguageReferences;
import net.phoneportal.idea.fuelphp.presenter.PresenterReferences;
import net.phoneportal.idea.fuelphp.views.ViewsReferences;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class GotoHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement psiElement, int i, Editor editor) {
        Collection<PsiElement> psiTargets = new ArrayList<>();

        psiTargets.addAll(new AppConfigReferences().getPsiTargets(psiElement));
        psiTargets.addAll(new LanguageReferences().getPsiTargets(psiElement));
        psiTargets.addAll(new ViewsReferences().getPsiTargets(psiElement));
        psiTargets.addAll(new PresenterReferences().getPsiTargets(psiElement));

        return psiTargets.toArray(new PsiElement[psiTargets.size()]);
    }

    @Nullable
    @Override
    public String getActionText(DataContext dataContext) {
        return null;
    }
}
