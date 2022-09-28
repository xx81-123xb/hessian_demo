package com.dupenghao.mapper;

import com.dupenghao.pojo.Book;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by 杜鹏豪 on 2022/9/28.
 */
@Mapper
public interface BookMapper {

    @Insert("insert into tbl_book (id,name,type,price,author) values(#{id},#{name},#{type},#{price},#{author})")
    void save(Book book);

    @Delete("delete from tbl_book where id = #{id} ")
    void delete(String id);

    void update(Book book);

    @Select("select * from tbl_book ")
    List<Book> getAll();

}
