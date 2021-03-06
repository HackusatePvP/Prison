/*
 *  Prison is a Minecraft plugin for the prison game mode.
 *  Copyright (C) 2017-2020 The Prison Team
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tech.mcprison.prison.spigot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.messages.Titles;

import tech.mcprison.prison.Prison;
import tech.mcprison.prison.PrisonCommand;
import tech.mcprison.prison.commands.PluginCommand;
import tech.mcprison.prison.convert.ConversionManager;
import tech.mcprison.prison.convert.ConversionResult;
import tech.mcprison.prison.file.FileStorage;
import tech.mcprison.prison.file.YamlFileIO;
import tech.mcprison.prison.gui.GUI;
import tech.mcprison.prison.internal.Player;
import tech.mcprison.prison.internal.Scheduler;
import tech.mcprison.prison.internal.World;
import tech.mcprison.prison.internal.block.PrisonBlock;
import tech.mcprison.prison.internal.platform.Capability;
import tech.mcprison.prison.internal.platform.Platform;
import tech.mcprison.prison.internal.scoreboard.ScoreboardManager;
import tech.mcprison.prison.mines.PrisonMines;
import tech.mcprison.prison.mines.managers.MineManager;
import tech.mcprison.prison.modules.Module;
import tech.mcprison.prison.output.BulletedListComponent;
import tech.mcprison.prison.output.ChatDisplay;
import tech.mcprison.prison.output.LogLevel;
import tech.mcprison.prison.output.Output;
import tech.mcprison.prison.spigot.game.SpigotCommandSender;
import tech.mcprison.prison.spigot.game.SpigotOfflinePlayer;
import tech.mcprison.prison.spigot.game.SpigotPlayer;
import tech.mcprison.prison.spigot.game.SpigotWorld;
import tech.mcprison.prison.spigot.gui.SpigotGUI;
import tech.mcprison.prison.spigot.placeholder.SpigotPlaceholders;
import tech.mcprison.prison.spigot.scoreboard.SpigotScoreboardManager;
import tech.mcprison.prison.spigot.util.ActionBarUtil;
import tech.mcprison.prison.spigot.util.SpigotYamlFileIO;
import tech.mcprison.prison.store.Storage;
import tech.mcprison.prison.util.Location;
import tech.mcprison.prison.util.Text;

/**
 * @author Faizaan A. Datoo
 */
class SpigotPlatform implements Platform {

    private SpigotPrison plugin;
    private List<PluginCommand> commands = new ArrayList<>();
    private Map<String, World> worlds = new HashMap<>();
    private List<Player> players = new ArrayList<>();

    private ScoreboardManager scoreboardManager;
    private Storage storage;
    
    private SpigotPlaceholders placeholders;

    SpigotPlatform(SpigotPrison plugin) {
        this.plugin = plugin;
        this.scoreboardManager = new SpigotScoreboardManager();
        this.storage = initStorage();
        
        this.placeholders = new SpigotPlaceholders();
        
        ActionBarUtil.init(plugin);
    }

    private Storage initStorage() {
        String confStorage = plugin.getConfig().getString("storage", "file");
        Storage storage = new FileStorage(plugin.getDataDirectory());
        
        if (!confStorage.equalsIgnoreCase("file")) {
            Output.get().logError("Unknown file storage type in configuration \"" + confStorage
                + "\". Using file storage.");
            Output.get().logWarn(
                "Note: In this version of Prison 3, 'file' is the only supported type of storage. We're working to bring other storage types soon.");
        }
        
        return storage;
    }

    @Override 
    public Optional<World> getWorld(String name) {
        if (name != null && worlds.containsKey(name)) {
            return Optional.of(worlds.get(name));
        }

        if (name == null || name.trim().length() == 0 || 
        		Bukkit.getWorld(name) == null) {
        	StringBuilder sb = new StringBuilder();
        	for ( org.bukkit.World bukkitWorld : Bukkit.getWorlds() ) {
        		if ( sb.length() > 0 ) {
        			sb.append( " " );
        		}
        		sb.append( bukkitWorld.getName() );
        	}
        	
        	Output.get().logWarn( "&cWorld does not exist: &a" + name + 
        			"  &7Available worlds: &a" + sb.toString() );
        	
            return Optional.empty(); // Avoid NPE
        }
        SpigotWorld newWorld = new SpigotWorld(Bukkit.getWorld(name));
        worlds.put(newWorld.getName(), newWorld);
        return Optional.of(newWorld);
    }

