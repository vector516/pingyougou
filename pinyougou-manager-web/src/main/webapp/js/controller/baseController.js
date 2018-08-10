app.controller('baseController' ,function($scope){



    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 5,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };


    $scope.selectIds = [];

    //更新复选框
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);
        }
    }

    $scope.searchEntity = {};

    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage, $scope.searchEntity);
    };



});