```
Spouts
    A spout is a source of streams in a topology. Generally spouts will read tuples from an external source and emit them into the topology (e.g. a Kestrel queue or the Twitter API). Spouts can either be reliable or unreliable. A reliable spout is capable of replaying a tuple if it failed to be processed by Storm, whereas an unreliable spout forgets about the tuple as soon as it is emitted.
    
    Spouts can emit more than one stream. To do so, declare multiple streams using the declareStream method of OutputFieldsDeclarer and specify the stream to emit to when using the emit method on SpoutOutputCollector.
    
    The main method on spouts is nextTuple. nextTuple either emits a new tuple into the topology or simply returns if there are no new tuples to emit. It is imperative that nextTuple does not block for any spout implementation, because Storm calls all the spout methods on the same thread.
    
    The other main methods on spouts are ack and fail. These are called when Storm detects that a tuple emitted from the spout either successfully completed through the topology or failed to be completed. ack and fail are only called for reliable spouts. See the Javadoc for more information.
    
    Resources:
    
    IRichSpout: this is the interface that spouts must implement.
    Guaranteeing message processing
    
数据源（Spouts）
    
    数据源（Spout）是拓扑中数据流的来源。一般 Spout 会从一个外部的数据源读取元组然后将他们发送到拓扑中。根据需求的不同，Spout 既可以定义为可靠的数据源，也可以定义为不可靠的数据源。一个可靠的 Spout 能够在它发送的元组处理失败时重新发送该元组，以确保所有的元组都能得到正确的处理；相对应的，不可靠的 Spout 就不会在元组发送之后对元组进行任何其他的处理。
    
    一个 Spout 可以发送多个数据流。为了实现这个功能，可以先通过 OutputFieldsDeclarer 的 declareStream 方法来声明定义不同的数据流，然后在发送数据时在 SpoutOutputCollector 的 emit 方法中将数据流 id 作为参数来实现数据发送的功能。
    
    Spout 中的关键方法是 nextTuple。顾名思义，nextTuple 要么会向拓扑中发送一个新的元组，要么会在没有可发送的元组时直接返回。需要特别注意的是，由于 Storm 是在同一个线程中调用所有的 Spout 方法，nextTuple 不能被 Spout 的任何其他功能方法所阻塞，否则会直接导致数据流的中断（关于这一点，阿里的 JStorm 修改了 Spout 的模型，使用不同的线程来处理消息的发送，这种做法有利有弊，好处在于可以更加灵活地实现 Spout，坏处在于系统的调度模型更加复杂，如何取舍还是要看具体的需求场景吧——译者注）。
    
    Spout 中另外两个关键方法是 ack 和 fail，他们分别用于在 Storm 检测到一个发送过的元组已经被成功处理或处理失败后的进一步处理。注意，ack 和 fail 方法仅仅对上述“可靠的” Spout 有效。

相关资料
    
    IRichSpout：这是实现 Spout 的接口
    
    消息的可靠性处理    
```

## 1、ISpout

org.apache.storm.spout.ISpout

### 概述
		
		核心接口(interface)，负责将数据发送到topology中去处理
		
		Storm会跟踪Spout发出去的tuple的DAG
		
		ack/fail
		
		tuple: message id
		
		ack/fail/nextTuple是在同一个线程中执行的，所以不用考虑线程安全方面

```
/**
 * ISpout is the core interface for implementing spouts. 
 * ISpout是一个核心的接口,当要实现spouts
 * A Spout is responsible for feeding messages into the topology for processing. 
 * Spout负责把消息发送到topology去执行
 * For every tuple emitted by a spout, 
 * 对于每一个tuple,会通过一个spout会发送出去
 * Storm will track the (potentially very large) DAG of tuples generated based on a tuple emitted by the spout. 
 * Storm会跟踪基于tuple产生的spout生成的DAG(可能非常大)
 * When Storm detects that every tuple in that DAG has been successfully processed, it will send an ack message to the Spout.
 * 当Storm检测到该DAG中的每个tuple都已成功处理时，它会向Spout发送一条确认消息。
 *
 * If a tuple fails to be fully processed within the configured timeout for the
 * topology (see {@link org.apache.storm.Config}), Storm will send a fail message to the spout
 * for the message.
 * 如果在配置的topology超时内无法完全处理tuple(详细的可以看`org.apache.storm.Config`来配置)，Storm会向发送一条失败消息给spout
 *
 * When a Spout emits a tuple, it can tag the tuple with a message id. 
 * 当一个Spout发送一个tuple时候,会给这个tuple添加一个标签,这个标签叫message id
 * The message id can be any type. 
 * 这个消息id可以是任意类型
 * When Storm acks or fails a message, it will pass back to the spout the same message id to identify which tuple it's referring to. 
 * 当Storm收到确认(acks)或者失败(fails)消息时，它会将相同的消息ID传回给spout，以识别它指的是哪个tuple
 * If the spout leaves out the message id, or sets it to null, then Storm will not track the message and the spout
 * will not receive any ack or fail callbacks for the message.
 * 如果spout省略了消息id，或者将其设置为null，那么Storm将不会跟踪消息，并且spou将不会收到消息的任何ack或fail回调
 *
 * Storm executes ack, fail, and nextTuple all on the same thread. This means that an implementor
 * of an ISpout does not need to worry about concurrency issues between those methods. 
 * Storm在同一个线程上执行ack、fail、nextTuple.这意味着Spout的实现类不需要担心这些方法之间的并发问题
 * However, it also means that an implementor must ensure that nextTuple is non-blocking: otherwise 
 * the method could block acks and fails that are pending to be processed.
 * 但是，这也意味着实现类必须确保`nextTuple`是非阻塞的：否则该方法可能会阻止待处理的acks和fails。
 */
```

