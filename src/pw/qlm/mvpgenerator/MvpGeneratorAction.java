package pw.qlm.mvpgenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * mvp 模板代码生成器
 * Created by Qiu Linmin on 2016/9/30.
 */
public class MvpGeneratorAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        String content = editor.getDocument().getText();
        String className = getClassName(content);
        if (className == null) {
            Messages.showMessageDialog("Create failed ,Can't found 'Contract' in your class name,your class name must contain 'Contract'", "Error", Messages.getErrorIcon());
            return;
        }
        String currentPath = getCurrentPath(e);
        if (currentPath == null || !currentPath.contains("contract")) {
            Messages.showMessageDialog("Your Contract should in package 'contract'.", "Error", Messages.getErrorIcon());
            return;
        }
        String basePath = currentPath.replace("contract/" + className + ".java", "");
        String basePackage = getPackageName(basePath);
        String modelName = className.substring(0, className.indexOf("Contract"));

        String contractContent = String.format(MvpTemplate.CONTRACT_TEMPLATE, basePackage, modelName);
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> editor.getDocument().setText(contractContent));

        try {
            createPresenterClass(basePackage, basePath, modelName);
            createModelClass(basePackage, basePath, modelName);
        } catch (IOException e1) {
            Messages.showMessageDialog("create file failed", "Error", Messages.getErrorIcon());
            return;
        }
        Messages.showMessageDialog("created success! please wait a moment", "Success", Messages.getInformationIcon());
        refreshProject(e);
    }

    private String getClassName(String content) {
        String[] words = content.split(" ");
        for (String word : words) {
            if (word.contains("Contract")) {
                return word;
            }
        }
        return null;
    }

    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false, true);
    }

    private void createModelClass(String basePackage, String path, String modelName) throws IOException {
        String dir = path + "model/" ;
        String filePath = dir + modelName + "Model.java";
        File dirs = new File(dir);
        File file = new File(filePath);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String content = String.format(MvpTemplate.MODEL_TEMPLATE, basePackage, modelName, modelName);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private void createPresenterClass(String basePackage, String path, String modelName) throws IOException {
        String dir = path + "presenter/";
        String filePath = dir + modelName + "Presenter.java";
        File dirs = new File(dir);
        File file = new File(filePath);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String content = String.format(MvpTemplate.PRESENTER_TEMPLATE, basePackage, modelName, modelName, modelName, modelName);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private String getPackageName(String path) {
        String[] strings = path.split("/");
        StringBuilder packageName = new StringBuilder();
        boolean packageBegin = false;
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if ((string.equals("com")) || (string.equals("org")) || (string.equals("cn")) || (string.equals("pw"))) {
                packageBegin = true;
            }
            if (packageBegin) {
                packageName.append(string);
                if (i != strings.length - 1) {
                    packageName.append(".");
                }
            }
        }
        return packageName.toString();
    }

    private String getCurrentPath(AnActionEvent e) {
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (currentFile != null) {
            return currentFile.getPath();
        }
        return null;
    }

}
