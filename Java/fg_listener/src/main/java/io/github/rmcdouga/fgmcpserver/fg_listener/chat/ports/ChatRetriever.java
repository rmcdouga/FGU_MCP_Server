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

	/** A command dispatched to the FG MCP bridge, with routing/timeout metadata. */
	public record BridgeCommand(
	        Command command,
	        int timeout,
	        String requestId,
	        String serverEpoch
	) {
	    public static Builder builder() {
	        return new Builder();
	    }

	    public static final class Builder {
	        private Command command;
	        private Integer timeout;
	        private String requestId;
	        private String serverEpoch;

	        private Builder() {
	        }

	        public Builder command(Command command) {
	            this.command = command;
	            return this;
	        }

	        public Builder timeout(int timeout) {
	            this.timeout = timeout;
	            return this;
	        }

	        public Builder requestId(String requestId) {
	            this.requestId = requestId;
	            return this;
	        }

	        public Builder serverEpoch(String serverEpoch) {
	            this.serverEpoch = serverEpoch;
	            return this;
	        }

	        public BridgeCommand build() {
	            return new BridgeCommand(command, timeout, requestId, serverEpoch);
	        }
	    }
	    
		/** The action payload. `action` selects the command kind. */
	    public record Command(
	            String action,
	            String queryId,
	            String text,
	            Boolean spoilerFree,
	            Boolean hostOnly,
	            Boolean statusError
	    ) {
	        public static Builder builder() {
	            return new Builder();
	        }

	        public static final class Builder {
	            private String action;
	            private String queryId;
	            private String text;
	            private boolean spoilerFree;
	            private boolean hostOnly;
	            private boolean statusError;

	            private Builder() {
	            }

	            public Builder action(String action) {
	                this.action = action;
	                return this;
	            }

	            public Builder queryId(String queryId) {
	                this.queryId = queryId;
	                return this;
	            }

	            public Builder text(String text) {
	                this.text = text;
	                return this;
	            }

	            public Builder spoilerFree(boolean spoilerFree) {
	                this.spoilerFree = spoilerFree;
	                return this;
	            }

	            public Builder hostOnly(boolean hostOnly) {
	                this.hostOnly = hostOnly;
	                return this;
	            }

	            public Builder statusError(boolean statusError) {
	                this.statusError = statusError;
	                return this;
	            }

	            public Command build() {
	                return new Command(action, queryId, text, spoilerFree, hostOnly, statusError);
	            }
	        }
	    }
	}

	public record CommandResponse(
	        Boolean ok,
	        Data data
	) {
	    public record Data(
	            String visibility,
	            Boolean delivered
	    ) {
	    }
	}

	Optional<CommandResponse> bridgeCommand(BridgeCommand bridgeCommand);
	
}
