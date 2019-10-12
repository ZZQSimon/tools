package cn.com.easyerp.framework.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;

import cn.com.easyerp.framework.enums.DataStatus;
import cn.com.easyerp.framework.util.DateUtil;
import lombok.Data;

/**
 * 一些常用的扩展字段
 */
@Data
@MappedSuperclass
public class AbstractPo implements Serializable {
    private static final long serialVersionUID = -100000000000001L;

    protected String createBy;// 创建人
    protected String updateBy;// 更新人
    protected String createTime;// 创建时间
    protected String updateTime;// 更新时间
    protected String recordStatus;// 记录状态 DataStatus 1:在用，2:废弃，3:已发送

    /**
     * 创建信息控制字段
     */
    public void setCreateInfo() {
        String dateStr = DateUtil.toStrAll_(new Date());
        createTime = dateStr;
        updateTime = dateStr;
        recordStatus = DataStatus.inuse.code();
    }

    /**
     * 更新信息控制字段
     */
    public void setUpdateInfo() {
        updateTime = DateUtil.toStrAll_(new Date());
    }

    /**
     * 取得数据在用状态码：0
     * 
     * @return 0
     */
    public static String getInuse() {
        return DataStatus.inuse.code();
    }

    /**
     * 取得数据废弃状态码：1
     * 
     * @return 1
     */
    public static String getUnuse() {
        return DataStatus.deleted.code();
    }

    /**
     * 取得数据已发送状态码：2
     * 
     * @return 2
     */
    public static String getSent() {
        return DataStatus.sent.code();
    }
}
