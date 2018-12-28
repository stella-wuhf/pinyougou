package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.SpecificationVo;

import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    //查询全部
    @Override
    public List<Specification> findAll() {
        return specificationDao.selectByExample(null);
    }

    //分页查询,带条件
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        //分页插件
        PageHelper.startPage(page, rows);

        //准备条件
        SpecificationQuery specificationQuery = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();

        if (null != specification.getSpecName() && !"".equals(specification.getSpecName().trim())) {
            criteria.andSpecNameLike("%" + specification.getSpecName().trim() + "%");
        }

        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        return new PageResult(p.getTotal(), p.getResult());
    }

    //添加规格,并添加规格项里的规格选项结果集
    @Override
    public void add(SpecificationVo specificationVo) {
        //获取包装类中的对象
        Specification specification = specificationVo.getSpecification();
        List<SpecificationOption> optionList = specificationVo.getSpecificationOptionList();
        //添加规格
        specificationDao.insertSelective(specification);
        //数据id回显,需要在xxxMapper.xml中设置id回显
        Long specificationId = specification.getId();
        //遍历集合,添加到规格选项中
        for (SpecificationOption specificationOption : optionList) {
            //添加外键
            specificationOption.setSpecId(specificationId);
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    //修改规格
    @Override
    public void update(SpecificationVo specificationVo) {
        //获取包装类中的对象
        Specification specification = specificationVo.getSpecification();
        List<SpecificationOption> optionList = specificationVo.getSpecificationOptionList();
        //更新规格
        specificationDao.updateByPrimaryKeySelective(specification);
        //更新规格选项
        //获取规格id,准备条件
        Long speId = specification.getId();
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(speId);
        //先删除掉原先的规格选项
        specificationOptionDao.deleteByExample(specificationOptionQuery);
        //在把新的添加到规格选项中
        if (optionList != null && optionList.size() > 0) {
            for (SpecificationOption specificationOption : optionList) {
                specificationOption.setSpecId(speId);
                specificationOptionDao.insertSelective(specificationOption);
            }
        }

    }

    //查询一个规格包装对象  修改之数据回显
    @Override
    public SpecificationVo findOne(Long id) {
        SpecificationVo vo = new SpecificationVo();
        //包装对象中添加规格
        Specification specification = specificationDao.selectByPrimaryKey(id);
        vo.setSpecification(specification);
        //包装对象中添加规格选项数据列表
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
        //排序
        specificationOptionQuery.setOrderByClause("orders desc");
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(specificationOptionQuery);
        vo.setSpecificationOptionList(specificationOptionList);

        return vo;
    }

    //删除规格,包含规格下面的选项
    //思路:先遍历数组,确定每个规格的id,根据id,即speId,找到全部的规格选项,删除,最后再删除掉规格本身
    @Override
    public void delete(Long[] ids) {
        for (Long speId : ids) {
            SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
            specificationOptionQuery.createCriteria().andSpecIdEqualTo(speId);
            //删除掉所有的以该id为外键的规格选项
            specificationOptionDao.deleteByExample(specificationOptionQuery);
            //删除id所对应的规格本身
            specificationDao.deleteByPrimaryKey(speId);
        }
    }

    //查询全部规格列表,返回是一个list<map>
    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
