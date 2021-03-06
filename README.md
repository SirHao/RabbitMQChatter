# RabbitMQChatter

+ ch1 to ch6 is the official demo for rabbitMQ 

  + official link is [here](https://www.rabbitmq.com/getstarted.html)
  + my blog is [here](https://www.jianshu.com/p/5cfb15bdd359)

+ ChatSystem ：基于官方demo实战，实现聊天系统（单聊和群聊功能）

  项目结构如下

> + Chatter
>   + chatRoom 
>     + FriendChatRoom.java
>     + GroupChatRoom.java
>   + Chatter.java
>   + ChatterModule.java
>
> + LoginRPCServer
>   + RPCLoginClient
>   + RPCLoginServer
> + Protocal
>   + PcaketImp
>     + Request
>       + LoginRequestPacket.java
>       + MessageRequestPacket.java
>     + Response
>       + LoginResponse.java
>   + Command.java
>   + Packet.java
> + Utils
>   + Serializer
>     + imp
>       + JSONSerializer.java
>     + Serializer.java
>     + SerializerAlgorithm.java

### 功能1：登录验证

> 基于RabbitMQ   RPC远程调用实现

登录需要通过```RPCLoginClient```持有用户账号密码像```RPCLoginServer```进行RPC密码验证；并设置失败重登以及最大尝试次数；

传输用的```LoginRequest```对象，实例化后通过序列化发送，server反序列化对象后进行登录验证并将```LoginResponse```响应序列化后返回。规范通讯过程。



### 功能2：单聊系统

> 基于RabbitMQ  普通队列实现

核心逻辑在```FriendChatRoom```,通过建立两条MQ，实现A和B之间通信

+ A 通过 ```A->B```这个队列向B发送消息，B通过```B->A```这个队列向A发送消息

+ A通过多线程新开启一条线程，在发送消息的同时可以通过该线程接收```B->A```队列发来的消息，B同理。



### 功能3： 群聊系统

> 基于RabbitMQ fanout发布订阅实现

核心逻辑在```GroupChatRoom```,通过向exchange发送消息实现消息的广播，通过在exchange上建立一条匿名队列实现消息的接收

+ 同样，接收队列通过开启多线程，实现发送消息同时可以接收消息；并通过过滤，对自己发送的消息忽视，不打印



### 功能4： 下线/退出聊天室，接收未读消息

> 基于多线程和RabbitMQ队列本身特性  实现

场景：

我们在qq中也并不是只有和好友在聊天界面才能收到消息，在其他聊天界面，好友发来消息也会提醒。

而在我们退出qq的时候不会接收消息，但是登陆的时候会可以接收离线期间的消息

+ 多线程，退出聊天室的时候接收线程不关闭，并改为离开状态（```isLeave```变量），A离开期间，B仍可向A发送消息，但是消息提醒方式会不同
+ A下线期间，B发送的消息会在队列中保存，A上线依然可以接受

离开聊天室之后，可以加入其他群聊和私聊；



### 功能5：退出聊天室与重新进入

> 基于HashMap 实现

第一次进入的聊天室的时候开开启一条接收消息的线程，当退出后仍然在运行；所以，如果已经加入过一个聊天室，第二次加入就不能新建一个聊天室聊天的实例，而是重新进入之前的聊天室

+ 第一次加入聊天室的时候，将其引用和名称存放进hashMap,如果下次加入聊天室，hashMap中保存有创建好了的，拿出来直接用