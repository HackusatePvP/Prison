package tech.mcprison.prison.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import tech.mcprison.prison.Prison;
import tech.mcprison.prison.commands.Command;
import tech.mcprison.prison.internal.CommandSender;
import tech.mcprison.prison.modules.Module;
import tech.mcprison.prison.modules.ModuleManager;
import tech.mcprison.prison.ranks.PrisonRanks;
import tech.mcprison.prison.ranks.managers.LadderManager;
import tech.mcprison.prison.spigot.SpigotPrison;
import tech.mcprison.prison.spigot.gui.ListenersPrisonManager;
import tech.mcprison.prison.spigot.gui.rank.SpigotConfirmPrestigeGUI;
import tech.mcprison.prison.spigot.gui.rank.SpigotPlayerPrestigesGUI;

public class PrisonSpigotPrestigeCommands
				extends PrisonSpigotBaseCommands {

	private final Configuration messages = SpigotPrison.getInstance().getMessagesConfig();
	
	@Command(identifier = "prestiges", onlyPlayers = true, altPermissions = {"-none-", "prison.admin"})
	public void prestigesGUICommand(CommandSender sender) {

		if ( !isPrisonConfig( "prestiges") ) {
			sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.PrestigesDisabledDefault")));
			return;
		}

		if ( isConfig( "prestiges-gui-enabled") ) {
			sender.dispatchCommand( "gui prestiges");
		}
		else {
			sender.dispatchCommand( "ranks list prestiges");
		}
	}


	@Command(identifier = "prestige", onlyPlayers = true, altPermissions = "-none-")
	public void prestigesPrestigeCommand(CommandSender sender) {

		if ( isPrisonConfig( "prestiges" ) ) {
			sender.dispatchCommand("gui prestige");
		}
	}



    @Command( identifier = "gui prestige", description = "GUI Prestige",
  		  aliases = {"prisonmanager prestige"} )
    public void prisonManagerPrestige(CommandSender sender ) {

        if ( isPrisonConfig("prestiges") ) {

            if (!(PrisonRanks.getInstance().getLadderManager().getLadder("prestiges").isPresent())) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ranks ladder create prestiges");
            }

            PrisonRanks rankPlugin;

            ModuleManager modMan = Prison.get().getModuleManager();
            Module module = modMan == null ? null : modMan.getModule( PrisonRanks.MODULE_NAME ).orElse( null );

            if ( module != null ) {

            	rankPlugin = (PrisonRanks) module;

            	LadderManager lm = null;
            	if (rankPlugin != null) {
            		lm = rankPlugin.getLadderManager();
            	}

            	if (lm != null && (!(lm.getLadder("default").isPresent()) ||
            			!(lm.getLadder("default").get().getLowestRank().isPresent()) ||
            			lm.getLadder("default").get().getLowestRank().get().name == null)) {
            		sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.DefaultLadderEmpty")));
            		return;
            	}

            	if (lm != null && (!(lm.getLadder("prestiges").isPresent()) ||
            			!(lm.getLadder("prestiges").get().getLowestRank().isPresent()) ||
            			lm.getLadder("prestiges").get().getLowestRank().get().name == null)) {
            		sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.CantFindPrestiges")));
            		return;
            	}

            	if ( isPrisonConfig( "prestige-confirm-gui") ) {
            		try {

            			Player player = getSpigotPlayer( sender );

            			SpigotConfirmPrestigeGUI gui = new SpigotConfirmPrestigeGUI( player );
            			gui.open();
            		} catch (Exception ex) {
            			prestigeByChat( sender );
            		}
            	}
            	else {
            		prestigeByChat( sender );
            	}

            }
        }
    }

    private void prestigeByChat(CommandSender sender) {

		ListenersPrisonManager listenersPrisonManager = ListenersPrisonManager.get();
		listenersPrisonManager.chatEventActivator();

        sender.sendMessage(SpigotPrison.format(getPrisonConfig("Gui.Lore.PrestigeWarning") +
        		getPrisonConfig("Gui.Lore.PrestigeWarning2") +
        		getPrisonConfig("Gui.Lore.PrestigeWarning3")));
        
        sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.ConfirmPrestige")));
        sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.CancelPrestige")));

        final Player player = getSpigotPlayer( sender );

        listenersPrisonManager.addMode("prestige");
        listenersPrisonManager.addChatEventPlayer(player);
        listenersPrisonManager.id = Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotPrison.getInstance(), () -> {
            if (listenersPrisonManager.chatEventCheck()) {
                listenersPrisonManager.chatEventDeactivate();
                player.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.PrestigeRanOutOfTime")));
                listenersPrisonManager.removeChatEventPlayer(player);
                listenersPrisonManager.removeMode();
            }
        }, 20L * 30);
    }


    @Command( identifier = "gui prestiges", description = "GUI Prestiges",
    		  aliases = {"prisonmanager prestiges"},
    		  onlyPlayers = true )
    private void prisonManagerPrestiges( CommandSender sender ) {

        if ( !isPrisonConfig("prestiges") ) {
            sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.PrestigesAreDisabled")));
            return;
        }


        if ( !isPrisonConfig("prison-gui-enabled") || !isConfig("Options.Prestiges.GUI_Enabled")){
            sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.GuiOrPrestigesDisabled")));
            return;
        }

        if ( isConfig("Options.Prestiges.Permission_GUI_Enabled") ){
        	String perm = getConfig( "Options.Prestiges.Permission_GUI");

            if ( !sender.hasPermission( perm ) ){
                sender.sendMessage(SpigotPrison.format(messages.getString("Gui.Message.missingGuiPrestigesPermission") + " [" +
        				perm + "]"));
                return;
            }
        }

        Player player = getSpigotPlayer( sender );
        SpigotPlayerPrestigesGUI gui = new SpigotPlayerPrestigesGUI( player );
        gui.open();
    }

 
}