<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="${path}/newsWeb/js/echarts.min.js"></script>
    <script src="${path}/newsWeb/js/jquery-3.2.1.js"></script>
    <style>
        body{
            text-align:center;
            background-color: #dbdddd;
        }
        .div{ margin:0 auto; width:1000px; height:800px; border:1px solid #F00}
        /* css注释：为了观察效果设置宽度 边框 高度等样式 */
    </style>
</head>
<body>

<div>
    <div id="main" style="width:1350px;height: 650px;float:left;">one</div>
</div>
<script type="text/javascript">
var myChart = echarts.init(document.getElementById('main'));
$(document).ready(function(){
	initCredit();
    setInterval(function() {
    echarts.init(document.getElementById('main'));
    initCredit();
}, 5000);
});
    function initCredit(){
		var action = "<%=path%>/AppSvlt";
		var $data = $.ajax({url:action, async:false}).responseText; 
		var sd = eval('('+$data+')');
		
		var appnames = sd.appnames;
		var counts = sd.counts;
		var userCount = sd.userCount;
		credit(appnames,counts,userCount);
	}

   function credit(appnames,counts,userCount){
	   var seriesData = [];
		for (var i=0;i<appnames.length;i++)
		{	  
		  seriesData.push({
			  value: counts[i],
	          name: appnames[i]           
	        });
		}
	   option = {
			    color : [
			        'rgba(255, 69, 0, 0.5)',
			        'rgba(255, 150, 0, 0.5)',
			        'rgba(255, 200, 0, 0.5)',
			        'rgba(155, 200, 50, 0.5)',
			        'rgba(55, 200, 100, 0.5)'
			    ],
			    title : {
			        text: '某金融APP用户行为实时统计分析',
			        subtext: '真实数据'
			    },
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c}"
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: false},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    legend: {
			        data : appnames
			    },
			    series : [
			        {
			            name:'业务指标',
			            type:'gauge',
			            center: ['25%','55%'],
			            splitNumber: 10,       // 分割段数，默认为5
			            axisLine: {            // 坐标轴线
			                lineStyle: {       // 属性lineStyle控制线条样式
			                    color: [[0.2, '#228b22'],[0.8, '#48b'],[1, '#ff4500']], 
			                    width: 8
			                }
			            },
			            axisTick: {            // 坐标轴小标记
			                splitNumber: 10,   // 每份split细分多少段
			                length :12,        // 属性length控制线长
			                lineStyle: {       // 属性lineStyle控制线条样式
			                    color: 'auto'
			                }
			            },
			            axisLabel: {           // 坐标轴文本标签，详见axis.axisLabel
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                    color: 'auto'
			                }
			            },
			            splitLine: {           // 分隔线
			                show: true,        // 默认显示，属性show控制显示与否
			                length :30,         // 属性length控制线长
			                lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
			                    color: 'auto'
			                }
			            },
			            pointer : {
			                width : 5
			            },
			            title : {
			                show : true,
			                offsetCenter: [0, '-40%'],       // x, y，单位px
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                    fontWeight: 'bolder'
			                }
			            },
			            detail : {
			                formatter:'{value}',
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                    color: 'auto',
			                    fontWeight: 'bolder'
			                }
			            },
			            data:[{value: userCount, name: '用户总量'}]
			        },
			        {
			            name:'访问页',
			            type:'funnel',
			            x: '45%',
			            width: '45%',
			            itemStyle: {
			                normal: {
			                    label: {
			                        formatter: '{b}访问页'
			                    },
			                    labelLine: {
			                        show : false
			                    }
			                },
			                emphasis: {
			                    label: {
			                        position:'inside',
			                        formatter: '{b}访问页: {c}'
			                    }
			                }
			            },
			            data:seriesData
			        },
			        {
			            name:'访问页',
			            type:'funnel',
			            x: '45%',
			            width: '45%',
			            maxSize: '80%',
			            itemStyle: {
			                normal: {
			                    borderColor: '#fff',
			                    borderWidth: 2,
			                    label: {
			                        position: 'inside',
			                        formatter: '{c}',
			                        textStyle: {
			                            color: '#fff'
			                        }
			                    }
			                },
			                emphasis: {
			                    label: {
			                        position:'inside',
			                        formatter: '{b}访问页 : {c}'
			                    }
			                }
			            },
			            data:seriesData
			        }
			    ]
			};
	   myChart.setOption(option);			                    
   } 
</script>
</body>
</html>
