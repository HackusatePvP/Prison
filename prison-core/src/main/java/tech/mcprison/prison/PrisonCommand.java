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

package tech.mcprison.prison;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import tech.mcprison.prison.autofeatures.AutoFeaturesFileConfig.AutoFeatures;
import tech.mcprison.prison.commands.Arg;
import tech.mcprison.prison.commands.Command;
import tech.mcprison.prison.commands.CommandPagedData;
import tech.mcprison.prison.commands.Wildcard;
import tech.mcprison.prison.integration.IntegrationManager;
import tech.mcprison.prison.integration.IntegrationType;
import tech.mcprison.prison.internal.CommandSender;
import tech.mcprison.prison.internal.Player;
import tech.mcprison.prison.modules.Module;
import tech.mcprison.prison.modules.ModuleStatus;
import tech.mcprison.prison.output.BulletedListComponent;
import tech.mcprison.prison.output.ChatDisplay;
import tech.mcprison.prison.output.DisplayComponent;
import tech.mcprison.prison.output.Output;
import tech.mcprison.prison.troubleshoot.TroubleshootResult;
import tech.mcprison.prison.troubleshoot.Troubleshooter;
import tech.mcprison.prison.util.Text;

/**
 * Root commands for managing the platform as a whole, in-game.
 *
 * @author Faizaan A. Datoo
 * @since API 1.0
 */
public class PrisonCommand {

	private List<String> registeredPlugins = new ArrayList<>();
	
	private TreeMap<String, RegisteredPluginsData> registeredPluginData = new TreeMap<>();
	
	
    @Command(identifier = "prison version", description = "Displays version information.", 
    		onlyPlayers = false )
    public void versionCommand(CommandSender sender) {
    	ChatDisplay display = displayVersion();
    	
        display.send(sender);
    }
    
    /**
     * <p>This class contains the data that is used to log the plugins, commands, and their aliases,
     * that may be setup on the server.  This is setting the ground work to store the command
     * data that can be used to trouble shoot complex problems, such as conflicts, that are
     * occuring with the prison plugin.
     * </p>
     * 
     * <p>This is just the data, and does not interact, or modify, any of the other commands.
     * </p>
     *
     */
    public class RegisteredPluginsData {
    	private String pluginName;
    	private String pluginVersion;
    	private List<RegisteredPluginCommandData> registeredCommands;
    	
    	private boolean registered = false;
    	private int aliasCount = 0;
    	
    	public RegisteredPluginsData( String pluginName, String pluginVersion, boolean registered ) {
    		super();
    		
    		this.pluginName = pluginName;
    		this.pluginVersion = pluginVersion;
    		this.registered = registered;
    		
    		this.registeredCommands = new ArrayList<>();
    	}
    	
    	public void addCommand( String commandName, List<String> commandAliases ) {
    		RegisteredPluginCommandData command =
    				new RegisteredPluginCommandData( commandName, commandAliases );
    		
    		getRegisteredCommands().add( command );
    		
    		setAliasCount( getAliasCount() + commandAliases.size() );
    	}


		public Object formatted()
		{
			String message = String.format( "&7%s &c%s&3(&a%s &7c:&a%s &7a:&a%s &3)", 
						getPluginName(), 
						(isRegistered() ? "" : "*"),
						getPluginVersion(), 
						Integer.toString(getRegisteredCommands().size()), 
						Integer.toString(getAliasCount()));
			return message;
		}
		

		public String getdetails() {
			StringBuilder sbCmd = new StringBuilder();
			StringBuilder sbAlias = new StringBuilder();
			for ( RegisteredPluginCommandData cmd : getRegisteredCommands() )
			{
				if ( sbCmd.length() > 0 ) {
					sbCmd.append( " " );
				}
				sbCmd.append( cmd.getCommand() );
				
				for ( String alias : cmd.getAliases() )
				{
					if ( sbAlias.length() > 0 ) {
						sbAlias.append( " " );
					}
					sbAlias.append( alias );
				}
				
			}
			
			return "Plugin: " + getPluginName() + " cmd: " + sbCmd.toString() + 
					(sbAlias.length() == 0 ? "" :
						" alias: " + sbAlias.toString());
		}
		
