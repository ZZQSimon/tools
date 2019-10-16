package cn.com.easyerp.module.japi.webServiceCXF;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface DXServiceCXFInterface {
    String getValue(@WebParam(name = "name") String paramString);

    String sendMailToGC(@WebParam(name = "msg") String paramString);
}
