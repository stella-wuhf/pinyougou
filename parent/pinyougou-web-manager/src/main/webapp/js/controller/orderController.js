//控制层
app.controller('orderController' ,function($scope,$controller,$location,ordersService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        ordersService.findAll().success(
            function(response){
                $scope.list=response;
            }
        );
    }
    // 显示状态
    $scope.status = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
    //分页
    $scope.findPage=function(page,rows){
        ordersService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }






    $scope.searchEntity={};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        ordersService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    // $scope.entity={goods:{},goodsDesc:{},itemList:[]}

    $scope.uploadFile = function(){
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(function(response){
            if(response.flag){
                // 获得url
                $scope.image_entity.url =  response.message;
            }else{
                alert(response.message);
            }
        });
    }

    // 获得了image_entity的实体的数据{"color":"褐色","url":"http://192.168.209.132/group1/M00/00/00/wKjRhFn1bH2AZAatAACXQA462ec665.jpg"}
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};

    $scope.add_image_entity = function(){
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_iamge_entity = function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    // 查询一级分类列表:
    $scope.selectItemCat1List = function(){
        itemCatService.findByParentId(0).success(function(response){
            $scope.itemCat1List = response;
        });
    }

    // 查询二级分类列表:
    $scope.$watch("entity.goods2.category1Id",function(newValue,oldValue){
        itemCatService.findByParentId(newValue).success(function(response){
            $scope.itemCat2List = response;
        });
    });

    // 查询三级分类列表:
    $scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
        itemCatService.findByParentId(newValue).success(function(response){
            $scope.itemCat3List = response;
        });
    });

    // 查询模块ID   在分类表中查询
    //findOne方法根据3级分类的主键,查询三级分类的信息,将对象中包含的模板id赋值给页面上的模板id展示
    $scope.$watch("entity.goods2.category3Id",function(newValue,oldValue){
        itemCatService.findOne(newValue).success(function(response){
            $scope.entity.goods.typeTemplateId = response.typeId;
        });
    });


});