		public String getPluginName() {
			return pluginName;
		}
		public void setPluginName( String pluginName ) {
			this.pluginName = pluginName;
		}

		public String getPluginVersion() {
			return pluginVersion;
		}
		public void setPluginVersion( String pluginVersion ) {
			this.pluginVersion = pluginVersion;
		}

		public boolean isRegistered() {
			return registered;
		}
		public void setRegistered( boolean registered ) {
			this.registered = registered;
		}

		public List<RegisteredPluginCommandData> getRegisteredCommands() {
			return registeredCommands;
		}
		public void setRegisteredCommands( List<RegisteredPluginCommandData> registeredCommands ) {
			this.registeredCommands = registeredCommands;
		}

		public int getAliasCount() {
			return aliasCount;
		}
		public void setAliasCount( int aliasCount ) {
			this.aliasCount = aliasCount;
		}

    	
    }
    
    public class RegisteredPluginCommandData {
    	private String command;
    	private List<String> aliases;
    	
    	public RegisteredPluginCommandData( String command, List<String> aliases ) {
    		super();
    		
    		this.command = command;
    		this.aliases = aliases;
    	}

		public String getCommand() {
			return command;
		}
		public void setCommand( String command ) {
			this.command = command;
		}

		public List<String> getAliases() {
			return aliases;
		}
		public void setAliases( List<String> aliases ) {
			this.aliases = aliases;
		}
    }
    
    public ChatDisplay displayVersion() {
    	
        ChatDisplay display = new ChatDisplay("/prison version");
        display.text("&7Prison Version: &3%s", Prison.get().getPlatform().getPluginVersion());

        display.text("&7Running on Platform: &3%s", Prison.get().getPlatform().getClass().getName());
        display.text("&7Minecraft Version: &3%s", Prison.get().getMinecraftVersion());

        display.text("");
        
        display.text("&7Commands: &2/prison");
        
        for ( Module module : Prison.get().getModuleManager().getModules() ) {
        	
        	display.text( "&7Module: &3%s&3 : %s %s", module.getName(), 
        			module.getStatus().getStatusText(),
        			(module.getStatus().getStatus() == ModuleStatus.Status.FAILED ? 
        						"&d[" + module.getStatus().getMessage() + "&d]" : "")
        			);
        	display.text( "    &7Base Commands: %s", module.getBaseCommands() );
        }
        
        List<String> disabledModules = Prison.get().getModuleManager().getDisabledModules();
        if ( disabledModules.size() > 0 ) {
        	display.text( "&7Disabled Module%s:", (disabledModules.size() > 1 ? "s" : ""));
        	for ( String disabledModule : Prison.get().getModuleManager().getDisabledModules() ) {
        		display.text( "&a    &cDisabled Module: &7%s&a. Related commands and placeholders are non-functional. ",
        				disabledModule );
        	}
        }
         
        
        display.text("");
        display.text("&7Integrations:");

        IntegrationManager im = Prison.get().getIntegrationManager();
        String permissions =
        		(im.hasForType(IntegrationType.PERMISSION) ?
                "&a" + im.getForType(IntegrationType.PERMISSION).get().getDisplayName() :
                "&cNone");

        display.text(Text.tab("&7Permissions: " + permissions));

        String economy =
        		(im.hasForType(IntegrationType.ECONOMY) ?
                "&a" + im.getForType(IntegrationType.ECONOMY).get().getDisplayName() : 
                "&cNone");

        display.text(Text.tab("&7Economy: " + economy));
        
        
        List<DisplayComponent> integrationRows = Prison.get().getIntegrationManager().getIntegrationComponents();
        for ( DisplayComponent component : integrationRows )
		{
        	display.addComponent( component );
		}
        
        Prison.get().getPlatform().identifyRegisteredPlugins();
        
        // NOTE: This list of plugins is good enough and the detailed does not have all the info.
        // Display all loaded plugins:
        if ( getRegisteredPlugins().size() > 0 ) {
        	display.text( "&7Registered Plugins: " );
        	StringBuilder sb = new StringBuilder();
        	for ( String plugin : getRegisteredPlugins() ) {
        		if ( sb.length() == 0) {
        			sb.append( "  " );
        			sb.append( plugin );
        		} else {
        			sb.append( ",  " );
        			sb.append( plugin );
        			display.text( sb.toString() );
        			sb.setLength( 0 );
        		}
        	}
        	if ( sb.length() > 0 ) {
        		display.text( sb.toString());
        	}
        }
        
        // This version of plugins does not have all the registered commands:
//        // The new plugin listings:
//        if ( getRegisteredPluginData().size() > 0 ) {
//        	display.text( "&7Registered Plugins Detailed: " );
//        	StringBuilder sb = new StringBuilder();
//        	Set<String> keys = getRegisteredPluginData().keySet();
//        	
//        	for ( String key : keys ) {
//        		RegisteredPluginsData plugin = getRegisteredPluginData().get(key);
//        		
//        		if ( sb.length() == 0) {
//        			sb.append( "  " );
//        			sb.append( plugin.formatted() );
//        		} else {
//        			sb.append( ",  " );
//        			sb.append( plugin.formatted() );
//        			display.text( sb.toString() );
//        			sb.setLength( 0 );
//        		}
//        	}
//        	if ( sb.length() > 0 ) {
//        		display.text( sb.toString());
//        	}
//        }
        
        
//        RegisteredPluginsData plugin = getRegisteredPluginData().get( "Prison" );
//        String pluginDetails = plugin.getdetails();
//        
//        display.text( pluginDetails );
        

        
        Prison.get().getPlatform().getWorldLoadErrors( display );
        
        
        return display;
    }

