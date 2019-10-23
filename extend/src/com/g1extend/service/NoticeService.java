package com.g1extend.service;

import com.g1extend.entity.Notice;
import java.util.List;

public interface NoticeService {
    void save(Notice paramNotice);

    boolean update(Notice paramNotice);

    boolean updateSent(Notice paramNotice);

    boolean delete(String paramInt);

    Notice findById(String paramInt);

    List<Notice> findAll();

    /**
     * 
     * @param typeString
     *            通知类型 <br>
     *            邮件: 01 <br>
     *            短信: 02 <br>
     *            其他: 00 <br>
     * @param counts
     *            发送次数小于该数据时有效
     * @return
     */
    List<Notice> findAllToSent(String typeString, int counts);
}
