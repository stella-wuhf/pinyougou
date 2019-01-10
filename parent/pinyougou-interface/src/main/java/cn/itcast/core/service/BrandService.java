package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Brandst;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<Brand> findAll();

    public PageResult findPage(Integer pageNum, Integer pageSize);

    public void add(Brand brand);

    public Brand findOne(Long id);

    public void update(Brand brand);

    void dele(Long[] ids);

    PageResult search(Integer pageNum, Integer pageSize, Brand brand);

    List<Map> selectOptionList();

    PageResult searchStaus(Integer pageNum, Integer pageSize, Brandst brandst);

    void addStaus(Brandst brandst);

    void delestu(Long[] ids);

    Brandst findOnest(Long id);

    void updatest(Brandst brandst);

    List<Map> selectOptionListSt(String name);

    void updateStatusst(Long[] ids, String status);

    PageResult searchst(Integer pageNum, Integer pageSize, Brandst brandst);
}
