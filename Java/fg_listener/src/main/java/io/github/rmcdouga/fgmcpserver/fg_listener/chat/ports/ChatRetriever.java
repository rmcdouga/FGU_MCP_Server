package io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports;

import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ChatRetriever {
	public record BridgeStatus(
	        String version,
	        Integer protocol,
	        String serverEpoch,
	        Boolean connected,
	        String session,
	        @Nullable Double lastSeenSeconds,
	        Context context,
	        Integer pendingCommands,
	        Integer eventCursor
	) {
	    /** Server context embedded in a {@link BridgeStatus}. */
	    public record Context(
	            @Nullable String ruleset,
	            @Nullable Boolean host,
	            @Nullable String version,
	            @Nullable String campaign
	    ) {
	    }
	    
	    /**
	     * Returns true if the bridge is up for any campaign, false otherwise.
	     * 
	     * @return
	     */
	    public boolean isBridgeUp() {
	    	// connected is true, and host is true
	        return connected && Objects.requireNonNullElse(context.host(), false);
	    }
	    /**
	     * Returns true if the bridge is up for the specified campaign, false otherwise.
	     * 
	     * @param campaign - the campaign to check
	     * @return
	     */
	    public boolean isBridgeUp(String campaign) {
	        return isBridgeUp() && Optional.ofNullable(context.campaign())
	        							   .map(String::trim)
	        							   .map(c -> c.equals(campaign))
	        							   .orElse(false);
	    }
	}
	
	public Optional<BridgeStatus> bridgeStatus();
}
