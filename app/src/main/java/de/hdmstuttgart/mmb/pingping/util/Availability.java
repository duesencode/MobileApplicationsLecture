package de.hdmstuttgart.mmb.pingping.util;

public enum Availability {
	HIGH_AVAILABILITY {
		public String toString() {
			return "Hohe Verfügbarkeit";
		}
	},
	AVAILABLE {
		public String toString() {
			return "Verfügbar";
		}
	},
	LOW_AVAILABILITY {
		public String toString() {
			return "Schlechte Verfügbarkeit";
		}
	},
	NO_SCORE {
		public String toString() {
			return "Score nicht verfügbar";
		}
	},
}
