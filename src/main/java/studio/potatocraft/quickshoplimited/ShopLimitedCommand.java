package studio.potatocraft.quickshoplimited;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.command.CommandHandler;
import org.maxgamer.quickshop.event.CalendarEvent;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.util.MsgUtil;

import java.util.Locale;

public class ShopLimitedCommand implements CommandHandler<Player> {
    @Override
    public void onCommand(Player commandSender, String s, String[] strings) {
        if (strings.length < 1) {
            MsgUtil.sendMessage(commandSender, ChatColor.RED + MsgUtil.getMessage("command.wrong-args", commandSender));
            return;
        }
        final BlockIterator bIt = new BlockIterator(commandSender, 10);
        Shop shop = null;

        if (!bIt.hasNext()) {
            MsgUtil.sendMessage(commandSender, MsgUtil.getMessage("not-looking-at-shop", commandSender));
            return;
        }
        while (bIt.hasNext()) {
            final Block b = bIt.next();
            final Shop searching = QuickShop.getInstance().getShopManager().getShop(b.getLocation());
            if (searching == null) {
                continue;
            }
            shop = searching;
            break;
        }
        if (shop == null) {
            MsgUtil.sendMessage(commandSender, MsgUtil.getMessage("not-looking-at-shop", commandSender));
            return;
        }
        ConfigurationSection manager = shop.getExtra(QuickShopLimited.instance);
        switch (strings[0]){
            case "set":
                try {
                    int limitAmount = Integer.parseInt(strings[1]);
                    if (limitAmount > 0) {
                        manager.set("limit", limitAmount);
                        MsgUtil.sendMessage(commandSender, ChatColor.GREEN + QuickShopLimited.instance.getConfig().getString("success-setup"));
                    } else {
                        manager.set("limit", null);
                        manager.set("data", null);
                        MsgUtil.sendMessage(commandSender, ChatColor.GREEN + QuickShopLimited.instance.getConfig().getString("success-reset"));
                    }
                    shop.setExtra(QuickShopLimited.instance,manager);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + MsgUtil.getMessage("not-a-integer", commandSender, strings[1]));
                }
                return;
            case "unset":
                manager.set("limit", null);
                manager.set("data", null);
                MsgUtil.sendMessage(commandSender, ChatColor.GREEN + QuickShopLimited.instance.getConfig().getString("success-reset"));
                shop.setExtra(QuickShopLimited.instance,manager);
                return;
            case "reset":
                manager.set("data", null);
                shop.setExtra(QuickShopLimited.instance,manager);
                MsgUtil.sendMessage(commandSender, ChatColor.GREEN + QuickShopLimited.instance.getConfig().getString("success-reset"));
                return;
            case "period":
                try {
                    CalendarEvent.CalendarTriggerType type = CalendarEvent.CalendarTriggerType.valueOf(strings[1].toUpperCase(Locale.ROOT));
                    manager.set("period", type.name());
                    MsgUtil.sendMessage(commandSender, ChatColor.GREEN + QuickShopLimited.instance.getConfig().getString("success-setup"));
                    shop.setExtra(QuickShopLimited.instance,manager);
                }catch (IllegalArgumentException ignored){
                    MsgUtil.sendMessage(commandSender, ChatColor.RED + MsgUtil.getMessage("command.wrong-args", commandSender));
                }
                return;
        }

    }
}
