# MyLive -- A  Rtmp server implemention in java for live streamming

### Introdution
MyLive is a rtmp server java implementation for live streaming.
It's not a full feature rtmp server,seek and play2 are not supported.


### Features 

1. Rtmp live stream push/pull(publish/play)
2. Save published stream as flv file
3. Http-Flv support
4. Gop Cache as default
5. Currently only support Amf0,Amf3 support is on the future plan

###   Build & Run
mvn package

java -jar mylive.jar

MyLive reads the configuration file "mylive.yaml" placed in the same folder as mylive.jar

Then you can push streams to rtmp://127.0.0.1/live/yourstream 

FFMPEG and VLC player had already tested. 

### Future Plan
1. Add Amf3 support
2. Support multiple bitrate,live format (eg HLS,DASH) with FFMPEG


[中文帮助](README_zh_CN.md)