package com.g1extend.service.impl;

import com.g1extend.entity.Notice;
import com.g1extend.mapper.NoticeMapper;
import com.g1extend.service.NoticeService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {
    @Resource
    private NoticeMapper mapper;

    public void save(Notice user) {
        this.mapper.save(user);
    }

    public boolean update(Notice paramNotice) {
        return this.mapper.update(paramNotice);
    }

    public boolean updateSent(Notice paramNotice) {
        return this.mapper.updateSent(paramNotice);
    }

    public boolean delete(String id) {
        return this.mapper.delete(id);
    }

    public Notice findById(String id) {
        return this.mapper.findById(id);
    }

    public List<Notice> findAll() {
        return this.mapper.findAll();
    }

    public List<Notice> findAllToSent(String typeString, int counts) {
        return this.mapper.findAllToSent(typeString, counts);
    }
}
