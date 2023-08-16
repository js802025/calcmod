
[![CalcMod Logo](https://i.ibb.co/gWjwWXv/calcmodsmall.png)](https://letmegooglethat.com/?q=a+calculator+in+your+chat+with+shortcuts+designed+for+minecraft!!!!!!!!!!!!!!!!!!!!!!)  [![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg)](https://github.com/js802025/calcmod)  [![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg) 
](https://www.curseforge.com/minecraft/mc-mods/calcmod) [![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg)](https://modrinth.com/mod/calcmod)


<br>

A calculator in your Minecraft chat with features to determine farm rates, generate random numbers, convert between Overworld and Nether coordinates, and a bunch of other nerdy things.


<br>  


| /calc craft piston 3 sb                                                                            |
|----------------------------------------------------------------------------------------------------|
| [![Craft feature example](https://i.ibb.co/2yyZT55/final.png)](https://i.ibb.co/6Yw0NsC/giphy.gif) |

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

Calculates the number of needed item sorters given a rate of items per hour *(can be in expression form)*. Additional input for multiple times hopper speed sorters, and a separate command for Allay based non-stackable sorters.

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

---

### ❎ Variables

Variables can be used inside equations in any number field. They act as shortcuts instead of having to remember that "a double chest full of 16 stackable items is 864."   
If no stack size is given, variables default to the stack size of each command


<details>
<summary>List of all variables</summary>

| **Name** | **Value**      |
|----------|----------------|
| dub      | 3456 (default) |
| dub64    | 3456           |
| dub16    | 864            |
| dub1     | 54             |
| sb       | 1728 (default) |
| sb64     | 1728           |
| sb16     | 432            |
| sb1      | 27             |
| stack    | 64 (default)   |
| stack64  | 64             |
| stack16  | 16             |
| stack1   | 1              |
| min      | 60             |
| hour     | 3600           |
</details>

<br>