package com.longyb.mylive.server.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author longyubo 2020年1月2日 下午3:36:29
 **/
@Data
@AllArgsConstructor
public class StreamName {
	private String app;
	private String name;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StreamName other = (StreamName) obj;
		if (app == null) {
			if (other.app != null)
				return false;
		} else if (!app.equals(other.app))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((app == null) ? 0 : app.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

}
