package cn.itcast.core.controller;

import cn.itcast.core.pojo.specification.SpecificationSt;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.SpecificationStVo;

import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /*查询全部*/
//    @RequestMapping("/findAll")
//    public List<Specification> findAll(){
//       return specificationService.findAll();
//    }

    /*带条件,分页查询 */
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SpecificationSt specificationSt){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        specificationSt.setSellerId(name);
        return specificationService.searchst(page,rows,specificationSt);
    }

    /*增加规格*/
    @RequestMapping("add")
    public Result add(@RequestBody SpecificationStVo specificationStVo){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            specificationService.addst(specificationStVo,name);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(true,"添加失败");
        }
    }

    /*更新规格*/
    @RequestMapping("update")
    public Result update(@RequestBody SpecificationStVo specificationStVo){
        try {
            specificationService.updatest(specificationStVo);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(true,"修改失败");
        }
    }

    /*查询一个规格包装对象  修改之数据回显*/
    @RequestMapping("findOne")
    public SpecificationStVo findOne(Long id){
       return specificationService.findOnest(id);
    }

    /*删除规格*/
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            specificationService.deletest(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(true,"删除失败");
        }
    }

    /*查询全部品牌列表,返回时一个list<map>*/
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return specificationService.selectOptionListst(name);
    }

}
