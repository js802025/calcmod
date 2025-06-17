![CalcMod Logo](https://i.ibb.co/gWjwWXv/calcmodsmall.png)  [![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg)](https://github.com/js802025/calcmod)  [![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg)](https://modrinth.com/mod/calcmod)
[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg)
](https://www.curseforge.com/minecraft/mc-mods/calcmod)

<big><center>Bring the power of calculation to your Minecraft chat with CalcMod! Enjoy features from simple calculations to advanced tools for optimizing gameplay.</center></big>


<br>   

|/calc 3+(28/8)^2|
|:-:|
|<img src="https://i.postimg.cc/MK3j46wm/basic.png" alt="Basic calculation response" width="75%" />|

|/calc craft piston 3 sb|
|:-:|
|<img src="https://i.postimg.cc/pTsPVJdv/Calc-Craft.png" alt="Craft command response" width="75%" />|

|/calc nether ~ ~ ~|
|:-:|
|<img src="https://i.postimg.cc/N0kwjk0M/Calc-Nether.png" alt="Nether command response" width="75%" />|

<br>
<i>Click <font color=#55ff55>any</font> green text to copy it to your clipboard!</i>

# 🔄 Compatibility
CalcMod supports [Fabric](https://fabricmc.net), [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/), [Quilt](https://quiltmc.org/en), and [Paper](https://papermc.io/) mod loaders, down to Minecraft version 1.12.

CalcMod can be installed on **both the client and server**, and also works on clients **without requiring servers to have the mod** (and vice versa.)

# ✅ Features:

See a few examples in [gallery](https://modrinth.com/mod/calcmod/gallery)
<details>
<summary>🔢 Simple Calculator</summary>

An intuitive calculator with some handy variables. To see a list of variables run /calc variables.

<details>
<summary>Supported Operators</summary>  

| **Symbol(s)** | **Syntax** |      **Operation**     |
|:-------------:|:----------:|:----------------------:|
|       +       |     a+b    | Addition               |
|       –       |     a–b    | Subtraction            |
|   * , × , ∙   |     a*b    | Multiplication         |
|   / , : , ÷   |     a/b    | Division               |
|       ^       |     a^b    | Exponentiation (power) |
|       !       |     n!     | Factorial              |
|       #       |     a#b    | Modulo                 |
|       %       |     x%     | Percentage             |
|       ^^      |    a^^b    | Tetration              |
|       √       |     √x     | Square root            |
|       ∛       |     ∛x     | Cube root              |
|       ∜       |     ∜x     | Fourth root            |  

> CalcMod has some more advanced functions and variables that can be used within expressions in [this spreadsheet](https://docs.google.com/spreadsheets/d/1L3jCFO6ZiUymXbRJKOJIWGpcOQjfvU3TpCcFPeUJDNA/edit?usp=sharing).
</details>  

>Usage: ```/calc <expression>```

</details>

<details>
<summary>📦 Storage Utils</summary>

When given a rate of items per hour *(expressions allowed)*, returns the minimum number of item sorters needed to sort in time.  
Additional input for multiple times hopper speed sorters, and a separate command for Allay based non stackable sorters.

>Usage: ```/calc storage <itemsPerHour>```  
>Usage: ```/calc storage <timesHopperSpeed> <numberOfItems> ```  
>Usage: ```/calc allaystorage <itemsPerHour>```
</details>   

<details>
<summary>🛠️ Crafting Utils</summary>

When given an amount of a recipe to craft *(expressions allowed)*, returns all the items required to craft that quantity of the recipe.

**<font color=#55ff55>(CalcMod 1.3.2+)</font>** The optional depth argument specifies how many levels of recursive crafting to perform on the recipe. Default depth is 1.

**<font color=#FF0000>(Minecraft 1.21.3+)</font>** Due to Minecraft's crafting system updates, CalcMod's craft command can now only use recipes unlocked in the user's recipe book in single player mode.

>Usage: ```/calc craft <item> <amount>```  
>Usage: ```/calc craft <item> <depth> <amount>```
</details>

<details>
<summary>🌐 Portal Linking Coords</summary>

When given a block position, returns the dimension in the command's corresponding coordinates. If no coordinates are given, command assumes current player position.

>Usage: ```/calc nether <x> <y> <z>```  
>Usage: ```/calc overworld <x> <y> <z>```
</details>

<details>
<summary>🌾 Farm Rates Calculator</summary>

When given an amount of items and a farm run time in seconds *(expressions allowed)*, returns the items per hour of the farm.

>Usage: ```/calc rates <numberOfItems> <seconds>```
</details>

<details>
<summary>🎲 Generate Random Number</summary>

When given a maximum and/or minimum value, returns a random number between those values (inclusive). If just a maximum value is entered, picks a random number from 0 to the max value (inclusive).

>Usage:```/calc random <max>```  
>Usage: ```/calc random minmax <min> <max>```
</details>

<details>
<summary>💪 Comparator Power Level Finder</summary>

When given a container and a desired comparator power level *(expressions allowed)*, returns the number of items needed to achieve that power level.

>Usage: ```/calc signaltoitems <container> <powerLevel>```
</details> 

<details>
<summary>🐷 Piglin Bartering Utils  </summary>

Calculates the average number of gold ingots needed to barter for a specific quantity of a desired item *(togold)*, or the average number of a desired item received when bartering a specific number of gold ingots *(toitem)*.

>Usage: ```/calc barter togold <numberOfItems> <item>```  
>Usage: ```/calc barter toitem <amountOfGold> <item>```
</details>

<details>
<summary>🧭 Travel Distance Evaluator <font color=tomato>(New!)</font></summary>

When given two block positions, returns the distance between them. If only one position is given, uses player's location. The 3D mode provides distance including height *(y coord)*.

>Usage: ```/calc dist <x1> <y1> <z1>```  
>Usage: ```/calc dist <x1> <y1> <z1> <x2> <y2> <z2>```  
>Usage: ```/calc dist 3d <x1> <y1> <z1>```  
>Usage: ```/calc dist  3d <x1> <y1> <z1> <x2> <y2> <z2>```
</details> 

<details>
<summary>🧮 Custom Functions </summary>   

Custom functions are reusable commands that perform a specific computation. Custom functions can be run in any number field formatted with the function name and its parameters in parentheses ```functionName(<param1>, <paramN>)```.

**Creating a Function:**  
Custom functions can have any number of parameters, specified in [square] brackets when adding a function.

|⚠️ Using any numbers, special characters, or variables in a parameter may result in a broken function.|
|:-:|


e.g. ```/calc custom add blockstoingots [numBlocks]*9```

| /calc blockstoingots(72) |
|:-:|
| ![Custom function response](https://i.ibb.co/FK35cqK/CalcFn.png) | 


>Usage: ```/calc custom add <functionName> <function>```  
>Usage: ```/calc custom run <functionName> <input>```  
>Usage: ```/calc <functionName>(<parameters>)```  
>Usage: ```/calc custom list```  
>Usage: ```/calc custom remove <functionName>```

</details>  


### ➡️ Converters:

<details>
<summary>Shulker Boxes ↔ Items</summary>

When given an amount of full shulker boxes *(expressions allowed)*, returns the number of items they contain, or vice versa.

>Usage: ```/calc itemtosb <numberOfItems>```  
>Usage: ```/calc sbtoitem <numberOfSbs>```
</details>

<details>
<summary>Items ↔ Stacks:</summary>

When given an amount of stacks *(expressions allowed)*, returns the number of items in those stacks, or vice versa.

>Usage: ```/calc itemtostack <numberOfItems>```  
>Usage: ```/calc stacktoitem <numberOfStacks>```
</details>

<details>
<summary>Seconds → Hopper Clock Items</summary>

When given a time in seconds *(expressions allowed)* returns the number of items to put in a hopper clock to achieve that time.  
*Uses formula and hopper clock from [hoppertimer.net](https://hoppertimer.net/).*

![Hopper clock](https://i.postimg.cc/pVg9W6Gw/final.png)
>Usage: ```/calc secondstohopperclock <seconds>```
</details>

<details>
<summary>Seconds → Repeater Delay</summary>

When given a time in seconds *(expressions allowed)*, returns the number of repeaters and their delays to achieve that time.

> Usage: ```/calc secondstorepeater <seconds>```
</details>  


# ❎ Variables

Variables can be used inside commands in **any number field**. They act as shortcuts instead of having to remember that "a double chest full of 16 stackable items is 864."
If no stack size is given, variables default to the contextualized stack size in each command.


<details>
<summary>Constant Variables</summary>


| **Name** | **Value**     |
|----------|---------------|
| dub      | 3456 (default)|
| dub64    | 3456          |
| dub16    | 864           |
| dub1     | 54            |
| sb       | 1728 (default)|
| sb64     | 1728          |
| sb16     | 432           |
| sb1      | 27            |
| stack    | 64 (default)  |
| stack64  | 64            |
| stack16  | 16            |
| stack1   | 1             |
| min      | 60            |
| h        | 3600          |

> There are some extra less Minecraft specific variables listed in [this spreadsheet](https://docs.google.com/spreadsheets/d/1L3jCFO6ZiUymXbRJKOJIWGpcOQjfvU3TpCcFPeUJDNA/edit?usp=sharing)
</details>  
<details>
<summary>Dynamic Variables</summary>
Dynamic variables change depending on an in-game status. These can be particularly useful inside custom functions.  


| **Name** | **Value**      |
|----------|----------------|
| x        | player x coord |
| y        | player y coord |
| z        | player z coord |
| health   | player health  |

</details>

<br>