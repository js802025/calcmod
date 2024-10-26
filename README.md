![CalcMod Logo](https://i.ibb.co/gWjwWXv/calcmodsmall.png)  [![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg)](https://github.com/js802025/calcmod)  [![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg)](https://modrinth.com/mod/calcmod)
  [![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg)
](https://www.curseforge.com/minecraft/mc-mods/calcmod)  

### <center>Bring the power of calculation to your Minecraft chat with CalcMod! Whether you're a casual or technical player, enjoy features from basic calculations to advanced tools for optimizing gameplay.</center>   


<br>   

|/calc craft piston 3 sb|  
|:-:| 
|![Craft Command Example](https://i.postimg.cc/pTsPVJdv/Calc-Craft.png)|
|**/calc nether ~ ~ ~**|  
|![X: -120 Z: 4500 to Nether = X: -15 Z: 563](https://i.postimg.cc/N0kwjk0M/Calc-Nether.png)|

<i>Click <font color=#55ff55>any</font> green text to copy it to your clipboard!</i>
# 🔄 Compatibility
CalcMod supports [Fabric](https://fabricmc.net), [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/), and [Quilt](https://quiltmc.org/en) mod loaders, down to Minecraft version 1.12.

CalcMod can be installed on **both the client and server**, and also works on clients **without requiring servers to have the mod** (and vice versa.)

# ✅ Features:

See a few examples in [gallery](https://modrinth.com/mod/calcmod/gallery)
<details>
<summary>🟢 Basic Calculator</summary>

Functions like a simple calculator with some handy variables. To see a list of variables run /calc variables.

>Usage: ```/calc <expression>```

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
|       %       |     n%     | Percentage             |
|       ^^      |    a^^b    | Tetration              |
|       √       |     √x     | Square root            |
|       ∛       |     ∛x     | Cube root              |
|       ∜       |     ∜x     | Fourth root            |
</details>

> CalcMod has even more advanced functions and variables for Minecraft, located in a spreadsheet [here](https://docs.google.com/spreadsheets/d/1L3jCFO6ZiUymXbRJKOJIWGpcOQjfvU3TpCcFPeUJDNA/edit?usp=sharing).
</details>



<details>
<summary>📦 Storage Utils</summary>

Calculates the number of needed item sorters given a rate of items per hour *(can be in expression form)*. Additional input for multiple times hopper speed sorters, and a separate command for Allay based non stackable sorters.

>Usage: ```/calc storage <itemsperhour>```  
>Usage: ```/calc storage <timesHopperSpeed> <itemsperhour> ```  
>Usage: ```/calc allaystorage <itemsperhour>```
</details>   



<details>
<summary>🛠️ Crafting Utils</summary>

Given a desired item and the quantity to be crafted, returns the amounts of the items needed to craft the amount of the desired item.  

**<font color=#55ff55>(1.3.2+)</font>** Depth is an optional argument that specifies how many levels of recursive crafting to perform on the recipe. Default depth is 1. 
  
>Usage: ```/calc craft <item> <amount>```  
>Usage: ```/calc craft <item> <depth> <amount>```
</details>



<details>
<summary>🌐 Portal Linking Coords</summary>

Given a block position returns the other dimension's corresponding coordinates. If no coordinates are given, command assumes current player position.

>Usage: ```/calc nether <x> <y> <z>```  
>Usage: ```/calc overworld <x> <y> <z>```
</details>



<details>
<summary>🌾 Farm Rates Calculator</summary>

Given a number of items and afk time in seconds *(can be in expression form)*, returns the number of items per hour.

>Usage: ```/calc rates <numberofitems> <time(seconds)>```
</details>



<details>
<summary>🎲 Generate Random Number</summary>

Given a maximum and/or minimum value, returns a random number between those values (inclusive). If just a maximum value is entered, picks a random number from 0 to the max value (inclusive).

>Usage:```/calc random <max>```  
>Usage: ```/calc random minmax <min> <max>```
</details>



<details>
<summary>💪 Comparator Strength Calculator</summary>

Given a container and a desired comparator signal strength, returns the number of items needed to achieve that signal strength.

>Usage: ```/calc signaltoitems <container> <signalstrength>```
</details> 



<details>
<summary>🐷 Piglin Bartering Utils  </summary>

Calculates the average amount of gold ingots to barter to get a number of a desired item *(togold)*, or the average amount of an item that will be recieved when bartering a number of gold ingots *(toitem)*.  

>Usage: ```/calc barter togold <numberofitems> <item>```  
>Usage: ```/calc barter toitem <amountofgold> <item>```  
</details>



<details>
<summary>🧮 Custom Functions </summary>   

Custom functions are reusable commands that perform a specific computation. Custom functions can be run in any number field formatted with the function name and the parameters in parentheses ```functionname(param1, paramN)```.  

**Creating a Function:**  
Custom functions can have any number of parameters, specified in [square] brackets when adding a function.  
|⚠️ Using any numbers or special characters in a parameter may result in a broken function.|
|:-:|
 
Ex) ```/calc custom add blockstoingots [numBlocks]*9```   

| /calc blockstoingots(72) |
|:-:|
| ![blockstoingots(72) = 648](https://i.ibb.co/FK35cqK/CalcFn.png) | 


>Usage: ```/calc custom add <name> <function>```  
>Usage: ```/calc custom run <name> <input>```  
>Usage: ```/calc <name>(<parameters>)```  
>Usage: ```/calc custom list```  
>Usage: ```/calc custom remove <name>```   

</details>  


### ➡️ Converters:

<details>
<summary>Shulker Boxes ↔ Items</summary>

Given a number of full Shulker Boxes *(can be in expression form)*, returns the number of items, or vice versa.

>Usage: ```/calc itemtosb <numberofitems>```  
>Usage: ```/calc sbtoitem <numberofsbs>```
</details>



<details>
<summary>Items ↔ Stacks:</summary>

Given a number of items *(can be in expression form)*, returns the number of stacks and remainder items, or vice versa.

>Usage: ```/calc itemtostack <numberofitems>```  
>Usage: ```/calc stacktoitem <numberofstacks>```
</details>



<details>
<summary>Seconds → Hopper Clock Items</summary>

Given a number of seconds *(can be in expression form)*, returns the number of items needed in a hopper clock to achieve that time. *Uses formula and hopper clock from [hoppertimer.net](https://hoppertimer.net/).*  

![final.png](https://i.postimg.cc/pVg9W6Gw/final.png)
>Usage: ```/calc secondstohopperclock <seconds>```
</details>


<details>
<summary>Seconds → Repeater Delay</summary>

Given a number of seconds *(can be in expression form)*, returns the number of repeaters and their delay.

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
</details>  

Dynamic variables change depending on an in-game status. These can be particularly useful inside custom functions.
<details>
<summary>Dynamic Variables</summary>

| **Name** | **Value**      |
|----------|----------------|
| x        | player x coord |
| y        | player y coord |
| z        | player z coord |
| health   | player health  |

</details>


</br>
