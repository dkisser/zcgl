<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>创建故障维修任务页面</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

  </head>
  
  <body>
	<div class="easyui-layout" data-options="fit: true">
                <div data-options="region:'south'" style="height:35px;">
                    <table align="center">
                        <tr>
                            <td width="30%"/>
                            <td width='10%' align="center"><a id="confirm_GZWX" href="#">确认</a></td>
                            <td width='20%'></td>
                            <td width='10%' align="center"><a id="cancel_GZWX" href="#">取消</a></td>
                            <td></td>
                        </tr>
                    </table>
                </div>
                <div data-options="region:'north',iconCls:'icon-more',collapsible:false" style="height:60px;">
                	 <form id="insertGZWX_Form" method="post">
                    <table  style="border-collapse:separate; border-spacing:0px 10px;" width="100%">
						<tr><td align="center">开始时间：</td><td><input id="kssj" name="kssj"/></td>
							<td>结束时间：</td><td><input id="jssj" name="jssj"/></td>
							<td align="center">操作人：</td><td><input id="czr_GZWX" name="czrmc"/></td>
				  			<td>验收人：</td><td><input id="ysr_GZWX" name="ysrmc"/></td>
				  			<td>验收时间：</td><td><input id="yssj" name="yssj"/></td>
						</tr>
					</table>
					</form>	
                </div>
                <div data-options="region:'center',iconCls:'icon-more',collapsible:false" style="width:100%;">
					<table id="gzwxZCList"></table>
                </div>
            </div>
	<script type="text/javascript">
		$("#confirm_GZWX").linkbutton({
			plain: true,
			iconCls: "icon-ok"
		});
		
		$("#confirm_GZWX").click(function(){
			$.messager.progress();
			$("#insertGZWX_Form").form('submit',{
				url: getContextPath()+"/console/rwgl/gzwx/insertGZWX.do",
				onSubmit: function(param){
					var isValidate=$(this).form("validate");
					if(!isValidate){
						$.messager.progress('close');
						$.messager.alert("提示","表单内容不能为空",'info');
						return false;
					}
					var zcidArr=[];
					var arr=$("#gzwxZCList").datagrid("getChecked");
					if(arr.length==0){
						$.messager.progress('close');
						$.messager.alert("提示","请至少选择一个资产",'info');
						return false;
					}
					if(arr[0].zjnx=="数据为空"){
						$.messager.progress('close');
						$.messager.alert("提示","当前无\"申请维修\"资产可选择",'info');
						return false;
					}
					for(var i=0;i<arr.length;i++){
						zcidArr[i]=arr[i].zcid;
					}
					param.zcidArray=zcidArr;
					param.czr=$("#czr_GZWX").attr("userid");
					param.ysr=$("#ysr_GZWX").attr("userid");
					return isValidate;
				},
				success: function(result){
					$.messager.progress('close');
					if(result=="success"){
						$.messager.alert("提示","创建故障维修任务成功",'info');
						$("#addGZWXDialog").dialog('close');
						$("#gzwxList").datagrid('reload');
					}else if(result==""){
						$.messager.alert('提示',"创建故障维修任务失败",'info');
					}else{
						$.messager.alert("提示",result,'info');
					}
				}
			});
		});
		
		$("#cancel_GZWX").linkbutton({
			plain: true,
			iconCls: 'icon-cancel'
		});
		
		$("#cancel_GZWX").click(function(){
			$("#addGZWXDialog").dialog('close');
		});
		
		$("#kssj").datebox({
			required: true,
			editable: false,
			width: 100,
			value: dateFormatter(new Date())
		});
		
		$("#jssj").datebox({
			required: true,
			editable: false,
			width: 100,
			value: dateFormatter(new Date())
		});
		
		$("#czr_GZWX").searchbox({
			width: 115,
			editable: false,
			required: true,
			searcher: function(value,name){
				queryWXUser("queryCzrDialog", "/console/wxgl/wxuser/queryWXUserUI.do", "查找操作人", {isEdit:true},"czr_GZWX");
			},
			prompt: "请点击右侧图标"
		});
		
		$("#ysr_GZWX").searchbox({
			width: 115,
			editable: false,
			required: true,
			searcher:function(value,name){
				queryWXUser("queryYsrDialog", "/console/wxgl/wxuser/queryWXUserUI.do", "查找验收人", {isEdit:true},"ysr_GZWX");
			},
			prompt: "请点击右侧图标"
		});
		
		
		$("#yssj").datebox({
			editable: false,
			value: dateFormatter(new Date()),
			required: true,
			width: 100
		});
		
		
		function queryWXUser(dialogId,url,title,param,searchboxId){
            		dialogObj = $('<div id="' + dialogId + '"></div>');
            		wxuser=null;
					dialogObj.appendTo("body");
					$("#" + dialogId).dialog({
	                    href: getContextPath() + url,
	                    title: title,
	                    queryParams: param,
	                    width: 512,
	                    height: 300,
	                    cache: false,
	                    collapsible: true,
	                    modal: true,
	                    draggable: true,
	                    inline: true,
	                    onClose: function() {
	                        if(wxuser!=null){
	                        	$("#"+searchboxId).searchbox("setValue",wxuser.name);
	                        	$("#"+searchboxId).attr("userid",wxuser.userid);
	                        }
	                        dialogObj.remove();// 关闭时remove对话框
	                    }
	                });
            	};
            	
        function dateFormatter(value){
			if(value==''){
				return '';
			}
			var newtime=new Date(value);
			var month=(newtime.getMonth()+1)+"";
			if(month.length<2){
				month="0"+month;
			}
			var day=newtime.getDate()+"";
			if(day.length<2){
				day="0"+day;
			}
			return newtime.getFullYear()+"-"+month+"-"+day;
		};
		
		$("#gzwxZCList").datagrid({
			url: getContextPath()+"/console/rwgl/gzwx/getZCList.do",
			height: "100%",
			onLoadError: function(){
				$(this).datagrid("appendRow",{
					zjnx: '加载数据失败'
				});
			},
			onLoadSuccess: function(data){
				if(data.total==0){
					$(this).datagrid("appendRow",{
						zjnx: '数据为空'
					});
				}else{
					$(".tip").tooltip({ 
	           		  	trackMouse: true,
	                    onShow: function(){ 
	                         $(this).tooltip('tip').css({   
	                             width:300, 
	                             height:300,
	                             boxShadow: '1px 1px 3px #292929',
	                         });  
	                    }  
	                 });
				}
			},
			columns: [[
				{
					title: "复选框",
					field: "fxk",
					align: "center",
					checkbox: true,
					width: "5%",
				},
				{
					title: "资产编号",
					field: "zcdm",
					align: "center",
					width:  "10%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				},
				{
					title: "资产名称",
					field: "zc",
					align: "center",
					width:  "15%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				},
				{
					title: "资产类型",
					field: "zclx",
					align: "center",
					width:  "20%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				},
				{
					title: "折旧年限",
					field: "zjnx",
					align: "center",
					width:  "8%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				},
				{
					title: "资产数量",
					field: "num",
					align: "center",
					width:  "10%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				},
				{
					title: "使用人",
					field: "syrmc",
					align: "center",
					width:  "10%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				},
				{
					title: "所属部门",
					field: "deptName",
					align: "center",
					width:  "15%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				},
				{
					title: "部门资产管理员",
					field: "glrmc",
					align: "center",
					width:  "11.5%",
					formatter:function(value,row){ 
                           var content = "<span title='<div><img src="+getContextPath()+row.picUrl+"_m.jpg /></div>' class='tip'>"+value+"</span>";   
                           return content;  
                    }
				} 
			]],
			idField: "zcdm",
			fitColumns: true,
			striped: true,
			loadMsg: "正在加载中，请稍候...",
			pagination: true,
			selectOnCheck: false,
			checkOnSelect: false,
			rownumbers: true,
			singleSelect: true,
			pageSize: 10,
			pageList: [10]
		});
	</script>
  </body>
</html>
