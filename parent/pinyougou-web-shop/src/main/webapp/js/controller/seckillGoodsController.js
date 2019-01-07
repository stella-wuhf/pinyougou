//控制层
app.controller('seckillController', function ($scope, $controller, seckillGoodsService, itemService, goodsService) {

    $controller('baseController',{$scope: $scope});//继承

    // 显示状态
    $scope.status = ["未审核", "审核通过", "审核未通过", "关闭"];

    // 查询全部商品信息:
    $scope.findAllGoodsList = function () {
        goodsService.findGoodsListBySellerId().success(function (response) {
            $scope.goodsList = response;
        });
    }

    // 查询库存结果集   在库存表中查询 根据商品id
    $scope.$watch("entity.goodsId", function (newValue, oldValue) {
        itemService.findItemListByGoodsId(newValue).success(function (response) {
            $scope.itemList = response;
        });
    });

    // 查询单个库存   在库存表中查询 根据库存id
    $scope.$watch("entity.itemId", function (newValue, oldValue) {
        itemService.findOne(newValue).success(function (response) {
            $scope.entity.price = response.price;
            $scope.entity.title = response.title;
            $scope.num = response.num;
        });
    });


    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = seckillGoodsService.update($scope.entity); //修改
        } else {
            serviceObject = seckillGoodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.flag) {
                    //重新查询
                    alert(response.message);
                    location.href = "seckill_goods_edit.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //返回按钮  返回到商品展示页
    $scope.back = function () {
        location.href = "seckill_goods_edit.html";
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        alert()
        seckillGoodsService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }


});	
