# MyLive 使用JAVA实现的直播RTMP服务器

### 介绍
MyLive 是一个Java实现的RTMP直播服务器，并不是一个全功能的RTMP实现，也就是说不支持seek和play2命令,支持AMF0编码。

### 功能 

1. Rtmp直播流推拉(publish/play)
2. 将推到服务器的流保存为FLV格式文件
3. 实时Http-FLV支持
4. GopCache


### 架构

![MyLive Architecture](https://sinacloud.net/longyb-myblog/mylive_arche.png)


###   Build & Run
mvn package

java -jar mylive.jar

MyLive会读取和mylive.jar在同一个文件夹下的mylive.yaml配置文件。

然后就可以推流到rtmp://127.0.0.1/live/yourstream，
Http-flv流使用 http://127.0.0.1:8080/live/yourstream 访问

FFMPEG/OBS 推Rtmp流和VLC player播放Rtmp流已经测试过,HTTP-FLV直播流已经使用bilibili的flv.js测试过

### 未来计划

1. HLS的支持
2. 配合FFMPEG支持更多直播形式 (HLS,DASH)和比特率

如果你对该项目有兴趣，请加入QQ群1028728337