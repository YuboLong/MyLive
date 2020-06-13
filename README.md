# MyLive -- A  Rtmp server implemention in java for live streaming

### Introdution
MyLive is a rtmp server java implementation for live streaming.
It's not a full feature rtmp server,seek and play2 are not supported. Amf0 is the only supported amf version.


### Features 

1. Rtmp live stream push/pull(publish/play)
2. Save published stream as flv file
3. Http-Flv support
4. Gop Cache as default


### Architecture
![MyLive Architecture](https://sinacloud.net/longyb-myblog/mylive_arche.png)

###   Build & Run

mvn package

java -jar mylive.jar

MyLive reads the configuration file "mylive.yaml" placed in the same folder as mylive.jar

Then you can push streams to rtmp://127.0.0.1/live/yourstream 

Publishing Rtmp streams using FFMPEG/OBS and playing rtmp stream by VLC player had been already tested. 
http-flv is tested with bilibili/flv.js

### Future Plan
1. HLS support
2. Support multiple bitrate,live format (eg HLS,DASH) with FFMPEG


[中文帮助](README_zh_CN.md)