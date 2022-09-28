package com.dupenghao.config;

import com.caucho.hessian.client.HessianProxyFactory;
import com.dupenghao.mapper.BookMapper;
import com.dupenghao.pojo.Book;
import com.dupenghao.service.HessianService;
import com.dupenghao.service.impl.HessianServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by 杜鹏豪 on 2022/9/28.
 */
@Configuration
@Slf4j
public class BeanConfig implements ApplicationContextAware{

    private ApplicationContext context;

    @Bean
    public Javers javers(){
        return JaversBuilder.javers().build();
    }

    @Bean("/hessian")
    public HessianServiceExporter exportHelloHessian(HessianServiceImpl hessianService){
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(hessianService);
        exporter.setServiceInterface(HessianService.class);
        return exporter;
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void sycn(){
        String port = context.getEnvironment().getProperty("server.port");
        if(port.equals("8002")){
            return;
        }
        log.info("sycn start");
        BookMapper bookMapper = context.getBean(BookMapper.class);
        List<Book> books = bookMapper.getAll();
        String url="http://localhost:8002/hessian";
        HessianService hessianService = getHessianService(url);
        hessianService.sycnBookData(books);
        log.info("sycn end");
    }

//    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    private HessianService getHessianService(String url){
        try {
            HessianProxyFactory proxyFactory = new HessianProxyFactory();
            Object result = proxyFactory.create(HessianService.class, url);
            return (HessianService) result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            log.error("hessian error");
            return null;
        }
    }

}
