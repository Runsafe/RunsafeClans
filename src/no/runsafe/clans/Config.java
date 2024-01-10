package no.runsafe.clans;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		Message.invalidPlayer = configuration.getConfigValueAsString("message.invalidPlayer");
		Message.error = configuration.getConfigValueAsString("message.error");
		Message.invalidLocation = configuration.getConfigValueAsString("message.invalidLocation");
		Message.wrongWorld = configuration.getConfigValueAsString("message.wrongWorld");
		Message.clanAlreadyExists = configuration.getConfigValueAsString("message.clanAlreadyExists");
		Message.clanDoesntExist = configuration.getConfigValueAsString("message.clanDoesntExist");
		Message.censoredInvalidClanTag = configuration.getConfigValueAsString("message.censoredInvalidClanTag");
		Message.invalidClanTag = configuration.getConfigValueAsString("message.invalidClanTag");
		Message.userAlreadyInClan = configuration.getConfigValueAsString("message.userAlreadyInClan");
		Message.userNotInClan = configuration.getConfigValueAsString("message.userNotInClan");
		Message.userNotClanLeader = configuration.getConfigValueAsString("message.userNotClanLeader");
		Message.playerAlreadyInClan = configuration.getConfigValueAsString("message.playerAlreadyInClan");
		Message.playerNotInClan = configuration.getConfigValueAsString("message.playerNotInClan");
		Message.playerNotInUserClan = configuration.getConfigValueAsString("message.playerNotInUserClan");

		Message.clanDisbanded = configuration.getConfigValueAsString("message.clanDisbanded");
		Message.userClanDisbanded = configuration.getConfigValueAsString("message.userClanDisbanded");
		Message.userLeaveClan = configuration.getConfigValueAsString("message.userLeaveClan");
		Message.userPassLeadership = configuration.getConfigValueAsString("message.userPassLeadership");
		Message.newPlayerGivenClanLeadership = configuration.getConfigValueAsString("message.newPlayerGivenClanLeadership");
		Message.playerClanJoin = configuration.getConfigValueAsString("message.playerClanJoin");
		Message.playerClanLeave = configuration.getConfigValueAsString("message.playerClanLeave");
		Message.clanOwnerLeaveFail = configuration.getConfigValueAsString("message.clanOwnerLeaveFail");
		Message.playerClanKick = configuration.getConfigValueAsString("message.playerClanKick");
		Message.userClanKick = configuration.getConfigValueAsString("message.userClanKick");
		Message.userClanKickSelfFail = configuration.getConfigValueAsString("message.userClanKickSelfFail");
		// Invite
		Message.Invite.sent = configuration.getConfigValueAsString("message.invite.sent");
		Message.Invite.declined = configuration.getConfigValueAsString("message.invite.declined");
		Message.Invite.userNotInvited = configuration.getConfigValueAsString("message.invite.userNotInvited");
		Message.Invite.playerAlreadyInvited = configuration.getConfigValueAsString("message.invite.playerAlreadyInvited");
		Message.Invite.failClanFull = configuration.getConfigValueAsString("message.invite.failClanFull");
		Message.Invite.joinFailClanFull = configuration.getConfigValueAsString("message.invite.joinFailClanFull");
		Message.Invite.userAccept = configuration.getConfigValueAsString("message.invite.userAccept");
		Message.Invite.userNotifyNew = configuration.getConfigValueAsString("message.invite.userNotifyNew");
		Message.Invite.userNotifyLine1 = configuration.getConfigValueAsString("message.invite.userNotifyLine1");
		Message.Invite.userNotifyLine2 = configuration.getConfigValueAsString("message.invite.userNotifyLine2");
		// Charter
		Message.Charter.created = configuration.getConfigValueAsString("message.charter.created");
		Message.Charter.userAlreadyInClan = configuration.getConfigValueAsString("message.charter.userAlreadyInClan");
		Message.Charter.userSigned = configuration.getConfigValueAsString("message.charter.userSigned");
		Message.Charter.userAlreadySigned = configuration.getConfigValueAsString("message.charter.userAlreadySigned");
		Message.Charter.invalidSignatures = configuration.getConfigValueAsString("message.charter.invalidSignatures");
		Message.Charter.clanForm = configuration.getConfigValueAsString("message.charter.clanForm");
		// Info
		Message.Info.assistanceRequired = configuration.getConfigValueAsString("message.info.assistanceRequired");
		Message.Info.motd = configuration.getConfigValueAsString("message.info.motd");
		Message.Info.welcome = configuration.getConfigValueAsString("message.info.welcome");
		Message.Info.playerLookup = configuration.getConfigValueAsString("message.info.playerLookup");
		Message.Info.playerStats = configuration.getConfigValueAsString("message.info.playerStats");
		Message.Info.dergonSlay = configuration.getConfigValueAsString("message.info.dergonSlay");

		minClanSize = configuration.getConfigValueAsInt("minClanSize");
		clanSize = configuration.getConfigValueAsInt("clanSize");
		clanStatTimeRangeDays = configuration.getConfigValueAsInt("clanStatTimeRangeDays");

		// Get all worlds in the clan universe.
		clanUniverse.clear();
		Collections.addAll(clanUniverse, configuration.getConfigValueAsString("clanUniverse").split(","));
	}

	public int getMinClanSize()
	{
		return minClanSize;
	}

	public int getClanSize()
	{
		return clanSize;
	}

	public List<String> getClanUniverse()
	{
		return clanUniverse;
	}

	public int getClanStatTimeRangeDays()
	{
		return clanStatTimeRangeDays;
	}

	public static final class Message
	{
		public static String invalidPlayer;
		public static String error;
		public static String invalidLocation;
		public static String wrongWorld;
		public static String clanAlreadyExists;
		public static String clanDoesntExist;
		public static String censoredInvalidClanTag;
		public static String invalidClanTag;
		public static String userAlreadyInClan;
		public static String userNotInClan;
		public static String userNotClanLeader;
		public static String playerAlreadyInClan;
		public static String playerNotInClan;
		public static String playerNotInUserClan;

		public static String clanDisbanded;
		public static String userClanDisbanded;
		public static String userLeaveClan;
		public static String userPassLeadership;
		public static String newPlayerGivenClanLeadership;
		public static String playerClanJoin;
		public static String playerClanLeave;
		public static String clanOwnerLeaveFail;
		public static String playerClanKick;
		public static String userClanKick;
		public static String userClanKickSelfFail;

		public static final class Invite
		{
			public static String sent;
			public static String declined;
			public static String userNotInvited;
			public static String playerAlreadyInvited;
			public static String failClanFull;
			public static String joinFailClanFull;
			public static String userAccept;
			public static String userNotifyNew;
			public static String userNotifyLine1;
			public static String userNotifyLine2;
		}

		public static final class Charter
		{
			public static String created;
			public static String userAlreadyInClan;
			public static String userSigned;
			public static String userAlreadySigned;
			public static String invalidSignatures;
			public static String clanForm;
		}

		public static final class Info
		{
			public static String assistanceRequired;
			public static String motd;
			public static String welcome;
			public static String playerLookup;
			public static String playerStats;
			public static String dergonSlay;
		}
	}

	private int minClanSize;
	private int clanSize;
	private int clanStatTimeRangeDays;
	private final List<String> clanUniverse = new ArrayList<>(0);
}
