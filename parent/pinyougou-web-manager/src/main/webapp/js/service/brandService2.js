// 定义服务层:
app.service("brandService2",function($http){
	
	this.add = function(entity){
		return $http.post("../brand/add.do",entity);
	}
	
	this.update=function(entity){
		return $http.post("../brand/update.do",entity);
	}
	
	this.findOne=function(id){
		return $http.get("../brand/findOne.do?id="+id);
	}
	
	this.dele = function(ids){
		return $http.get("../brand/dele.do?ids="+ids);
	}
	
	this.search = function(page,rows,searchEntity){
		return $http.post("../brand/searchst.do?pageNum="+page+"&pageSize="+rows,searchEntity);
	}

	this.selectOptionList = function(){
		return $http.get("../brand/selectOptionList.do");
	}
    //更改状态
    this.updateStatus = function(ids,status){
        return $http.get('../brand/updateStatus.do?ids='+ids+"&status="+status);
    }


});

