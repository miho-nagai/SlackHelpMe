package slackhelpme.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import slackhelpme.handlers.SlackHelpMeHandler;
import slackhelpme.model.Channel;

// Slackに送信するための情報を入力させるダイアログ
public class SlackPostDialog extends Dialog {

    private SlackHelpMeHandler handler;
    private ArrayList<Channel> channelList;
    private String code;
    private Combo channelCombo;
    private Text titleText;
    private Text commentText;


    public SlackPostDialog(Shell parent, String codeText, SlackHelpMeHandler handler, ArrayList<Channel> channelList) {
        super(parent);
        this.code = codeText;
        this.handler = handler;
        this.channelList = channelList;
    }

    // 選択中のチャンネル名からチャンネルIDを取得
    public String getChannelId() {
        String channelName = channelCombo.getText();
        return (String) channelCombo.getData(channelName);
    }

    // ダイアログの初期サイズ
    protected Point getInitialSize() {
        return new Point(700, 700);
    }

    @Override
    protected boolean isResizable() {
        // ダイアログをリサイズ可能に設定
        return true;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Slackメッセージ投稿設定");
    }

    // 表示部分のメイン
    protected Control createDialogArea(Composite parent) {

        Composite base = (Composite) super.createDialogArea(parent);

        // スクロール可能なコンポジットに部品を乗せていく
        ScrolledComposite sc = new ScrolledComposite(base, SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setLayout(new FillLayout());
        sc.setLayoutData(new GridData(GridData.FILL_BOTH));
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        Composite composite = new Composite(sc, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createLabel(composite, "以下のコードをSlackに投稿します。投稿先のチャンネルを選び、OKボタンを押してください。\n");

        // エディタで選択していたコードを表示（編集不可）
        Text codeText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
        codeText.setText(this.code);
        codeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // チャンネル選択コンボ
        createLabel(composite, "チャンネル(必須)");
        channelCombo = new Combo(composite, SWT.READ_ONLY);
        for (Channel channelInfo : channelList) {
            channelCombo.add(channelInfo.getName());
            channelCombo.setData(channelInfo.getName(), channelInfo.getId());
        }
        // チャンネルが選択されたらOKボタンを活性化
        channelCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (channelCombo.getSelectionIndex() != -1) {
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing.
            }
        });

        // スニペットタイトル入力欄
        createLabel(composite, "タイトル");
        this.titleText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
        this.titleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // スニペットコメント入力欄
        createLabel(composite, "コメント");
        this.commentText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
        this.commentText.setLayoutData(new GridData(GridData.FILL_BOTH));


        sc.setContent(composite);
        sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        // 初期状態はチャンネル未選択なのでOKボタンを非活性に
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    // 指定したコンポジットの上にラベルを作成する
    private Label createLabel(Composite composite, String text) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(text);
        return label;
    }

    @Override
    protected void okPressed() {
        // OKボタンが押されたらハンドラクラスに入力値をセットする
        handler.setParam(getChannelId(), titleText.getText(), commentText.getText());
        super.okPressed();
    }
}