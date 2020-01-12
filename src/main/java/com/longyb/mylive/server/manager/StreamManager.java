package com.longyb.mylive.server.manager;
/**
@author longyubo
2020年1月2日 下午3:32:04
**/
import java.util.concurrent.ConcurrentHashMap;
import com.longyb.mylive.server.entities.Stream;
import com.longyb.mylive.server.entities.StreamName;

/**
 * all stream info store here,including publisher and subscriber and their live type video & audio
 * @author longyubo
 *
 */
public class StreamManager {
	private ConcurrentHashMap<StreamName, Stream> streams=new ConcurrentHashMap<>();
	
	public void newStream(StreamName streamName,Stream s) {
		streams.put(streamName, s);
	}
	
	public boolean exist(StreamName streamName) {
		return streams.containsKey(streamName);
	}
	
	public Stream getStream(StreamName streamName) {
		return streams.get(streamName);
	}
	
	public void remove(StreamName streamName) {
		streams.remove(streamName);
	}
}

