 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,$location,typeTemplateService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	// //查询实体
	// $scope.findOne=function(id){
	// 	goodsService.findOne(id).success(
	// 		function(response){
	// 			$scope.entity= response;
	// 		}
	// 	);
	// }


    //查询实体02
    $scope.findOne = function (id) {
        // var id= $location.search()['id'];//获取参数值
        if(id==null){
            return ;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                // //向富文本编辑器添加商品介绍
                // editor.html($scope.entity.goodsDesc.introduction);

                //显示图片列表
                $scope.entity.goodsDesc.itemImages=
                    JSON.parse($scope.entity.goodsDesc.itemImages);

                //显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);

                //规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);

                //SKU列表规格列转换
                for( var i=0;i<$scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec =
                        JSON.parse( $scope.entity.itemList[i].spec);
                }

            }
        );
    }



    //读取一级分类
    $scope.selectItemCat1List = function () {

        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    }

    //读取二级分类
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        //$watch可以监控指定的值,发生变化执行指定的函数
        $scope.itemCat3List = {};
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        )

    })

    //读取三级分类
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        //$watch可以监控指定的值,发生变化执行指定的函数

        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        )

    })

    //三级分类选择后  读取模板ID
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId; //更新模板ID
            }
        );
    });



    //模板ID选择后  更新品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                if($location.search()['id']==null){
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }

            }
        );

        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );
    })







	//保存
	$scope.save=function(){
		var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}
		);
	}




	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}


    //商家管理后台逻辑删除
    $scope.delete=function(){
        //获取选中的复选框
        goodsService.delete( $scope.selectIds ).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }


	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态

	$scope.itemCatList=[];//商品分类列表

	//查询商品分类
	$scope.findItemCatList=function () {
        itemCatService.findAll().success(
        	function (response) {
        		for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
		)
    }


    //商品审核更新商品的状态
	$scope.updateStatus=function (status) {
		goodsService.updateStatus($scope.selectIds,status).success(
			function (response) {
				if(response.success){
					$scope.reloadList();
                    $scope.selectIds=[];//清空ID集合
				}else {
					alert(response.message);
				}
            }
		)

    }



});