    @Override 
    public void getWorldLoadErrors( ChatDisplay display ) {
    
    	Optional<Module> prisonMinesOpt = Prison.get().getModuleManager().getModule( PrisonMines.MODULE_NAME );
    	
    	if ( prisonMinesOpt.isPresent() ) {
    		MineManager mineManager = ((PrisonMines) prisonMinesOpt.get()).getMineManager();
    		
    		// When finished loading the mines, then if there are any worlds that
    		// could not be loaded, dump the details:
    		List<String> unavailableWorlds = mineManager.getUnavailableWorldsListings();
    		for ( String uWorld : unavailableWorlds ) {
    			
    			display.text( uWorld );
    		}
    		
    	}
        
    }
    
    @Override public Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(
            players.stream().filter(player -> player.getName().equalsIgnoreCase( name)).findFirst()
                .orElseGet(() -> {
                	org.bukkit.entity.Player playerBukkit = Bukkit.getPlayer(name);
                    if (playerBukkit == null) {
                        return null;
                    }
                    SpigotPlayer player = new SpigotPlayer(playerBukkit);
                    players.add(player);
                    return player;
                }));
    }

    @Override public Optional<Player> getPlayer(UUID uuid) {
        return Optional.ofNullable(
            players.stream().filter(player -> player.getUUID().equals(uuid)).findFirst()
                .orElseGet(() -> {
                	org.bukkit.entity.Player playerBukkit = Bukkit.getPlayer(uuid);
                    if (playerBukkit == null) {
                        return null;
                    }
                    SpigotPlayer player = new SpigotPlayer(playerBukkit);
                    players.add(player);
                    return player;
                }));
    }

    @Override public List<Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> getPlayer(player.getUniqueId()).get()).collect(Collectors.toList());
    }

    @Override
    public Optional<Player> getOfflinePlayer(String name) {
    	return getOfflinePlayer(name, null);
    }
    
    @Override
    public Optional<Player> getOfflinePlayer(UUID uuid) {
    	return getOfflinePlayer(null, uuid);
    }
    private Optional<Player> getOfflinePlayer(String name, UUID uuid) {
    	SpigotOfflinePlayer player = null;
    	
    	for ( OfflinePlayer offP : Bukkit.getOfflinePlayers() ) {
    		if ( name != null && offP.getName().equalsIgnoreCase( name) ||
					  uuid != null && offP.getUniqueId().equals(uuid) ) {
    			player = new SpigotOfflinePlayer( offP );
    			
	  			players.add(player);
	              break;
	  		}
		}
    	
//    	List<OfflinePlayer> olPlayers = Arrays.asList( Bukkit.getOfflinePlayers() );
//    	for ( OfflinePlayer offlinePlayer : olPlayers ) {
//    		if ( name != null && offlinePlayer.getName().equals(name) ||
//					  uuid != null && offlinePlayer.getUniqueId().equals(uuid) ) {
//    			player = new SpigotPlayer(offlinePlayer.getPlayer());
//    			players.add(player);
//                break;
//    		}
//		}
    	return Optional.ofNullable( player );
    }
    
    @Override public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override public File getPluginDirectory() {
        return plugin.getDataFolder();
    }

    @Override public void registerCommand(PluginCommand command) {
        try {
        	Command cmd = new Command(command.getLabel(), command.getDescription(), command.getUsage(),
                    Collections.emptyList()) {

                    @Override public boolean execute(CommandSender sender, String commandLabel,
                        String[] args) {
                        if (sender instanceof org.bukkit.entity.Player) {
                            return Prison.get().getCommandHandler()
                                .onCommand(new SpigotPlayer((org.bukkit.entity.Player) sender),
                                    command, commandLabel, args);
                        }
                        return Prison.get().getCommandHandler()
                            .onCommand(new SpigotCommandSender(sender), command, commandLabel,
                                args);
                        
                        /*
                         * ###Tab-Complete###
                         * 
                         * Disabled for now until a full solution can be implemented for tab complete.
                         * 
	//                  Output.get().logInfo( "SpigotPlatform.registerCommand: Command: %s :: %s", 
	//                  		command.getLabel(), command.getUsage() );
						@Override
						public List<String> tabComplete( CommandSender sender, String[] args )
						{
					    	Output.get().logInfo( "SpigotPlatform.registerCommand: Command.tabComplete 1" );
							// TODO Auto-generated method stub
							return super.tabComplete( sender, args );
						}

						@Override
						public List<String> tabComplete( CommandSender sender, String alias, String[] args )
								throws IllegalArgumentException
						{
							Output.get().logInfo( "SpigotPlatform.registerCommand: Command.tabComplete 2" );
							// TODO Auto-generated method stub
							return super.tabComplete( sender, alias, args );
						}
                         */
                    }       
            };
        	
            @SuppressWarnings( "unused" )
			boolean success = 
            			((SimpleCommandMap) plugin.commandMap.get(Bukkit.getServer()))
            				.register(command.getLabel(), "prison", cmd );
            
            commands.add(command);
            
//            if ( !success ) {
//            	Output.get().logInfo( "SpigotPlatform.registerCommand: %s  " +
//            			"Duplicate command. Fall back to Prison: [%s] ", command.getLabel(), 
//            			cmd.getLabel() );
//            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked") @Override public void unregisterCommand(String command) {
        try {
            ((Map<String, Command>) plugin.knownCommands
                .get(plugin.commandMap.get(Bukkit.getServer()))).remove(command);
            this.commands.removeIf(pluginCommand -> pluginCommand.getLabel().equals(command));
        } catch (IllegalAccessException e) {
            e.printStackTrace(); // This should only happen if something's wrong up there.
        }
    }

    @Override public List<PluginCommand> getCommands() {
        return commands;
    }

    @Override public void dispatchCommand(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }
    
    @Override public void dispatchCommand(tech.mcprison.prison.internal.CommandSender sender, String cmd) {
    	Bukkit.getServer().dispatchCommand( ((SpigotCommandSender) sender).getWrapper(), cmd);
    }

    @Override public Scheduler getScheduler() {
        return plugin.scheduler;
    }

    @Override public GUI createGUI(String title, int numRows) {
        return new SpigotGUI(title, numRows);
    }

//    @SuppressWarnings( "deprecation" )
	public void toggleDoor(Location doorLocation) {
        org.bukkit.Location bLoc =
            new org.bukkit.Location(Bukkit.getWorld(doorLocation.getWorld().getName()),
                doorLocation.getX(), doorLocation.getY(), doorLocation.getZ());
        Block block = bLoc.getWorld().getBlockAt(bLoc).getRelative(BlockFace.DOWN);
        if (!isDoor(block.getType())) {
            return;
        }

        boolean isOpen = XBlock.isOpen( block );
        XBlock.setOpened( block, !isOpen );
        
//        BlockState state = block.getState();
//        Openable openable = (Openable) state.getData();
//        openable.setOpen(!openable.isOpen());
//        state.setData((MaterialData) openable);
//        state.update();
        
        SpigotPrison.getInstance().getCompatibility()
        					.playIronDoorSound(block.getLocation());
    }

    @Override public void log(String message, Object... format) {
        message = Text.translateAmpColorCodes(String.format(message, format));

        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        if (sender == null) {
            Bukkit.getLogger().info(ChatColor.stripColor(message));
        } else {
            sender.sendMessage(message);
        }
    }

    @Override public void debug(String message, Object... format) {
        if (!plugin.debug) {
            return;
        }

        log(Output.get().gen("&eDebug") + " &7", message, format);
    }

    @Override public String runConverter() {
        File file = new File(plugin.getDataFolder().getParent(), "Prison.old");
        if (!file.exists()) {
            return Output.get().format(
                "I could not find a 'Prison.old' folder to convert. You probably haven't had Prison 2 installed before, so you don't need to convert :)",
                LogLevel.WARNING);
        }

        List<ConversionResult> results = ConversionManager.getInstance().runConversion();

        if (results.size() == 0) {
            return Text
                .translateAmpColorCodes("&7There are no conversions to be run at this time.");
        }

        BulletedListComponent.BulletedListBuilder builder =
            new BulletedListComponent.BulletedListBuilder();
        for (ConversionResult result : results) {
            String status =
                result.getStatus() == ConversionResult.Status.Success ? "&aSuccess" : "&cFailure";
            builder.add(
                result.getAgentName() + " &8- " + status + " &7(" + result.getReason() + "&7)");
        }

        return builder.build().text();
    }

//    @SuppressWarnings( "deprecation" )
	@Override public void showTitle(Player player, String title, String subtitle, int fade) {
        org.bukkit.entity.Player play = Bukkit.getPlayer(player.getName());
//        play.sendTitle(title, subtitle);
        
        Titles.sendTitle( play, title, subtitle );
    }

    @Override public void showActionBar(Player player, String text, int duration) {
        org.bukkit.entity.Player play = Bukkit.getPlayer(player.getName());
        ActionBarUtil.sendActionBar(play, Text.translateAmpColorCodes(text), duration);
    }

    @Override public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override public Storage getStorage() {
        return storage;
    }

    @Override
    public boolean shouldShowAlerts() {
        return plugin.getConfig().getBoolean("show-alerts", true);
    }

    private boolean isDoor(Material block) {
    	
    	Material acaciaDoor = Material.matchMaterial( "ACACIA_DOOR" );
    	Material birchDoor = Material.matchMaterial( "BIRCH_DOOR" );
    	Material darkOakDoor = Material.matchMaterial( "DARK_OAK_DOOR" );
    	Material ironDoor = Material.matchMaterial( "IRON_DOOR_BLOCK" );
    	Material jungleDoor = Material.matchMaterial( "JUNGLE_DOOR" );
    	Material woodenDoor = Material.matchMaterial( "WOODEN_DOOR" );
    	Material spruceDoor = Material.matchMaterial( "SPRUCE_DOOR" );    	
    	
//        return block == Material.ACACIA_DOOR || block == Material.BIRCH_DOOR
//            || block == Material.DARK_OAK_DOOR || block == Material.IRON_DOOR_BLOCK
//            || block == Material.JUNGLE_DOOR || block == Material.WOODEN_DOOR
//            || block == Material.SPRUCE_DOOR;
    	
    	return block == acaciaDoor || block == birchDoor || 
    		   block == darkOakDoor || block == ironDoor ||
    		   block == jungleDoor || block == woodenDoor ||
    		   block == spruceDoor;
    }

    @Override public Map<Capability, Boolean> getCapabilities() {
        Map<Capability, Boolean> capabilities = new HashMap<>();
        capabilities.put(Capability.ACTION_BARS, true);
        capabilities.put(Capability.GUI, true);
        return capabilities;
    }

    @Override
	public void identifyRegisteredPlugins() {
		 PrisonCommand cmdVersion = Prison.get().getPrisonCommands();
		 
		 // reset so it will reload cleanly:
		 cmdVersion.getRegisteredPlugins().clear();
//		 cmdVersion.getRegisteredPluginData().clear();
		 
		 Server server = SpigotPrison.getInstance().getServer();
		 
        // Finally print the version after loading the prison plugin:
//        PrisonCommand cmdVersion = Prison.get().getPrisonCommands();
        
        // Store all loaded plugins within the PrisonCommand for later inclusion:
        for ( Plugin plugin : server.getPluginManager().getPlugins() ) {
        	String name = plugin.getName();
        	String version = plugin.getDescription().getVersion();
        	String value = "&7" + name + " &3(&a" + version + "&3)";
        	cmdVersion.getRegisteredPlugins().add( value );
        	
        	cmdVersion.addRegisteredPlugin( name, version );
		}

        // NOTE: The following code does not actually get all of the commands that have been
        //       registered with the bukkit plugin registry.  So commenting this out and may revisit
        //       in the future.  Only tested with 1.8.8 so may work better with more cent version.
//        SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
//        
//        try {
//        	// The following code is based upon work provided by Technius:
//        	// https://bukkit.org/threads/get-all-the-available-commands.61941/
//			PluginManager manager = server.getPluginManager();
//			SimplePluginManager spm = (SimplePluginManager) manager;
//			//List<Plugin> plugins = null;
//			//Map<String, Plugin> lookupNames = null;
//			SimpleCommandMap commandMap = null;
//			Map<String, Command> knownCommands = null;
//			if (spm != null) {
//			    //Field pluginsField = spm.getClass().getDeclaredField("plugins");
//			    //Field lookupNamesField = spm.getClass().getDeclaredField("lookupNames");
//			    Field commandMapField = spm.getClass().getDeclaredField("commandMap");
//			    //pluginsField.setAccessible(true);
//			    //lookupNamesField.setAccessible(true);
//			    commandMapField.setAccessible(true);
//			    //plugins = (List<Plugin>) pluginsField.get(spm);
//			    //lookupNames = (Map<String, Plugin>) lookupNamesField.get(spm);
//			    commandMap = (SimpleCommandMap) commandMapField.get(spm);
//			    Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
//			    knownCommandsField.setAccessible(true);
//			    knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
//			}
//			 
//			if (commandMap != null) {
//			    for (Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext(); ) {
//			        Map.Entry<String, Command> entry = it.next();
//			        if (entry.getValue() instanceof org.bukkit.command.PluginCommand) {
//			        	org.bukkit.command.PluginCommand c = (org.bukkit.command.PluginCommand) entry.getValue();
//			            //"c" is the command
//			            
//			            String pluginName = c.getPlugin().getName();
//			            String pluginVersion = c.getPlugin().getDescription().getVersion();
//			            String commandName = c.getName();
//			            List<String> commandAliases = c.getAliases();
//			            
//			            // Log the command and it's aliases:
//			            cmdVersion.addPluginDetails( pluginName, pluginVersion, commandName, commandAliases );
//			        }
//			    }
//			}
//		}
//		catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
//			e.printStackTrace();
//		}
        
        
	}
    
    
    
    public SpigotPlaceholders getPlaceholders() {
		return placeholders;
	}


	
	@Override
	public YamlFileIO getYamlFileIO( File yamlFile ) {
		return new SpigotYamlFileIO( yamlFile );
	}
	
	
	/**
	 * Forces the plugin config to reload.
	 * 
	 * @return
	 */
	@Override
	public void reloadConfig() {
		
		SpigotPrison.getInstance().reloadConfig();
	}
	
	@Override
	public String getConfigString( String key ) {
		return SpigotPrison.getInstance().getConfig().getString( key );
	}
	
	/**
	 * <p>This returns the boolean value that is associated with the key.
	 * It has to match on true to return a true value.  If the key does
	 * not exist, then it returns a value of false.
	 * </p>
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public boolean getConfigBooleanFalse( String key ) {
		
		String val = SpigotPrison.getInstance().getConfig().getString( key );
		
		return ( val != null && val.trim().equalsIgnoreCase( "true" ) );
	}
	
	/**
	 * <p>This returns the boolean value that is associated with the key.
	 * It has to match on true to return a true value.  If the key does
	 * not exist, then it returns a value of true.
	 * </p>
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public boolean getConfigBooleanTrue( String key ) {
		
		String val = SpigotPrison.getInstance().getConfig().getString( key );
		
		return ( val == null || val.trim().equalsIgnoreCase( "true" ) );
	}
	
	/**
	 * This listing that is returned, should be the XMaterial enum name
	 * for the blocks that are valid on the server.
	 * 
	 * @return
	 */
	@Override
	public void getAllPlatformBlockTypes( List<PrisonBlock> blockTypes ) {
		
		SpigotUtil.getAllPlatformBlockTypes( blockTypes );
	}
	
	@Override
	public PrisonBlock getPrisonBlock( String blockName ) {
		
		return SpigotUtil.getPrisonBlock( blockName );
	}
}
