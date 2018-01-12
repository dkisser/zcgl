<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//w3c//DTD HTML 4.01 Transtional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html"; charset=UTF-8>
    <title>资产统计</title>
    <style type="text/css">
        body{
           margin-left:0px;             
           margin-top:0px;
           margin-right:0px;
           margin-bottom:0px;
        }
    </style>           
  </head>
  
  <body>   
   <div id="BMZC_ZCFLStatMain">
        <div id="divBMZC_ZCFLStatToolbar" style="padding:3px">
           <form id="downLoadForm" method="post">
			  <input type="hidden" name="randomInfo" value="<%=Math.random()%>"/>
		   </form>
           <table>
              <tr>
                 <td style="padding-left:20px"><a href="#" class="easyui-linkbutton" plain="true" data-options="disabled: true,iconCls: 'icon-export'" style="color:#6666FF;display: none;" onclick="objZCBM.ZCZCFLExcelExport()">导出</a></td>	 	 	
                 <td style="padding-left:300px;padding-right:10px">请选择要查看的部门:</td>
                 <td><input id="selectZcbm" name="deptName" style="width:125px"/></td>  
              </tr>
           </table>
        </div>
        <div id="container" style="height:480px;width:600px;margin:0 auto;text-align:center;line-height:480px;font-size:20px;"></div>
   </div>
     
     <script type="text/javascript">
       var objZCBM={
    		   ZCZCFLExcelExport:function(){
    			   $.messager.confirm('导出','确定要导出该分类资产表格吗?',function(r){
    				   if(r){
    					   downloadZCZCFLExcel();
    				   }
    			   });
    		   },
   		   query_dept: function () {
				dialogObj = $("<div id='dialogObj'></div>");
				wxdept  = null;
				dialogObj.appendTo("body");
				$("#dialogObj").dialog({
					href: getContextPath() + "/console/wxgl/wxdept/queryWXDeptUI.do",
					title: "资产管理部门查询",
					width: 512,
					height: 300,
					modal: true,
					onClose: function () {
						if(wxdept != null) {
							dialogObj.remove();
							$("#selectZcbm").searchbox("setValue",wxdept.deptName);
							setChart(getChartData(wxdept.deptNo),wxdept.deptName); 
						}
						dialogObj.remove();
					}
				});
			}
       };
       
       function downloadZCZCFLExcel(){
    	    $("#downLoadForm").form('submit',{
    	    	 url : getContextPath() + "/console/zcpd/bmzcStat/exportZCFLStat.do",
    	    	 success:function(data){
    	    		 if(data != "success"){
    	    			 $.messager.alert('失败',data,'warning');
    	    		 }
    	    	 }
    	    });  
       };
       
       $("#BMZC_ZCFLStatMain").dialog({
    	 title:'资产分类统计',
    	 width:1024,
    	 height:600,
    	 closed:false,
    	 cache:false,
         shadow:true, //显示阴影
         resizable:false, //不允许改变大小
         model:true, //是否窗口化
         inline:true, //是否在父容器中，若不在会出现很多bug
         toolbar:"#divBMZC_ZCFLStatToolbar",
       });
                   
         $("#selectZcbm").searchbox({
       	 required:true,
       	 editable:false,
       	 searcher: function(value,name){
       	 	objZCBM.query_dept();
       	 },
       	 value: "全部"
        });
        
        $(function(){
	        setChart(getChartData(null),"全部");
        });
 
   function getChartData(deptNo) {
   	  var resultObj = null;    
  	  $.ajax({  
  	       	url: getContextPath() + "/console/zcpd/bmzcStat/getZCFLStat.do",
  	       	type:"post",
  	       	cache:false,
  	       	async:false,
  	       	data:{deptNo: deptNo},
  	       	dataType:"json",    	
  	       	success:function (result) { 	       		
  	       		resultObj = result; 	         		       	  	       			
  	       		var array =[];
	  	       	for(var p in resultObj){
	  	       		if(p != 'dept_name'){
		  	       		var item = [];
		  	       		item.push(p);
		  	       		item.push(resultObj[p]);
		  	       		array.push(item);
	  	       		}
	  	       	}
	  	       	resultObj = array; 	       		
  	       	},
  	       	error: function () {
  	       		alert("Aajx error!");
  	        	}
  	       });
  	  return resultObj;
    }
         
   function setChart(resultObj,objDeptName) {
	   if(Object.keys(resultObj).length == 0){
		   document.getElementById("container").innerHTML = objDeptName + '资产视图为空';
	   }else{
		  $("#container").highcharts({
	          chart: {
	              plotBackgroundColor: null,
	              plotBorderWidth: null,
	              plotShadow: false
	          },
	          title: {           	
		              text: objDeptName + '一级分类资产分布图',
		              align: "center",
		              verticalAlign: "bottom",	 
		              style:{	            	     	  
		            	  fontSize: '16px',
		            	  fontWeight:'bold'
		              }
		        },         
	          plotOptions: {
	              pie: {
	                  allowPointSelect: true,
	                  cursor: 'pointer',
	                  dataLabels: {
	                      enabled: true,
	                      format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                      style: {
	                          color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                      }
	                  },
	                  showInLegend: true  //是否显示图例  
	              }
	          },
	          credits:{
	        	  enabled:false
	          },  
	          exporting:{
	        	  enabled:false
	          },
	          legend:{
	        	  layout:'vertical',
	        	  align: 'right',
	        	  verticalAlign:'top',
	        	  borderWidth:0,       	 
	        	  y:140,
	        	  itemMarginBottom:5
	          },         
	          series: [{
	              type: 'pie',
	              name: '数量',
	              data: resultObj
	          }]
	      }); 
	   }
  }
     </script>
  </body>
</html>
