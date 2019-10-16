package cn.com.easyerp.module.japi.webServiceCXF;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.easyerp.core.approve.ApproveRequestModel;
import cn.com.easyerp.core.mail.MailDescribe;
import cn.com.easyerp.core.mail.MailServer;
import cn.com.easyerp.core.mail.MailService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.widget.message.MessageDescribe;
import cn.com.easyerp.core.widget.message.MessageService;

@Path("/DXServiceCXF")
@Produces({ "application/json", "application/xml" })
public class DXServiceCXF implements DXServiceCXFInterface {
    @Autowired
    private MailService mailService;
    @Autowired
    private MessageService msgService;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;

    private HttpServletResponse getResponse() {
        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletResponse response = (HttpServletResponse) message.get("HTTP.RESPONSE");
        // HttpServletRequest request = (HttpServletRequest)message.get("HTTP.REQUEST");
        // ServletContext servletContext = (ServletContext) message.get("HTTP.CONTEXT");
        return response;
    }

    @GET
    @Path("/getValue/{name}")
    @Produces({ "application/json" })
    public String getValue(@PathParam("name") String name) {
        return "ccc;" + name + ":";
    }

    @POST
    @Path("/sendMailToGC")
    @Produces({ "application/json" })
    public String sendMailToGC(String msg) {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = JSONObject.parseObject(msg);
        Object username = jsonObject.get("username");
        Object e_mail = jsonObject.get("e_mail");
        Object telephon = jsonObject.get("telephon");
        Object message = jsonObject.get("msg");

        String userMsg = "";
        if (username == null) {
            userMsg = userMsg + "濮撳悕 锛氭湭濉啓\r\n";
        } else {
            userMsg = userMsg + "濮撳悕 锛�" + username.toString() + "\r\n";
        }
        if (e_mail == null) {
            userMsg = userMsg + "閭 锛氭湭濉啓\r\n";
        } else {
            userMsg = userMsg + "閭 锛�" + e_mail.toString() + "\r\n";
        }
        if (telephon == null) {
            userMsg = userMsg + "鑱旂郴鐢佃瘽 锛氭湭濉啓\r\n";
        } else {
            userMsg = userMsg + "鑱旂郴鐢佃瘽 锛�" + telephon.toString() + "\r\n";
        }
        if (message == null) {
            userMsg = userMsg + "鍏徃鍚嶇О鍙婂挩璇㈠唴瀹� 锛氭湭濉啓\r\n";
        } else {
            userMsg = userMsg + "鍏徃鍚嶇О鍙婂挩璇㈠唴瀹� 锛�" + message.toString() + "\r\n";
        }
        MailDescribe mail = new MailDescribe("鍜ㄨ淇℃伅", userMsg);
        mail.setRecipients("wangguanru@gainit.cn");
        mail.setCC("");
        MailServer mailServer = new MailServer("smtp.exmail.qq.com", "info@raypp.com", "info@raypp.com", "Raypp1");
        this.mailService.send(mail, mailServer);
        return "success";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GET
    @Path("/sendSystemMessage/{message}")
    @Produces({ "application/json" })
    public String sendSystemMessage(@PathParam("message") String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> param = (Map) mapper.readValue(message, Map.class);
            MessageDescribe msg = new MessageDescribe();
            msg.setTitle((String) param.get("title"));
            msg.setContent((String) param.get("content"));
            msg.setSender((String) param.get("sender"));
            msg.setType("type");
            List<String> receivers = Arrays.asList(((String) param.get("receiver")).split(","));
            this.dxRoutingDataSource.setDomainKey((String) param.get("connection"));
            this.msgService.addMessages(msg, receivers);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @GET
    @Path("/sendSystemEmail/{email}")
    @Produces({ "application/json" })
    public String sendSystemEmail(@PathParam("email") String email) {
        // ObjectMapper mapper = new ObjectMapper();
        try {
            ApproveRequestModel request = new ApproveRequestModel();
            request.setTableId("111");
            request.setDataId("222");
            request.setApproveId("333");
            request.setApproveReason("444");
            sendApproveEmail(request);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @GET
    @Path("/approve/{message}")
    @Produces({ "application/json" })
    public String approve(@PathParam("message") String message) {
        return message;
    }

    public void sendApproveEmail(ApproveRequestModel request) {
        this.dxRoutingDataSource.setDomainKey("gc_test1");
        MailDescribe mail = new MailDescribe("瀹℃壒閫氱煡#8888888", getHtmlContext(""));
        mail.setRecipients("583773081@qq.com");
        mail.setCC("");
        this.mailService.sendHtmlEmail(mail);
    }

    private String getHtmlContext(Object obj) {
        String fileContent = "";
        String path = "D:\\approveHtml.html";
        try (FileInputStream fis = new FileInputStream(path);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader br = new BufferedReader(isr);) {

            String line = null;
            while ((line = br.readLine()) != null) {
                fileContent = fileContent + line;
                fileContent = fileContent + "\r\n";
            }
        } catch (Exception e) {
            return "";
        }
        return fileContent.replace("{approveUrl}", "");
    }
}