package cn.itcast.core.service;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.dao.address.AreasDao;
import cn.itcast.core.dao.address.CitiesDao;
import cn.itcast.core.dao.address.ProvincesDao;
import cn.itcast.core.pojo.address.*;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 收件地址管理
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;
    @Autowired
    private ProvincesDao provincesDao;
    @Autowired
    private CitiesDao citiesDao;
    @Autowired
    private AreasDao areasDao;

    //查询当前登录人全部收件地址
    @Override
    public List<Address> findListByLoginUser(String name) {
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(name);
        return addressDao.selectByExample(addressQuery);
    }

    @Override
    public List<Provinces> findProvice() {

        List<Provinces> provinces = provincesDao.selectByExample(null);
        return provinces;
    }

    @Override
    public List<Cities> findCity(String provinceId) {
        CitiesQuery citiesQuery = new CitiesQuery();
        citiesQuery.createCriteria().andProvinceidEqualTo(provinceId);
        return citiesDao.selectByExample(citiesQuery);
    }

    @Override
    public List<Areas> findArea(String cityId) {
        AreasQuery query = new AreasQuery();
        query.createCriteria().andCityidEqualTo(cityId);
        return areasDao.selectByExample(query);
    }

    @Override
    public Address findOne(Long id) {
        return addressDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Address address) {
        Long id = address.getId();
        AddressQuery query = new AddressQuery();
        query.createCriteria().andIdEqualTo(id);
        addressDao.updateByExampleSelective(address, query);
    }

    @Override
    public void delete(Long id) {
        addressDao.deleteByPrimaryKey(id);
    }

    @Override
    public void setDefault(Long id, String name) {
        Address address = new Address();
        address.setIsDefault("0");
        AddressQuery query = new AddressQuery();
        query.createCriteria().andUserIdEqualTo(name);
        addressDao.updateByExampleSelective(address, query);

        AddressQuery newQuery = new AddressQuery();
        newQuery.createCriteria().andIdEqualTo(id);
        address.setIsDefault("1");
        addressDao.updateByExampleSelective(address, newQuery);
    }

    @Override
    public void add(Address address, String name) {
        address.setUserId(name);
        address.setIsDefault("0");
        addressDao.insertSelective(address);
    }
}
