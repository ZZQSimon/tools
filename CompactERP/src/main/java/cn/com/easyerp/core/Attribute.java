/**
	* Title: Attribute.java <br>
	* Description:[] <br>
	* Copyright (c)  2018<br>
	* Company: <br>
	* @Date 2019.10.12 <br>
	* 
	* @author Simon Zhang
	* @version V1.0
	*/
package cn.com.easyerp.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName: Attribute <br>
 * @Description: [] <br>
 * @date 2019.10.12 <br>
 * 
 * @author Simon Zhang
 */
@Component
public class Attribute {

    public static int corePoolSize = 10;
    public static int maxPoolSize = 200;
    public static int queueCapacity = 10;

    @Value("${asyncconfig.corePoolSize}")
    public void setCorePoolSize(int corePoolSize) {
        Attribute.corePoolSize = corePoolSize;
    }

    @Value("${asyncconfig.maxPoolSize}")
    public void setMaxPoolSize(int maxPoolSize) {
        Attribute.maxPoolSize = maxPoolSize;
    }

    @Value("${asyncconfig.queueCapacity}")
    public void setQueueCapacity(int queueCapacity) {
        Attribute.queueCapacity = queueCapacity;
    }

}
