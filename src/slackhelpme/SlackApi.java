package slackhelpme;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

import com.fasterxml.jackson.databind.ObjectMapper;

import slackhelpme.model.Channel;
import slackhelpme.model.GetChannelsResponse;
import slackhelpme.model.SlackResponse;
import slackhelpme.preference.SlackHelpMePreferenceInitializer;

@SuppressWarnings("restriction")
// SlackAPIにリクエストを投げる
public class SlackApi {

    private static final String API_GET_CHANNELS = "https://slack.com/api/channels.list?";
    private static final String API_UPLOAD_FILE = "https://slack.com/api/files.upload?";


    // Slackにファイルをアップロード
    public static void uploadFile(String text, String channel, String title, String comment,
            String fileName, String fileType) throws Exception {

        // リクエストクエリ
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", getToken());
        params.put("channels", channel);
        params.put("content", text);
        params.put("filename", fileName);
        params.put("filetype", fileType);
        params.put("title", title);
        params.put("initial_comment", comment);
        String url = API_UPLOAD_FILE + http_build_query(params);

        // API叩く
        String response = callApi(url);
        ObjectMapper mapper = new ObjectMapper();
        SlackResponse res = mapper.readValue(response, SlackResponse.class);

        if (!res.isOk()) {
            throw new Exception("Slackへのファイルアップロードに失敗しました。APIエラーメッセージ:" + res.getError());
        }
    }

    // Slackの全チャンネルを取得
    public static ArrayList<Channel> getChannels() throws Exception {

        // リクエストクエリ
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", getToken());
        String url = API_GET_CHANNELS + http_build_query(params);

        // API叩く
        String response = callApi(url);
        ObjectMapper mapper = new ObjectMapper();
        GetChannelsResponse res = mapper.readValue(response, GetChannelsResponse.class);

        if (!res.isOk()) {
            throw new Exception("Slackのチャンネル一覧の取得に失敗しました。APIエラーメッセージ:" + res.getError());
        }

        return res.getChannels();
    }

    // 実際にAPIを叩く
    private static String callApi(String url) throws Exception {

        URL connectUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) connectUrl.openConnection();

        if (con.getResponseCode() != 200) {
            throw new Exception("SlackとのAPI接続に失敗しました。レスポンスコード:" + con.getResponseCode());
        }

        // レスポンス値取得
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        return sb.toString();
    }

    // クエリの組み立て（http://wauke.org/59 から拝借）
    private static String http_build_query(Map<String, String> params) throws UnsupportedEncodingException {
        String result = "";
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (e.getKey().isEmpty()) continue;
            if (!result.isEmpty()) result += "&";
            result += URLEncoder.encode(e.getKey(), "UTF-8") + "=" +
                      URLEncoder.encode(e.getValue(), "UTF-8");
        }
        return result;
    }

    // 設定済みのtokenを取得する
    private static String getToken() throws Exception {

        // ウィンドウ＞設定＞SlackHelpMeに設定されているtoken値を取得
        String token = Activator.getDefault().getPreferenceStore().getString(SlackHelpMePreferenceInitializer.KEY_TOKEN);
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
