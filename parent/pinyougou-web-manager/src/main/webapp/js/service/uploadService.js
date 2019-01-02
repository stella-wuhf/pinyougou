app.service("uploadService",function($http){
	
	// this.uploadFile = function(){
	// 	// 向后台传递数据:
	// 	var formData = new FormData();
	// 	// 向formData中添加数据:
	// 	formData.append("file",file.files[0]);
	//
	// 	return $http({
	// 		method:'post',
	// 		url:'../upload/uploadFile.do',
	// 		data:formData,
	// 		headers:{'Content-Type':undefined} ,// Content-Type : text/html  text/plain
	// 		transformRequest: angular.identity
	// 	});
	// }
    this.uploadFileExcel = function(){
        // 向后台传递数据:
        var formData = new FormData();
        var file=document.getElementById("fileUpload").files[0];
        // 向formData中添加数据:
        formData.append("file",file);

        return $http({
            method:'post',
            url:'../upload/uploadFileExcel.do',
            data:formData,
            headers:{'Content-Type':undefined} ,// Content-Type : text/html  text/plain
            transformRequest: angular.identity
        });
    }
});