//自定义服务实现然后实现service层和controller层的代码重用
app.service("sellerService", function ($http) {

    this.add=function (entity) {
        return $http.post("/seller/add.do",entity);
    }



})