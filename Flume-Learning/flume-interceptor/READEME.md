# Flume拦截器

##（1）拦截器注意事项
	项目中自定义了：ETL拦截器和区分类型拦截器。
   
   采用两个拦截器的优缺点：
    优点，模块化开发和可移植性；
    缺点，性能会低一些

##（2）自定义拦截器步骤

a）实现 Interceptor

b）重写四个方法
    initialize 初始化
    public Event intercept(Event event) 处理单个Event
    public List<Event> intercept(List<Event> events) 处理多个Event，在这个方法中调用Event intercept(Event event)
    close 方法
    
c）静态内部类，实现Interceptor.Builder

##（3）使用
Flume直接读log日志的数据，log日志的格式是app-yyyy-mm-dd.log
```shell script
#interceptor
a1.sources.r1.interceptors =  i1 i2
a1.sources.r1.interceptors.i1.type = com.atguigu.flume.interceptor.LogETLInterceptor$Builder
a1.sources.r1.interceptors.i2.type = com.atguigu.flume.interceptor.LogTypeInterceptor$Builder
```

注意：`com.atguigu.flume.interceptor.LogETLInterceptor`和
`com.atguigu.flume.interceptor.LogTypeInterceptor`是自定义的拦截器的全类名。
需要根据用户自定义的拦截器做相应修改。