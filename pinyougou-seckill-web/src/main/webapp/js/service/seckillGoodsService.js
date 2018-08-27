//服务层
app.service('seckillGoodsService',function($http){
    //发送请求读取秒杀列表

    this.findList=function () {
    return $http.get("/seckillGoods/findList.do")

    }

    //秒杀商品的详情页
    this.findOne=function(id){
        return $http.get('seckillGoods/findOneFromRedis.do?id='+id);
    }

    //提交秒杀订单--->传递秒杀商品的参数,注意秒杀只有一个商品构成一个订单
    this.submitOrder=function (seckillId) {
        return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
    }


})



