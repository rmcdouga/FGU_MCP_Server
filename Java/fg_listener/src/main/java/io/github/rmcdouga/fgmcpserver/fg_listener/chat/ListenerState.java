package io.github.rmcdouga.fgmcpserver.fg_listener.chat;

import java.util.List;
import java.util.Map;

import io.github.rmcdouga.fgmcpserver.fg_listener.chat.domain.QueryId;

// This is placeholder for the state of the listener.  
// It will be used to restart processing if the listener shuts down and is restarted.
// This functionality has not yet been implemented.
public class ListenerState {
    List<String> identity;
	String campaign;
	Integer cursor;
    List<Job> jobs;
	
	public class Job {
		enum JobState {
			Queued,
			Running,
			Applying,
			Posting,
			Done,
			Failed,
			;
		}

        public record JobRequest(
            QueryId queryId,
            String question,
            String sender,
            String requesterRole,
            String replyVisibility,
            String character,
            String campaign,
            String ruleset
	    ) {
	    }
	    	JobState state;
	        JobRequest request;

	}
	
}
