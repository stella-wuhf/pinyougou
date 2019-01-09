package cn.itcast.core.controller;


import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.Areas;
import cn.itcast.core.pojo.address.Cities;
import cn.itcast.core.pojo.address.Provinces;
import cn.itcast.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address1")
public class AddressController {

    @Reference
    private AddressService addressService;


    //查询当前用户地址信息
    @RequestMapping("/findAddressList")
    public List<Address> findAddressList() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Address> listByLoginUser = addressService.findListByLoginUser(name);
        return listByLoginUser;
    }

    /**
     * 查询省
     *
     * @return
     */
    @RequestMapping("/findprovice")
    public List<Provinces> findProvice() {
        List<Provinces> findProvice = addressService.findProvice();
        return findProvice;
    }

    /**
     * 查询市
     *
     * @param provinceId
     * @return
     */
    @RequestMapping("/findcity")
    public List<Cities> findCity(String provinceId) {
        return addressService.findCity(provinceId);
    }

    /**
     * 查询县
     *
     * @param cityId
     * @return
     */
    @RequestMapping("/findarea")
    public List<Areas> findArea(String cityId) {
        return addressService.findArea(cityId);
    }

    /**
     * 查新一个实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Address findOne(Long id) {
        return addressService.findOne(id);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Address address) {
        try {
            //获取用户id
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //将商家id保存到包装对象的商品表中
            addressService.add(address, name);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    @RequestMapping("/update")
    public Result update(@RequestBody Address address) {
        try {
            addressService.update(address);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }


    }

    @RequestMapping("/delete")
    public Result delete(Long id) {
        try {
            addressService.delete(id);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }

    }

    //设为默认地址
    @RequestMapping("/setDefault")
    public Result setDefault(Long id) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            addressService.setDefault(id, name);
            return new Result(true, "设置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "设置失败");
        }

    }


}
