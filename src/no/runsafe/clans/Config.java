package no.runsafe.clans;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

public class Config implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		clanSize = configuration.getConfigValueAsInt("clanSize");
	}

	public int getClanSize()
	{
		return clanSize;
	}

	private int clanSize;
}
