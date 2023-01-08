# linux-something
To make it work, if you use zsh do `nano .zshrc` or if bash `nano .bashrc`... if fish i have no idea.
and then..
```bash
PATH="/home/workspace/bin/:$PATH"
```
Now replace all occurencies of /home/workspace/bin with the folder of your choice (Empty folder recommended) and the copy all the commands in thisrepo/bin there ad done, you're all set

## Commands
Now then, let's proceed to what the commands are. aside from the fact that i have no idea what some commands are. lemme just put some text here to make you think i am actually talking about something.

### fsrch
Well now i know this once, cuz i cried making it... sadly i lost the real code but these java classes exist. so anyways, what it does is searching for files and it supports regex... i think?
```bash
$ fsrch -p ./ -f Hello  
```

### grne
Now this one is interesting... cuz it is useless. it generates a lot of random text but why would you use it? it is complicated
```bash
$ grne 10 1
Generates 10*1 characters
```

### ppath
This command searches for a certain folder in whatever path you set as the main workspace folder
```
$ ppath linque (will search for a folder called linque in all subfolders of the specified path inside the code)
```

### mkcmd
Creates a new empty command

### loopit
Now idk if this exists, but this command will loop and execute a command until you click CTRL+C

I am tired of writing like this so...
+ `overload` Loades a certain amount of mb to your RAM
+ `batp` Battery percentage
+ `cong` Easy access to your configs
+ `d` If it's a file, it opens it, if it's a dir, it ls it
+ `deauth` Aliases for deauthing with aimon-ng
+ `mem` Memory information
+ `nserver` Starts a server in the specified port to the current dir
+ `randfrom` Gives a random number from a-b
+ `req` GET or POST request
+ `setbg` Set Wallpaper

Now, idk the rest of the commands. but feel free to see the code and decide...

## One more thing
There is a module called `colorom` for nodejs scripts, it is important don't forget it
