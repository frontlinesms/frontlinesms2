package frontlinesms2

import org.quartz.*

// Quartz trigger which should never trigger.
// This is to allow for self-trigger-only jobs - couldn't
// figure out how to do this in the Quartz plugin.
class NeverTrigger extends Trigger {
	private static final Date START_OF_TIME = new Date(0L)
	private static final Date END_OF_TIME = new Date(Long.MAX_VALUE)
	
	Date computeFirstFireTime(Calendar calendar) { START_OF_TIME }
	int executionComplete(JobExecutionContext context, JobExecutionException result) { 0 }
	Date getStartTime() { START_OF_TIME }
	Date getEndTime() { END_OF_TIME }
	Date getFinalFireTime() { END_OF_TIME }
	Date getFireTimeAfter(Date afterTime) { END_OF_TIME }
	Date getNextFireTime() { END_OF_TIME }
	Date getPreviousFireTime() { START_OF_TIME }
	boolean mayFireAgain() { true }
	void setEndTime(Date endTime) {}
	void setStartTime(Date startTime) {}
	void triggered(Calendar calendar) {}
	void updateAfterMisfire(Calendar cal) {}
	void updateWithNewCalendar(Calendar cal, long misfireThreshold) {}
	boolean validateMisfireInstruction(int misfireInstruction) { true }
}