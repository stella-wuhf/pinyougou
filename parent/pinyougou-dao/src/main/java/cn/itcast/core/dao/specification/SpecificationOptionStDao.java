package cn.itcast.core.dao.specification;

import cn.itcast.core.pojo.specification.SpecificationOptionSt;
import cn.itcast.core.pojo.specification.SpecificationOptionStQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpecificationOptionStDao {
    int countByExample(SpecificationOptionStQuery example);

    int deleteByExample(SpecificationOptionStQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(SpecificationOptionSt record);

    int insertSelective(SpecificationOptionSt record);

    List<SpecificationOptionSt> selectByExample(SpecificationOptionStQuery example);

    SpecificationOptionSt selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SpecificationOptionSt record, @Param("example") SpecificationOptionStQuery example);

    int updateByExample(@Param("record") SpecificationOptionSt record, @Param("example") SpecificationOptionStQuery example);

    int updateByPrimaryKeySelective(SpecificationOptionSt record);

    int updateByPrimaryKey(SpecificationOptionSt record);
}