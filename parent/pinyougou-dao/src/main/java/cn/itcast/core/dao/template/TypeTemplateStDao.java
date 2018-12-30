package cn.itcast.core.dao.template;

import cn.itcast.core.pojo.template.TypeTemplateSt;
import cn.itcast.core.pojo.template.TypeTemplateStQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TypeTemplateStDao {
    int countByExample(TypeTemplateStQuery example);

    int deleteByExample(TypeTemplateStQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(TypeTemplateSt record);

    int insertSelective(TypeTemplateSt record);

    List<TypeTemplateSt> selectByExample(TypeTemplateStQuery example);

    TypeTemplateSt selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TypeTemplateSt record, @Param("example") TypeTemplateStQuery example);

    int updateByExample(@Param("record") TypeTemplateSt record, @Param("example") TypeTemplateStQuery example);

    int updateByPrimaryKeySelective(TypeTemplateSt record);

    int updateByPrimaryKey(TypeTemplateSt record);

    List<Map> findOptionList(String name);
}