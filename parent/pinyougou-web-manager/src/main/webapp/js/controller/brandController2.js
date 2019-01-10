// 定义控制器:
app.controller("brandController2",function($scope,$controller,$http,brandService2){
	// AngularJS中的继承:伪继承
	$controller('baseController',{$scope:$scope});
	

	// 查询一个:
	$scope.findById = function(id){
		brandService.findOne(id).success(function(response){
			// {id:xx,name:yy,firstChar:zz}
			$scope.entity = response;
		});
	}
	
	// 删除品牌:
	$scope.dele = function(){
		if(confirm("你确定要删除么?")){
            brandService.dele($scope.selectIds).success(function(response){
                // 判断保存是否成功:
                if(response.flag==true){
                    // 保存成功
                    // alert(response.message);
                    $scope.reloadList();
                    $scope.selectIds = [];
                }else{
                    // 保存失败
                    alert(response.message);
                }
            });
		}
	}
    $scope.searchEntity={};

    // 假设定义一个查询的实体：searchEntity
    $scope.search = function(page,rows){
        // 向后台发送请求获取数据:
        brandService2.search(page,rows,$scope.searchEntity).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }



    // 显示状态
    $scope.status = ["未审核","审核通过","审核未通过","关闭"];

    $scope.itemCatList = [];
    // 显示分类:
    $scope.findItemCatList = function(){

        itemCatService.findAll().success(function(response){
            for(var i=0;i<response.length;i++){
                $scope.itemCatList[response[i].id] = response[i].name;
            }
        });
    }

    // 审核的方法:
    $scope.updateStatus = function(status){
        brandService2.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }
});
