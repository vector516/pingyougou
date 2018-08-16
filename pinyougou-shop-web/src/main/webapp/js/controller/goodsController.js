//控制层
app.controller('goodsController', function ($scope, $controller, goodsService,$location, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }


    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id= $location.search()['id'];//获取参数值
        if(id==null){
            return ;
        }

        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);

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


    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){
        var items= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(items,'attributeName',specName);
        if(object==null){
            return false;
        }else{
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else{
                return false;
            }
        }
    }




    //保存
    $scope.save = function () {
        //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction=editor.html();

        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            // $scope.entity.goodsDesc.introduction = editor.html();
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    // //重新查询
                    // $scope.reloadList();//重新加载
                    alert('保存成功');
                    $scope.entity = {};
                    editor.html('');//清空富文本编辑器
                    location.href="goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    // $scope.image_entity.url="#";
    //添加商品图片上传的功能
    $scope.uploadFile = function () {
        uploadService.upload().success(function (response) {
            if (response.success) {
                //如果上传成功，取出url
                $scope.image_entity.url = response.message;
            } else {
                alert(response.message);
            }

        }).error(function () {
            alert("上传发生错误");
        });
    }

    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


    //列表中移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }


    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];
    $scope.marketstatus = ['已下架','已上架'];

    $scope.showCat = function () {
        goodsService.showCat().success(
            function (response) {
                if (response.success) {
                    $scope.catList = response;
                } else {
                    alert(response.message);
                }
            })
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

    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        if (object != null) {
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {
                //取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);//移除选项
                //如果选项都取消了，将此条记录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName": name, "attributeValue": [value]});
        }
    }


    //创建SKU列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//初始
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }
//添加列值
    addColumn = function (list, columnName, conlumnValues) {
        var newList = [];//新的集合
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < conlumnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }


    $scope.itemCatList=[];//商品分类列表

    //加载商品分类列表--->根据商品的id构建一个新的数组为name
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function (response) {
            for(var i=0;i<response.length;i++){
                $scope.itemCatList[response[i].id]=response[i].name;
            }
        })
    }



    //全选和子选控制全选
    $(function () {
        //全选控制子选
        $("#selall02").click(function () {
            $(".checkOne02").prop("checked", $("#selall02").prop("checked"));
        })

    })


    $scope.selectIds02 = [];

    $scope.auditStatus=[];

    //更新复选框
    $scope.updateSelection02 = function ($event, id,auditStatus) {

        //子选控制反选
        if ($(".checkOne02").length == $(".checkOne02:checked").length) {
            $("#selall02").prop("checked", true);
        } else {
            $("#selall02").prop("checked", false);
        }

        //点击时将选中的id放入数组中
        if ($event.target.checked) {
            $scope.selectIds02.push(id);
            $scope.auditStatus.push(auditStatus);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            var as=$scope.auditStatus.indexOf(auditStatus)
            $scope.selectIds02.splice(idx, 1);
            $scope.auditStatus.splice(as, 1);
        }
    }





    //商品上下架
    $scope.updateMaketable=function (status) {
        // //上架需要判断是否已审核
        if(status==1){
            for(var i=0;i<$scope.auditStatus.length;i++){
                if($scope.auditStatus[i]!="1"){
                    alert("请选择已审核商品")
                    return;
                }
            }
        }

        goodsService.updateMaketable($scope.selectIds02,status).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectIds02=[];//清空ID集合
                    $scope.auditStatus=[];
                }else {
                    alert(response.message);
                }
            }
        )
    }



});
