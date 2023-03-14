package de.pentagonlp.craftynetwork.controller.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import de.pentagonlp.craftynetwork.controller.main.CraftyNetworkController;
import de.pentagonlp.simplelogging.Level;
import de.pentagonlp.simplelogging.Log;



/**
 * EventManager for a single event type. Used to register {@link Listener
 * Listeners} and trigger {@link Event Events} for the Listeners.
 * 
 * @author PentagonLP
 */
// TODO implement canceling events
public class EventManager extends ArrayList<Listener> {

	/**
	 * The {@code serialVersionUID}, required as {@link EventManager} extends
	 * {@link ArrayList}
	 */
	private static final long serialVersionUID = -8754576194430177585L;

	/**
	 * Name of the method in the event specific {@link Listener}, which is called to
	 * fire the {@link Event}
	 */
	private final String methodName;

	/**
	 * Class of the event specific {@link Listener}. Required for casting.
	 */
	private final Class<?> listenerclass;
	/**
	 * The event specific {@link Event} class. Required for casting.
	 */
	private final Class<?> eventclass;

	/**
	 * Creates a new {@link EventManager} and stores it in the standard
	 * {@link CraftyNetworkController#getEventmanagers()} {@link EventManagerCollection}.
	 * Checks if the given {@code methodName} exists in the events' listener
	 * interface.
	 * 
	 * @param methodName    the name of the method in the events' {@link Listener}
	 *                      class that is called when the {@link Event} is
	 *                      triggered.
	 * @param listenerclass the events' listener interface extending
	 *                      {@link Listener} and containing the method
	 *                      {@code methodName(Event)}
	 * @param eventclass    the events' event class extending {@link Event}. Used to
	 *                      pass on event parameters to the registered listeners
	 *                      upon triggering the event.
	 * @throws NoSuchMethodException if {@code methodName(Event)} does not exist in
	 *                               the events' listener interface
	 * @throws SecurityException     if {@code methodName(Event)} exists in the
	 *                               events' listener interface, but is not
	 *                               reachable from the {@link EventManager} class.
	 *                               <b>Note:</b> This exception also occurs if the
	 *                               events' listener interface is used for an
	 *                               anonymous type, so always use a non anonymous
	 *                               context for your classes subscribed to the
	 *                               event!
	 */
	public EventManager(String methodName, Class<?> listenerclass, Class<?> eventclass)
			throws NoSuchMethodException, SecurityException {
		this(methodName, listenerclass, eventclass, CraftyNetworkController.getEventmanagers());
	}

	/**
	 * Creates a new {@link EventManager} and stores it in a given
	 * {@link EventManagerCollection}. Checks if the given {@code methodName} exists
	 * in the events' listener interface.
	 * 
	 * @param methodName    the name of the method in the events' {@link Listener}
	 *                      class that is called when the {@link Event} is
	 *                      triggered.
	 * @param listenerclass the events' listener interface extending
	 *                      {@link Listener} and containing the method
	 *                      {@code methodName(Event)}
	 * @param eventclass    the events' event class extending {@link Event}. Used to
	 *                      pass on event parameters to the registered listeners
	 *                      upon triggering the event.
	 * @param eventmanagers {@link EventManagerCollection} to add the
	 *                      {@code EventManager} to.
	 * @throws NoSuchMethodException if {@code methodName(Event)} does not exist in
	 *                               the events' listener interface
	 * @throws SecurityException     if {@code methodName(Event)} exists in the
	 *                               events' listener interface, but is not
	 *                               reachable from the {@link EventManager} class.
	 *                               <b>Note:</b> This exception also occurs if the
	 *                               events' listener interface is used for an
	 *                               anonymous type, so always use a non anonymous
	 *                               context for your classes subscribed to the
	 *                               event!
	 */
	public EventManager(String methodName, Class<?> listenerclass, Class<?> eventclass,
			EventManagerCollection eventmanagers) throws NoSuchMethodException, SecurityException {

		this.methodName = methodName;
		this.listenerclass = listenerclass;
		this.eventclass = eventclass;

		// This will throw an exception if method does not exist or is inaccessible
		listenerclass.getMethod(methodName, new Class[] { getEventClass() });

		eventmanagers.add(this);

		Log.log(Level.DEBUG, "EventManager for " + eventclass.getName() + " registered");

	}

