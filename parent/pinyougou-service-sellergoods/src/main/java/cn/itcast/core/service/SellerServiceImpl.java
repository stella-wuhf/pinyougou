package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家管理
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;

    //保存商家入驻信息
    @Override
    public void add(Seller seller) {
        //密码加密,使用 spring security的加密算法 BCrypt
        seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));
        //未审核的商品,状态为1
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    //分页查询未审核商家   分页查询全部商家
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //分页插件
        PageHelper.startPage(page, rows);

        //准备条件
        SellerQuery query = new SellerQuery();
        SellerQuery.Criteria criteria = query.createCriteria();
        if (null != seller.getStatus()) {
            //商家状态为 0,为审核状态
            criteria.andStatusEqualTo(seller.getStatus());
        }
        if (null != seller.getName() && !"".equals(seller.getName().trim())) {
            criteria.andNameLike("%" + seller.getName().trim() + "%");
        }
        if (null != seller.getNickName() && !"".equals(seller.getNickName().trim())) {
            criteria.andNickNameLike("%" + seller.getNickName().trim() + "%");
        }
        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(query);

        return new PageResult(p.getTotal(), p.getResult());
    }

    //查询一个实例
    @Override
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id);
    }

    //修改商家状态
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}
