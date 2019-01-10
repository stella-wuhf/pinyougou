package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.specification.SpecificationOptionStDao;
import cn.itcast.core.dao.specification.SpecificationStDao;
import cn.itcast.core.pojo.specification.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.SpecificationStVo;
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
/**
 * 后台商家
 */
    /**
     * 查询商家规格申请
     * @param page
     * @param rows
     * @param specificationSt
     * @return
     */
    @Autowired
    private SpecificationStDao specificationStDao;
    @Autowired
    private SpecificationOptionStDao specificationOptionStDao;
    @Override
    public PageResult searchst(Integer page, Integer rows, SpecificationSt specificationSt) {
        //分页插件
        PageHelper.startPage(page, rows);

        //准备条件
        SpecificationStQuery specificationQuery=new SpecificationStQuery();
        SpecificationStQuery.Criteria criteria = specificationQuery.createCriteria();
        if (specificationSt.getSellerId()!=null){
            criteria.andSellerIdEqualTo(specificationSt.getSellerId());
        }
        if (null!=specificationSt.getStatus()&&!"".equals(specificationSt.getStatus().trim())){
            criteria.andStatusEqualTo(specificationSt.getStatus().trim());
        }

        if (null != specificationSt.getSpecName() && !"".equals(specificationSt.getSpecName().trim())) {
            criteria.andSpecNameLike("%" + specificationSt.getSpecName().trim() + "%");
        }

        Page<SpecificationSt> p = (Page<SpecificationSt>) specificationStDao.selectByExample(specificationQuery);
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public void addst(SpecificationStVo specificationStVo,String name) {
        SpecificationSt specificationSt = specificationStVo.getSpecificationSt();
        specificationSt.setSellerId(name);
        specificationSt.setStatus("0");
        specificationStDao.insertSelective(specificationSt);
        Long id = specificationSt.getId();
        List<SpecificationOptionSt> list = specificationStVo.getSpecificationOptionStList();
        for (SpecificationOptionSt st : list) {
         st.setSpecId(id);
         st.setSellerId(name);
         st.setStatus("0");
         specificationOptionStDao.insertSelective(st);
        }
    }
    @Override
    public void updatest(SpecificationStVo specificationStVo) {
        SpecificationSt specificationSt = specificationStVo.getSpecificationSt();
        specificationStDao.updateByPrimaryKey(specificationSt);
        Long id = specificationSt.getId();
        List<SpecificationOptionSt> list = specificationStVo.getSpecificationOptionStList();
        SpecificationOptionStQuery query=new SpecificationOptionStQuery();
        SpecificationOptionStQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        specificationOptionStDao.deleteByExample(query);
        String name = specificationSt.getSellerId();
        for (SpecificationOptionSt st : list) {
            st.setSpecId(id);
            st.setSellerId(name);
            st.setStatus("0");
            specificationOptionStDao.insertSelective(st);
        }
    }


    @Override
    public SpecificationStVo findOnest(Long id) {
        SpecificationStVo vo = new SpecificationStVo();
        SpecificationSt specificationSt = specificationStDao.selectByPrimaryKey(id);
        vo.setSpecificationSt(specificationSt);
        SpecificationOptionStQuery query=new SpecificationOptionStQuery();
        query.createCriteria().andSpecIdEqualTo(id);
        query.setOrderByClause("orders desc");
        List<SpecificationOptionSt> list = specificationOptionStDao.selectByExample(query);
        vo.setSpecificationOptionStList(list);
        return vo;

    }
    //    @Autowired
//    private SpecificationStDao specificationStDao;
//    @Autowired
//    private SpecificationOptionStDao specificationOptionStDao;
    @Override
    public void deletest(Long[] ids) {
        for (Long id : ids) {
            specificationStDao.deleteByPrimaryKey(id);
            SpecificationOptionStQuery query=new SpecificationOptionStQuery();
            query.createCriteria().andSpecIdEqualTo(id);
            specificationOptionStDao.deleteByExample(query);
        }
    }

    @Override
    public List<Map> selectOptionListst(String name) {
        return specificationStDao.selectOptionListst(name);
    }

    @Override
    public void updateStatusst(Long[] ids, String status) {
        SpecificationSt specificationSt = new SpecificationSt();
        specificationSt.setStatus(status);
        for (Long id : ids) {
            specificationSt.setId(id);
            specificationStDao.updateByPrimaryKeySelective(specificationSt);
        }
    }



}
