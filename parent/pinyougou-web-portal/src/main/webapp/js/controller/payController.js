app.controller('payController' ,function($scope ,$location,payService){
	
	//生成二维码
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){//Map<String,String>
				
				//显示订单号和金额
				$scope.money= (response.total_fee/100).toFixed(2);
				$scope.out_trade_no=response.out_trade_no;
				
				//生成二维码
				//静态二维码: 扫只能知道把钱转给谁  但是金额是你自己填写的  生成一份一直放在那里
				//动态二维码: 现使用现生成  不但有转钱给谁 金额也是固定的  付还是不付(密码)  微信官方 2小时
                var qr=new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    value:response.code_url,//引导手机上微信客户端 去微信服务器付钱  钱已经固定了
                    level:'H'
                });
				 
				 queryPayStatus();//调用查询
				
			}	
		);	
	}
	
	//调用查询
	queryPayStatus=function(){
		payService.queryPayStatus($scope.out_trade_no).success(
			function(response){
				if(response.flag){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if(response.message=='二维码超时'){
						alert("你也太慢了吧,超时了,别付了")
						//$scope.createNative();//重新生成二维码
					}else{
						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
	
});