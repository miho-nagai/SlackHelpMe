package slackhelpme;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import slackhelpme.model.Channel;
import slackhelpme.model.GetChannelsResponse;
import slackhelpme.model.SlackResponse;
import slackhelpme.preference.SlackHelpMePreferenceInitializer;

@SuppressWarnings("restriction")
// SlackAPIにリクエストを投げる
public class SlackApi {

    private static final String SLACK_HOST = "slack.com";
    private static final String SLACK_API_PATH_GET_CHANNELS = "/api/channels.list";
    private static final String SLACK_API_PATH_UPLOAD_FILE = "/api/files.upload";


    // Slackにファイルをアップロード
    public static void uploadFile(String text, String channel, String title, String comment,
            String fileName, String fileType) throws Exception {

        // URI設定
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost(SLACK_HOST)
                .setPath(SLACK_API_PATH_UPLOAD_FILE)
                .setParameter("token", getToken())
                .setParameter("channels", channel)
                .setParameter("content", text)
                .setParameter("filename", fileName)
                .setParameter("filetype", fileType)
                .setParameter("title", title)
                .setParameter("initial_comment", comment)
                .build();

        // API叩く
        String response = callApi(uri);

        // jsonをモデルに
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SlackResponse res = mapper.readValue(response, SlackResponse.class);

        if (!res.isOk()) {
            throw new Exception("Slackへのファイルアップロードに失敗しました。APIエラーメッセージ:" + res.getError());
        }
    }

    // Slackの全チャンネルを取得
    public static List<Channel> getChannels() throws Exception {

        // URI設定
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost(SLACK_HOST)
                .setPath(SLACK_API_PATH_GET_CHANNELS)
                .setParameter("token", getToken())
                .build();

        // API叩く
        String response = callApi(uri);

        // jsonをモデルに
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        GetChannelsResponse res = mapper.readValue(response, GetChannelsResponse.class);

        if (!res.isOk()) {
            throw new Exception("Slackのチャンネル一覧の取得に失敗しました。APIエラーメッセージ:" + res.getError());
        }

        return res.getChannels();
    }

    // 実際にAPIを叩く
    private static String callApi(URI uri) throws Exception {

        HttpUriRequest httpGet = new HttpGet(uri);
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        } else {
            throw new Exception("SlackとのAPI接続に失敗しました。レスポンスコード:" + response.getStatusLine().getStatusCode());
        }

        return EntityUtils.toString(response.getEntity());
    }

    // 設定済みのtokenを取得する
    private static String getToken() throws Exception {

        // ウィンドウ＞設定＞SlackHelpMeに設定されているtoken値を取得
        String token = Activator.getDefault().getPreferenceStore()
                .getString(SlackHelpMePreferenceInitializer.KEY_TOKEN);
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        // 未設定の場合は設定するまで延々と設定画面を開く
        if (token == null || token.length() == 0) {
            MessageDialog.openInformation(shell, "SlackHelpMe", "Slackに接続するためのtokenが設定されていません。設定ページを開きます。");
            WorkbenchPreferenceDialog.createDialogOn(shell, "slackhelpme.preference.page").open();
            return getToken();
        }
        return token;
    }
}
