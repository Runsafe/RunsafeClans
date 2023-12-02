package no.runsafe.clans;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		invalidPlayerMessage = configuration.getConfigValueAsString("message.invalidPlayer");
		errorMessage = configuration.getConfigValueAsString("message.error");
		invalidLocationMessage = configuration.getConfigValueAsString("message.invalidLocation");
		wrongWorldMessage = configuration.getConfigValueAsString("message.wrongWorld");
		clanAlreadyExistsMessage = configuration.getConfigValueAsString("message.clanAlreadyExists");
		clanDoesntExistMessage = configuration.getConfigValueAsString("message.clanDoesntExist");
		censoredInvalidClanTagMessage = configuration.getConfigValueAsString("message.censoredInvalidClanTag");
		invalidClanTagMessage = configuration.getConfigValueAsString("message.invalidClanTag");
		userAlreadyInClanMessage = configuration.getConfigValueAsString("message.userAlreadyInClan");
		userNotInClanMessage = configuration.getConfigValueAsString("message.userNotInClan");
		userNotClanLeaderMessage = configuration.getConfigValueAsString("message.userNotClanLeader");
		playerAlreadyInClanMessage = configuration.getConfigValueAsString("message.playerAlreadyInClan");
		playerNotInClanMessage = configuration.getConfigValueAsString("message.playerNotInClan");
		playerNotInUserClanMessage = configuration.getConfigValueAsString("message.playerNotInUserClan");

		clanDisbandedMessage = configuration.getConfigValueAsString("message.clanDisbanded");
		userClanDisbandedMessage = configuration.getConfigValueAsString("message.userClanDisbanded");
		userLeaveClanMessage = configuration.getConfigValueAsString("message.userLeaveClan");
		userPassLeadershipMessage = configuration.getConfigValueAsString("message.userPassLeadership");
		newPlayerGivenClanLeadershipMessage = configuration.getConfigValueAsString("message.newPlayerGivenClanLeadership");
		playerClanJoinMessage = configuration.getConfigValueAsString("message.playerClanJoin");
		playerClanLeaveMessage = configuration.getConfigValueAsString("message.playerClanLeave");
		clanOwnerLeaveFailMessage = configuration.getConfigValueAsString("message.clanOwnerLeaveFail");
		playerClanKickMessage = configuration.getConfigValueAsString("message.playerClanKick");
		userClanKickMessage = configuration.getConfigValueAsString("message.userClanKick");
		userClanKickSelfFailMessage = configuration.getConfigValueAsString("message.userClanKickSelfFail");

		inviteSentMessage = configuration.getConfigValueAsString("message.inviteSent");
		invitationDeclinedMessage = configuration.getConfigValueAsString("message.invitationDeclined");
		userNotInvitedMessage = configuration.getConfigValueAsString("message.userNotInvited");
		playerAlreadyInvitedMessage = configuration.getConfigValueAsString("message.playerAlreadyInvited");
		inviteFailClanFullMessage = configuration.getConfigValueAsString("message.inviteFailClanFull");
		joinFailClanFullMessage = configuration.getConfigValueAsString("message.joinFailClanFull");
		userAcceptInviteMessage = configuration.getConfigValueAsString("message.userAcceptInvite");
		userNotifyNewInviteMessage = configuration.getConfigValueAsString("message.userNotifyNewInvite");
		userNotifyInviteLine1Message = configuration.getConfigValueAsString("message.userNotifyInviteLine1");
		userNotifyInviteLine2Message = configuration.getConfigValueAsString("message.userNotifyInviteLine2");

		charterCreatedMessage = configuration.getConfigValueAsString("message.charterCreated");
		charterUserAlreadyInClanMessage = configuration.getConfigValueAsString("message.charterUserAlreadyInClan");
		charterUserSignedMessage = configuration.getConfigValueAsString("message.charterUserSigned");
		charterUserAlreadySignedMessage = configuration.getConfigValueAsString("message.charterUserAlreadySigned");
		charterInvalidSignaturesMessage = configuration.getConfigValueAsString("message.charterInvalidSignatures");
		clanFormMessage = configuration.getConfigValueAsString("message.clanForm");

		assistanceRequiredMessage = configuration.getConfigValueAsString("message.assistanceRequired");
		motd = configuration.getConfigValueAsString("message.motd");
		welcomeMessage = configuration.getConfigValueAsString("message.welcome");
		playerLookupMessage = configuration.getConfigValueAsString("message.playerLookup");
		playerStatsMessage = configuration.getConfigValueAsString("message.playerStats");
		dergonSlayMessage = configuration.getConfigValueAsString("message.dergonSlay");

		minClanSize = configuration.getConfigValueAsInt("minClanSize");
		clanSize = configuration.getConfigValueAsInt("clanSize");

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

	public static String invalidPlayerMessage;
	public static String errorMessage;
	public static String invalidLocationMessage;
	public static String wrongWorldMessage;
	public static String clanAlreadyExistsMessage;
	public static String clanDoesntExistMessage;
	public static String censoredInvalidClanTagMessage;
	public static String invalidClanTagMessage;
	public static String userAlreadyInClanMessage;
	public static String userNotInClanMessage;
	public static String userNotClanLeaderMessage;
	public static String playerAlreadyInClanMessage;
	public static String playerNotInClanMessage;
	public static String playerNotInUserClanMessage;

	public static String clanDisbandedMessage;
	public static String userClanDisbandedMessage;
	public static String userLeaveClanMessage;
	public static String userPassLeadershipMessage;
	public static String newPlayerGivenClanLeadershipMessage;
	public static String playerClanJoinMessage;
	public static String playerClanLeaveMessage;
	public static String clanOwnerLeaveFailMessage;
	public static String playerClanKickMessage;
	public static String userClanKickMessage;
	public static String userClanKickSelfFailMessage;

	public static String inviteSentMessage;
	public static String invitationDeclinedMessage;
	public static String userNotInvitedMessage;
	public static String playerAlreadyInvitedMessage;
	public static String inviteFailClanFullMessage;
	public static String joinFailClanFullMessage;
	public static String userAcceptInviteMessage;
	public static String userNotifyNewInviteMessage;
	public static String userNotifyInviteLine1Message;
	public static String userNotifyInviteLine2Message;

	public static String charterCreatedMessage;
	public static String charterUserAlreadyInClanMessage;
	public static String charterUserSignedMessage;
	public static String charterUserAlreadySignedMessage;
	public static String charterInvalidSignaturesMessage;
	public static String clanFormMessage;

	public static String assistanceRequiredMessage;
	public static String motd;
	public static String welcomeMessage;
	public static String playerLookupMessage;
	public static String playerStatsMessage;
	public static String dergonSlayMessage;

	private int minClanSize;
	private int clanSize;
	private List<String> clanUniverse = new ArrayList<>(0);
}
