package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 模板管理
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;

    //带条件 分页查询
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate tt) {
        //1.查询模板全部结果集
        List<TypeTemplate> typeTemplateList = findAll();
        //2.将模板结果集保存到缓存库中
        for (TypeTemplate typeTemplate : typeTemplateList) {
            // 2.1 k:模板id v:品牌列表list<map>
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);
            // 2.2 k:模板id v:规格列表list<map>,需要规格项结果集,所以调用其他方法查询到完整的
            List<Map> specList = findBySpecList(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
        }

        //分页助手
        PageHelper.startPage(page, rows);
        //准备条件
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();
        typeTemplateQuery.setOrderByClause("id desc");

        if (null != tt.getName() && !"".equals(tt.getName().trim())) {
            criteria.andNameLike("%" + tt.getName().trim() + "%");
        }
        //条件查询
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(p.getTotal(), p.getResult());
    }

    //增加模板
    @Override
    public void add(TypeTemplate tt) {
        typeTemplateDao.insertSelective(tt);
    }

    //查询一个模板
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    //更新模板
    @Override
    public void update(TypeTemplate tt) {
        typeTemplateDao.updateByPrimaryKeySelective(tt);
    }

    //删除模板
    @Override
    public void delete(Long[] ids) {
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        typeTemplateQuery.createCriteria().andIdIn(Arrays.asList(ids));
        typeTemplateDao.deleteByExample(typeTemplateQuery);
    }

    //根据模板id查询规格列表,返回值是List<Map>
    @Override
    public List<Map> findBySpecList(Long id) {
        //先查询到模板对象
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //获取到规格id集合的json串 [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //将该json串变为List<Map>  一个map对象中包含id,text
        List<Map> mapList = JSON.parseArray(specIds, Map.class);
        //向每个map中添加options属性
        for (Map map : mapList) {
            //查询到以指定规格id为外键的全部规格选项
            SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
            //map中的数据类型为object,需要强转为long类型,但是不能转,需要先转成简单数据类型,再转成long类型
            specificationOptionQuery.createCriteria().andSpecIdEqualTo((long)(Integer)map.get("id"));
            List<SpecificationOption> options = specificationOptionDao.selectByExample(specificationOptionQuery);
            //添加到map集合中
            map.put("options",options);
        }

        return mapList;
    }

    //查询所有
    public List<TypeTemplate> findAll(){
       return typeTemplateDao.selectByExample(null);
    }
}
