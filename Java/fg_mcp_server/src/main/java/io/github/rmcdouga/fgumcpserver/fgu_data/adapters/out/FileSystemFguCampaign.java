package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import java.nio.file.Path;
import java.util.Objects;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguCampaign;

/**
 * An implementation of the {@link FguCampaign} interface that represents a campaign stored in the file system. 
 * 
 * The campaign is represented by a directory on the file system.
 * 
 */
public class FileSystemFguCampaign implements FguCampaign {
	private final Path campaignRootPath;

	public FileSystemFguCampaign(Path campaignRootPath) {
		this.campaignRootPath = Objects.requireNonNull(campaignRootPath, "campaignRootPath cannot be null");
	}

	/**
	 * Returns the name of the campaign, which is the name of the directory that contains the campaign files.
	 * 
	 * @return
	 */
	@Override
	public String name() {
		return campaignRootPath.getFileName().toString();
	}

}
