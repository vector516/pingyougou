//控制层
app.controller('seckillGoodsController',function($scope,seckillGoodsService,$location,$interval){
    //读取列表数据绑定到表单中
    $scope.findList=function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list=response;
            }
        )
    }


    //秒杀商品的详情页--->$location用来接收另一个页面跳转时传递的请求参数
    $scope.findOne=function () {
        seckillGoodsService.findOne($location.search()['id']).success(
            function(response){
                $scope.entity= response;

                //秒杀倒计时服务
                allsecond =Math.floor( (  new Date($scope.entity.endTime).getTime()- (new Date().getTime())) /1000); //总秒数
                time=$interval(function () {
                    allsecond = allsecond - 1;
                    if(allsecond == 0){
                        $interval.cancel(time);
                    }else{
                        $scope.allsecondStr = convertTimeString(allsecond);
                    }
                },1000);

            }
        );
    }

    //转换秒为天小时分钟秒格式  XXX天 10:22:33
    convertTimeString=function(allsecond){
        var days= Math.floor( allsecond/(60*60*24));//天数
        var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
        var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
        var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
        var timeString="";
        if(days>0){
            timeString=days+"天 ";
        }
        return timeString+hours+":"+minutes+":"+seconds;
    }

    //提交秒杀订单
    $scope.submitOrder=function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
                if(response.success){
                    alert("下单成功,请在1分钟内完成支付");
                    location.href="pay.html";
                }else {
                    alert(response.message);
                }
            }
        )
    }

});