	/**
	 * Registers a new object implementing the events' listener interface to have
	 * {@link EventManager#getMethodName() (EventManager.getMethodeName())(Event)}
	 * called when the event is triggered.
	 * 
	 * <p>
	 * <b>Note:</b> Please use {@link EventManager#registerEvent(Listener)} instead,
	 * as it requires less method calls and does the same thing. <br>
	 * {@code add()} is only overridden to prevent listeners to be added without
	 * checking if it is actually of type {@link EventManager#getListenerClass()}.
	 * 
	 * @param listener the object implementing the events' listener interface
	 * @throws IllegalArgumentException if the object does not implementing the
	 *                                  events' listener interface
	 * @throws NullPointerException     if {@code listener} is null
	 */
	@Override
	public final boolean add(Listener listener) {
		registerEvent(listener);
		return true;
	}

	/**
	 * Registers a new object implementing the events' listener interface to have
	 * {@link EventManager#getMethodName() (EventManager.getMethodeName())(Event)}
	 * called when the event is triggered.
	 * 
	 * @param listener the object implementing the events' listener interface
	 * @throws IllegalArgumentException if the object does not implementing the
	 *                                  events' listener interface
	 * @throws NullPointerException     if {@code listener} is null
	 */
	public void registerEvent(Listener listener) {
		if (!getListenerClass().isInstance(listener))
			throw new IllegalArgumentException("Cant register " + listener.getClass().getName()
					+ " as event manager manages " + getListenerClass().getName());
		super.add(listener);
	}

	/**
	 * Triggers the event and calls {@link EventManager#getMethodName()
	 * (EventManager.getMethodeName())(Event)} for all listeners. Note that while
	 * all occurring {@link Exception Exceptions} are caught, the event is still
	 * triggered in the same {@link Thread} as the call for
	 * {@code EventManager.fireEvent(Event)}.
	 * 
	 * @param event the triggered event of type
	 *              {@link EventManager#getEventClass()}. Used to pass on event
	 *              parameters to the registered listeners upon triggering the
	 *              event.
	 * @throws IllegalArgumentException if the {@code event} is not of type
	 *                                  {@link EventManager#getEventClass()}
	 * @throws NullPointerException     if {@code event} is null
	 */
	public void fireEvent(Event event) {
		if (!event.getClass().equals(getEventClass()))
			throw new IllegalArgumentException("Cant fire " + event.getClass().getName() + " as event manager manages "
					+ getEventClass().getName());
		for (Listener listener : this) {
			try {
				try {
					listener.getClass().getMethod(methodName, new Class[] { getEventClass() }).invoke(listener,
							getEventClass().cast(event));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					Log.log(Level.WARNING,
							"Failed to fire event " + event.getClass().getSimpleName() + " for "
									+ (listener.getClass().getSimpleName().equals("") ? listener.getClass().getName()
											: listener.getClass().getSimpleName())
									+ " (Is something wrong with the class setup?):");
					e.printStackTrace();
				}
			} catch (Throwable t) {
				Log.log(Level.WARNING,
						"Fireing event " + event.getClass().getSimpleName() + " for "
								+ (listener.getClass().getSimpleName().equals("") ? listener.getClass().getName()
										: listener.getClass().getSimpleName())
								+ " threw an exception:");
				Log.printStackTrace(t);
			}
		}
	}

	/**
	 * Get the name of the name of the method in the events' {@link Listener} class
	 * that is called when the {@link Event} is triggered.
	 * 
	 * @return the name of the event-triggered method in the events'
	 *         {@link Listener} class
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Get the events' listener interface extending {@link Listener} and containing
	 * the method {@code methodName(Event)}
	 * 
	 * @return the events' listener interface
	 */
	public Class<?> getListenerClass() {
		return listenerclass;
	}

	/**
	 * Get the events' event class extending {@link Event}. Used to pass on event
	 * parameters to the registered listeners upon triggering the event.
	 * 
	 * @return the events' event class
	 */
	public Class<?> getEventClass() {
		return eventclass;
	}

}
