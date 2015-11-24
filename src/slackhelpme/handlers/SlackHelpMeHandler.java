package slackhelpme.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import slackhelpme.SlackApi;
import slackhelpme.dialogs.SlackPostDialog;
import slackhelpme.model.Channel;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SlackHelpMeHandler extends AbstractHandler {

    private String channel;
    private String title;
    private String comment;

    public void setParam(String channel, String title, String comment) {
        this.channel = channel;
        this.title   = title;
        this.comment = comment;
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        // アクティブエディタの取得
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        Shell shell = window.getShell();
        IEditorPart editorPart = window.getActivePage().getActiveEditor();

        if (editorPart != null) {

            // アクティブエディタで開いているファイル名/拡張子
            String fileName = editorPart.getEditorInput().getName();
            String suffix = getSuffix(fileName);

            // エディタ内で現在選択されている文字列
            TextSelection selection = (TextSelection) editorPart.getEditorSite().getSelectionProvider().getSelection();
            String selectedText = selection.getText();

            if (selectedText != null && selectedText.length() > 0) {

                try {
                    // チャンネル一覧を取得
                    List<Channel> channelList = SlackApi.getChannels();

                    // 送信設定ダイアログ表示
                    SlackPostDialog dialog = new SlackPostDialog(shell, selectedText, this, channelList);
                    int retCode = dialog.open();

                    // OKボタンが押されたらSlackへ投稿
                    if (retCode == IDialogConstants.OK_ID) {
                        SlackApi.uploadFile(selectedText, channel, title, comment, fileName, suffix);
                        MessageDialog.openInformation(shell, "SlackHelpMe", "Slackに送信しました。");
                    }

                } catch (Exception e) {
                    // 何かしらのエラーが発生したらエラーダイアログ表示
                    MessageDialog.openError(shell, "SlackHelpMe", e.getMessage());
                    e.printStackTrace();
                }

            } else {
                // エディタで文字が選択されていない時もエラーダイアログ
                MessageDialog.openError(shell, "SlackHelpMe", "Slackに送信したい文字列をエディタ上で選択してください。");
            }
        }
        return null;
    }

    // ファイル名から拡張子部分だけ取得
    private String getSuffix(String fileName) {
        if (fileName == null)
            return null;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return null;
    }
}
