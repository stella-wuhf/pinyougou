package cn.itcast.core.dao.specification;

import cn.itcast.core.pojo.specification.SpecificationSt;
import cn.itcast.core.pojo.specification.SpecificationStQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SpecificationStDao {
    int countByExample(SpecificationStQuery example);

    int deleteByExample(SpecificationStQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(SpecificationSt record);

    int insertSelective(SpecificationSt record);

    List<SpecificationSt> selectByExample(SpecificationStQuery example);

    SpecificationSt selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SpecificationSt record, @Param("example") SpecificationStQuery example);

    int updateByExample(@Param("record") SpecificationSt record, @Param("example") SpecificationStQuery example);

    int updateByPrimaryKeySelective(SpecificationSt record);

    int updateByPrimaryKey(SpecificationSt record);

    List<Map> selectOptionListst(String name);
}