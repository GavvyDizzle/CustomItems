# Custom Items

**Overview:**
 - Any item inside of any .yml in the plugins/CustomItems will be loaded. This allows you to easily organize all of your custom items.
 - Items can either be a Material from the vanilla game or any custom skull texture

**Creating Items:**
 - To list any item, they must be inside of `items:` in any .yml file
 - The following image shows how to make both types of items
![img.png](img.png)
 - The initial item identifier, `test_item` and `test_skull` are set as a unique identifier to the item under `custom_item_id` in the item's NBT data
 - `displayName:` This is the name of the item. Supports all colors.
 - `material:` This is the material of the item. This can be omitted if the item is a skull.
 - `lore:` A list of lines to add to the lore. Supports all colors. Empty single quotes '' can be used to create a blank line.
 - `isEnchanted:` If set to true, the item will have an enchant glint applied to it. Glints do not show up on skulls, so it can be omitted for skulls.
 - `isSkull:` If set to true, the item will look to the `skullLink` field for its skull link. If this field is omitted, the item will default it to false.
 - `skullLink:` The link the item will parse to get the skull's texture. I use https://minecraft-heads.com/ and use the link from `Other -> Minecraft-URL`

**Final Notes:**
 - If an item looks wrong, then something likely went wrong internally. Check the console for a description of the error
 - Even in different files, items cannot have the same item id. This error will stop all duplicate occurrences from loading
 - Clicking items in the `/citems list` menu will put one in your inventory