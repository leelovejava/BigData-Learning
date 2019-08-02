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
</head>
<body>

<div>
	<div id="result2" style="width:710px;height: 450px;float:left;margin-bottom:100px;">two</div>
    <div id="result1" style="width:710px;height: 450px;float:left;margin-bottom:100px;">one</div> 
    <div id="result3" style="width:1410px;height: 420px;float:left;margin-bottom:100px;margin-top:100px;">three</div>
    <div id="result4" style="width:710px;height: 420px;float:left;margin-bottom:100px;margin-top:100px;">four</div> 
    <div id="result5" style="width:710px;height: 420px;float:left;margin-bottom:100px;margin-top:100px;">five</div> 
    <div id="result6" style="width:710px;height: 420px;float:left;margin-bottom:100px;margin-top:100px;">six</div> 
    <div id="result7" style="width:710px;height: 420px;float:left;margin-bottom:100px;margin-top:100px;">seven</div> 
    <div id="result8" style="width:710px;height: 420px;float:left;margin-bottom:100px;margin-top:100px;">eight</div> 
    <div id="result9" style="width:710px;height: 420px;float:left;margin-bottom:100px;margin-top:100px;">nine</div> 
</div>
<script type="text/javascript">
var myChart_result1 = echarts.init(document.getElementById('result1'));
var myChart_result2 = echarts.init(document.getElementById('result2'));
var myChart_result3=echarts.init(document.getElementById('result3'));
var myChart_result4=echarts.init(document.getElementById('result4'));
var myChart_result5=echarts.init(document.getElementById('result5'));
var myChart_result6=echarts.init(document.getElementById('result6'));
var myChart_result7=echarts.init(document.getElementById('result7'));
var myChart_result8=echarts.init(document.getElementById('result8'));
var myChart_result9=echarts.init(document.getElementById('result9'));
$(document).ready(function(){
	initCredit();
    echarts.init(document.getElementById('result1'));
    echarts.init(document.getElementById('result2'));
    echarts.init(document.getElementById('result3'));
    echarts.init(document.getElementById('result4'));
    echarts.init(document.getElementById('result5'));
    echarts.init(document.getElementById('result6'));
    echarts.init(document.getElementById('result7'));
    echarts.init(document.getElementById('result8'));
    echarts.init(document.getElementById('result9'));
});

function initCredit(){
		var action = "<%=path%>/CreditSvlt";
		var $data = $.ajax({url:action, async:false}).responseText; 
		var sd = eval('('+$data+')');
		var periods = sd.periods;
		var periodnums = sd.periodnums;
		result1(periods,periodnums);
		
		var sexs = sd.sexs;
		var sexnums = sd.sexnums;
		result2(sexs,sexnums);
		
		var provincenames = sd.provincenames;
		var provincenums = sd.provincenums;
		result3(provincenames,provincenums);
		
		var salarylevels = sd.salarylevels;
		var sumsalarys = sd.sumsalarys;
		result4(salarylevels,sumsalarys);
		
		var banknums = sd.banknums;
		var cardnums = sd.cardnums;
		result5(banknums,cardnums);
		
		var banknames = sd.banknames;
		var usernums = sd.usernums;
		result6(banknames,usernums);
		
		var consumeproportions = sd.consumeproportions;
		var consumenums = sd.consumenums;
		result7(consumeproportions,consumenums);
		
		var billlevels = sd.billlevels;
		var billnums = sd.billnums;
		result8(billlevels,billnums);
		
		var creditlevels = sd.creditlevels;
		var creditnums = sd.creditnums;
		result9(creditlevels,creditnums);
	}
