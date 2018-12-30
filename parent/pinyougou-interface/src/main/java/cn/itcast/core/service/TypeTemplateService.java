package cn.itcast.core.service;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateSt;
import entity.PageResult;

import java.util.List;
import java.util.Map;


public interface TypeTemplateService {
    public PageResult search(Integer page, Integer rows, TypeTemplate tt);

    void add(TypeTemplate tt);

    TypeTemplate findOne(Long id);

    void update(TypeTemplate tt);

    void delete(Long[] ids);

    List<Map> findBySpecList(Long id);

    /**
     * 后台申请
     * @param page
     * @param rows
     * @param tt
     * @return
     */

    PageResult searchst(Integer page, Integer rows, TypeTemplateSt tt);

    void addst(TypeTemplateSt tt);

    void updatest(TypeTemplateSt tt);

    TypeTemplateSt findOnest(Long id);

    void deletest(Long[] ids);

    List<TypeTemplateSt> findOptionList(String name);
}
