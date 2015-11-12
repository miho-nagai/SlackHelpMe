package slackhelpme.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
// channels.listのレスポンスJSONの"channels"を表すオブジェクト
public class Channel {

    private String id;
    private String name;
    // 他要素はとりあえず使わないのでingoneUnknown=trueでスルー

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
