[Prison Documents - Table of Contents](docs/prison_docs_000_toc.md)

# Prison Known Issues and To Do's for v3.2.x

This document is intended to keep track of known issues and also provide for
a short list of To Do's. This list is intended to help work through known
issues, and/or to serve as items that should be added, or fixed.


# To Do Items - During Beta v3.2.1


* **Rename Mines**
Been a few requests to be able to rename mines.  Since so much can go wrong with manually changing the files, this should be a reasonable new feature added before beta.


* **New Block Model - Implement in parallel**
Implement and have a fully functional new block handling mechanism that operate in complete parallel to the old method.  This way admins can turn it on when they want to use it, otherwise their server will continue to use the old code.

 1. Done: Create core classes for new block types.  String based so 100% flexible.  Names based upon XMaterial enums so easy mapping to XMaterial then to system version dependency.
 
 2. Done: Available blocks based upon XMateral and spigot version they are running. Create the initial list to be used for block searches.
 
 3. Done: Needs Review: Disable MineData.getBlocks() and use that to write all the main parallel code.  Reenable.
 
 4. Disable BlockType and write parallel code. Reenable.
 
 5. Hookup block search. 
 
 

* **Rework commands within the spigot module so all user facing commands are routed through Prison's Command Interface**
 Blue should work on this.


 

 
 * **Problem with rank removal from Ladders**
Create a new rank to the default ladder.  Add a player to it. Then remove the rank from the ladder.
 
The ladder no longer contains the rank. But the player is still associated with the rank, but yet ranks cannot contain players if they are not on a ladder.  The commands expect a valid ladder name.  Also there does not appear to be any checks and balances when ranks are moved from one ladder to the other since ranks have no idea what ladder they are in.

This could cause major corruption if moving ranks between ladders, removing ranks from a ladder, and players being associated with those ranks. 

Create a "void" ladder and prevent ladders from being named: default, void, and prestige.  When a rank is removed from a ladder, place it in to none and update all players that use that rank so the rank is still valid.  Do not include void ladders in placeholders.



* **Update config.yml when changes are detected**
Preserving the current settings, replace the out of date config.yml file with the latest that is stored within the jar.  Updating the settings as it goes.


* **Ladder commands - global for all ranks in that ladder**
Add new placeholders for ladder commands to be able to have generic ladder commands that will apply and be ran for all ranks. May be able to eliminate the need for most rank commands.


* **Rank Commands - Edit and delete**
Add line numbers and enable the ability to edit and delete by line number.




GABRYCAToday at 3:46 PM
3.2.1 -> Nothing to fix 

3.3.0 before -> 
Bug: Bug fixes which will be reported by the user, if nothing will be reported in a month, then some of the next planned features will be integrated within this:
New:
- Add Ranks, Mines, Prestiges and RankupCommands presets
- Add a walktrough GUI and Command to set up Prison presets on first start in the server
- <rank>, <rank_next>, <prestige>, <prestige_next> placeholders for RankupCommands

