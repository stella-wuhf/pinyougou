app.controller('indexController', function ($scope,$controller,indexService,loginService) {
    // loginService,
    // $controller('baseController',{$scope:$scope});//继承
    $scope.showName = function () {
        loginService.showName().success(function (response) {
            $scope.loginName = response.username;
            $scope.curTime = response.curTime;
        });
    };

    $scope.findindexCatList = function () {
        indexService.findindexCatList().success(function (response) {
            $scope.newOderNUM = response.newOderNum;
            $scope.orderNum = response.monthOrderNum;

        });
    }





});