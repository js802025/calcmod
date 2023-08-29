![CalcMod Logo](https://i.ibb.co/gWjwWXv/calcmodsmall.png)  [![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg)](https://github.com/js802025/calcmod)  [![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg)
](https://www.curseforge.com/minecraft/mc-mods/calcmod)  
<br>

A calculator in your Minecraft chat with extra features to determine farm rates, generate random numbers, convert between Overworld and Nether coordinates, and a bunch of other nerdy things.


<br>  


|/calc craft piston 3 sb|  
|:-:| 
|![Craft feature example](https://i.ibb.co/sQ0S9rP/Calc-Craft.png)|

<i>Click <font color=#55ff55>any</font> green text to copy it to your clipboard!</i>

___

### 🟢 Features:


<details>
<summary>Basic Calculator</summary>

Functions like a simple calculator with some handy variables. To see a list of variables run /calc variables.

>Usage: ```/calc <expression>```
</details>



<details>
<summary>Storage Utils</summary>

Calculates the number of needed item sorters given a rate of items per hour *(can be in expression form)*. Additional input for multiple times hopper speed sorters, and a separate command for Allay based non stackable sorters.

>Usage: ```/calc storage <itemsperhour>```  
>Usage: ```/calc storage <timesHopperSpeed> <itemsperhour> ```  
>Usage: ```/calc allaystorage <itemsperhour>```
</details>   



<details>
<summary>Crafting Utils</summary>

Given a desired item and the quantity to be crafted, returns the amounts of the items needed to craft the amount of the desired item.

>Usage: ```/calc craft <item> <amount>```
</details>



<details>
<summary>Portal Coords</summary>

Given a block position returns the other dimension's corresponding coordinates. If no coordinates are given, command assumes current player position.

>Usage: ```/calc nether <x> <y> <z>```  
>Usage: ```/calc overworld <x> <y> <z>```
</details>



<details>
<summary>Farm Rates Calculator</summary>

Given a number of items and afk time in seconds *(can be in expression form)*, returns the number of items per hour.

>Usage: ```/calc rates <numberofitems> <time(seconds)>```
</details>



<details>
<summary>Generate Random Number</summary>

Given a maximum and/or minimum value, returns a random number between those values (inclusive). If just a maximum value is entered, picks a random number from 0 to the max value (inclusive).

>Usage:```/calc random <max>```  
>Usage: ```/calc random minmax <min> <max>```
</details>



<details>
<summary>Comparator Strength Calculator</summary>

Given a container and a desired comparator signal strength, returns the number of items needed to achieve that signal strength.

>Usage: ```/calc signaltoitems <container> <signalstrength>```
</details> 



<details>
<summary>Piglin Bartering Utils <font color=#ff5555>(New)</font></summary>

Calculates the average amount of gold ingots to barter to get a number of a desired item *(togold)*, or the average amount of an item that will be recieved when bartering a number of gold ingots *(toitem)*.

>Usage: ```/calc barter togold <numberofitems> <item>```  
>Usage: ```/calc barter toitem <amountofgold> <item>```
</details>



<details>
<summary>Custom Functions <font color=#ff5555>(New)</font></summary>

Custom functions are reusable commands that perform a specific computation. Custom functions can be run in any number field formatted with the function name and the parameters in perenthases ```customfunction(param1, paramN)```.  
Custom functions can have any number of parameters, specified in [square] brackets when adding a function.

* **Create your function:**  
  ```/calc custom add blockstoingots [num-blocks]*9```

* **Run your function with an input:**  
  ```/calc blockstoingots(72)```

* **Output:**  
  ```blockstoingots(72) = 648```


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

Given a number of seconds *(can be in expression form)*, returns the number of items needed in a hopper clock to achieve that time.

>Usage: ```/calc secondstohopperclock <seconds>```
</details>


<details>
<summary>Seconds → Repeater Delay</summary>

Given a number of seconds *(can be in expression form)*, returns the number of repeaters and their delay.

> Usage: ```/calc secondstorepeater <seconds>```
</details>  

> Aside from the Minecrafty functions, CalcMod also features a gratuitous amount of more advanced functions and variables linked in a spreadsheet [here](https://docs.google.com/spreadsheets/d/1L3jCFO6ZiUymXbRJKOJIWGpcOQjfvU3TpCcFPeUJDNA/edit?usp=sharing) (in case you need to calculate an inverse hyperbolic cotangent to play Minecraft).

---

### ❎ Variables

Variables can be used inside equations in any number field. They act as shortcuts instead of having to remember that "a double chest full of 16 stackable items is 864."
If no stack size is given, variables default to the stack size in each command.


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
| hour     | 3600          |
</details>  

Dynamic variables change depending on an in-game status. These are particularly useful inside custom functions.
<details>
<summary>Dynamic Variables</summary>

| **Name** | **Value**      |
|----------|----------------|
| x        | player x coord |
| y        | player y coord |
| z        | player z coord |

</details>

<br>
