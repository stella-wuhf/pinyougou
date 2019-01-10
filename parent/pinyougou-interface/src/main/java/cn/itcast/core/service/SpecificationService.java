package cn.itcast.core.service;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationSt;
import entity.PageResult;
import pojogroup.SpecificationStVo;
import pojogroup.SpecificationVo;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    PageResult search(Integer page, Integer rows, Specification specification);

    List<Specification> findAll();

    void add(SpecificationVo specificationVo);

    SpecificationVo findOne(Long id);

    void update(SpecificationVo specificationVo);

    void delete(Long[] ids);

    //查询全部规格列表,返回是一个list<map>
    List<Map> selectOptionList();

    /**
     * 商家后台申请
     * @param page
     * @param rows
     * @param specificationSt
     * @return
     */
    PageResult searchst(Integer page, Integer rows, SpecificationSt specificationSt);

    void addst(SpecificationStVo specificationStVo, String name);

    void updatest(SpecificationStVo specificationStVo);

    SpecificationStVo findOnest(Long id);

    void deletest(Long[] ids);

    List<Map> selectOptionListst(String name);

    void updateStatusst(Long[] ids, String status);
}
