package com.gluonapplication.components.entity;

public class Density {
	public enum DENSITY {
		D000(0, 10), D010(10, 50), D050(50, 100), D100(100, 250), D250(250, 500), D500(500, 10000);

		final double ini;
		final double end;

		private DENSITY(double ini, double end) {
			this.ini = ini;
			this.end = end;
		}

		public double getEnd() {
			return end;
		}

		public double getIni() {
			return ini;
		}

	}

	/**
	 *
	 * @param state
	 * @return DENSITY category for the given US State
	 */
	public static DENSITY getDensity(USState state) {
		return getDensity(state.getDensity());
	}

	/**
	 *
	 * @param density
	 * @return DENSITY category for a given population density
	 */
	public static DENSITY getDensity(double density) {
		for (DENSITY d : DENSITY.values()) {
			if (d.getIni() <= density && density < d.getEnd()) {
				return d;
			}
		}
		return DENSITY.D000;
	}
}
