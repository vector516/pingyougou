app.service("uploadService",function ($http) {
    this.uploadFile=function () {
        var formData=new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:'post',
            url:"../upload.do",
            data:formData,
            headers:{'Content-Type':undefined},//通过设置‘Content-Type’: undefined，这样浏览器会帮我们把Content-Type 设置为 multipart/form-data.
            transformRequest:angular.identity//通过设置 transformRequest: angular.identity ，anjularjs transformRequest function 将序列化我们的formdata object.
        })
    }
})