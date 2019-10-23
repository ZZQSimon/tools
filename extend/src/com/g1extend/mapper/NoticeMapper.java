package com.g1extend.mapper;

import com.g1extend.entity.Notice;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeMapper {
    void save(@Param("notice") Notice paramNotice);

    boolean update(@Param("notice") Notice paramNotice);

    boolean updateSent(@Param("notice") Notice paramNotice);

    boolean delete(@Param("id") String paramInt);

    Notice findById(@Param("id") String paramInt);

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
    List<Notice> findAllToSent(@Param("noticetype") String typeString, @Param("counts") int counts);
}
