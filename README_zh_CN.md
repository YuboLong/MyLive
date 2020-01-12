# MyLive 使用JAVA实现的直播RTMP服务器

### 介绍
MyLive 是一个Java实现的RTMP直播服务器，并不是一个全功能的RTMP实现，也就是说不支持seek和play2命令。

### 功能 

1. Rtmp直播流推拉(publish/play)
2. 将推到服务器的流保存为文件
3. 实时Http-FLV支持
4. GopCache
5. 目前只支持Amf0,Amf3会在未来支持

###   Build & Run

mvn package

java -jar mylive.jar

MyLive会读取和mylive.jar在同一个文件夹下的mylive.yaml配置文件。

然后就可以推流到rtmp://127.0.0.1/live/yourstream 
Http-flv使用 http://127.0.0.1:8080/live/yourstream 访问

FFMPEG 和 VLC player 已经测试过。  

### 未来计划
1. 加入AMF3的支持
2. 配合FFMPEG支持更多直播形式 (HLS,DASH)和比特率

如果你对该项目有兴趣，请加入QQ群1028728337