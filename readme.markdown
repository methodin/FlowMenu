FlowMenu
======================================================================

# Introduction

These classes provide a more flexible menu system that builds in a hierarchical left-to-right manner. It was my intention to make all the containers have standard floating positions (LEFT, CENTER, RIGHT) but so far I've only explicitly used the CENTER so feel free to modify to support other parameters.

This class HAS NOT BEEN FULLY IMPLEMENTED. There are things I intended to do but never needed them. At some point I may resume.

In order to start a new vertical line simply add a br() 

# Dependencies

  * AndEngine (http://www.andengine.org/)

# Usage

Reference the Example.java file included in this repo.

# CustomMenuScene Functions

## add(IShape pItem)
## add(IShape pItem, float width)
## add(IShape pItem, float width, float height)

The main function you use to add any Shape derived item (Sprite, Animated Sprite, Text, MenuItem etc...) to the Menu. You can optionally provide explicit width and heights if you need more granular control.

## br()
## br(float spacing)

Adds a break to move the next added item to a new line in the Menu. The optional spacing parameter will add padding between the lines.

## br(float spacing, boolean reset)

This will reset the Y position to 0 - particularly useful where your first element is a transparent rectangle over your game. This also allows you to render layers but having multiple br(0,true) flags to your menu.

## container()
## container(int pPositionControl)
## container(int pPositionControl, int pWidthControl)

Adds a new grouping of items. All positions in the container will be relative to the container. The pPositionControl value can be one of LEFT, CENTER or RIGHT and the pWidthControll can be FLOAT or FILL.

## end()

Ends the last container added.

# CustomMenuAnimator Functions

## FlowMenu(Camera pCamera, int pWidthControl, int pHeightControl)

Adding a new Menu requires you to add it with a reference to your camera, a width control and a height control.

# NOTES:

CustomMenuAnimator extends the AlphaMenuAnimator.

Setting an item to visible=false will make that object disappear from the menu and the space it takes up will no longer be there. In order for this to work properly you must call myCustomMenuScene.buildAnimations() after modifying any of the items you added.