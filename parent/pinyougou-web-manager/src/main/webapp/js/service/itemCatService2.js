//服务层
app.service('itemCatService2',function($http){
	    	

	this.findByParentId = function(parentId){
		return $http.get("../itemCat/findByParentIdst.do?parentId="+parentId);
	}

    //更改状态
    this.updateStatus = function(ids,status){
        return $http.get('../itemCat/updateStatusst.do?ids='+ids+"&status="+status);
    }
});
