package io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.in;

import java.util.stream.Stream;

public interface FguData {

	Stream<Campaign> campaigns();
}
