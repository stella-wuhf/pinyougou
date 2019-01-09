// 定义控制器:
app.controller('addressController', function ($scope, $controller, addressService) {
    // AngularJS中的继承:伪继承
    $controller('baseController', {$scope: $scope});
    //获取当前用户的地址列表
    $scope.findAddressList=function(){
        addressService.findAddressList().success(
            function(response){
                $scope.addressList=response;
            }
        );
    }


    // 查询省列表:
    $scope.findProvice=function(){
        addressService.findprovice().success(function(response){
            $scope.proviceList = response;
        });
    }

    // 查询市分类列表:
    $scope.$watch("entity.provinceId",function(newValue,oldValue){
        addressService.findcity(newValue).success(function(response){
            $scope.cityList = response;
        });
    });

    // 查询县分类列表:
    $scope.$watch("entity.cityId",function(newValue,oldValue){
        addressService.findarea(newValue).success(function(response){
            $scope.areaList = response;
        });
    });
    //查询一个用于回显

    $scope.findOne=function(id){
        addressService.findOne(id).success(
            function(response){
                $scope.entity= response;
            }
        );
    }
    //添加
    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=addressService.update( $scope.entity ); //修改
        }else{
            serviceObject=addressService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){

                if(response.flag){
                    //重新查询
                    alert(response.message);
                    $scope.findAddressList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //根据id删除
    $scope.delete=function (id) {
        addressService.delete(id).success(function (response) {
            if (response.flag){
                alert(response.message);
                $scope.findAddressList();
            }
            else {
                alert(response.message);
            }
        })
    }
    //设为默认地址
    $scope.setDefault=function (id) {
        addressService.setDefault(id).success(function (response) {
            if (response.flag){
                alert(response.message);
                //location.href="home-setting-address.html";
                $scope.findAddressList();
            }
            else {
                alert(response.message);
            }
        })

    }

    //选择地址别名
    $scope.toAlias=function (alias) {
        $scope.entity.alias=alias;
    }

});



