package com.sun.manager.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomizeMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        LocalDateTime localDate = LocalDateTime.now();
        // filedName 是实体的名字，不是数据库的名字
        this.setFieldValByName("createTime", localDate,metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        LocalDateTime localDate = LocalDateTime.now();
        // filedName 是实体的名字，不是数据库的名字
        this.setFieldValByName("updateTime", localDate, metaObject);
    }
}
