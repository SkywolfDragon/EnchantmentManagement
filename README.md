# EnchantmentManagement


This plugin's aim is to provide a new way of managing enchantments, removing the regular restrictions that the game has in place.

Features:

    Add enchantments, setting custom limits to their levels
    Remove enchantments
    Transfer enchantments from one item to another, no matter what those items are!
    Vault integration! if you want to pay with money instead of xp
    Configurable prices!

New Features on 2.0.0:

    Custom conflicts: define two enchantments that won't be added into the same item by the plugin
    Per-enchantment weights: so you can fine-tune the chance of finding each enchantment
    Toggleable features: so you can activate and deactivate parts of the plugin as you want
    Event cancellation: minecraft events can be cancelled in favor of this plugin's interactions
    Cost groups: define different costs, max enchantment levels, min and max player levels and enchantment weights for different people (with the appropriate permission)
    Min and max player levels: so you can't add/transfer/remove an enchantment before (or after) you have a defineable xp level


Players can use "/enchcost" to get the price for enchanting/disenchanting/transfering enchantments on the item(s) they hold in their hand!

All of it is configurable either open the config.yml (after starting a server with the plugin once) on your preferred text editor and use "/enchmanagementconfig reload", or let "/enchmanagementconfig help" explain to you the different options

Now with a brand new command "/enchmanagementgroups", to help you with cost groups
