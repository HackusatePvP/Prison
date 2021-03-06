package tech.mcprison.prison.spigot.gui.sellall;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.mcprison.prison.spigot.SpigotPrison;
import tech.mcprison.prison.spigot.gui.ListenersPrisonManager;
import tech.mcprison.prison.spigot.gui.SpigotGUIComponents;

import java.util.List;

/**
 * @author GABRYCA
 */
public class SellAllPriceGUI extends SpigotGUIComponents {

    private final Player p;
    private final String itemID;
    private final Double val;

    public SellAllPriceGUI(Player p, Double val, String itemID){
        this.p = p;
        this.val = val;
        this.itemID = itemID;
    }

    public void open() {

        // Create a new inventory
        int dimension = 45;
        Inventory inv = Bukkit.createInventory(null, dimension, SpigotPrison.format("&3SellAll -> ItemValue"));

        // Load config
        Configuration GuiConfig = SpigotPrison.getGuiConfig();

        if (guiBuilder(inv, GuiConfig)) return;

        // Open the inventory
        this.p.openInventory(inv);
        ListenersPrisonManager.get().addToGUIBlocker(p);
    }

    private boolean guiBuilder(Inventory inv, Configuration guiConfig) {
        try {
            buttonsSetup(inv, guiConfig);
        } catch (NullPointerException ex){
            p.sendMessage(SpigotPrison.format("&cThere's a null value in the GuiConfig.yml [broken]"));
            ex.printStackTrace();
            return true;
        }
        return false;
    }

    private void buttonsSetup(Inventory inv, Configuration guiConfig) {
        // Create a new lore
        List<String> changeDecreaseValueLore;
        changeDecreaseValueLore = createLore(
                guiConfig.getString("Gui.Lore.ClickToDecrease")
        );

        // Create a new lore
        List<String> confirmButtonLore = createLore(
                guiConfig.getString("Gui.Lore.LeftClickToConfirm"),
                guiConfig.getString("Gui.Lore.Price2") + val,
                guiConfig.getString("Gui.Lore.RightClickToCancel")
        );

        // Create a new lore
        List<String> changeIncreaseValueLore = createLore(
                guiConfig.getString("Gui.Lore.ClickToIncrease")
        );


        // Decrease button
        ItemStack decreaseOf1 = createButton(Material.REDSTONE_BLOCK, 1, changeDecreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " - 1" ));
        inv.setItem(1, decreaseOf1);

        // Decrease button
        ItemStack decreaseOf5 = createButton(Material.REDSTONE_BLOCK, 10, changeDecreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " - 10"));
        inv.setItem(10, decreaseOf5);

        // Decrease button
        ItemStack decreaseOf10 = createButton(Material.REDSTONE_BLOCK, 1, changeDecreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " - 100"));
        inv.setItem(19, decreaseOf10);

        // Decrease button
        ItemStack decreaseOf50 = createButton(Material.REDSTONE_BLOCK, 1, changeDecreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " - 1000"));
        inv.setItem(28, decreaseOf50);

        // Decrease button
        ItemStack decreaseOf100 = createButton(Material.REDSTONE_BLOCK, 1, changeDecreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " - 10000"));
        inv.setItem(37, decreaseOf100);


        // Create a button and set the position
        ItemStack confirmButton = createButton(Material.TRIPWIRE_HOOK, 1, confirmButtonLore, SpigotPrison.format("&3" + "Confirm: " + itemID + " " + val));
        inv.setItem(22, confirmButton);


        // Increase button
        ItemStack increseOf1 = createButton(Material.EMERALD_BLOCK, 1, changeIncreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " + 1" ));
        inv.setItem(7, increseOf1);

        // Increase button
        ItemStack increaseOf5 = createButton(Material.EMERALD_BLOCK, 10, changeIncreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " + 10"));
        inv.setItem(16, increaseOf5);

        // Increase button
        ItemStack increaseOf10 = createButton(Material.EMERALD_BLOCK, 1, changeIncreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " + 100"));
        inv.setItem(25, increaseOf10);

        // Increase button
        ItemStack increaseOf50 = createButton(Material.EMERALD_BLOCK, 1, changeIncreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " + 1000"));
        inv.setItem(34, increaseOf50);

        // Increase button
        ItemStack increaseOf100 = createButton(Material.EMERALD_BLOCK, 1, changeIncreaseValueLore, SpigotPrison.format("&3" + itemID + " " + val + " + 10000"));
        inv.setItem(43, increaseOf100);
    }

}
