package io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.*;

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
	
	
	/** A batch of pending chat queries returned by the FG MCP bridge. */
	public record QueryBatch(
	        List<QueryEntry> queries,
	        Integer cursor,
	        Boolean missedEntries,
	        String serverEpoch,
	        String session
	) {

	    /** A single query: the routing instructions, the raw request, and metadata. */
	    public record QueryEntry(
	            String instructions,
	            ChatRequest request,
	            String wrappedQuery,
	            Integer sequence
	    ) {
	    }

	    /** The per-query request fields supplied by the server. */
	    public record ChatRequest(
	            String queryId,
	            String question,
	            String sender,
	            String requesterRole,
	            String replyVisibility,
	            String character,
	            String campaign,
	            String ruleset
	    ) {
	    }
	}

	Optional<QueryBatch> pollForChatEvents(int cursor, int waitSeconds);
	
}
