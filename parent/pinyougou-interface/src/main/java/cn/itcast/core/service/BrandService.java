package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Brand;
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
}
