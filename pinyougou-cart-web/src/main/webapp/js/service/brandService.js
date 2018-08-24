//自定义服务实现然后实现service层和controller层的代码重用
app.service("brandService", function ($http) {
    this.searchBrand = function (page, rows, eleAaa) {
        return $http.post("../brand/search.do?page=" + page + "&rows=" + rows, eleAaa);
    }

    this.save = function (methodName,eleAaa) {
        return $http.post("../brand/"+methodName+".do", eleAaa);
    }

    this.delete = function (ids) {
        return $http.get('../brand/delete.do?ids=' + ids);
    }

    this.findOne = function (id) {
        return $http.post("../brand/findOne.do?id=" + id);
    }

    this.selectOptionList=function () {
        return $http.post("../brand/selectOptionList.do");
    }
})