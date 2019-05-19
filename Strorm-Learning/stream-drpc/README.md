# stream DRPC

[storm-distributed-rpc](http://ifeve.com/storm-distributed-rpc/)

DRPC (Distributed RPC)  remote procedure call
分布式远程过程调用

DRPC 是通过一个 DRPC 服务端(DRPC server)来实现分布式 RPC 功能的。
DRPC Server 负责接收 RPC 请求，并将该请求发送到 Storm中运行的 Topology，等待接收 Topology 发送的处理结果，并将该结果返回给发送请求的客户端。
（其实，从客户端的角度来说，DPRC 与普通的 RPC 调用并没有什么区别。）

DRPC设计目的：
为了充分利用Storm的计算能力实现高密度的并行实时计算。
（Storm接收若干个数据流输入，数据在Topology当中运行完成，然后通过DRPC将结果进行输出。）

客户端通过向 DRPC 服务器发送待执行函数的名称以及该函数的参数来获取处理结果。实现该函数的拓扑使用一个DRPCSpout 从 DRPC 服务器中接收一个函数调用流。DRPC 服务器会为每个函数调用都标记了一个唯一的 id。随后拓扑会执行函数来计算结果，并在拓扑的最后使用一个名为 ReturnResults 的 bolt 连接到 DRPC 服务器，根据函数调用的 id 来将函数调用的结果返回。

定义DRPC拓扑：
   方法1：
        通过LinearDRPCTopologyBuilder （该方法也过期，不建议使用）
        该方法会自动为我们设定Spout、将结果返回给DRPC Server等，我们只需要将Topology实现
    
   方法2：
        直接通过普通的拓扑构造方法TopologyBuilder来创建DRPC拓扑
        需要手动设定好开始的DRPCSpout以及结束的ReturnResults


