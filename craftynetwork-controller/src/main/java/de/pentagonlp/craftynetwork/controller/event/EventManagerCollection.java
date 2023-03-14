package de.pentagonlp.craftynetwork.controller.event;

import java.util.ArrayList;

/**
 * A list of {@link EventManager EventManagers}. Used to register all
 * {@link Event Events} of an object and to automatically find the correct
 * {@link EventManager} to trigger an {@link Event}. <br>
 * Extends {@link ArrayList} to store its {@link EventManager EventManagers}.
 * 
 * @author PentagonLP
 */
// TODO implement canceling events
public class EventManagerCollection extends ArrayList<EventManager> {

	/**
	 * The {@code serialVersionUID}, required as {@link EventManagerCollection}
	 * extends {@link ArrayList}
	 */
	private static final long serialVersionUID = 5861985937079112805L;

	/**
	 * Register all {@link Event Events} of an object by finding all listener
	 * interfaces it implements.
	 * 
	 * @param listener the object to register all {@link Event Events} for
	 */
	public void registerAllEvents(Listener listener) {
		for (EventManager e : this) {
			if (e.getListenerClass().isInstance(listener)) {
				e.registerEvent(listener);
			}
		}
	}

	/**
	 * Find the correct {@link EventManager} for the {@link Event} and trigger it.
	 * Only the {@link Listener Listeners} first suitable {@link EventManager} will
	 * be triggered.
	 * 
	 * @param event the triggered event of type
	 *              {@link EventManager#getEventClass()}. Used to pass on event
	 *              parameters to the registered listeners upon triggering the
	 *              event.
	 * 
	 */
	public void fireEvent(Event event) {
		for (EventManager e : this) {
			if (e.getEventClass().isInstance(event)) {
				e.fireEvent(event);
				return;
			}
		}
	}

}
