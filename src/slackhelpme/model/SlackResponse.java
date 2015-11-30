package slackhelpme.model;

// SlackAPIの基本的なJSONレスポンスを表すオブジェクト
public class SlackResponse {

    private boolean ok;
    private String error;   // errorはok=false時しかないのでignoreUnknown=trueを設定

    public boolean isOk() {
        return ok;
    }
    public void setOk(boolean ok) {
        this.ok = ok;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