function result9(creditlevels,creditnums){
	var legendData = [];
    var seriesData = [];
    var selected = {};
	for (var i=0;i<creditlevels.length;i++)
	{	  
	  legendData.push(creditlevels[i]);
	  seriesData.push({
            name: creditlevels[i],
            value: creditnums[i]
        });
	  selected[creditlevels[i]]=i+1;
	}
	option = {
		    title : {
		        text: '信用卡总额度不同等级用户占比',
		        subtext: '数据来自某金融公司',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        type: 'scroll',
		        orient: 'vertical',
		        right: 10,
		        top: 20,
		        bottom: 20,
		        data: legendData,

		        selected: selected
		    },
		    series : [
		        {
		            name: '信用卡总额度等级',
		            type: 'pie',
		            radius : '55%',
		            center: ['40%', '50%'],
		            data: seriesData,
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};	
	myChart_result9.setOption(option);	
}
function result8(billlevels,billnums){
	var seriesData = [];
	for (var i=0;i<billlevels.length;i++)
	{	  
	  seriesData.push({
		  value: billnums[i],
          name: billlevels[i]           
        });
	}
	option = {
		    title : {
		        text: '不同账单等级用户占比',
		        subtext: '数据来做某金融公司',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data: billlevels
		    },
		    series : [
		        {
		            name: '账单等级',
		            type: 'pie',
		            radius : '55%',
		            center: ['50%', '60%'],
		            data:seriesData,
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};
	myChart_result8.setOption(option);
}
function result7(consumeproportions,consumenums){
	option = {
		    title: {
		        text: '败家用户量',
		        subtext: '数据来自某金融公司'
		    },
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'shadow'
		        }
		    },
		    legend: {
		        data: ['2018年']
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    xAxis: {
		        type: 'value',
		        boundaryGap: [0, 0.01]
		    },
		    yAxis: {
		        type: 'category',
		        data: consumeproportions
		    },
		    series: [
		        {
		            name: '2018年',
		            type: 'bar',
		            data: consumenums
		        }
		    ]
		};
	myChart_result7.setOption(option);
}
function result6(banknames,usernums){
	var seriesData = [];
	for (var i=0;i<banknames.length;i++)
	{	  
	  seriesData.push({
		  value: usernums[i],
          name: banknames[i]           
        });
	}
	option = {
		    title : {
		        text: '银行信用卡持卡用户占比',
		        subtext: '来自某金融公司数据',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        x : 'center',
		        y : 'bottom',
		        data:banknames
		    },
		    toolbox: {
		        show : true,
		        feature : {
		            mark : {show: true},
		            dataView : {show: true, readOnly: false},
		            magicType : {
		                show: true,
		                type: ['pie', 'funnel']
		            },
		            restore : {show: true},
		            saveAsImage : {show: true}
		        }
		    },
		    calculable : true,
		    series : [
		        {
		            name:'面积模式',
		            type:'pie',
		            radius : [30, 110],
		            center : ['50%', '50%'],
		            roseType : 'area',
		            data:seriesData
		        }
		    ]
		};
	myChart_result6.setOption(option);
}
function result5(banknums,cardnums){
	var seriesData = [];
	for (var i=0;i<banknums.length;i++)
	{	  
	  seriesData.push({
		  value: cardnums[i],
          name: banknums[i]           
        });
	}
	option = {
		    title: {
		        text: '信用卡持卡用户统计',
		        subtext: '某金融公司数据',
		        left: 'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        // orient: 'vertical',
		        // top: 'middle',
		        bottom: 10,
		        left: 'center',
		        data: banknums
		    },
		    series : [
		        {
		            type: 'pie',
		            radius : '65%',
		            center: ['50%', '50%'],
		            selectedMode: 'single',
		            data:seriesData,
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};
	myChart_result5.setOption(option);
}
function result4(salarylevels,sumsalarys){
	var seriesData = [];
	for (var i=0;i<salarylevels.length;i++)
	{	  
	  seriesData.push({
		  value: sumsalarys[i],
          name: salarylevels[i]           
        });
	}
	option = {
		    backgroundColor: '#2c343c',

		    title: {
		        text: '不同收入人群持有信用卡占比',
		        left: 'center',
		        top: 20,
		        textStyle: {
		            color: '#ccc'
		        }
		    },

		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },

		    visualMap: {
		        show: false,
		        min: 80,
		        max: 600,
		        inRange: {
		            colorLightness: [0, 1]
		        }
		    },
		    series : [
		        {
		            name:'收入等级',
		            type:'pie',
		            radius : '55%',
		            center: ['50%', '50%'],
		            data: seriesData.sort(function (a, b) { return a.value - b.value; }),
		            roseType: 'radius',
		            label: {
		                normal: {
		                    textStyle: {
		                        color: 'rgba(255, 255, 255, 0.3)'
		                    }
		                }
		            },
		            labelLine: {
		                normal: {
		                    lineStyle: {
		                        color: 'rgba(255, 255, 255, 0.3)'
		                    },
		                    smooth: 0.2,
		                    length: 10,
		                    length2: 20
		                }
		            },
		            itemStyle: {
		                normal: {
		                    color: '#FF00FF',
		                    shadowBlur: 200,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            },

		            animationType: 'scale',
		            animationEasing: 'elasticOut',
		            animationDelay: function (idx) {
		                return Math.random() * 200;
		            }
		        }
		    ]
		};	
	myChart_result4.setOption(option);
}
function result3(provincenames,provincenums){
	myChart_result3.hideLoading();

    option = {
        tooltip : {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow',
                label: {
                    show: true
                }
            }
        },
        toolbox: {
            show : true,
            feature : {
                mark : {show: true},
                dataView : {show: true, readOnly: false},
                magicType: {show: true, type: ['line', 'bar']},
                restore : {show: true},
                saveAsImage : {show: true}
            }
        },
        calculable : true,
        legend: {
            data:['Growth', '信用卡总用户数'],
            itemGap: 5
        },
        grid: {
            top: '12%',
            left: '1%',
            right: '10%',
            containLabel: true
        },
        xAxis: [
            {
                type : 'category',
                data : provincenames
            }
        ],
        yAxis: [
            {
                type : 'value',
                name : '信用卡总用户数',
                axisLabel: {
                    formatter: function (a) {
                        a = +a;
                        return isFinite(a)
                            ? echarts.format.addCommas(+a / 1000)
                            : '';
                    }
                }
            }
        ],
        dataZoom: [
            {
                show: true,
                start: 0,
                end: 100
            },
            {
                type: 'inside',
                start: 100,
                end: 100
            },
            {
                show: true,
                yAxisIndex: 0,
                filterMode: 'empty',
                width: 30,
                height: '80%',
                showDataShadow: false,
                left: '93%'
            }
        ],
        series : [
            {
                name: '信用卡总用户数',
                type: 'bar',
                data: provincenums
            }
        ]
    };

    myChart_result3.setOption(option);
}
function result2(sexs,sexnums){
    var seriesData = [];
	for (var i=0;i<sexs.length;i++)
	{	  
	  seriesData.push({
            name: sexs[i],
            value: sexnums[i]
        });
	}
	option = {
		    tooltip: {
		        trigger: 'item',
		        formatter: "{a} <br/>{b}: {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        x: 'left',
		        data:sexs
		    },
		    series: [
		        {
		            name:'性别',
		            type:'pie',
		            radius: ['70%', '40%'],
		            avoidLabelOverlap: false,
		            label: {
		                normal: {
		                    show: false,
		                    position: 'center'
		                },
		                emphasis: {
		                    show: true,
		                    textStyle: {
		                        fontSize: '30',
		                        fontWeight: 'bold'
		                    }
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: false
		                }
		            },
		            data:seriesData
		        }
		    ]
		};
	myChart_result2.setOption(option);	
	
}
function result1(periods,periodnums){
	var legendData = [];
    var seriesData = [];
    var selected = {};
	for (var i=0;i<periods.length;i++)
	{	  
	  legendData.push(periods[i]);
	  seriesData.push({
            name: periods[i],
            value: periodnums[i]
        });
	  selected[periods[i]]=i+1;
	}
	option = {
		    title : {
		        text: '80、90后信用卡持卡用户占比',
		        subtext: '数据来自某金融公司',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        type: 'scroll',
		        orient: 'vertical',
		        right: 10,
		        top: 20,
		        bottom: 20,
		        data: legendData,

		        selected: selected
		    },
		    series : [
		        {
		            name: '年龄段',
		            type: 'pie',
		            radius : '70%',
		            center: ['40%', '50%'],
		            data: seriesData,
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};	
	myChart_result1.setOption(option);	
}
</script>
</body>
</html>
