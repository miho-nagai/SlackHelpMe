package slackhelpme.model;

import java.util.ArrayList;
import java.util.Collections;

// channels.listのレスポンスJSONを表すオブジェクト
public class GetChannelsResponse extends SlackResponse {

    private ArrayList<Channel> channels;

    public ArrayList<Channel> getChannels() {
        // チャンネル名順にソート
        Collections.sort(channels, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }
}