    @Command(identifier = "prison modules", onlyPlayers = false, permissions = "prison.modules", 
    				description = "Lists the modules that hook into Prison to give it functionality.")
    public void modulesCommand(CommandSender sender) {
        ChatDisplay display = new ChatDisplay("/prison modules");
        display.emptyLine();

        BulletedListComponent.BulletedListBuilder builder =
            new BulletedListComponent.BulletedListBuilder();
        for (Module module : Prison.get().getModuleManager().getModules()) {
            builder.add("&3%s &8(%s) &3v%s &8- %s", module.getName(), module.getPackageName(),
                module.getVersion(), module.getStatus().getMessage());
        }

        display.addComponent(builder.build());

        display.send(sender);
    }

//    @Command(identifier = "prison troubleshoot", description = "Runs a troubleshooter.", 
//    					onlyPlayers = false, permissions = "prison.troubleshoot")
    public void troubleshootCommand(CommandSender sender,
        @Arg(name = "name", def = "list", description = "The name of the troubleshooter.") String name) {
        // They just want to list stuff
        if (name.equals("list")) {
            sender.dispatchCommand("prison troubleshoot list");
            return;
        }

        TroubleshootResult result =
            PrisonAPI.getTroubleshootManager().invokeTroubleshooter(name, sender);
        if (result == null) {
            Output.get().sendError(sender, "The troubleshooter %s doesn't exist.", name);
            return;
        }

        ChatDisplay display = new ChatDisplay("Result Summary");
        display.text("&7Troubleshooter name: &b%s", name.toLowerCase()) //
            .text("&7Result type: &b%s", result.getResult().name()) //
            .text("&7Result details: &b%s", result.getDescription()) //
            .send(sender);

    }

//    @Command(identifier = "prison troubleshoot list", description = "Lists the troubleshooters.", 
//    						onlyPlayers = false, permissions = "prison.troubleshoot")
    public void troubleshootListCommand(CommandSender sender) {
        ChatDisplay display = new ChatDisplay("Troubleshooters");
        display.text("&8Type /prison troubleshoot <name> to run a troubleshooter.");

        BulletedListComponent.BulletedListBuilder builder =
            new BulletedListComponent.BulletedListBuilder();
        for (Troubleshooter troubleshooter : PrisonAPI.getTroubleshootManager()
            .getTroubleshooters()) {
            builder.add("&b%s &8- &7%s", troubleshooter.getName(), troubleshooter.getDescription());
        }
        display.addComponent(builder.build());

        display.send(sender);
    }

    
    @Command(identifier = "prison placeholders test", 
    		description = "Converts any Prison placeholders in the test string to their values", 
    		onlyPlayers = false, permissions = "prison.placeholder")
    public void placeholdersTestCommand(CommandSender sender,
    		@Wildcard(join=true)
    		@Arg(name = "text", description = "Placeholder text to test" ) String text ) {
    	
    	ChatDisplay display = new ChatDisplay("Placeholder Test");
    	
        BulletedListComponent.BulletedListBuilder builder =
                new BulletedListComponent.BulletedListBuilder();
        
    	Player player = getPlayer( sender );
    	UUID playerUuid = (player == null ? null : player.getUUID());
    	String translated = Prison.get().getPlatform().getPlaceholders()
    					.placeholderTranslateText( playerUuid, sender.getName(), text );
    	
    	builder.add( String.format( "&a    Include one or more Prison placeholders with other text..."));
    	builder.add( String.format( "&a    Use { } to escape the placeholders."));
    	builder.add( String.format( "&7  Original:   %s", text));
    	builder.add( String.format( "&7  Translated: %s", translated));
    	
    	display.addComponent(builder.build());
    	display.send(sender);
    }
    
