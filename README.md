# ChatCalc
Convert overworld to nether coordinates, calculate the rates of your farm, figure out how many hoppers you need in your storage, and more all in one mod
see a full list of features below:

## Commands

### Basic(no arguments):
Functions like a simple calculator but with variables, to see the variables available run /calc variables<br/>
Usage: ```/calc <expression>```
            
### Storage:
Given rate in terms of items per hour(can be in expression form) and optionally hopper speed, returns the number of needed sorters and rates in terms of sbs per hour<br/>
Usage: ```/calc storage <itemsperhour>```<br/>
Usage: ```/calc storage <timesHopperSpeed> <itemsperhour> ```
            
### Nether:
Given a block position, returns the nether coordinates<br/>
Usage:```/calc nether <x> <y> <z>```
            
### Overworld:
Given a block position, returns the overworld coordinates<br/>
Usage: ```/calc overworld <x> <y> <z>```
            
### Sb to Item:
Given a number of sbs(can be in expression form), returns the number of items<br/>
Usage: ```/calc sbtoitem <numberofsbs>```
            
### Item to Sb:
Given a number of items(can be in expression form), returns the number of sbs<br/>
Usage: ```/calc itemtosb <numberofitems>```
            
### Seconds to Hopper Clock:
Given a number of seconds(can be in expression form), returns the number of ticks in a hopper clock<br/>
Usage: `/calc seconds2hopperclock <seconds>`
            
### Seconds to Repeater:
Given a number of seconds(can be in expression form), returns the number of repeaters and the last tick of the last repeater<br/>
Usage: `/calc seconds2repeater <seconds>`

### Item to Stack:
Given a number of items(can be in expression form), returns the number of stacks and leftover items<br/>
                `Usage: /calc itemtostack <numberofitems>`

### Stack to Item:
Given a number of stacks(can be in expression form), returns the number of items<br/>
                Usage: `/calc stacktoitem <numberofstacks>`

### Rates:
                Given a number of items and afk time in seconds (can be in expression form), returns the number of items per hour
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                Usage: `/calc rates <numberofitems> <time>`

## Variables
    Dynamic variables will default to the stack size of each command. Here are the variables for the majority of commands which use a stack size of 64:
                dub: 3456(dynamic)
                dub64: 3456
                dub16: 864
                dub1: 54
                sb: 1728(dynamic)
                sb64: 1728
                sb16: 432
                sb1: 27
                stack: 64(dynamic)
                stack64: 64
                stack16: 16
                stack1: 1
                min: 60
                hour: 3600