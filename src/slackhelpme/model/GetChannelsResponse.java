package slackhelpme.model;

import java.util.Collections;
import java.util.List;

// channels.listのレスポンスJSONを表すオブジェクト
public class GetChannelsResponse extends SlackResponse {

    private List<Channel> channels;

    public List<Channel> getChannels() {
        // チャンネル名順にソート
        Collections.sort(channels, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
}
