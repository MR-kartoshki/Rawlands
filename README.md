# This isnt the main branch!
Thecreeper3326 is working on a port to 1.21.11 that will be found here. Currently does not run but will try to grt that fixed soon.

---
[![Rawlands](https://raw.githubusercontent.com/MR-kartoshki/Rawlands/refs/heads/main/banner.png)](https://modrinth.com/mod/rawlands)

---

## Features
This mod adds several new biomes to the game along with new blocks and features.
<details>
<summary><b>Biomes</b></summary>

- Salt Flat
- Shrubland
- Subalpine Meadow
- Mediterranean Scrubland
- Flooded Delta
- Dead Forest
- Temperate Rainforest
- Gravel Flats
- Alpine Forest
- Rocky Fields
- Coral Forest
- Abyssal Trenches
- Mosswood Forest
- Mist Coast
- Rocky Shrubland
- Azalea Forest
- Prairie
- Monsoon Forest
- Amber Steppe
- Glacial Flats

</details>

<details>
<summary><b>Blocks</b></summary>

| Block | Generates in |
|-------|--------------|
| Salt Block | Salt Flat |
| Coarse Salt | Salt Flat |
| Fine Salt | Salt Flat |
| Dry Scrub | Dead Forest, Shrubland, Mediterranean Scrubland, Gravel Flats, Amber Steppe |
| Olive Leaves | Mediterranean Scrubland |
| Olive Sapling | Mediterranean Scrubland |
| Broadleaf Lupine | Subalpine Meadow |
| Nightshade | Alpine Forest, Temperate Rainforest |
| Short Cattail | Flooded Delta |
| Tall Cattail | Flooded Delta |
| Delta Lily | Flooded Delta, Mosswood Forest |

</details>

<details>
<summary><b>Misc</b></summary>

- **Coarse salt** falls faster than sand or gravel
- **Dead Forest** has custom background music
- **Abyssal Trenches** has custom ambient sounds and particles
- **Azalea Trees** have a 10% chance to get replaced by the mod's new **Large Azalea Tree**

</details>

---
## Other Stuff


<details> <summary><b>Compatibility</b></summary>

This mod works with most biome and worldgen mods.

If a “Feature order cycle found” error includes this mod, the cause comes from feature ordering conflicts between multiple mods or datapacks. 
This is a Minecraft worldgen limitation and not caused by this mod alone.
This mod strictly follows vanilla Minecraft feature ordering rules.

Please attach latest.log, a crash report and full mod list when reporting issues.

---

Known issues:

[**CliffTree**](https://modrinth.com/datapack/clifftree) (as of version 3.2.1)  
Feature order cycle errors have been seen when used together with this version of CliffTree. 

</details>

<details>
<summary><b>Requirements</b></summary>

| Dependency    | Version  |
|---------------|----------|
| Minecraft     | 1.21.11  |
| Java          | 21+      |
| Fabric Loader | 0.19.3+  |
| Fabric API    | 0.141.0+ |
| TerraBlender  | any      |

</details>

<details>
<summary><b>Building from Source</b></summary>

```bash
./gradlew.bat build
```
The output JAR will be in `build/libs/`.

</details>

<details>
<summary><b>License</b></summary>

[AGPL-3.0-only](LICENSE)

</details>

<details>
<summary><b>Credits</b></summary>

- MR-Kartoshki (Lead Developer, Biome Designer)
- thecreeper3326 (Artist, Music Composer)

</details>
