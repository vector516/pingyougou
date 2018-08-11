app.service("loginService",function ($http) {
    //定义一个方法访问controller获得服务器存储的用户名
    this.loginName=function () {
        return $http.get("../login/name.do");
    }


})