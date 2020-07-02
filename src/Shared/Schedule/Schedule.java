package Shared.Schedule;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Schedule manages a collection of Events. You can add billboards to the Schedule by adding Events.
 * Complex behaviour like looping events is also supported here.
 *
 * @author Lucas Maldonado n10534342
 */
public class Schedule implements Serializable {

	/**
	 * The events that are currently active. Normally only one thing is here, but multiple are allowed to support
	 * layering of events, as specified in the requirements.
	 */
	public ArrayList<Event> activeEvents = new ArrayList<Event>();

	/**
	 * The events that have been scheduled but are not being displayed at the moment. When it's a billboard's turn to be
	 * displayed, it will be moved into the activeEvents array.
	 */
	public ArrayList<Event> upcomingEvents = new ArrayList<Event>();


	// Constructor
	public Schedule() {
	}


	/**
	 * Figures out the event to display, from the list of billboards that can be displayed right now.
	 * Newer events have priority over older ones.
	 * If there are no events to display, will return a blank event. Use event.isBlank to check
	 */
	public Event getCurrentEvent() {
		populateActiveEvents();
		cleanupActiveEvents();

		if (activeEvents.size() == 0) {
			// Return a blank event if there is none to display
			return new Event(0, 0, "", "", 0);

		}

		// TODO unsure if we need a priority system, for now just uses age where newer ones get displayed over old ones
		return activeEvents.get(activeEvents.size() - 1); // Gets the last element in the array


	}


	/**
	 * Adds an event to the schedule. Supports hot-swapping active events, so you can add an event that is currently
	 * active.
	 * @param newEvent the event to add
	 */
	public void scheduleEvent(Event newEvent) {
		System.out.println("Added new event");
		upcomingEvents.add(newEvent);
	}



	/**
	 * Removes an event from the schedule.
	 * @param e the event to remove
	 * @param removeActive if false, will not affect currently active events.
	 * @param removeThisOnly only applies for looping events. If true, the next event will still be scheduled
	 * @return true if the event was found and deleted
	 */
	public boolean removeEvent(Event e, boolean removeActive, boolean removeThisOnly) {
		boolean foundInUpcomingE = upcomingEvents.remove(e);
		boolean foundInActiveE = removeActive && activeEvents.remove(e);

		// Only schedule the next event if we found and deleted something, otherwise we would be creating a new event
		if (removeThisOnly && (foundInActiveE || foundInUpcomingE)) { scheduleNextRepeatingEvent(e); }
		System.out.println("Hello there " + e.billboardName);
		return foundInActiveE || foundInUpcomingE;
	}


	/**
	 * Gets every event scheduled (passed events are deleted for good).
	 * Useful for saving the data to disk.
	 */
	public ArrayList<Event> exportEvents() {
		populateActiveEvents();
		cleanupActiveEvents();
		ArrayList<Event> returnValue = new ArrayList<Event>();
		returnValue.addAll(upcomingEvents);
		returnValue.addAll(activeEvents);
		return returnValue;
	}

	/***
	 * Bulk add a list of events. This will schedule everything in the list
	 * @param events ArrayList contains a list of events to import
	 */
	public void importEvents(ArrayList<Event> events) {
		for (Event e : events) {
			scheduleEvent(e);
		}
	}


	/**
	 * Moves billboards in upcomingEvents to activeEvents if they are to be displayed now.
	 */
	private void populateActiveEvents() {
		ArrayList<Integer> indicesToRemove = new ArrayList<Integer>();

		// Check which events are active now, copy them to activeEvents and mark them for death
		for (int i = upcomingEvents.size() - 1; i >= 0; --i) {
			if (currentTime() >= upcomingEvents.get(i).startTime) {
				indicesToRemove.add(i);
				activeEvents.add(upcomingEvents.get(i));
				System.out.printf("Moved an event to Active Events: start time is %d, current time is %d (%dms late)\n", upcomingEvents.get(i).startTime, currentTime(), currentTime() - upcomingEvents.get(i).startTime);
			}
		}

		// Clear the events that got marked
		for (int i : indicesToRemove) {
			upcomingEvents.remove(i);
		}
	}


	/**
	 * Removes passed events from activeEvents, keeping it nice and tidy.
	 */
	private void cleanupActiveEvents() {
		ArrayList<Integer> indicesToRemove = new ArrayList<Integer>();

		// Check which events have passed, mark them for deletion
		for (int i = activeEvents.size() - 1; i >= 0; --i) {
			if (currentTime() > activeEvents.get(i).endTime) {
				indicesToRemove.add(i);
				System.out.printf("Deleted an old event: end time was %d, current time is %d (finished %dms ago)\n", activeEvents.get(i).endTime, currentTime(), currentTime() - activeEvents.get(i).endTime);
			}
		}


		// Delete the events that got marked
		for (int i : indicesToRemove) {
			scheduleNextRepeatingEvent(activeEvents.get(i));
			activeEvents.remove(i);
		}
	}


	/**
	 * Given a repeating event, calculates when it should next be displayed and schedules it at that time.
	 * This will not schedule the event at the EventEditor given on the variables. Use
	 * the regular scheduleEvent function to do that. This function is intended to be used
	 * to schedule the next event once the current event event has expired.
	 * @param re the event to repeat. Will silently fail if not a Repeating Event
	 */
	private void scheduleNextRepeatingEvent(Event re) {
		if (re.loopInterval > 0) {
			scheduleEvent(re.nextEvent());
		}
	}


	private long currentTime() {
		return System.currentTimeMillis();
	}


	@Override
	public String toString() {
		return Integer.toString(activeEvents.size() + upcomingEvents.size());
	}
}
