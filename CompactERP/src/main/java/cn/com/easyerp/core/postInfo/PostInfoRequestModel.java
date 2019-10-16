package cn.com.easyerp.core.postInfo;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class PostInfoRequestModel extends FormRequestModelBase<PostInfoModel> {
    String postId;
    String postName;
    List<Map<String, Object>> postInfo;

    public String getPostId() {
        return this.postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return this.postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public List<Map<String, Object>> getPostInfo() {
        return this.postInfo;
    }

    public void setPostInfo(List<Map<String, Object>> postInfo) {
        this.postInfo = postInfo;
    }
}
