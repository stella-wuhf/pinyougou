package cn.itcast.core.service;


import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.BrandstDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.good.Brandst;
import cn.itcast.core.pojo.good.BrandstQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;

    //查询所有品牌数据
    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    //分页查询品牌列表
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //分页插件
        PageHelper.startPage(pageNum, pageSize);
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //分页查询品牌列表,带条件
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {
        //分页插件
        PageHelper.startPage(pageNum, pageSize);
        //准备条件
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //判断品牌名不为空,且不是空格
        if (null != brand.getName() && !"" .equals(brand.getName().trim())) {
            criteria.andNameLike("%" + brand.getName().trim() + "%");
        }
        //判断品牌首字母不为空,且不是空格
        if (null != brand.getFirstChar() && !"" .equals(brand.getFirstChar().trim())) {
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //添加品牌
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    //查询单个品牌
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    //更新品牌
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    //删除品牌
    @Override
    public void dele(Long[] ids) {
        //delete from tb_brand where id in (...);
        BrandQuery query = new BrandQuery();
        query.createCriteria().andIdIn(Arrays.asList(ids));
        brandDao.deleteByExample(query);

        /*for(Long id:ids){
            brandDao.deleteByPrimaryKey(id);
        }*/
    }

    //查询全部品牌列表,返回时一个list<map>
    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }


    /**
     * 商家品牌申请查询
     *
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @Autowired
    private BrandstDao brandstDao;

    @Override
    public PageResult searchStaus(Integer pageNum, Integer pageSize, Brandst brandst) {
        PageHelper.startPage(pageNum, pageSize);
        BrandstQuery query = new BrandstQuery();
        BrandstQuery.Criteria criteria = query.createCriteria();
        criteria.andSellerIdEqualTo(brandst.getSellerId());
        if (null != brandst.getStatus() && !"" .equals(brandst.getStatus().trim())) {
            criteria.andStatusEqualTo(brandst.getStatus().trim());
        }
        //判断品牌名不为空,且不是空格
        if (null != brandst.getName() && !"" .equals(brandst.getName().trim())) {
            criteria.andNameLike("%" + brandst.getName().trim() + "%");
        }
        //判断品牌首字母不为空,且不是空格
        if (null != brandst.getFirstChar() && !"" .equals(brandst.getFirstChar().trim())) {
            criteria.andFirstCharEqualTo(brandst.getFirstChar().trim());
        }
        Page<Brandst> page = (Page<Brandst>) brandstDao.selectByExample(query);
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 添加审核品牌
     *
     * @param
     */
    @Override
    public void addStaus(Brandst brandst) {
        brandst.setStatus("0");
        brandstDao.insertSelective(brandst);
    }

    /**
     * 删除
     *
     * @param ids
     */
    @Override
    public void delestu(Long[] ids) {

        BrandstQuery query = new BrandstQuery();
        query.createCriteria().andIdIn(Arrays.asList(ids));
        brandstDao.deleteByExample(query);

    }

    @Override
    public Brandst findOnest(Long id) {
        return brandstDao.selectByPrimaryKey(id);
    }

    @Override
    public void updatest(Brandst brandst) {
        brandstDao.updateByPrimaryKeySelective(brandst);
    }

    @Override
    public List<Map> selectOptionListSt(String name) {
        return brandstDao.selectOptionListSt(name);
    }

    @Override
    public PageResult searchst(Integer pageNum, Integer pageSize, Brandst brandst) {
        //分页插件
        PageHelper.startPage(pageNum, pageSize);
        //准备条件
        BrandstQuery brandstQuery = new BrandstQuery();
        BrandstQuery.Criteria criteria = brandstQuery.createCriteria();
        //判断品牌名不为空,且不是空格
        if (null != brandst.getName() && !"" .equals(brandst.getName().trim())) {
            criteria.andNameLike("%" + brandst.getName().trim() + "%");
        }
        //判断品牌首字母不为空,且不是空格
        if (null != brandst.getFirstChar() && !"" .equals(brandst.getFirstChar().trim())) {
            criteria.andFirstCharEqualTo(brandst.getFirstChar().trim());
        }
        Page<Brandst> page = (Page<Brandst>) brandstDao.selectByExample(brandstQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatusst(Long[] ids, String status) {
        Brandst brandst = new Brandst();
        brandst.setStatus(status);
        for (Long id : ids) {
            brandst.setId(id);
            brandstDao.updateByPrimaryKeySelective(brandst);
        }
    }

}