	private Player getPlayer( CommandSender sender ) {
		Optional<Player> player = Prison.get().getPlatform().getPlayer( sender.getName() );
		return player.isPresent() ? player.get() : null;
	}
   

    /**
     * <p>Gets a player by name.  If the player is not online, then try to get them from 
     * the offline player list. If not one is found, then return a null.
     * </p>
     * 
     * @param sender
     * @param playerName is optional, if not supplied, then sender will be used
     * @return Player if found, or null.
     */
	private Player getPlayer( CommandSender sender, String playerName ) {
		Player result = null;
		
		playerName = playerName != null ? playerName : sender != null ? sender.getName() : null;
		
		if ( playerName != null ) {
			Optional<Player> opt = Prison.get().getPlatform().getPlayer( playerName );
			if ( !opt.isPresent() ) {
				opt = Prison.get().getPlatform().getOfflinePlayer( playerName );
			}
			if ( opt.isPresent() ) {
				result = opt.get();
			}
		}
		return result;
	}
	
    @Command(identifier = "prison placeholders search", 
    				description = "Search all placeholders that match all patterns", 
    		onlyPlayers = false, permissions = "prison.placeholder")
    public void placeholdersSearchCommand(CommandSender sender,
    		@Arg(name = "playerName", description = "Player name to use with player rank placeholders (optional)", 
    				def = "." ) String playerName,
    		@Arg(name = "pageNumber", description = "page number of results to display", def = "." ) String pageNumber,
    		@Wildcard(join=true)
    		@Arg(name = "patterns", description = "Patterns of placeholders to search for" ) String patterns ) {
    
    	
    	
    	// blank defaults do not work when there are more than one at a time.  So had to
    	// default to periods.  So convert periods to blanks initially:
    	playerName = (playerName.equals( "." ) ? "" : playerName );
    	pageNumber = (pageNumber.equals( "." ) ? "" : pageNumber );
    	patterns = (patterns.equals( "." ) ? "" : patterns );
    	
    	Player player = getPlayer( null, playerName );
    	if ( player == null ) {
    		// No player found, or none specified. Need to shift parameters over by one:
    		if ( pageNumber != null && pageNumber.trim().length() > 0 ) {
    			
    			// playerName should be moved to the pageNumber, after pageNumber is moved to patterns:
    			patterns = (pageNumber.trim() + " " + patterns).trim();
    		} 
    		pageNumber = playerName;
    	}
    	
    	
    	int page = 1;
    	
    	/**
    	 * Please note: Page is optional and defaults to a value of 1.  But when it is not
    	 * provided, it "grabs" the first pattern.  So basically, if pageNumber proves not
    	 * to be a number, then we must prefix whatever is in patterns with that value.
    	 */
    	if ( pageNumber != null ) {
    		
    		try {
				page = Integer.parseInt( pageNumber );
			}
    		catch ( NumberFormatException e ) {
    			// If exception, add pageNumber to the beginning patterns.
    			// So no page number was specified, it was part of the patterns
    			patterns = (pageNumber.trim() + " " + patterns).trim();
			}
    		
    	}
    	

    	// Cannot allow pages less than 1:
    	if ( page < 1 ) {
    		page = 1;
    	}
    	
    	ChatDisplay display = new ChatDisplay("Placeholders Search");
    
    	
    	if ( patterns == null || patterns.trim().length() == 0 ) {
    		sender.sendMessage( "&7Pattern required. Placeholder results must match all pattern terms." );
    		return;
    	}
    	
        BulletedListComponent.BulletedListBuilder builder =
                						new BulletedListComponent.BulletedListBuilder();
        
        if ( player == null ) {
        	// playerName was not provided, or was invalid. So use sender.
        	player = getPlayer( sender );
        }
        UUID playerUuid = (player == null ? null : player.getUUID());
        
        List<String> placeholders = Prison.get().getPlatform().getPlaceholders()
        					.placeholderSearch( playerUuid, (player == null ? null : player.getName()), 
        								patterns.trim().split( " " ) );
        
        builder.add( String.format( "&a    Include one or more patterns to search for placeholders. If more"));
        builder.add( String.format( "&a    than one is provided, the returned placeholder will hit on all."));
        builder.add( String.format( "&a    Player based placeholders will return nulls for values if ran from console,"));
        builder.add( String.format( "&a    unless player name is specified. Can view placeholders for any player."));
        
        if ( player != null ) {
        	builder.add( String.format( "&a    Player: &7%s  &aPlayerUuid: &7%s", player.getName(), 
        			(playerUuid == null ? "null" : playerUuid.toString())));
        	
        }
        
        
        DecimalFormat dFmt = new DecimalFormat("#,##0");
        builder.add( String.format( "&7  Results: &c%s  &7Original patterns:  &3%s", 
        		dFmt.format(placeholders.size()), patterns ));
    	
        
        CommandPagedData cmdPageData = new CommandPagedData(
        		"/prison placeholders search", placeholders.size(),
        		0, Integer.toString( page ), 12 );
        // Need to provide more "parts" to the command that follows the page number:
        cmdPageData.setPageCommandSuffix( patterns );
    	
        int count = 0;
    	for ( String placeholder : placeholders ) {
    		if ( cmdPageData == null ||
            		count++ >= cmdPageData.getPageStart() && 
            		count <= cmdPageData.getPageEnd() ) {
    			
    			builder.add( String.format( placeholder ));
    		}
		}
    	
    	display.addComponent(builder.build());
    	
    	cmdPageData.generatePagedCommandFooter( display );
    	
    	display.send(sender);
    }
    
    
    @Command(identifier = "prison placeholders list", 
    		description = "List all placeholders templates", 
    		onlyPlayers = false, permissions = "prison.placeholder")
    public void placeholdersListCommand(CommandSender sender
    		) {
    	
    	ChatDisplay display = new ChatDisplay("Placeholders List");
    	
    	display.text( "&a    Placeholders are case insensitive, but are registered in all lowercase.");
    	display.text( "&a    Chat based placeholders use { }, but others may use other escape codes like %% %%.");
    	display.text( "&a    Mine based placeholders uses the mine name to replace 'minename'.");
    	
    	for ( String disabledModule : Prison.get().getModuleManager().getDisabledModules() ) {
    		display.text( "&a    &cDisabled Module: &7%s&a. Related placeholders maybe listed but are non-functional. ",
    				disabledModule );
    	}
    	
    	List<DisplayComponent> placeholders = new ArrayList<>();
        Prison.get().getIntegrationManager().getPlaceholderTemplateList( placeholders );


    	for ( DisplayComponent placeholder : placeholders ) {
    		display.addComponent( placeholder );
    	}
    	
    	display.send(sender);
    }
    
    
    @Command(identifier = "prison reload placeholders", 
    		description = "Placeholder reload: Regenerates all placeholders and reregisters them.", 
    		onlyPlayers = false, permissions = "prison.placeholder")
    public void placeholdersReloadCommandAlias(CommandSender sender ) {
    	placeholdersReloadCommand( sender );
    }
    
