 package cn.com.easyerp.core.postInfo;
 
 import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.dao.PostInfoDao;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 @Controller
 @RequestMapping({"/postInfo"})
 public class PostInfoController extends FormViewControllerBase {
   @Autowired
   private PostInfoDao postInfodao;
   
   @RequestMapping({"/postInfo.view"})
   public ModelAndView postInfo(@RequestBody PostInfoRequestModel request) {
     PostInfoModel form = new PostInfoModel(request.getParent());
     return buildModelAndView(form);
   }
   @ResponseBody
   @RequestMapping({"/selectPostInfo.do"})
   public ActionResult selectPostInfo() {
     List<Map<String, Object>> selectPostInfo = this.postInfodao.selectPostInfo();
     return new ActionResult(true, selectPostInfo);
   }
   @ResponseBody
   @RequestMapping({"/addPostInfo.do"})
   public ActionResult addPostInfo(@RequestBody PostInfoRequestModel request, AuthDetails user) {
     String postId = request.postId;
     String postName = request.postName;
     if (postId == null || "".equals(postId)) {
       postId = this.postInfodao.getPostInfoId("m_role", "");
     }
     else if (this.postInfodao.selectPostById(postId) > 0) {
       return new ActionResult(true);
     } 
     
     this.postInfodao.addPostInfo(postId, postName, user.getId(), new Date());
     return new ActionResult(true, postId);
   }
   @ResponseBody
   @RequestMapping({"/deletePost.do"})
   public ActionResult deletePost(@RequestBody PostInfoRequestModel request, AuthDetails user) {
     String postId = request.postId;
     this.postInfodao.deletePostInfo(postId);
     return new ActionResult(true);
   }
 }


