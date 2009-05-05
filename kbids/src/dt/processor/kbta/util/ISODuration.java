package dt.processor.kbta.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 * Quick and dirty duration parser for ISO 8601 format
 * http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#duration
 */
public class ISODuration {
	private int plusMinus;

	private double years;

	private double months;

	private double days;

	private double hours;

	private double minutes;

	private double seconds;

	/**
	 * Instantiates a Duration from an ISO 8601 format duration string
	 */
	public ISODuration(String isodur) throws InvalidDateException {
		boolean isTime = false;
		String value = null;
		String delim = null;

		plusMinus = 1;
		years = 0.0;
		months = 0.0;
		days = 0.0;
		hours = 0.0;
		minutes = 0.0;
		seconds = 0.0;

		// DURATION FORMAT IS: (-)PnYnMnDTnHnMnS
		StringTokenizer st = new StringTokenizer(isodur, "-PYMDTHS", true);

		try {
			// OPTIONAL SIGN
			value = st.nextToken();

			if (value.equals("-")) {
				plusMinus = -1;
				value = st.nextToken();
			}

			// DURATION MUST START WITH A "P"
			if (!value.equals("P"))
				throw new InvalidDateException(isodur + " : " + value
						+ " : no P deliminator for duration");

			// GET NEXT FIELD
			while (st.hasMoreTokens()) {
				// VALUE

				value = new String(st.nextToken());
				if (value.equals("T")) {
					if (!st.hasMoreTokens())
						throw new InvalidDateException(isodur + " : " + value
								+ ": no values after duration T delimitor");
					value = st.nextToken();
					isTime = true;
				}

				// DELIMINATOR
				if (!st.hasMoreTokens())
					throw new InvalidDateException(isodur + " : " + value
							+ "No deliminator for duration");
				delim = new String(st.nextToken());

				// YEAR
				if (delim.equals("Y")) {
					years = Double.parseDouble(value);
				}

				// MONTH
				else if (delim.equals("M") && isTime == false) {
					months = Double.parseDouble(value);
					if (months != (double) ((int) months))
						throw new InvalidDateException(
								"Cannot process decimal months!");
				}

				// DAYS
				else if (delim.equals("D")) {
					days = Double.parseDouble(value);
				}

				// HOURS
				else if (delim.equals("H")) {
					hours = Double.parseDouble(value);
					isTime = true;
				}

				// MINUTES
				else if (delim.equals("M") && isTime == true) {
					minutes = Double.parseDouble(value);
				}

				// SECONDS
				else if (delim.equals("S")) {
					seconds = Double.parseDouble(value);
				} else {
					throw new InvalidDateException(isodur
							+ ": what duration delimiter is " + delim + "?");
				}
			}

		} catch (NumberFormatException ex) {
			throw new InvalidDateException("[" + ex.getMessage()
					+ "] is not valid");
		}
	}

