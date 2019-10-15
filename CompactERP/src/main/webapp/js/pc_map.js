registerInit('pc_map', function (form) {

	$(function(){
		
//		postJson('/map/selectTableDeploy.do', {}, function (result) {
//		   console.log(result);
//        });
		
		
	    function inputui(){
	        $(".address input[type=checkbox]").iCheck({
	            checkboxClass: 'icheckbox_flat-blue',
	            radioClass: 'iradio_flat-blue'
	        });
	    }
	    $(".address input[type=checkbox]").iCheck({
	        checkboxClass: 'icheckbox_flat-blue',
	        radioClass: 'iradio_flat-blue'
	    });


	    // 百度地图API功能
		var map = new BMap.Map("dxaddrmap");    // 创建Map实例
	    var point = new BMap.Point(116.331398,39.897445);
	    var curCity = '';
	    var index = 0;
	    map.centerAndZoom(point,12);
	    var adds=[];
	    var circles = [];
	 // 创建地址解析器实例
		var myGeo = new BMap.Geocoder();
	    function myFun(result){
	        var cityName = result.name;
	        map.setCenter(cityName);
	        curCity = cityName;
	        //alert("当前定位城市:"+cityName);
	        
	     // 加载数据
		    postJson('/map/selectAttendanceArea.do', {}, function (result) {
			    for(var obj in result){
			    	var r = result[obj];
			    	 $(".address ul").append('<li class="area-item" lon="'+r.attendanceLon+'" lat="'+r.attendanceLat+'" aid="'+r.attendanceId+'"><input type="checkbox"><div><span>'+r.attendanceName+'</span><p>'+r.attendanceArea+'</p></div></li> ')
				        inputui();
			    	// geocodeSearch(r.attendanceArea);
			    	 adds[obj] = r.attendanceArea;
			    }
			       
	            bdGEO();
	        });
	    }
	    var myCity = new BMap.LocalCity();
	    myCity.get(myFun);
	    map.enableScrollWheelZoom(true);

	    //search address
	    var local = new BMap.LocalSearch(map, {
	        renderOptions:{map: map}
	    });

	    if(event.keyCode==13){
	       alert('click enter');
	    }
	    
	    $(".area-ul").on("click",".area-item",function(){
	    	var area  = $(this).find("p").text();
	    	
	    	myGeo.getPoint(area, function(point){
	    		if (point) {
	    		map.centerAndZoom(point, 18);
	    		//map.addOverlay(new BMap.Marker(point));
	    		}else{
	    			alert("您选择地址没有解析到结果!");
	    		}
	    	}, curCity);
	    });
	    

	    $(".searchAddr").on("keydown",function(e){
	          if(e.key=="Enter"){
	              $(".searchBtn").click();
	              //trigger("click");
	          }
	    });

	    $(".searchBtn").on("click",function(){
	        var address=$(".searchAddr").val();

	        local.search(address);
	    });


	    $(".addr-del").on("click",function(){
	    	var areas =  $(".icheckbox_flat-blue.checked").parents("li");
	    	var arr = [];
	    	var lon = '';
	    	var lat = '';
	    	var text = '';
	    	for(var i=0;i<areas.length;i++){
	    		arr[i] = $(areas[i]).attr("aid");
	    		text = $(areas[i]).find("p").text();
	    		lon = $(areas[i]).attr("lon");
	    		lat = $(areas[i]).attr("lat");
	    		deleteMarker(text,lon,lat);
	    	}
	    	
	        postJson('/map/removeAttendanceArea.do', {areas: arr}, function (result) {
	        	if(result == "success"){
	        		for(var i=0;i<areas.length;i++){
	        			$(areas[i]).remove();
	        		}
	        	}else{
	        		alert("处理数据失败");
	        	}
	           
	        });
	    });

	    $(".addr-add").on("click",function(){

	        var companyName=$(".address-name").val();
	        var companyDetail=$(".address-detail").val();
	        
	       
		    // 将地址解析结果显示在地图上,并调整地图视野
		    myGeo.getPoint(companyDetail, function(point){
			if (point) {
			    // 保存数据
			    var param = {
			       attendanceName:companyName,
			       attendanceArea:companyDetail,
			       attendanceLon:point.lng,
			       attendanceLat:point.lat
			    }
			    postJson('/map/setAttendanceArea.do', param, function (result) {
				    var address = new BMap.Point(point.lng, point.lat);
					addMarker(address,new BMap.Label(companyDetail,{offset:new BMap.Size(20,-10)}));
			        $(".address ul").append('<li class="area-item" aid="'+result+'"  lon="'+point.lng+'" lat="'+point.lat+'"><input type="checkbox"><div><span>'+companyName+'</span><p>'+companyDetail+'</p></div></li> ')
			        inputui();
	            
	           });
			    
			    // 清空输入框
			   $(".address-name").val("");
		       $(".address-detail").val("");
		        
			
			}else{
				alert("您选择地址没有解析到结果!");
			}
		}, curCity);
	    });
	    
	    
	    function bdGEO(){
			var add = adds[index];
			if(index < adds.length){
				geocodeSearch(add);
			}
			
			index++;
		}
	
	    // 编写自定义函数,创建标注
		function addMarker(point,label){
			var marker = new BMap.Marker(point);
			map.centerAndZoom(point, 18);
			map.addOverlay(marker);
			// marker.setLabel(label);

			var circle = new BMap.Circle(point,100,{strokeColor:"blue", strokeWeight:0.1, strokeOpacity:0.3}); //创建圆
			map.addOverlay(circle);
		}
		
		function deleteMarker(label,lon,lat){
			var allOverlay = map.getOverlays();
			for (var i = 0; i < allOverlay.length; i++){
				if(allOverlay[i].toString()=='[object Marker]'){
					if(allOverlay[i].getLabel()!=null){
						if(allOverlay[i].getLabel().content == label){
							map.removeOverlay(allOverlay[i]);
						}
					}
				}else if(allOverlay[i].toString()=='[object Circle]'){
					map.removeOverlay(allOverlay[i]);
				}
			}
			allOverlay = map.getOverlays();
			
			for (var i = 0; i < allOverlay.length; i++){
				if(allOverlay[i].toString()=='[object Marker]'){
					if(allOverlay[i].getLabel()!=null){
							var circle = new BMap.Circle(allOverlay[i].getPosition(),100,{strokeColor:"blue", strokeWeight:0.1, strokeOpacity:0.3}); //创建圆
							map.addOverlay(circle);
					}
				}
			}
		}
		
		function geocodeSearch(add){
			if(index < adds.length){
				setTimeout(bdGEO,500);
			} 
			myGeo.getPoint(add, function(point){
				if (point) {
					var address = new BMap.Point(point.lng, point.lat);
					addMarker(address,new BMap.Label(add,{offset:new BMap.Size(20,-10)}));
				}else{
					alert("您选择地址没有解析到结果!");
				}
			}, curCity);
		}

	  
	});
	
});