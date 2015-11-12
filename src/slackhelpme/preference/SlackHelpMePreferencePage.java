package slackhelpme.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import slackhelpme.Activator;

// ウィンドウ＞設定＞SlackHelpMe で表示される設定ページ
public class SlackHelpMePreferencePage  extends PreferencePage implements IWorkbenchPreferencePage {

    private Text tokenText;

    @Override
    public void init(IWorkbench workbench) {
        noDefaultButton();

        // tokenが未設定ならokボタンを非活性化
        String token = Activator.getDefault().getPreferenceStore().getString(SlackHelpMePreferenceInitializer.KEY_TOKEN);
        if (token == null || token.length() == 0) {
            setValid(false);
        }
    }

    @Override
    // メイン表示部分
    protected Control createContents(Composite parent) {
        setTitle("SlackHelpMe");

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NONE);
        label.setText("tokenを入力してください。 (https://api.slack.com/web の最下部から作成可能)");

        // token入力欄（初期値は現在設定値）
        tokenText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        tokenText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        tokenText.setText(Activator.getDefault().getPreferenceStore().getString(SlackHelpMePreferenceInitializer.KEY_TOKEN));
        // 値が入力されたらOKボタンを活性化、空にされたら非活性化
        tokenText.addModifyListener(e -> setValid(tokenText.getText().length() > 0));

        return composite;
    }


    @Override
    // OKボタンが押されたら設定値を保存
    public boolean performOk() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(SlackHelpMePreferenceInitializer.KEY_TOKEN, tokenText.getText());
        return true;
    }
}