	/**
	 * Add Duration to a date
	 * 
	 * @return modified Date
	 */
	public long addTo(long date) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(date);
		cal = addTo(cal);
		return cal.getTimeInMillis();
	}

	/**
	 * Add Duration to a Calendar
	 * 
	 * @return modified Calendar
	 */
	public Calendar addTo(Calendar cal) {
		int iyears, imonths, idays, ihours, imins, isecs, millis;

		if (years > 0) {
			iyears = (int) years;
			imonths = 0; // AVOID USING MONTHS
			idays = (int) (365.254 * (years - iyears));
			ihours = (int) (24.0 * 365.254 * (years - iyears - idays / 365.254));
			imins = (int) (60.0 * 24.0 * 365.254 * (years - iyears - idays
					/ 365.254 - ihours / 24.0 / 365.254));
			isecs = (int) (60.0 * 60.0 * 24.0 * 365.254 * (years - iyears
					- idays / 365.254 - ihours / 24.0 / 365.254 - imins / 60.0 / 24.0 / 365.254));
			millis = (int) (1000.0 * 60.0 * 60.0 * 24.0 * 365.254 * (years
					- iyears - idays / 365.254 - ihours / 24.0 / 365.254
					- imins / 60.0 / 24.0 / 365.254 - isecs / 60.0 / 60.0
					/ 24.0 / 365.254));
			cal.add(Calendar.YEAR, plusMinus * iyears);
			cal.add(Calendar.DAY_OF_YEAR, plusMinus * idays);
			cal.add(Calendar.HOUR, plusMinus * ihours);
			cal.add(Calendar.MINUTE, plusMinus * imins);
			cal.add(Calendar.SECOND, plusMinus * isecs);
			cal.add(Calendar.MILLISECOND, plusMinus * millis);
		}

		if (months > 0) {
			imonths = (int) months;
			cal.add(Calendar.MONTH, plusMinus * imonths);
		}

		if (days > 0) {
			idays = (int) days;
			ihours = (int) (24.0 * (days - idays));
			imins = (int) (60.0 * 24.0 * (days - idays - ihours / 24.0));
			isecs = (int) (60.0 * 60.0 * 24.0 * (days - idays - ihours / 24.0 - imins / 24.0 / 60.0));
			millis = (int) (1000.0 * 60.0 * 60.0 * 24.0 * (days - idays
					- ihours / 24.0 - imins / 60.0 / 24.0 - isecs / 60.0 / 60.0 / 24.0));
			cal.add(Calendar.DAY_OF_YEAR, plusMinus * idays);
			cal.add(Calendar.HOUR, plusMinus * ihours);
			cal.add(Calendar.MINUTE, plusMinus * imins);
			cal.add(Calendar.SECOND, plusMinus * isecs);
			cal.add(Calendar.MILLISECOND, plusMinus * millis);
		}

		if (hours > 0) {
			ihours = (int) hours;
			imins = (int) (60.0 * (hours - ihours));
			isecs = (int) (60.0 * 60.0 * (hours - ihours - imins / 60.0));
			millis = (int) (1000.0 * 60.0 * 60.0 * (hours - ihours - imins
					/ 60.0 - isecs / 60.0 / 60.0));
			cal.add(Calendar.HOUR, plusMinus * ihours);
			cal.add(Calendar.MINUTE, plusMinus * imins);
			cal.add(Calendar.SECOND, plusMinus * isecs);
			cal.add(Calendar.MILLISECOND, plusMinus * millis);
		}

		if (minutes > 0) {
			imins = (int) minutes;
			isecs = (int) (60.0 * (minutes - imins));
			millis = (int) (1000.0 * 60.0 * (minutes - imins - isecs / 60.0));
			cal.add(Calendar.MINUTE, plusMinus * imins);
			cal.add(Calendar.SECOND, plusMinus * isecs);
			cal.add(Calendar.MILLISECOND, plusMinus * millis);
		}

		if (seconds > 0) {
			isecs = (int) seconds;
			millis = (int) (1000.0 * (seconds - isecs));
			cal.add(Calendar.SECOND, plusMinus * isecs);
			cal.add(Calendar.MILLISECOND, plusMinus * millis);
		}
		return cal;
	}

	/**
	 * Returns the duration in milliseconds
	 * 
	 * @return The duration in milliseconds
	 */
	public long toMillis() {
		long millis = 0;
		millis += seconds * 1000;
		millis += minutes * 1000 * 60;
		millis += hours * 1000 * 60 * 60;
		millis += days * 1000 * 60 * 60 * 24;
		millis += months * 1000 * 60 * 60 * 30;
		millis += years * 1000 * 60 * 60 * 30 * 365;
		return millis;
	}

	/**
	 * Generate a string representation of an ISO 8601 duration
	 * 
	 * @return a string representing the duration in the ISO 8601 format
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		// OPTIONAL SIGN

		if (plusMinus == -1)
			buffer.append("-");

		// REQUIRED "P"

		buffer.append("P");

		if (years > 0) {
			if (years == (double) ((int) years))
				buffer.append((int) years);
			else
				buffer.append(years);
			buffer.append("Y");
		}
		if (months > 0) {
			if (months == (double) ((int) months))
				buffer.append((int) months);
			else
				buffer.append(months);
			buffer.append("M");
		}
		if (days > 0) {
			if (days == (double) ((int) days))
				buffer.append((int) days);
			else
				buffer.append(days);
			buffer.append("D");
		}

		// DATE-TIME SEPARATOR (IF NEEDED)

		if ((years > 0 || months > 0 || days > 0)
				&& (hours > 0 || minutes > 0 || seconds > 0))
			buffer.append("T");

		if (hours > 0) {
			if (hours == (double) ((int) hours))
				buffer.append((int) hours);
			else
				buffer.append(hours);
			buffer.append("H");
		}
		if (minutes > 0) {
			if (minutes == (double) ((int) minutes))
				buffer.append((int) minutes);
			else
				buffer.append(minutes);
			buffer.append("M");
		}
		if (seconds > 0) {
			buffer.append(seconds);
			buffer.append("S");
		}

		return buffer.toString();
	}
}
