# Cultures Map Development Kit

A development kit for maps of cultures games (Northland and 8th wonder of the world).
It allows the creation of internal maps without the internal editor using only the included one (normal editor).
It is an extension for [Visual Studio Code](https://code.visualstudio.com/) that gives you syntax highlighting of the ini-files as well as command and parameter documentation, quickfixes and some error-checking.
You create the terrain in the normal editor and export it (and the ini content) from the map file for you to use.

You might want to put a game.ini file in the root directory of your game:
```
gfx_fullscreen 0
gfx_screen_width 1024
gfx_screen_height 768
fx_off
dm_off
cda_off
game_pre_stuff_skip
```

[Here is a short intro/tutorial video.](https://www.youtube.com/watch?v=VYwABWk8Ky0)

# Using this extension
Download the extension and drop it into the extensions menu in VS Code.

## First time
Go to you game folder and the find the folder `data` and then `maps`.
If they do not exist, either create them or install a custom user map that was created via the internal editor.

Create a new map by creating a folder. Only use plain letters A to Z, numbers and underscores.
Use the external editor to create a (simple) map and save it in the folder you just created (the map folder) as "map.c2m".

In VS Code, go to File -> Open Folder and open your map folder.
In the "explorer" view (ctrl+shift+e) you should see the two files the editor created: the `map.c2m` and `map.c2e`.
 
Go to View -> Command Palette (ctrl+shift+p) and search for "Extract c2m".
This converts the `map.c2m` to the internal format:
* The `map.dat` holds all "static" data. For example: the terrain height, terrain texture, landscape elements, ...
* The `extracted.ini` holds all "logical" and "dynamic" data. For example: Players, diplomacy, vikings, houses, ... 

You now need to rename the `extracted.ini` to the `map.ini`.
The `map.ini` is the file that is used by the game and can be freely edited.

## Updating
While creating your map you will most likely make some changes in the external editor you want to apply to the internal format.
You run the extraction process again that got you started in the first place (generating the `extracted.ini`) from the previous chapter.

The `map.dat` will be overwritten automatically, so the terrain and landscape will be already finished. 
However, applying the changes of the `map.ini` is a manual and tedious process.
You have to know exactly what you changed, find these changes in the `exported.ini` and port the to the `map.ini` manually.

Currently, there is no better way, sorry.
In the future there might be some helper that shows the changes between exports.
If you are technically inclined you could use a diff tool or git to track the changes.

# Tips
* String/text files need to be encoded in `ISO 8859-1` (also called "Western").
You can find the current encoding in the bottom right toolbar. `UTF-8` might be selected by default or VS Code might have already guessed `ISO 8859-15`.


# Building
Building and packaging is currently only available for the minimal JVM.
Although the build files are there, there are still problems with the GraalVM native-image at runtime with (de)serialization with gson at runtime.

Please refer to the [github workflow files](.github/workflows) for building as they are used by github actions directly to create VS Code extension package..

