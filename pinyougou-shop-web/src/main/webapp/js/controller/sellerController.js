app.controller('sellerController', function ($scope, $controller, sellerService) {
    $controller('baseController',{$scope:$scope});//继承

    $scope.add=function () {
        sellerService.add($scope.entity).success(
            function (response) {
                if(response.success){
                    location.href="/shoplogin.html";
                }else {
                    alert(response.message);
                }

        })

    }



})
