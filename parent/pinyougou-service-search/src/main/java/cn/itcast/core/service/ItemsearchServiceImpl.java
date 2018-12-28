package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * 搜索管理
 */
@Service
public class ItemsearchServiceImpl implements ItemsearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //搜索
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //关键字空格处理
        String keywords = searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

        Map<String, Object> map = searchList(searchMap);//查询结果集,总页数等
        List<String> categoryList = searchCategory(searchMap);//查询商品分类,封装了分类名称
        map.put("categoryList", categoryList);

        //在查询商品分类的基础上,查询第一个分类的品牌列表和规格列表
        if (null != categoryList && categoryList.size() > 0) {
            String categoryName = categoryList.get(0);//获取第一个分类名称
            //通过分类名称获取模板id
            Object typeId = redisTemplate.boundHashOps("itemCat").get(categoryName);
            //通过模板id,获取商品列表
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            //通过模板id,获取规格列表
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }

    //搜获商品分类结果集
    public List<String> searchCategory(Map<String, String> searchMap) {
        //$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
        //---------关键词---------------------
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        Query query = new SimpleQuery(criteria);
        //--------设置分组条件----------------
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //执行条件分组查询
        GroupPage<Item> page = solrTemplate.queryForGroupPage(query, Item.class);
        GroupResult<Item> category = page.getGroupResult("item_category");
        //创建集合,封装获取到的分类信息
        ArrayList<String> categoryList = new ArrayList<>();
        List<GroupEntry<Item>> content = category.getGroupEntries().getContent();
        for (GroupEntry<Item> itemGroupEntry : content) {
            categoryList.add(itemGroupEntry.getGroupValue());
        }
        return categoryList;
    }

    //搜索结果集/总页数/总条数
    public Map<String, Object> searchList(Map<String, String> searchMap) {
        Map<String, Object> map = new HashMap<>();
        //$scope.searchMap={'keywords':'','category':'','brand':'','spec':'{}','price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
        //---------关键词---------------------
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords").trim());
        HighlightQuery highlightQuery = new SimpleHighlightQuery(criteria);

        //---------过滤条件-----------------------
        //1. 商品分类 'category':''
        if (null != searchMap.get("category") && !"".equals(searchMap.get("category").trim())) {
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category").trim());
            FilterQuery filterQuery = new SimpleFilterQuery(categoryCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //2. 品牌 'brand':''
        if (null != searchMap.get("brand") && !"".equals(searchMap.get("brand").trim())) {
            Criteria categoryCriteria = new Criteria("item_brand").is(searchMap.get("brand").trim());
            FilterQuery filterQuery = new SimpleFilterQuery(categoryCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //3. 规格  'spec':{网络": "联通3G",机身内存": "16G"}
        if (null != searchMap.get("spec") && !"".equals(searchMap.get("spec").trim())) {
            Map<String, String> specMap = JSON.parseObject(searchMap.get("spec"), Map.class);
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> specOption : entries) {
                Criteria specCriteria = new Criteria("item_spec_" + specOption.getKey())
                        .is(specOption.getValue());
                FilterQuery filterQuery = new SimpleFilterQuery(specCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }
        //4 .价格区间 0-500  3000-*  'price':''
        if (null != searchMap.get("price") && !"".equals(searchMap.get("price").trim())) {
            String[] p = searchMap.get("price").trim().split("-"); //将字符串截取成为数组
            Criteria priceCriteria = null;
            if (!searchMap.get("price").trim().contains("*")) { //判断是否是3000-* 这种区间
                //不包含*,则是区间
                priceCriteria = new Criteria("item_price").
                        between(p[0], p[1], true, false);
            } else {
                //包含*,则是大于低值
                priceCriteria = new Criteria("item_price").greaterThan(p[0]);
            }
            FilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }

        //---------排序 : 根据价格/上架时间 -----------------------
        //'sort':'','sortField':''   两个参数要么都有要么都没有,所以判断一个就行
        if (null != searchMap.get("sortField") && !"".equals(searchMap.get("sortField").trim())) {
            if ("ASC".equals(searchMap.get("sort").trim())) {
                highlightQuery.addSort(new Sort(Sort.Direction.ASC,"item_"+searchMap.get("sortField")));
            } else {
                highlightQuery.addSort(new Sort(Sort.Direction.DESC,"item_"+searchMap.get("sortField")));
            }
        }

        //---------分页--偏移量(当前页-1 * 每页显示),每页显示数--------------
        String pageNo = searchMap.get("pageNo");
        String pageSize = searchMap.get("pageSize");
        highlightQuery.setOffset((Integer.parseInt(pageNo) - 1) * Integer.parseInt(pageSize));
        highlightQuery.setRows(Integer.parseInt(pageSize));
        //---------开启高亮-------------
        HighlightOptions options = new HighlightOptions();
        //需要高亮的域名
        options.addField("item_title");
        //前缀
        options.setSimplePrefix("<span style='color:red'>");
        //后缀
        options.setSimplePostfix("</span>");
        //设置高连条件
        highlightQuery.setHighlightOptions(options);
        //执行高亮分页查询
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(highlightQuery, Item.class);
        //获取高亮集合
        List<HighlightEntry<Item>> highlighted = page.getHighlighted();
        if (null != highlighted && highlighted.size() > 0) {
            for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                Item item = itemHighlightEntry.getEntity();//item中的title是不高亮的标题
                List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();//该list只有一个对象
                if (null != highlights && highlights.size() > 0) {//一个对象中有两个属性,其中snipplets是一个list且只有一个对象
                    //如果高亮里有值,将高亮的title设置到item里
                    item.setTitle(highlights.get(0).getSnipplets().get(0));//对象就是加亮的title
                }
            }
        }
        //-------获取普通集合------------------
        List<Item> docs = page.getContent();
        map.put("rows", docs);
        //获取总页数
        int totalPages = page.getTotalPages();
        map.put("totalPages", totalPages);
        //获取总条数
        long totalElements = page.getTotalElements();
        map.put("total", totalElements);

        return map;
    }
}
