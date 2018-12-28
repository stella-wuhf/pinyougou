package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentCategoryDao;
import cn.itcast.core.pojo.ad.ContentCategory;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  广告分类管理
 */
@Service
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private ContentCategoryDao contentCategoryDao;

    //广告分类 分页条件查询
    @Override
    public PageResult search(Integer page, Integer rows, ContentCategory contentCategory) {
        //分页插件
        PageHelper.startPage(page,rows);
        Page<ContentCategory> p= (Page<ContentCategory>) contentCategoryDao.selectByExample(null);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //保存广告分类
    @Override
    public void add(ContentCategory contentCategory) {
        contentCategoryDao.insertSelective(contentCategory);
    }

    //查询单个广告分类
    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryDao.selectByPrimaryKey(id);
    }

    //修改广告分类
    @Override
    public void update(ContentCategory contentCategory) {
        contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
    }

    //批量删除广告分类
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            contentCategoryDao.deleteByPrimaryKey(id);
        }
    }

    //查询全部广告分类
    @Override
    public List<ContentCategory> findAll() {
        return contentCategoryDao.selectByExample(null);
    }
}
