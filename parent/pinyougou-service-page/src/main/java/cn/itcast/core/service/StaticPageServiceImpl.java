package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 静态化页面管理
 */
@Service
public class StaticPageServiceImpl implements StaticPageService, ServletContextAware {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;

    //静态化处理的方法
    public void toStaticPage(Long id) {//id为商品id
        //创建freemarker
        Configuration conf = freeMarkerConfigurer.getConfiguration();
        //输出路径
        String outPath = getPath("/" + id + ".html");
        //创建集合
        Map<String, Object> map = new HashMap<>();
        //查询商品
        Goods goods = goodsDao.selectByPrimaryKey(id);
        map.put("goods",goods);
        //查询商品详情 goodsDesc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        map.put("goodsDesc",goodsDesc);
        //查询三级分类 itemCat1 itemCat2  itemCat3
        map.put("itemCat1",itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
        map.put("itemCat2",itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
        map.put("itemCat3",itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        //查询库存结果集
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1");
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        map.put("itemList",itemList);

        Writer out = null;
        try {
            //创建模型
            Template template = conf.getTemplate("item.ftl");
            //创建write
            out = new OutputStreamWriter(new FileOutputStream(outPath),"utf-8");
            //执行静态化
            template.process(map, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //获取全路径
    public String getPath(String path) {
        return servletContext.getRealPath(path);
    }

    private ServletContext servletContext;

    //ServletContextAware接口的重写方法,获取servletContext对象
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
