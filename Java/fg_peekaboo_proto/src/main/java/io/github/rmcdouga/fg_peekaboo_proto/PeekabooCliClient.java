package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;

public class PeekabooCliClient implements PeekabooClient {

	@Override
	public void image(AppTarget target, ImageFormat format, ImageScale scale, Path outputPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void image(AppTarget target, ImageScale scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void see(AppTarget target, Path outputPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void click(long pid, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(AppTarget target, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public long appLaunch(String bundleId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long appFocus(long pid) {
		// TODO Auto-generated method stub
		return 0;
	}

}
