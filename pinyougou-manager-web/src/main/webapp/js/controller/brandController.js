app.controller('brandController', function ($scope, $controller, brandService) {
    $controller('baseController',{$scope:$scope});//继承

    //******************************************************************************//

    $scope.save = function (entity) {
        var methodName="add";
        if(entity.id!=null){
            methodName="update";
        }

        brandService.save(methodName,entity).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                    // window.location.reload()
                }
                else {
                    alert(response.message);
                }
            }
        )
    }


    /************************************************************/
    //删除功能的实现




    $scope.delete = function () {
        if ($scope.selectIds.length == 0) {
            alert("请选择要删除的数据");
        } else {
            brandService.delete($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reload();
                    } else {
                        alert(response.message);
                    }
                }
            )
        }
    }

    ///////////////////////////////////////////////////////////

    /***********************************************************************/
    //修改功能的实现,主要是查询单条数据并回显
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );

    }


    /***********************************************************************/
    //分页查询功能------>注意查询的功能是在分页的基础上添加条件,所以当不传条件的时候就是普通的分页查询,
    //而search()方法被reloadList调用,而reloadList()被pagenationConf调用
    // /*
    //条件查询


    $scope.search = function (page, rows, eleAaa) {
        brandService.searchBrand(page, rows, eleAaa).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }


    ///////////////////////////////////////////////////////////////////////////////////

});