3.3.0 ->
New:
- Add close GUI Button 
- Next page button for ranks, ladders, rankupcommands and mines
- Eventually add some features which are missing in the GUIs
- Enhanced GUI listeners and management system (I'll need this for the next feature)
- Tracker and "cache" of the previous open GUI (will be deleted if there aren't Open GUIs... to sort this out)
-  Go Back button to go to the previous GUI





Enable zero block counts for parent mines.
if 100% block type of IGNORE, then after reset do an full mine air count so zero block reset works. :)




[Plugin Prison - To be able to manage at which layer such or such block appears]
- For example if I create a mine of 10 high, with iron_ore, gold_ore, emerald_ore,... I would like choose between which layers a particular bloc appears.

  "blocks": [
    "STONE-20.0-0",         0 when not configured = all layers 
    "IRON_ORE-25.0-0,5",    0,5 for all layers up to the layer 5
    "GOLD_ORE-25.0-4,7",    4,7 for layers between layer 4 and 7
    "EMERALD_ORE-25.0-8,0"  8,0 for all layers after layer 8 (in example : so up to layer 10)
  ],




# To Do Items - Post v3.2.1


<h2> Major tasks</h2>



* **Integration in to WordEdit & WorldGuard**
Largest problem with providing a solution is that prison supports 1.8 through 1.17!!
WE and WG has a strict policy that they only support the current version of spigot/mc. They do not support older versions, but their older versions that worked with older versions still exists to be used.
The major problem is that you cannot run scripts from console since WG and WE require to know what world the actions are in, and they get that from the active player.
There is an API that can be used, but they warn that there are changes to the API from version to version, so it may not be possible to provide the same API calls from 1.8 through 1.17.  Will not know until we try to use them?

Just had this idea... What if for these main commands, for configuration purposes, the player would have to be in game.  When they run these commands we TP the admin to the mines' spawn point, then run them through the console on behalf of that player?  That way WE and WG could get the world from the on line player.  But this will not work for mine resets... :(





```
  Note: May be spread out over multiple releases.
  1. New Block handling if not fully apart of v3.2.1.
  2. New Multi-language support.
  3. New Wizard Configuration support.
     A. Basic Prison functions. 
     B. Basic Mine behaviors
     C. Basic Rank and Ladder behaviors
     D. Basic prestige behaviors
  4.Creation of new scripting language for automation tied to wizards
     A. When creating a mine perform scripted actions to configure each one
     B. When creating a rank perform scripted actions to configure each rank
     C. Create the permission group within the permission plugin selected
     D. Add perms for that permission group, including warp support (prison/warps)
     E. Auto generate rank commands to add and remove group perms from each rank
```

.


<h2> Higher Priority TO DO Items </h2>


* **Need to figure out how to better handle the Spigot commands that bypass the prison command interface**
They are inconsistent and non-standard to prison. They do not support the `help` keyword, nor do they show what perms they need.  


* **Refactor GUI?**
Performance updates and validation to ensure computationally expensive processes are not running constantly.



* **Possible Issue with delete Mine**
Delete a mine and try to add a new one right away with the same name.  A user said it would not allow them, that the mine already exists.
* Tested on a few versions and could not reproduce. Keeping this entry here to retest in the near future.


<h2>To consider - Lower priority</h2>



* **When creating a new mine, register that mine with the placeholders**
Might be easier to just reregister all mines?  Not sure if that will work?
Right now, if a mine is added, in order for it to show up in the placeholders, you would have to restart the server so all the placeholders are reregistered.
 * * maybe just automatically run reset when vital elements change? * *



* **Add WorldGuard Support**
This is a work in progress. The biggest problem is that although older versions exist, they only support the latest version, and they have removed the older documentation and maven repositories. So access is highly limited to functional resources for performing builds.  The current documentation identifies that major functional changes exist even between minor versions.  So one API for v6.1 (supports mc 1.7 through 1.8 only) may not work WG v6.2.2 (supports only mc 1.12), and may not even be close to support v7.0.2 (supports 1.14 through 1.15).  

Not knowing what "range" of WorldGuard behaviors are supported through the API, or if they are even the same, the initial range of supported integrations for WorldGuard will be very limited until it can be fully tested to see where those limits are.

List of features that could be nice to have, ordered from easiest to most difficult to implement, with the possibility of never being able to do any of these:
  * Using prison's mine boundaries, set a WorldEdit selection to those same dimensions so the user does not have to reselect the same area. 
  * Using prison's mine boundaries, define, or resize, a given WorldGuard region.  Using a Prison defined naming schema.
  * If a prison is deleted, then remove the Prison's WorldGuard region.
  * Auto define the WorldGuard global templates and permissions, then auto define, update, and remove all mine related regions as the mines are added, changed, or removed.   * Detect if there is an out of sync situation between prison and worldGuard.
  

* **Get started on new Multi-Language Support**
This is put on hold for the v3.2.2 release.



* **Upon startup validate all Blocks that are defined in the mines**

Upon loading prison, validate that all blocks that are defined within each mine are actually valid for that version of minecraft.  This will be important in that it may help eliminate possible errors when the server owner upgrades the server, or other plugins.  Also it will be very helpful when Prison's block handling is enhanced since it will be a tool used to verify and maybe even fix incorrect block types.



* **Add to the Command annotations an option of *async* to run that command asynchronously**
Check to see if the commands are being ran sync or async.  Add a parameter support so 
commands that can be ran async.


* **Add prison Placeholders to papi's website for downloads**
Prison is already using papi (PlaceholderAPI).  But see if we can add prison to the supported
plugis for papi's cloud.  Should time this with the v3.2.1 release so there are more 
placeholder to use.

DeadlyKill: This what he needs ita
Papi
Hook Plugin
They have those expansions which hook other plug-ins

https://github.com/help-chat/PlaceholderAPI/wiki/Placeholders



* **Improve some of the display pages for ranks and ladders***
Can add more information to the listings so they have more value.


* **Tab Completion***
Hook up tab completion on the prison commands.


* **Better logging of major events***
Need to log major events such as rankups, both to the server log, and also
to the community.  Server logs for these events, especially when money is
involved, is important.




* **Possible new feature: Track how many blocks a player mines, including types***
Stats could be interesting over time, and could also be used for in game
bonuses and rewards and incentives.


* **Redesign the save files to eliminate the magic numbers***
Most of the save files within prison, for players, ranks, and ladders, are
using magic numbers which is highly prone to errors and is considered 
very dangerous.  Also prison would benefit from a redesign in file layout
to improve efficiencies in loading and saving, not to mention greatly reduce
the complexities within the actual prison code, which in turn will help 
eliminate possible bugs too and give tighter code.



* **Notification that inventory is full***
In progress!


* **Built in selling system***

* **Custom Mine reset messages per mine***


* **Enhancement: Multi-Language Support***

Offers for translation:
  Italian : Gabryca
  Greek : NerdTastic
  German: DeadlyKill ?? Did not ask, but a possibility?
  French: LeBonnetRouge
  Portuguese: 1Pedro ? 
  

* **Auto-Config of other Prison Related Plugins***

GABRYCA: [Idea]

Not sure if it's possible, but an hook with worldguard on mine making, like a region where the mine region's made automatically with basic permissions'd be neat

RoyalBlueRanger:

Gabryca... yes. I think a "clean" integration in to WorldGuard regions and WorldEdit selection tools would be a great improvement.  I've been wanting to do this from day one actually.  But I'm not 100% sure how much "automation" can be done here.

I guess if the first thing you do when you activate prison, is to "confirm" that you want prison that you want to "link" prison to WorldGuard and your Permission plugin (assuming supported ones exist), then auto generate all of the "step" that are outlined in the document that I create on github for WorldGuard.  That would really help everyone setup their basic servers.

A lot of work there.... but it would be VERY beneficial for sure.  It could be the foundation of an automated setup where prison "proactively" works with other plugins to help build and configure the server.

Areas of possibilities in "Auto-Configure Prison Environment":
  * WorldGuard - Regions for mines 
  * WorldEdit - Selection tools
  * LuckPerms - Perms
  * EssentialsX - Warps tied to mine warps
  * QuickSell - Place a [Sell All} sign at the mine warp
  * Citizens - NPC generated to replace the QuckSell Sign
  * CitizensCMD - Links the Citizen NPC to the QuickSell "tiered" pricing (requires Citizens)

I think those few integrations could really provide a huge bootstrap to getting the basics of a prison server up and running.



# Features recently added:



* **Done: Prestige references**
Add the prestige command to the /prison version page; rework the commands layout.
Add the documentation for prestige.  Gabryca provided the base docs.


* **Done: Prestige Fixes**
The existing prestige command is GUI only.  That may be an issue with 1.8.x. When testing, it appears to work, but may need an non-gui way to rank up.

The /prestige command does not show the cost.  The cost must be shown, along with a warning that the player's balance will be set to zero.

Since the player loses their balance, which may be far more than what the prestige may cost, there really must be a confirmation added confirming the cost, and the player's balance.  It should also identify that the excess amount of money the player will lose.

Must change the cancel button lore.  It's grammatically incorrect with a double negative.  Should only be: "Cancel Prestige".  Likewise the confirmation button should just be "Confirm Prestige" since the colon is grammatically ambiguous.



**Done! Parameterize Sort order for /mines list.**  
Default sort order should probably be alphabetical.  
Currently it is alphabetical with most active mines since restart at the top of the list, based upon blocks mined.


 
* **Done!! Add GUI support for v1.8.x**
 Might be able to add GUI support for 1.8.x with a few simple lines of code?



* **Done: Autosell feature**
Be able to allow the players to toggle it on or off. Will not sell what is in inventory, but only the blocks that they are mining.  Use /autosell .
What would be nice is if you could look up all permissions that start with prison.sellall.1.05 where the number at the end is the multiplier.  So you setup the multiplier based upon permissions and no other configs.  Then take all the multipliers and multiply them together.  So if they have a 1.05 (donor), 1.03 (special limited time), 0.97 (prestige penalty... more difficult the higher you go) would have a total of 1.049.  So basically different ranks, or even different tools could increase or decrease payout.




* **FIXED: BUG? Able to add more than 100% in one mine?**
User reported having three copies of everything which totaled more than 300%.
Used the /mines block add function?  

 
 
* **DONE: Add player names to the player file**
 Have no idea who is who in the player files.
 Make it an array of a new object, player name that has name and timestamp.
 When the file is loaded and the player is online, check name, and if not recorded, then add it. 
 

 
* **DONE: Add permissions on notifications for mine resets **
Only send a message to the player if they have the permission set, and follow the other notification settings too.
Add permission `mines.notification.[mineName]` and have each mine have a boolean field to indicate that it should check for permissions on the players prior to sending the message.

 
* **DONE: Broadcast all rankups**
Option to disable it in config settings.



* **DONE: Placeholders - Reregister**
Add a new command `/prison placeholders reload` that will reregister all placeholders.  I don't think there is a clean way to unload the registrations, but just going through registration may be good enough. Deleted, or obsolete, placeholders may still be registered, but at least the new ones will get registered too.


* **DONE: Placeholders - Add placeholder counts to startup**
Counts on how many placeholders are generate and registered.


* **DONE: Placeholders - Add new placeholders**
prison_rankup_cost_remaining 
Such that `prison_mines_*_minename` will also be mapped to `prison_mines_*_player` and will only report the values when a player is in a mine.  
I think that will be super cool, since you could put some current mine stats in your placeholder such as `prison_mines_timeleft_formatted_player`  `prison_mines_remaining_player` and even `prison_mines_player_count_player`.

NOTE this was added: the new ones are playermines and there are a total of 24 new ones.  Will work great with scoreboards.



 
 * **Not an issue: Prison Wand breaks blocks**
 Should probably monitor block break events and cancel breakage with wand.
 This was caused by a conflict with a scoreboard plugin that advertised as supporting 1.16.2, but broke a few different plugins.
 
 
 
* **DONE! Complete the new Mines Reset Paging**
  Holding up release v3.2.1.


* **DONE: Enhance AutoManager**
Make auto manager more like vanilla on the drops and support silking.  Try to make more consistent between versions. 
This is a lot closer to the vanilla. May need to revisit in the future to enhance even more.


* **DONE: Add permissions to the AutoManger***
Add permission checking to AutoManager to allow a per-mine selection of which mines to enable it in or to tie it to some rank or donor rank.  Could also put lore checking in place so tools could be enchanted to perform these functions too.  Could have it so there is a percent chance related to the permission or lore.


* **DONE: Prestige and Rebirth**



* **Already Exists: Update the Prison command handlers to support help context**
-= Note I was unaware if you use the command `help` as the only parameter it shows the help =-
This would show the parameter details for the commands. Right now the annotations that defines the command parameters has descriptions for the command and the parameters, but they are not displayed anywhere useful.



* **DONE: Add support for player use of /mines tp**

Could be done through other perms and then checking to see if they have access t

that mine.  Perm:  mines.tp (all mines) or mines.tp.<mineName>.



* **DONE: Add a placeholder test command**
Create a command, like under **/prison** that can be used to test placeholders.
Have one where the user can enter any free text and then translate it.
Also have a page(s) that goes through all of them printing the place holder name and the current values.



* **Improve the prestige laddering system***
A plugin named EZprestige has been attempted to be used with prison. Not sure if successful?
-= Prison now has its own prestige system =-



* **DONE: Remove world check before loading mine***
Now supports deferred world loading, where the world loads after prison initializes.
This is a problem with Multiverse-core plugin since a softdepend loads way before a hard depend. As such, the worlds that were created with Multiverse-core have not yet been added to the bukkit list of worlds.

If the world is checked after the server is running, they will be available.  Put in a class variable that identifies if the world was verified, and if not, then check. 

Problem is that at startup time, we won't know if there is a problem with missing worlds.


* **DONE: Add /ranks remove currency [rankName] [currency]**
Done. Currently no way to remove a currency from a Rank to return it to normal currency.


* **DONE: Add onBlockBreak monitor to prison mines to count blocks mined**
Set EventPriority to monitor. Don't change anything. Just confirm block was changed to air and increment the count.
Do not have to precheck if the block was air before, since air cannot trigger a block break event.  Just confirm it is air when monitoring to ensure it was removed. If this works, then this would allow the elimination of the air counts.


* **DONE: Delay Mine Resets based upon blocks remaining**
 Done: This is the Skip Mine Reset processing.  It will not do a reset until enough blocks are mined.


* **DONE: Skip Mine Resets - Based upon usage and percent remaining**
    * If a mine is not being used, this can greatly reduce the processing needs within the server. If there are about 30 mines, and no players are online for hours, then it can greatly reduce the server loads and reduce the number of chunk updates.
    * Placeholder added percent mined so data exists to use for this calculation.
    * At reset time, if enabled, check to see how many blocks were mined.
    * If more than threshold percentage, such as 10%, then reset the mine.
    * Even if one block is mined, and it is below the threshold, may want to reset the mine after X number of *skipped* resets. 
    * Do not count as skipped if zero blocks are mined.
    * All mines will reset after server reload, after the timer expires the first time, since in-mine block data and stats are not saved to the file system. 

    * Fields to add to the Mine data:
      * skipReset - true = enabled, false = disabled
      * skipResetPercent - double - threshold to reset based upon blocks mined. Does not include original air-blocks.
      * skipResetBypassThreshold - int - number of *skips* before a forced reset.
      * skipResetBypassCount - transient - int - counts the number of times a reset is skipped. This is transient value and is never saved.






* **Not needed: Exclude specific Prison commands**
NOTE: The player asking for this was able to disable the ranks module and that was able to solve their problem. There are no other reasons to cause this much trouble and redesign of the command handling internally, so this is being removed from consideration.
Ability to exclude, or ignore, specific commands upon startup. 
NOTE: this may not be needed. Disabling the Prison Ranks module solved the problem, which was trying to use EZRanksPro and prison's /rankup command was conflicting with that plugin's /rankup command




* **OBSOLETE: Sellall is now internal. Support QuickSell project for use with Prison *Only* ***

*Goal:* Something to consider. See if it can work with 1.15.x. This would provide a solution for prison servers to use with the full range of our supported platform versions.  Intentions of pushing changes back in to the main project and not maintaining a new project.

QuickSell has be abandoned, but could be very useful for prison to provide a simplified integration of features. 

Quickly reviewed code and it looks fairly good and probably has very low maintenance. Base initial support could be updating dependencies within Maven. Goal to get QuickSell to work with all supported versions of Prison and all supported versions of spigot.

Explicit support going forward would be directly related to Prison. If a support issue has to do with another 3rd party plugin, then support "could" be refused or unsupported 3rd party plugins could be removed. Primary focus would be for the support of Prison and to provide a QuickSell feature to users of the Prison plugin.

https://www.spigotmc.org/resources/quicksell.6107/

https://github.com/TheBusyBiscuit/QuickSell
Currently 15 forks.  Activity unknown.




* **Obsolete: New block handling system - put on hold**
-= Using XMaterial so this is covered, except for possibility of custom blocks =-

Current system is based upon enumerations which are static and may not reflect the actual run time environment.  Prison is compiled with 1.9.4, but yet the list may not include all blocks for all versions of bukkit/spigot/minecraft


If the new block handling system gets all blocks from org.bukkit.Material.values(), then it should reflect what's available on the server version that is running.  If the server owner decides to upgrade, or down grade, their server version, then they will be responsible for "correcting" any block name that is no longer supported.  This would be the negative for such a system


The benefits would be less to manage within prison; attitude of do what you want to do, instead of micro managing the list of blocks.  Dynamic to support newest blocks available on minecraft/bukkit/spigot, or another platform.  Ability to pickup custom blocks if they have been injected in to the Material enumeration


Currently there is a HUGE problem.  Upon testing, I have determined that although a block exists within the server's org.bukkit.Material enum, Prison cannot select it.  I do not know why. It could be related to the fact that prison is built with Gradle using spigot v1.9.4 and that imposes restrictions upon what enumerations can be accessed at runtime?  That makes no sense since no artifacts of org.bukkit.Material should be carried over outside of the compile time instance.  Until this issue can be addressed, there will be no work around or implmentation.



* **Obsolete: Block Types for Specific Versions of Minecraft**
-= Using XMaterial going forward =-
Add in support for the loss of magic values, and also provide for newer block types too.  Basically have a minecraft version selector that can tailor the list of available block types that can be used, based upon the minecraft version that is running



* **DONE: Add parameter to charge player for Promote command**
On /ranks promote you can now charage a player for that rank.  Also on /ranks demote you can issue a refund to the player too.  See the actual commands for usage.


* **DONE: New Feature: Upon block break events, log block changes**
This will allow dynamic live tracking of mine stats, such as how many blocks
remain and relating percentages.  The new async processing will enable this
to actually track individual blocks.


* **DONE: Document how to use essentials warps for each mine**
See documentation within github for these details.


* **DONE: Add placeholder aliases so they are not as long**
Done.


* **DONE: Eliminate the internal Placeholders**
Done.  Performance improvement.


* **DONE: Integrate GUI in to bleeding**
Done.  More GUI features will be added in the next release.


* **DONE: Setup GUI to use /prison gui**
Done.


* **DONE: Mine Placeholders**
Added a number of placeholders for mines.


* **DONE: Player Placeholder - prison_rankup_rank_tag**
When adding the new placeholder code, the prison_rankup_rank was set to return the 
rank name and not tag. So added a prison_rankup_rank_tag so there would be access
to both to give the most flexibility.

* **DONE: New Feature: List all registered plugins**
To better support server owners when they have issues with Prison, it would 
be very helpful if **/prison version** would list all registered plugins in
a concise listing. In progress.  Included now in bleeding.
All integrations are now listed with their version.
The spigot command **/pl** (plugin list) shows all active plugins.

* **DONE: New feature: Track how many blocks are mined from the mines**
Stats on how many blocks are mined from each mine. May be bad to track,
but could open the door to interesting stats.
**DONE:** this is a whole mine sweep to count air blocks and is being performed
for the benefit of placeholders.  This could be improved with block break
event tracking.

* **DONE: New Feature: Add Placeholders for Mine related items**
Examples would be place holders for all mines, and their stats such as
size, dimensions, percent remaining, reset duration, time left until reset,
players currently within the mine, ect...

* **DONE: Offline player support**
Was not possible to get offline users through the prison API. 

* **DONE: New Feature: Admin reset of Player Ranks**
Bypass the costs for the players. The admins can now use
/ranks promote, /ranks demote, and /ranks set rank.

* **DONE: Eliminate support for Sponge**
It's not being used, so eliminate it and allow prison to possibly eliminate the
extra layers of indirection it currently has to improve performance and to 
possibly reduce the possibilities for errors. 


* **DONE: List currencies that are used in Ranks**

On startup, gather all currencies that are defined within the Ranks, confirm there is a economy that supports it, and then print the list with the ranks module.



## Known Issues - v3.2.1-alpha.9 - 2020-04-26


* **Multiverse-core prevents worlds from loading**
Multiverse-core when it is a softdepend prevents prison from being able to access all of the worlds that it will normally load.  When it is a softdepend Prison loads prior to multiverse-core so bukkit has no knowledge of those worlds.

To fix this problem, you must manually add Multiverse-core as a hard dependency. 

**NOTICE** the line that starts with **depend:** since that line does not exist in the normal config.yml file. 

```
website: https://mc-prison.tech
softdepend: [Essentials, Vault, LuckPerms, Multiverse-Core, Multiworld, MVdWPlaceholderAPI, PlaceholderAPI, GemsEconomy, WorldEdit, WorldGuard, ProtocolLib, PerWorldInventory, Multiverse-SignPortals, Multiverse-NetherPortals ]
depend: [Multiverse-Core]
commands:
```


* **Prison v3.2.0 (and older) has limited Placeholder Support**

Prison v3.2.0 only has one chat placeholder and it is {PRISON_RANK}, which must be in all uppercase.  

The only Prison integrations supported with this version are PlaceholderAPI and MVdWPlaceholder.  

The placeholders supported by PlaceholderAPI are prison_rank, prison_rankup_cost, and prison_rankup_rank.  Supported case is unknown, so use lowercase.

The actual placeholders for MVdWPlaceholder is actually unknown, but may be the same as listed for PlaceholderAPI, but I cannot confirm it.


* **Prison v3.2.1 Placeholder Support**

The best way to find the available placeholders is to use the command **/prison version**.  Keep in mind that any placeholder that ends with **minename** will be expanded for each mine, substituting the mine name for the suffix **minename**.  For example, if there are 30 mines, then Prison will register 30 placeholders for each listed placeholder under **/prison version**.  

The placeholders will be registered as shown, in lowercase.  They actually are case insensitive, but since they are registered in lowercase, the various placeholder APIs may only recognize lowercase entries.  Also Prison's placeholders are registered without any brackets.  Most placeholder tools use {} and a few others use %%; refer to the placeholder's documentation on correct usage. 

Prison's Ranks has a chat handler that now supports all placeholders.

Support for PlaceholderAPI is through the prefix of "prison", of which it will route all placeholders with prefixes of "prison" through prison.  When PlaceholderAPI makes calls to Prison, it strips the prefix.  Therefore, Prison's placeholders will respond to the full placeholder, or the the placeholder minus the prefix.  The list of all existing placeholders within Prison are not pre-registered.

The MVdWPlaceholder api requires all placeholders be registered.


* **Some block types may not work for 1.15.x**
Since prison is not currently using the correct block names for 1.15.x, some
block types may not work. Prison is still using magic numbers for the 
block types and those no longer work for 1.15.x.  Symptom would be that
you set a block type such as birch block, but with the loss of the magic 
number, it will revert back to just an oak block.  ETA may be with
release v3.2.2?


* **Unable to change language on all Aspects of Prison**
Currently the number of phrases that can be changed to support other
languages is very limiting and requires a decent amount of manipulation
of extracting files from the Prison Jar. Future releases will allow just
about all uses of English to be replaced by external language files.
This will include error messages for players and mods (console errors), 
command descriptions, and even replacement of the English commands as 
aliases.  The implementation of this will help ensure "errors" are caught
at compile time, and not runtime to help improve stability of the game.
Also the way the language files are structured at runtime will make it 
easier to edit them.



* **Information: Setting the correct currency for Prison**

	Note: Moved to FAQ docs.



* **Known issue with LuckPerms v5.0.x Causing Prison Load Failures**

	Note: Moved to FAQ docs.



* [ ] **Reports that other plugins may cause issues with Prison**
It's been mentioned that a plugin or two, named something like 
"nohunger or nofalldmg", may have been causing issues with Prison.  
Not sure if its the loading or running, but it behaved as if the
mines and ranks module was not loaded since those commands were not
functional.  Only /prison was working.  This appears as the same general
effect of LuckPerms v5.x failures, where they caused a failure that
prevented prison from performing a normal load.  
I have not looked in to these plugins, but I would suggest that 
WorldGuard should be used instead of these plugins to eliminate possible
conflicts.



* [x] **REMOVED: No support for sponge - appears like it never had it**
Note: Support for sponge was commented out in two gradle files. The source code
remains, but it will not impact the builds anymore.

There is a sponge module, but there is so little code that has been written,
that it does not appear to be hooked up.  There is no way it could have ever worked
correctly since so many core components needed for the functionality of prison 
are dependent upon Spigot internals, of which those same function calls under 
Sponge's API are just empty or returning null values.

For example, getScheduler() and dispatchCommand() both are empty, but they 
are currently heavily used in both the mine reset process and also for
ranking up. 

It would be a really major effort to hook up the missing parts. I don't even 
think anyone is trying to run it under sponge.  Maybe best in the long run to 
eliminate the sponge module and just focus on making prison better overall.
I think if I do get around to disabling it, it will just be commented out of the
gradle build such that the source will still be there, but it will be excluded
from the build.  Otherwise as new features are added, and existing ones under go
major changes, then the Sponge components will have to be revisited and would be 
wasting resources (and time) for no reasonable purpose.


