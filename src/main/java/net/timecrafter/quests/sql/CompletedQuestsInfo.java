package net.timecrafter.quests.sql;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.timecrafter.quests.data.Quest;
import net.timecrafter.timelib.database.TimeDB;
import net.timecrafter.timelib.database.api.InfoCore;
import net.timecrafter.timelib.database.api.InfoTable;

public class CompletedQuestsInfo extends InfoCore {

	private static final Map<UUID, Set<String>> cache = new HashMap<>();
	private static final InfoTable TABLE = InfoTable
			.get("completed_quests", "player_id int, quest_id varchar(64)");

	private Set<String> completedQuestIds;

	@Override
	protected void load() {
		UUID uuid = getPlayerInfo().getUniqueId();
		if (cache.containsKey(uuid)) {
			completedQuestIds = cache.get(uuid);
		} else {
			completedQuestIds = new HashSet<>();
			int playerId = getPlayerInfo().getPlayerId();
			try (Connection connection = TimeDB.getConnection();
					PreparedStatement statement = connection
							.prepareStatement("SELECT quest_id FROM " + TABLE.getTableName() + " WHERE player_id=?")) {
				statement.setInt(1, playerId);
				ResultSet result = statement.executeQuery();
				while (result.next()) {
					completedQuestIds.add(result.getString(1));
				}
				cache.put(uuid, completedQuestIds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Marks a quest as complete by adding it as a record to the database
	 *
	 * @param quest to be marked as complete by the player
	 * @return whether the update was successful
	 */
	public CompletableFuture<Boolean> markComplete(Quest quest) {
		if (isQuestComplete(quest)) {
			return CompletableFuture.completedFuture(false);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				try (Connection connection = TimeDB.getConnection();
						PreparedStatement statement = connection.prepareStatement(
								"INSERT INTO " + TABLE.getTableName() + " (player_id, quest_id) VALUES (?,?)")) {
					statement.setInt(1, getPlayerInfo().getPlayerId());
					String id = quest.getQuestIdentifier();
					statement.setString(2, id);
					statement.executeUpdate();
					completedQuestIds.add(id);
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			});
		}
	}

	/**
	 * Removes the record making this quest as complete from the database
	 *
	 * @param quest to be unmarked as complete
	 * @return whether the update was successful
	 */
	public CompletableFuture<Boolean> markIncomplete(Quest quest) {
		if (isQuestComplete(quest)) {
			return CompletableFuture.supplyAsync(() -> {
				try (Connection connection = TimeDB.getConnection();
						PreparedStatement statement = connection.prepareStatement(
								"DELETE FROM " + TABLE.getTableName() + " WHERE player_id=? AND quest_id=?")) {
					statement.setInt(1, getPlayerInfo().getPlayerId());
					String id = quest.getQuestIdentifier();
					statement.setString(2, id);
					statement.executeUpdate();
					completedQuestIds.remove(id);
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			});
		} else {
			return CompletableFuture.completedFuture(false);
		}
	}

	/**
	 * Checks whether there is a record marking the given quest as complete
	 *
	 * @param quest to be checked
	 * @return whether the quest is complete
	 */
	public boolean isQuestComplete(Quest quest) {
		return completedQuestIds.contains(quest.getQuestIdentifier());
	}

	@Override
	public List<InfoTable> getReferencedTables() {
		return Collections.singletonList(TABLE);
	}

	/**
	 * Removes a player from the cache
	 *
	 * @param player to be removed
	 */
	static void purgeCache(Player player) {
		cache.remove(player.getUniqueId());
	}

}
