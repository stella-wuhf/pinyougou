// 定义服务层:
app.service('addressService',function($http){
    // 查询当前用户地址信息
    this.findAddressList=function(){
        return $http.get('../address1/findAddressList.do');
    }
    this.findprovice = function(){
        return $http.get('../address1/findprovice.do');
    }
    this.findcity = function(provinceId){
        return $http.get('../address1/findcity.do?provinceId='+provinceId);
    }
    this.findarea = function(cityId){
        return $http.get('../address1/findarea.do?cityId='+cityId);
    }
    //查询实体
    this.findOne=function(id) {
        return $http.get('../address1/findOne.do?id=' + id);
    }
    // 添加
    this.add=function (entity) {
        return $http.post('../address1/add.do',entity);
    }
    //修改
    this.update=function (entity) {
        return $http.post('../address1/update.do',entity);

    }
    //删除
    this.delete=function (id) {
        return $http.get('../address1/delete.do?id='+id);
    }
    //设为默认
    this.setDefault=function (id) {
        return $http.get('../address1/setDefault.do?id='+id);
    }

});


