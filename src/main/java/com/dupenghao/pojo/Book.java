package com.dupenghao.pojo;

import lombok.Data;
import org.javers.core.metamodel.annotation.Id;

import java.io.Serializable;

/**
 * Created by 杜鹏豪 on 2022/9/28.
 */
@Data
public class Book implements Serializable{

    @Id
    private String id;
    private String name;
    private String type;
    private String price;
    private String author;

}
