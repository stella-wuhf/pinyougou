//首页控制器
app.controller("indexController", function ($scope, loginService, orderService) {
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

    //定义搜索对象的结构
    $scope.searchMap = {'pageNo': 1, 'pageSize': 3, 'userId': ''};
    //
   // $scope.resultMap={'totalPages':null,'orderVoList':null};
    //订单状态   状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
    $scope.status = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
    $scope.status2 = ["","等待买家付款","等待卖家发货","等待卖家发货","商品正在路上","交易成功","交易关闭","待评价"];
    //搜索
    $scope.search = function () {

        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);//转换为数字
        //入参: searchMap
        // 返回值:resultMap
        // URL : itemsearch/search.do
        orderService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
               // $scope.resultMap.totalPages = response.totalPages;
               // $scope.resultMap.orderVoList = response.orderVoList;
                buildPageLabel();//构建分页栏
                //$scope.searchMap.pageNo=1;//查询后显示第一页
            }
        );


    }

    //构建分页栏
    buildPageLabel = function () {
        //构建分页栏
        $scope.pageLabel = [];

        var firstPage = 1;//开始页码
        var lastPage = $scope.resultMap.totalPages;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点

        if ($scope.resultMap.totalPages > 5) {  //如果页码数量大于5

            if ($scope.searchMap.pageNo <= 3) {//如果当前页码小于等于3 ，显示前5页
                lastPage = 5;
                $scope.firstDot = false;//前面没点
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) {//显示后5页
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false;//后边没点
            } else {  //显示以当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }


        //构建页码
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }

    //分页查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();//查询
    }

    //取消订单
    $scope.cancleOrder = function (orderId) {
       if( confirm("你确定要取消么?")){
           orderService.cancleOrder(orderId).success(
               function (response) {
                   alert(response.message);
                   $scope.search();
               }
           );
       }
    }
});