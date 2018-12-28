package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 广告管理
 */
@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;
    @Autowired
    private RedisTemplate redisTemplate; //使用Redis的hash类型,键为广告分类id,值为list集合

    //分页条件查询
    @Override
    public PageResult search(Integer page, Integer rows, Content content) {
        PageHelper.startPage(page, rows);
        Page<Content> p = (Page<Content>) contentDao.selectByExample(null);
        return new PageResult(p.getTotal(), p.getResult());
    }

    //添加广告
    @Override
    public void add(Content content) {
        contentDao.insertSelective(content);
        //新增广告,设计到缓存库,所以需要将原先的分类id对应的缓存数据删除掉
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
    }

    //查询单个广告实例
    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    //更新广告
    //修改广告,可能会涉及到两个缓存数据(修改广告的分类id,致使一个分类减少一个,另一个分类增加一个,都需要重新获取缓存数据)
    @Override
        public void update(Content content) {
        //1. 因为只是修改广告其他信息,其id值保持不变,根据广告主键,查询到数据库中的数据
        Content c = contentDao.selectByPrimaryKey(content.getId()); //数据库中的就广告信息
        //2: 涉及到两个数据库,所以事务执行有要求,为了保持事务的一致性,要先执行数据库操作,后执行缓存库操作
        contentDao.updateByPrimaryKeySelective(content);
        //3: 比较两个分类id是是否相同
        if (c.getCategoryId()!=content.getCategoryId()){
            //不同,则需要删除两个对应的缓存数据
            redisTemplate.boundHashOps("content").delete(c.getCategoryId());
        }
        //删除另一个缓存数据
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
    }

    //批量删除
    @Override
    public void delete(Long[] ids) {
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                Content content = contentDao.selectByPrimaryKey(id);
                contentDao.deleteByPrimaryKey(id);
                //删除广告,设计到缓存库,所以需要将原先的分类id对应的缓存数据删除掉
                redisTemplate.boundHashOps("content").delete(content.getCategoryId());
            }
        }
    }


    //通过广告分类ID查询  广告结果集
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        //1. 先重缓冲库中查询
        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
        //2. 判断该集合是否为空,长度是否为0
        if (contentList == null || contentList.size() == 0) {
            //不存在,则从数据库中查询
            ContentQuery contentQuery = new ContentQuery();
            ContentQuery.Criteria criteria = contentQuery.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            //只查询启用的广告
            criteria.andStatusEqualTo("1");
            //排序
            contentQuery.setOrderByClause("sort_order desc");
            contentList = contentDao.selectByExample(contentQuery);

            //并保存一份到缓存库中
            redisTemplate.boundHashOps("content").put(categoryId, contentList);
            //设置缓存事件
            redisTemplate.boundHashOps("content").expire(24, TimeUnit.HOURS);
        }
        return contentList;
    }
}
