package com.dupenghao.service.impl;

import com.dupenghao.mapper.BookMapper;
import com.dupenghao.pojo.Book;
import com.dupenghao.service.HessianService;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by 杜鹏豪 on 2022/9/28.
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class HessianServiceImpl implements HessianService ,ApplicationContextAware,InitializingBean{

    @Autowired
    private BookMapper bookMapper;

    private ApplicationContext context;

    @Autowired
    private Javers javers;

    @Override
    public void sycnBookData(List<Book> books) {
        List<Book> findBooks = bookMapper.getAll();
        Diff diff = javers.compareCollections(findBooks, books, Book.class);
        diff.groupByObject().forEach(changesByObject -> {
            if(changesByObject.getGlobalId().getTypeName().equals(Book.class.getName())){
                if(changesByObject.get().get(0) instanceof NewObject){
                    log.info("add");
                    bookMapper.save((Book) changesByObject.get().get(0).getAffectedObject().get());
                }else if (changesByObject.get().get(0) instanceof ObjectRemoved){
                    log.info("remove");
                    bookMapper.delete((String) changesByObject.get().get(0).getAffectedLocalId());
                }else if(changesByObject.get().get(0) instanceof ValueChange){
                    log.info("update");
                    bookMapper.delete((String) changesByObject.get().get(0).getAffectedLocalId());
                    bookMapper.save((Book) changesByObject.get().get(0).getAffectedObject().get());
                }
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("afterProperties!");
    }
}
