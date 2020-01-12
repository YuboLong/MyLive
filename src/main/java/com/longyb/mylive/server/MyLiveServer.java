package com.longyb.mylive.server;

import com.longyb.mylive.server.manager.StreamManager;

/**
@author longyubo
2020年1月7日 下午3:02:39
**/
public class MyLiveServer {
	public static void main(String[] args) throws Exception {
		
		StreamManager streamManager=new StreamManager();
		
		int rtmpPort = 1935; 

		RTMPServer rtmpServer = new RTMPServer(rtmpPort,streamManager);
		rtmpServer.run();
		
		int httpPort=8080;
		HttpFlvServer httpFlvServer = new HttpFlvServer(httpPort, streamManager);
		httpFlvServer.run();
		
	}
}

