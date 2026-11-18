package io.github.rmcdouga.fgmcpserver.fg_listener.chat.domain;

public record CursorLocation(Integer location) {
	
	public CursorLocation {
		if (location < 0) {
			throw new IllegalArgumentException("Cursor location cannot be negative: " + location);
		}
	}

	public static CursorLocation fromInteger(Integer location) {
		return new CursorLocation(location);
	}

	public static CursorLocation fromString(String locationString) {
		try {
			return new CursorLocation(Integer.parseInt(locationString));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid cursor location string: " + locationString, e);
		}
	}
	
	public String asString() {
		return location.toString();
	}

	public Integer asInteger() {
		return location;
	}
}
