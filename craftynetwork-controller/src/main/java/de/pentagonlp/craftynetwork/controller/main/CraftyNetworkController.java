package de.pentagonlp.craftynetwork.controller.main;

import de.pentagonlp.craftynetwork.controller.event.EventManagerCollection;
import de.pentagonlp.simplelogging.Log;

public class CraftyNetworkController {
	
	private final static EventManagerCollection DEFAULTEVENTMANAGERCOLLECTION = new EventManagerCollection();
	
	public static void main(String args[]) {
		Log.log("Good morning, rise and shine!");
	}

	public static EventManagerCollection getEventmanagers() {
		return DEFAULTEVENTMANAGERCOLLECTION;
	}

}
