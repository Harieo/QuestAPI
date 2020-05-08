package net.timecrafter.quests.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableSet;
import java.util.*;
import net.timecrafter.quests.Quest;
import net.timecrafter.quests.stages.QuestStage;

public class QuestParty {

	private static final Set<QuestParty> openParties = new HashSet<>();

	private Quest quest;
	private QuestStage currentStage;
	private final Set<UUID> members = new HashSet<>();

	/**
	 * A set of {@link Player} who are all doing the same {@link Quest}. Parties are cached upon instantiation so please
	 * use {@link #getOrCreateParty(Player)} to create a new one to prevent caching errors.
	 *
	 * Note: A player may not be in more than 1 party at once and no party may be created empty to prevent redundant
	 * caching.
	 */
	private QuestParty() {
		openParties.add(this);
	}

	/**
	 * @return the quest this party is currently doing or null if they are not doing a quest
	 */
	public Quest getQuest() {
		return quest;
	}

	/**
	 * Sets the quest that this party is currently doing
	 *
	 * @param quest that this party is currently doing
	 */
	public void setQuest(Quest quest) {
		this.quest = quest;
	}

	/**
	 * @return the current stage in the {@link Quest} that the party is doing
	 */
	public QuestStage getCurrentStage() {
		return currentStage;
	}

	/**
	 * Sets the {@link QuestStage} that this party is at in the {@link Quest}
	 *
	 * @param stage that the party is currently doing
	 */
	public void setCurrentStage(QuestStage stage) {
		currentStage = stage;
	}

	/**
	 * @return a set of the UUIDs of all party members added to this party
	 */
	public Set<UUID> getPartyMembers() {
		return ImmutableSet.copyOf(members);
	}

	/**
	 * @return a set of all party members which are online in {@link Player} form
	 */
	public Set<Player> getOnlinePartyMembers() {
		Set<Player> players = new HashSet<>();
		for (UUID uuid : members) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				players.add(player);
			}
		}
		return players;
	}

	/**
	 * Adds a player to this party
	 *
	 * @param player to be added
	 */
	public void addMember(Player player) {
		if (members.isEmpty()) {
			cacheParty(this); // Caches the party if it went from empty to not empty
		}
		members.add(player.getUniqueId());
	}

	/**
	 * Removes a player from this party
	 *
	 * @param player to be removed
	 */
	public void removeMember(Player player) {
		members.remove(player.getUniqueId());
		if (members.isEmpty()) {
			deleteParty(this); // Uncaches empty parties
		}
	}

	/**
	 * Checks whether a player is a member of this party
	 *
	 * @param player to be checked
	 * @return whether the player is a member of this party
	 */
	public boolean isMember(Player player) {
		return members.contains(player.getUniqueId());
	}

	/**
	 * Gets the party the player is currently added to from the cache if applicable
	 *
	 * @param player to get the party of
	 * @return the party this player is in or null if they aren't added to any cached parties
	 */
	public static QuestParty getParty(Player player) {
		for (QuestParty openParty : openParties) {
			if (openParty.isMember(player)) {
				return openParty;
			}
		}
		return null;
	}

	/**
	 * Calls {@link #getParty(Player)} but if none exists, creates a new party instead
	 *
	 * @param player to get or create the party of
	 * @return the party retrieved or created
	 */
	public static QuestParty getOrCreateParty(Player player) {
		QuestParty party = getParty(player);
		if (party == null) {
			party = new QuestParty();
			party.addMember(player);
		}
		return party;
	}

	/**
	 * Checks whether a player is in any of the cached parties
	 *
	 * @param player to check against the cached parties
	 * @return whether the player is in any of the cached parties
	 */
	public static boolean isInParty(Player player) {
		return getParty(player) != null;
	}

	/**
	 * @return a set of all the cached parties
	 */
	public static Set<QuestParty> getOpenParties() {
		return openParties;
	}

	/**
	 * Deletes a party from the cache
	 *
	 * @param party to be deleted
	 */
	public static void deleteParty(QuestParty party) {
		openParties.remove(party);
	}

	/**
	 * Caches a party
	 *
	 * @param party to be cached
	 */
	private static void cacheParty(QuestParty party) {
		openParties.add(party);
	}

}
