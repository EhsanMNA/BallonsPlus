package be.shark_zekrom.commands;

import be.shark_zekrom.Main;
import be.shark_zekrom.inventory.Menu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class BalloonsTabCompleter implements TabCompleter {

    static ArrayList<String > list = new ArrayList<>();

    static {
        list.add("help"); list.add("reload"); list.add("menu");
        list.add("spawn"); list.add("remove"); list.add("particles");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String > result = new ArrayList<>();

        if (args.length == 1){
            for (String arg : list){
                if (arg.toLowerCase().startsWith(args[0].toLowerCase())) result.add(arg);
            }
            return result;
        }else if (args.length == 2){
            if (args[0].equalsIgnoreCase("particles")){
                if (args[1].isEmpty() || args[1].toLowerCase().startsWith("o")) {result.add("off"); result.add("on");}
                if (args[1].toLowerCase().startsWith("on")) result.add("on");
                if (args[1].toLowerCase().startsWith("of")) result.add("off");
            }
            if (args[0].equalsIgnoreCase("spawn")){
                for (String ballonName : Menu.list){
                    if (ballonName.toLowerCase().startsWith(args[1].toLowerCase())) result.add(ballonName);
                }
            }
            return result;
        }

        return null;
    }
}
