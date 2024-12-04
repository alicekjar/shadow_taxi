# Project 2B - ShadowTaxi

This game was created to fulfill the requirements specified for Project 2B for SWEN20003. It was written by Alice Kjar. This game is run through the ShadowTaxi class. An additional PDF document 'Project_2A.pdf' is included which consists of a UML diagram created int the planning phase, prior to implementing this program.

Both components 2A and 2B received full marks upon submission.

## Game Description

In this game, the player controls a taxi and must use the arrow keys to move around, picking up passengers to earn money. The player must avoid obstacles such as other cars, enemy cars and fireballs which will damage the taxi's health. If the taxi's health is depleted, the driver will be ejected and they must find a new taxi. The player is able to collect coins (increasing the fee of passengers) and invincible powers (grant protection against collisions).

## Credits

This assignment built upon [skeleton code](https://canvas.lms.unimelb.edu.au/courses/189335/pages/project-2?module_item_id=5839265) and the sample solution for [Project 1](https://canvas.lms.unimelb.edu.au/courses/189335/pages/project-1-solution-4?module_item_id=6115167) which was provided by the SWEN20003 teaching team. Furthermore, the [bagel](https://gitlab.eng.unimelb.edu.au/emcmurtry/bagel-public) library written by Eleanor McMurtry was used. Image credits can be found in the credits.txt file. 

## Assumptions

A number of assumptions were made in the implementation of this assignment to decide on ambiguous areas of the project specifications.

 - A fireball cannot inflict damage on the enemy car that created it
 - Any priority buffs applied to a passenger will revert if the passenger is not dropped of before the buff times out
 - A new, empty taxi may still take damage and if it is destroyed, a new one will be created
 - While an entity is moving away from another following a collision, no other movement is allowed
 - When a passenger is ejected, it will follow the driver but still maintain 50px distance so they are not standing on top of each other
 - A passenger is vulnerable if they are not standing on the side of the road or in a taxi (as this makes more logical sense than only making them vulnerable when they are moving)
 - After a driver (with a passenger) gets into a new taxi, if the taxi is in a valid spot to drop off the passenger, the passenger will go straight back to the end flag. Otherwise, it will approach the taxi and get in.


## Skills

This project allowed me to develop a number of new/emerging skills, including
- **Java** programming
- **Object-Oriented** Software Design Techniques, including implementation of a **Singleton** design pattern
- Utilising **UML** to make design choices
- Experience with a basic **Graphical User Interface** (bagel)
- File Handling
- Abstraction/Encapsulation

