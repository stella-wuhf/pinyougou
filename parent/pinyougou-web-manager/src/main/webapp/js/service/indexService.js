app.service("indexService",function($http){

    this.findindexCatList = function(){
        return $http.get("../index/findindexCatList.do");
    }

});