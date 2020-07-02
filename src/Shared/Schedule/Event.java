package Shared.Schedule;

import java.io.Serializable;
import java.util.Random;

/**
 * An event is a single billboard time allocation that exists in the Schedule.
 * You can treat startTime as a unique identifier, as no two events should start at the same time
 *
 * If you're looking for the more complex behaviour, take a look at the Schedule class.
 *
 * @author Lucas Maldonado n10534342
 */
public class Event implements Serializable {

	// Times are measured in milliseconds since unix epoch
	public long startTime;
	public long endTime;

	public String billboardName; // Reference to the billboard to display
	public String author; // The user that created this event

	public long loopInterval; // if >0 event should loop


	/** Constructor */
	public Event(long inStartTime, long inEndTime, String inBillboardName, String inAuthor, long inLoopInterval) {
		startTime = inStartTime;
		endTime = inEndTime;
		billboardName = inBillboardName;
		author = inAuthor;
		if (inLoopInterval > 0) {
			loopInterval = Math.max(inLoopInterval, getDuration());
		} else {
			loopInterval = 0;
		}

	}

	/** Constructor that inits everything to zero */
	public Event() {
		startTime = 0;
		endTime = 0;
		billboardName = "";
		author = "";
	}

	/** Constructor for quickly creating an event for debugging */
	public Event(long start, long end) {
		startTime = start;
		endTime = end;
		billboardName = "test";
		Random r = new Random(System.currentTimeMillis());
		author = "Test Event " + (r.nextInt() % 100);
	}

	/**
	 * Creates the next event if it's a repeating event.
	 * Only call if loopInterval >0
	 */
	public Event nextEvent() {
		return new Event(
			startTime + loopInterval,
			endTime + loopInterval,
			billboardName,
			author,
			loopInterval);
	}


	/** Gets how long this billboard will run for */
	public long getDuration() {
		return endTime - startTime;
	}


	/** Sets endTime so that Duration will be this amount */
	public void setDuration(long duration) {
		endTime = startTime + duration;
	}


	/**
	 * Used to check if this event has no info in it, like when the scheduler has no billboard to display.
	 * An event is blank when all values are 0 or equivalent.
	 * @return true if this Event is blank, false otherwise.
	 */
	public boolean isBlank() {
		return startTime == 0
			&& endTime == 0
			&& billboardName == ""
			&& author == "";
	}

	/***
	 * prints information from event
	 * @return a string containing the formatted data
	 */
	public String toString() {
		return billboardName;
	}


	public boolean equals(Object otherObject) {
		if (otherObject instanceof Event) {
			Event e = (Event)otherObject;
			return e.billboardName.equals(billboardName)
				&& e.startTime == startTime
				&& e.author.equals(author)
				&& e.endTime == endTime
				&& e.loopInterval == loopInterval;
		}
		return false;
	}
}
