//服务层
app.service('orderService',function($http){

    //分页查询订单集合
    this.search=function(searchMap){
        return $http.post('order/search.do',searchMap);
    }

    //取消订单
    this.cancleOrder=function(orderId){
        return $http.get('../order/cancleOrder.do?orderId='+orderId);
    }
});