### 核心方法
		
		open： 初始化操作
		
		close： 资源释放操作
		
		nextTuple： 发送数据   core api
		
		ack： tuple处理成功，storm会反馈给spout一个成功消息
		
		fail：tuple处理失败，storm会发送一个消息给spout，处理失败

### 实现类
		
		public abstract class BaseRichSpout extends BaseComponent implements IRichSpout {
		
		public interface IRichSpout extends ISpout, IComponent 
		
		DRPCSpout
		
		ShellSpout
		
## 2、IComponent	

org.apache.storm.topology.IComponent

```
Topologies
    The logic for a realtime application is packaged into a Storm topology. 
    A Storm topology is analogous to a MapReduce job. One key difference is that a MapReduce job eventually finishes, 
    whereas a topology runs forever (or until you kill it, of course). 
    A topology is a graph of spouts and bolts that are connected with stream groupings. 
    These concepts are described below.
    Storm 的拓扑是对实时计算应用逻辑的封装，它的作用与 MapReduce 的任务（Job）很相似，区别在于 MapReduce 的一个 Job 在得到结果之后总会结束，而拓扑会一直在集群中运行，直到你手动去终止它。
    拓扑还可以理解成由一系列通过数据流（Stream Grouping）相互关联的 Spout 和 Bolt 组成的的拓扑结构。
    Spout 和 Bolt 称为拓扑的组件（Component）
    
Resources:

    TopologyBuilder: 
        use this class to construct topologies in Java
        在 Java 中使用此类构造拓扑
        
    Running topologies on a production cluster
        在生产环境中运行拓扑
        
    Local mode: Read this to learn how to develop and test topologies in local mode.
    本地模式：通过本文学习如何在本地模式中开发、测试拓扑
    
```

```
/**
 * Common methods for all possible components in a topology. 
 * `topology`中所有可能组件的常用方法
 * This interface is used when defining topologies using the Java API. 
 * 使用Java API定义`topology`时使用此接口
 */
```

### 概述：
    public interface IComponent extends Serializable
    为topology中所有可能的组件提供公用的方法

    void declareOutputFields(OutputFieldsDeclarer declarer);
    用于声明当前Spout/Bolt发送的tuple的名称
    使用OutputFieldsDeclarer配合使用
    
    
### 实现类：
    
    public abstract class BaseComponent implements IComponent	
    
## 3、IBolt接口

org.apache.storm.task.IBolt

```
Bolts
    All processing in topologies is done in bolts. Bolts can do anything from filtering, functions, aggregations, joins, talking to databases, and more.
    拓扑中所有的数据处理均是由 Bolt 完成的。通过数据过滤（filtering）、函数处理（functions）、聚合（aggregations）、联结（joins）、数据库交互等功能，Bolt 几乎能够完成任何一种数据处理需求
    
    Bolts can do simple stream transformations. Doing complex stream transformations often requires multiple steps and thus multiple bolts. 
    For example, transforming a stream of tweets into a stream of trending images requires at least two steps: a bolt to do a rolling count of retweets for each image, 
    and one or more bolts to stream out the top X images (you can do this particular stream transformation in a more scalable way with three bolts than with two).
    一个 Bolt 可以实现简单的数据流转换，而更复杂的数据流变换通常需要使用多个 Bolt 并通过多个步骤完成。
    例如，将一个微博数据流转换成一个趋势图像的数据流至少包含两个步骤：其中一个 Bolt 用于对每个图片的微博转发进行滚动计数，
    另一个或多个 Bolt 将数据流输出为“转发最多的图片”结果（相对于使用2个Bolt，如果使用3个 Bolt 你可以让这种转换具有更好的可扩展性）
    
    Bolts can emit more than one stream. To do so, declare multiple streams using the declareStream method of OutputFieldsDeclarer and specify the stream to emit to when using the emit method on OutputCollector.
    与 Spout 相同，Bolt 也可以输出多个数据流。为了实现这个功能，可以先通过 OutputFieldsDeclarer 的 declareStream 方法来声明定义不同的数据流，然后在发送数据时在 OutputCollector 的 emit 方法中将数据流 id 作为参数来实现数据发送的功能。
    
    When you declare a bolt's input streams, you always subscribe to specific streams of another component. 
    If you want to subscribe to all the streams of another component, you have to subscribe to each one individually. 
    InputDeclarer has syntactic sugar for subscribing to streams declared on the default stream id. 
    Saying declarer.shuffleGrouping("1") subscribes to the default stream on component "1" and is equivalent to declarer.shuffleGrouping("1", DEFAULT_STREAM_ID).
    在定义 Bolt 的输入数据流时，你需要从其他的 Storm 组件中订阅指定的数据流。
    如果你需要从其他所有的组件中订阅数据流，你就必须要在定义 Bolt 时分别注册每一个组件。
    对于声明为默认 id（即上文中提到的“default”）的数据流，InputDeclarer支持订阅此类数据流的语法糖。
    也就是说，如果需要订阅来自组件“1”的数据流，declarer.shuffleGrouping("1") 与 declarer.shuffleGrouping("1", DEFAULT_STREAM_ID) 两种声明方式是等价的
    
    The main method in bolts is the execute method which takes in as input a new tuple. 
    Bolts emit new tuples using the OutputCollector object. 
    Bolts must call the ack method on the OutputCollector for every tuple they process so that Storm knows when tuples are completed (and can eventually determine that its safe to ack the original spout tuples). For the common case of processing an input tuple, emitting 0 or more tuples based on that tuple, and then acking the input tuple, 
    Storm provides an IBasicBolt interface which does the acking automatically.
    Bolt 的关键方法是 execute 方法。
    execute 方法负责接收一个元组作为输入，并且使用 OutputCollector 对象发送新的tuple。
    如果有消息可靠性保障的需求，Bolt 必须为它所处理的每个元组调用 OutputCollector 的 ack 方法，
    以便 Storm 能够了解tuple是否处理完成（并且最终决定是否可以响应最初的 Spout 输出tuple树）。
    一般情况下，对于每个输入tuple，在处理之后可以根据需要选择不发送还是发送多个新tuple，
    然后再响应（ack）输入tuple。IBasicBolt 接口能够实现元组的自动应答
    
    Its perfectly fine to launch new threads in bolts that do processing asynchronously. OutputCollector is thread-safe and can be called at any time.
    在 Bolt 中启动新线程来进行异步处理是一种非常好的方式，因为 OutputCollector 是线程安全的对象，可以在任意时刻被调用
    
Resources:

    IRichBolt: this is general interface for bolts. 
                用于定义 Bolt 的基本接口
    IBasicBolt: this is a convenience interface for defining bolts that do filtering or simple functions.
                用于定义带有过滤或者其他简单的函数操作功能的 Bolt 的简便接口
    OutputCollector: bolts emit tuples to their output streams using an instance of this class 
                用于定义带有过滤或者其他简单的函数操作功能的 Bolt 的简便接口
    Guaranteeing message processing 
                消息的可靠性处理
```

```
/**
 * An IBolt represents a component that takes tuples as input and produces tuples
 * as output. An IBolt can do everything from filtering to joining to functions
 * to aggregations. It does not have to process a tuple immediately and may
 * hold onto tuples to process later.
 *
 * A bolt's lifecycle is as follows:
 *
 * IBolt object created on client machine. The IBolt is serialized into the topology
 * (using Java serialization) and submitted to the master machine of the cluster (Nimbus).
 * Nimbus then launches workers which deserialize the object, call prepare on it, and then
 * start processing tuples.
 *
 * If you want to parameterize an IBolt, you should set the parameters through its
 * constructor and save the parameterization state as instance variables (which will
 * then get serialized and shipped to every task executing this bolt across the cluster).
 *
 * When defining bolts in Java, you should use the IRichBolt interface which adds
 * necessary methods for using the Java TopologyBuilder API.
 */
 ```
 
### 概述
    
    职责：接收tuple处理，并进行相应的处理(filter/join/....)
   
    hold住tuple再处理
    
    IBolt会在一个运行的机器上创建，使用Java序列化它，然后提交到主节点(nimbus)上去执行
    
    nimbus会启动worker来反序列化，调用prepare方法，然后才开始处理tuple处理


### 方法
    
    prepare：初始化
    
    execute：处理一个tuple暑假，tuple对象中包含了元数据信息
    
    cleanup：shutdown之前的资源清理操作

### 实现类：
    public abstract class BaseRichBolt extends BaseComponent implements IRichBolt {
    
    public interface IRichBolt extends IBolt, IComponent 
    
    RichShellBolt    