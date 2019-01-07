package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.GoodsVo;

import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品管理
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao; //商品
    @Autowired
    private GoodsDescDao goodsDescDao; //商品详情
    @Autowired
    private ItemDao itemDao; //库存
    @Autowired
    private ItemCatDao itemCatDao; //分类
    @Autowired
    private SellerDao sellerDao;//商家
    @Autowired
    private BrandDao brandDao;//品牌


    //添加商品
    @Override
    public void add(GoodsVo vo) {
        //添加商品 前端传来9个值,需要设置其他的字段值
        //商家id在controller层中设置,商品审核状态,
        vo.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(vo.getGoods());
        //-----------------------------------------------------------------------------------
        //添加商品详情对象,需要商品的id,在商品的xml中设置id回显
        //需要设置: 商家id
        vo.getGoodsDesc().setGoodsId(vo.getGoods().getId());
        goodsDescDao.insertSelective(vo.getGoodsDesc());
        //-----------------------------------------------------------------------------------
        //添加库存对象结果集
        //先判断商品表中是否启用规格
        if ("1".equals(vo.getGoods().getIsEnableSpec())) {
            //如果启用,该库存结果集才有值
            List<Item> itemList = vo.getItemList();

            for (Item item : itemList) {//item为每一个库存对象
                //设置标题,标题=商品名+ 规格1+规格2 ..
                String title = vo.getGoods().getGoodsName();
                //获取库存对象中的规格json串  {"机身内存":"16G","网络":"联通3G"}
                String spec = item.getSpec();
                //将规格json串转变为 map对象,便于取值
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                //获取map的键值对,再遍历键值对,取值,和商品名拼接
                Set<Map.Entry<String, String>> entries = specMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);

                //设置图片,选择保存的图片集合中的第一张作为展示的图片
                //该字段在数据库中是以json串的形式保存的 [{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
                String itemImages = vo.getGoodsDesc().getItemImages();
                //将规格json串转变为 List<Map>对象,便于取值
                List<Map> images = JSON.parseArray(itemImages, Map.class);
                if (images != null && images.size() > 0) {
                    //取集合中的第一个图片的url键的值
                    item.setImage((String) images.get(0).get("url"));
                }

                //设置三级分类的id和名称
                Long category3Id = vo.getGoods().getCategory3Id();
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(category3Id);
                item.setCategoryid(category3Id);
                item.setCategory(itemCat.getName());

                //设置添加时间和修改时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());

                //设置商品表的id,本表的外键
                item.setGoodsId(vo.getGoods().getId());

                //设置商家id和商家名称
                item.setSellerId(vo.getGoods().getSellerId());
                Seller seller = sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId());
                item.setSeller(seller.getName());

                //设置品牌名称
                Brand brand = brandDao.selectByPrimaryKey(vo.getGoods().getBrandId());
                item.setBrand(brand.getName());

                //保存
                itemDao.insertSelective(item);
            }
        }

    }

    //分页查询商品列表
    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        //分页插件
        PageHelper.startPage(page, rows);

        //准备条件
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();

        //判断商品状态
        if (null != goods.getAuditStatus() && !"".equals(goods.getAuditStatus())) {
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }
        //判断商品名称
        if (null != goods.getGoodsName() && !"".equals(goods.getGoodsName().trim())) {
            criteria.andGoodsNameLike("%" + goods.getGoodsName().trim() + "%");
        }
        //判断商家id是否存在,如果存在,是商家在查询商品,如果不存在,是运营商在查询商品
        if (null != goods.getSellerId()) {
            criteria.andSellerIdEqualTo(goods.getSellerId());
        }
        //只查询没有被标记为删除的商品
