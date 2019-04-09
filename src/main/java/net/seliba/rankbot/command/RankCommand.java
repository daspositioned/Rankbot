package net.seliba.rankbot.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.seliba.rankbot.files.LevelDao;

import java.awt.Color;
import java.util.List;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class RankCommand extends Command {

    private final LevelDao levelDao;

    public RankCommand(LevelDao levelDao) {
        this.levelDao = levelDao;
        this.name = "rank";
    }

    @Override
    protected void execute(CommandEvent event) {
        List<User> mentions = event.getMessage().getMentionedUsers();
        User user = mentions.isEmpty() ? event.getAuthor() : mentions.get(0);
        RankStats stats = fetchRankStats(user);
        String content = String.format(
                "Level: %d\nXP: %d / %d\n%s", stats.level, stats.xp, stats.xpToLevelUp, buildProgressBar(stats)
        );
        event.reply(Messages.createMessage(user, "Rank", content));
    }

    private RankStats fetchRankStats(User user) {
        long level = levelDao.getLevel(user);
        long xp = levelDao.getXp(user);
        long xpToLevelUp = levelDao.getXpToLevelup(level);
        return new RankStats(level, xp, xpToLevelUp);
    }

    private String buildProgressBar(RankStats stats) {
        StringBuilder builder = new StringBuilder();
        double barProgress = (((double) stats.xp) / stats.xpToLevelUp) * 20;
        for (int i = 0; i < barProgress + (20 - barProgress); i++)
            builder.append("▒");
        return builder.toString();
    }

    private class RankStats {
        final long level, xp, xpToLevelUp;

        RankStats(long level, long xp, long xpToLevelUp) {
            this.level = level;
            this.xp = xp;
            this.xpToLevelUp = xpToLevelUp;
        }
    }
}
