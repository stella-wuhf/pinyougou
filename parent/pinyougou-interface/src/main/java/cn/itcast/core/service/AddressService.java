package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.Areas;
import cn.itcast.core.pojo.address.Cities;
import cn.itcast.core.pojo.address.Provinces;

import java.util.List;

public interface AddressService {
    List<Address> findListByLoginUser(String name);

    List<Provinces> findProvice();

    List<Cities> findCity(String provinceId);

    List<Areas> findArea(String cityId);

    Address findOne(Long id);

    void update(Address address);

    void delete(Long id);

    void setDefault(Long id, String name);

    void add(Address address, String name);
}
