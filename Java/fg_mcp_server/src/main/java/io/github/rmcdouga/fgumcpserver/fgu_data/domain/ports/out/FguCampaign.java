package io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out;

public interface FguCampaign {

	/**
	 * Returns the name of the campaign.
	 * 
	 * @return
	 */
	String name();
	
	/**
	 * Returns the FguImagesStore for this campaign.
	 * 
	 * @return
	 */
	FguImagesStore imagesStore();

}