//        criteria.andIsDeleteIsNull();

        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getTotal(), p.getResult());
    }

    //查询单个商品包装类实例
    @Override
    public GoodsVo findOne(Long id) {
        //实例化包装对象
        GoodsVo vo = new GoodsVo();
        //根据商品id查询商品对象
        Goods goods = goodsDao.selectByPrimaryKey(id);
        vo.setGoods(goods);
        //根据商品id查询商品详情对象
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        vo.setGoodsDesc(goodsDesc);
        //根据商品id作为外键查询库存对象结果集
        //准备条件
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        vo.setItemList(itemList);
        return vo;
    }

    //更新商品
    @Override
    public void update(GoodsVo vo) {
        //更新商品
        goodsDao.updateByPrimaryKeySelective(vo.getGoods());
        //更新商品详情
        goodsDescDao.updateByPrimaryKeySelective(vo.getGoodsDesc());
        //更新库存
        //准备条件
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(vo.getGoods().getId());
        //先删除掉原先的库存
        itemDao.deleteByExample(itemQuery);
        //再添加新的库存
        //先判断商品表中是否启用规格
        if ("1".equals(vo.getGoods().getIsEnableSpec())) {
            //如果启用状态,该库存结果集才有值
            List<Item> itemList = vo.getItemList();

            for (Item item : itemList) {//item为每一个库存对象
                //设置标题,标题=商品名+ 规格1+规格2 ..
                String title = vo.getGoods().getGoodsName();
                //获取库存对象中的规格json串  {"机身内存":"16G","网络":"联通3G"}
                String spec = item.getSpec();
                //将规格json串转变为 map对象,便于取值
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                //获取map的键值对,再遍历键值对,取值,和商品名拼接
                Set<Map.Entry<String, String>> entries = specMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);

                //设置图片,选择保存的图片集合中的第一张作为展示的图片
                //该字段在数据库中是以json串的形式保存的 [{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
                String itemImages = vo.getGoodsDesc().getItemImages();
                //将规格json串转变为 List<Map>对象,便于取值
                List<Map> images = JSON.parseArray(itemImages, Map.class);
                if (images != null && images.size() > 0) {
                    //取集合中的第一个图片的url键的值
                    item.setImage((String) images.get(0).get("url"));
                }

                //设置三级分类的id和名称
                Long category3Id = vo.getGoods().getCategory3Id();
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(category3Id);
                item.setCategoryid(category3Id);
                item.setCategory(itemCat.getName());

                //设置添加时间和修改时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());

                //设置商品表的id,本表的外键
                item.setGoodsId(vo.getGoods().getId());

                //设置商家id和商家名称
                item.setSellerId(vo.getGoods().getSellerId());
                Seller seller = sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId());
                item.setSeller(seller.getName());

                //设置品牌名称
                Brand brand = brandDao.selectByPrimaryKey(vo.getGoods().getBrandId());
                item.setBrand(brand.getName());

                //保存
                itemDao.insertSelective(item);
            }
        }

    }

    @Autowired
    private JmsTemplate jmsTemplate;//消息中间件
    @Autowired
    private Destination topicPageAndSolrDestination;//主题模式

    //修改商品的审核状态
    //在进行商品审核通过后更新到solr索引库,在商品删除后删除solr索引库中相应的记录
    @Override
    public void updateStatus(Long[] ids, String status) {
        Goods goods = new Goods();
        goods.setAuditStatus(status);
        for (Long id : ids) {
            goods.setId(id);
            //1: 更新数据库商品状态
            goodsDao.updateByPrimaryKeySelective(goods);
            //如果审核通过,保存
            if ("1".equals(status)) {
                /*
                    2. sellergoods-service项目更改数据库中商品的审核状态
                    3. sellergoods-service项目向activeMq服务器发送消息(发布订阅模式), 消息为审核商品的id
                    4. search-service接收商品id, 根据id查询商品详细数据放入solr索引库
                    5. page-service接收商品id, 根据id查询商品详细数据生成静态化页面
                * */
                //2. 向activeMq服务器发送消息,消息为商品id
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(id));
                    }
                });
            }
        }
    }

    @Autowired
    private Destination queueSolrDeleteDestination;

    //批量删除商品
    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");
        for (Long id : ids) {
            goods.setId(id);
            //1: 更新数据库商品状态
            goodsDao.updateByPrimaryKeySelective(goods);
            /*
              2. sellergoods-service项目更改数据库中商品的删除状态
              3. sellergoods-service项目向activeMq服务器发送消息(点对点模式), 消息为删除商品的id
              4. search-service项目接收商品id, 根据id删除solr索引库中对应的数据
            * */
            //2. 向activeMq服务器发送消息,消息为商品id
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });
        }
    }

    /**
     * 商家上下架管理
     * @param ids
     * @param dStatus
     * @param name
     */
    @Autowired
    private Destination topicPageAndSolrDestinationManager;
    @Override
    public void updateDelStatus(Long[] ids, String dStatus, String name) {
        Goods goods = new Goods();
        goods.setIsDelete(dStatus);
        if (null!=dStatus&&!"".equals(dStatus.trim())){
            for (Long id : ids) {
                GoodsQuery query=new GoodsQuery();
                query.createCriteria().andIdEqualTo(id);
                goodsDao.updateByExampleSelective(goods,query);

//直接将id和上架或者下架状态发过去，根据状态来进行下一步操作
                jmsTemplate.send(topicPageAndSolrDestinationManager, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        MapMessage map = session.createMapMessage();
                        map.setLong("id",id);
                        map.setString("dStatus",dStatus);
                        return map;
                    }
                });
            }
        }
    }
    //根据商家id查询全部商品
    @Override
    public List<Goods> findGoodsListBySellerId(String sellerId) {
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.createCriteria().andSellerIdEqualTo(sellerId);
        return goodsDao.selectByExample(goodsQuery);
    }
}
