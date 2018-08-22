app.controller("brandController", function ($scope, $controller,brandService) {
    $controller('baseController',{$scope:$scope});//继承

    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }

    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            })
    }

    $scope.save = function () {
        brandService.save($scope.entity).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message)
                }
            }
        )
    }

    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }

    $scope.save = function () {
        var obj=null;
        //判断id是否存在，存在就执行更新方法，不存在执行保存方法
        if ($scope.entity.id != null) {
            obj=brandService.update($scope.entity);
        }else{
            obj=brandService.add($scope.entity);
        }
        obj.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message)
                }
            }
        )
    }

    $scope.dele = function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                }
            }
        )
    }

    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            }
        )
    }

})