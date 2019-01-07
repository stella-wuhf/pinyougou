//服务层
app.service('itemService',function($http){
    //增加
    this.findItemListByGoodsId=function(goodsId){
        return $http.get('../item/findItemListByGoodsId.do?goodsId='+goodsId);
    }

    //查询单个实体 findOne
    this.findOne=function(itemId){
        return $http.get('../item/findOne.do?itemId='+itemId);
    }
});
