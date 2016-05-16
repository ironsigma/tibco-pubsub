package com.hawkprime.tibco;

import org.jboss.weld.environment.se.Weld;

public final class TibcoPubSub {
	private TibcoPubSub() {
		/* empty */
	}

	// CHECKSTYLE IGNORE UncommentedMain
	public static void main(String[] args) {
		System.out.println("Tibco Pub/Sub v3.0");
		Weld weld = new Weld()
			.disableDiscovery()
			.packages(TibcoPubSub.class);

		weld.initialize();
		weld.shutdown();
	}
}
