 //控制层 
app.controller('itemCatController2' ,function($window,$scope,$controller,itemCatService2){
	
	$controller('baseController',{$scope:$scope});//继承

	
	// 根据父ID查询分类
    $scope.parentId=0;
	$scope.findByParentId =function(parentId){
		itemCatService2.findByParentId(parentId).success(function(response){
			$scope.list=response;
		});
	}


	
	// 定义一个变量记录当前是第几级分类
	$scope.grade = 1;
	
	$scope.setGrade = function(value){
		$scope.grade = value;
	}
	
	$scope.selectList = function(p_entity){
		
		if($scope.grade == 1){
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}
		if($scope.grade == 2){
			$scope.entity_1 = p_entity;
			$scope.entity_2 = null;
		}
		if($scope.grade == 3){
			$scope.entity_2 = p_entity;
		}
		
		$scope.findByParentId(p_entity.id);
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
        itemCatService2.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
              $window.location.reload();//刷新列表
                $scope.selectIds =[];
            }else{
                alert(response.message);
            }
        });
    }










});
