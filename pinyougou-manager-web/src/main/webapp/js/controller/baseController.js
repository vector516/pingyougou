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

        //子选控制反选
        if($(".checkOne").length==$(".checkOne:checked").length){
            $("#selall").prop("checked",true);
        }else{
            $("#selall").prop("checked",false);
        }

        //点击时将选中的id放入数组中
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

    //全选和子选控制全选
    $(function () {
        //全选控制子选
        $("#selall").click(function () {
            $(".checkOne").prop("checked",$("#selall").prop("checked"));
        })

    })



});