    @Command(identifier = "prison placeholders reload", 
    		description = "Placeholder reload: Regenerates all placeholders and reregisters them.", 
    		onlyPlayers = false, permissions = "prison.placeholder")
    public void placeholdersReloadCommand(CommandSender sender ) {
    	
    	Prison.get().getPlatform().getPlaceholders().reloadPlaceholders();
    	
    	String message = "Placeholder reload was attempted. " +
    			"No guarentees that it worked 100%. Restart server if any doubts.";

    	sender.sendMessage( message );
    }
    
    
    /**
     * <p>This command does not do anything, except to provide a command placeholder to
     * make owners aware that there is auto features enabled within prison. 
     * Running this command will show the permissions needed to use these auto features.
     * </p>
     * 
     * <p>Cannot use the @Command altPermissions parameter since the permissions can be
     * dynamically altered to fit the needs of the owner's server.  Using the command 
     * altPermissions will also require a server restart to reflect any online changes, 
     * not to mention a recompile since the end users cannot make these changes.
     * </p>
     * 
     * @param sender
     */
    @Command(identifier = "prison autofeatures", 
    		description = "Autofeatures for prison: pickup, smelt, and block", 
    		onlyPlayers = false )
//    		, altPermissions = { "prison.autofeatures.pickup", "prison.autofeatures.smelt" , 
//    				"prison.autofeatures.block" })
    public void autoFeaturesInformation(CommandSender sender) {
    	
    	ChatDisplay display = new ChatDisplay("Auto Features Information");
    	
    	display.text( "&a Prison auto features provide the following options:");
    	display.text( "&7   Auto pickup - &aUpon block break, items are placed directly in to player inventory.");
    	display.text( "&7   Auto smelt - &aItems that can be smelted will be smelted automatically.");
    	display.text( "&7   Auto block - &aConverts ores to blocks.");
    	display.text( "&7   Tool lore starts with: Pickup, Smelt, or Block. Only one per line." );
    	display.text( "&7   Tool lore 100 percent with just name. Can have value 0.001 to 100.0 percent." );
    	display.text( "&7   Tool lore examples: Pickup, Pickup 7.13, Smelt 55, Block 75.123" );
    	
    	display.text( "&a To configure modify plugin/Prison/autoFeaturesConfig.yml");
    	display.text( "&a Or use &7/prison gui");
    	
    	List<AutoFeatures> afs = AutoFeatures.permissions.getChildren();
    	StringBuilder sb = new StringBuilder();
    	for ( AutoFeatures af : afs ) {
			if ( sb.length() > 0 ) {
				sb.append( " " );
			}
			sb.append( af.getMessage() );
		}
    	display.text( "&3Permissions:" );
    	display.text( "&b   %s", sb.toString() );
    	
    	display.send(sender);
    	
    	// altPermissions are now a part of this command.
//    	// After displaying the help information above, rerun the same command for the player
//    	// with the help keyword to show the permissions.
//    	String formatted = "prison autofeatures help";
//		Prison.get().getPlatform().dispatchCommand(sender, formatted);
        
    }
    
    
    
// This functionality should not be available in v3.2.1!  If someone is still running Prison 2.x.x 
//							    then they must first upgrade to
// prison v3.0.0 and perform the upgrade, at the most recent, then v3.1.1.
//    @Command(identifier = "prison convert", description = "Convert your Prison 2 data to Prison 3 data.", 
//						    onlyPlayers = false, permissions = "prison.convert")
//    public void convertCommand(CommandSender sender) {
//        sender.sendMessage(Prison.get().getPlatform().runConverter());
//    }

	public List<String> getRegisteredPlugins() {
		return registeredPlugins;
	}

	public TreeMap<String, RegisteredPluginsData> getRegisteredPluginData() {
		return registeredPluginData;
	}

	public RegisteredPluginsData addRegisteredPlugin( String pluginName, String pluginVersion ) {
		RegisteredPluginsData rpd = new RegisteredPluginsData( pluginName, pluginVersion, true );
		getRegisteredPluginData().put( pluginName, rpd);
		return rpd;
	}
	
	public RegisteredPluginsData addUnregisteredPlugin( String pluginName, String pluginVersion ) {
		RegisteredPluginsData rpd = new RegisteredPluginsData( pluginName, pluginVersion, false );
		if ( !getRegisteredPluginData().containsKey( pluginName ) ) {
			getRegisteredPluginData().put( pluginName, rpd);
		}
		return rpd;
	}
	
	public void addPluginDetails( String pluginName, String pluginVersion, 
											String command, List<String> commandAliases ) {
		
		// just in case this plugin was not registered before:
		addUnregisteredPlugin( pluginName, pluginVersion );
		
		RegisteredPluginsData plugin = getRegisteredPluginData().get( pluginName );
		
		plugin.addCommand( command, commandAliases );
	}

}
