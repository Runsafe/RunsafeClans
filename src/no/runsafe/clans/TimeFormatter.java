package no.runsafe.clans;

import java.time.Duration;
import java.time.Instant;

public class TimeFormatter
{
	public static String formatInstant(Instant instant)
	{
		if (instant == null)
			return "Invalid Time";

		return formatMillis(Duration.between(instant, Instant.now()).toMillis());
	}

	private static String formatMillis(long millis)
	{
		if (millis < DAY_MILLISECONDS)
			return "Less than one day";

		String timeReturn = "";

		if (millis >= YEAR_MILLISECONDS) // Years
		{
			int years = (int) (millis / YEAR_MILLISECONDS);
			millis -= (years * YEAR_MILLISECONDS);

			timeReturn += (" " + years + " year");
			if (years != 1)
				timeReturn += "s";
		}
		if (millis >= MONTH_MILLISECONDS) // Months
		{
			int months = (int) (millis / MONTH_MILLISECONDS);
			millis -= (months * MONTH_MILLISECONDS);

			timeReturn += (" " + months + " month");
			if (months != 1)
				timeReturn += "s";
		}
		if (millis >= WEEK_MILLISECONDS) // Weeks
		{
			int weeks = (int) (millis / WEEK_MILLISECONDS);
			millis -= (weeks * WEEK_MILLISECONDS);

			timeReturn += (" " + weeks + " week");
			if (weeks != 1)
				timeReturn += "s";
		}
		if (millis >= DAY_MILLISECONDS) // Days
		{
			int days = (int) (millis / DAY_MILLISECONDS);

			timeReturn += (" " + days + " day");
			if (days != 1)
				timeReturn += "s";
		}

		return timeReturn.substring(1); // Get rid of first space
	}

	private static final long YEAR_MILLISECONDS = 31536000000L; // 365 days
	private static final long MONTH_MILLISECONDS = 2628000000L; // 1/12 of a year
	private static final long WEEK_MILLISECONDS = 604800000L;
	private static final long DAY_MILLISECONDS = 86400000L;
}