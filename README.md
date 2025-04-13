# SnackMan

SnackMan is a multiplayer browser game you can play with your friends. It's closely inspired by PacMan but in 3D and First-Person. Snack on the Food, Chase your friends, get stuck on chickens und skate your way to victory!

# Home Screen

![Home Screen](/doku/main.png "Home Screen")

# Lobby 

![Lobby Screen](/doku/lobby.png "Lobby Screen")

# In Game

![Lobby Screen](/doku/In%20Game2.png "In Game")

## Start from bootJar
In the root directory build and run the project from bootJar

```bash
./gradlew build
java -jar build/libs/snackman-0.0.1-SNAPSHOT.jar
```

While the program is running open your browser at "localhost:8080".
You can now play the game locally with others in your network.

## or play on dev Server

Use npm to install the frontend packages needed for SnackMan

```bash
cd frontend
npm i
```

## Start the dev Server

```bash
# start backend in the root folder
./gradlew bootRun

# start frontend in a new terminal
cd frontend
npm run dev
```

While the program is running open your browser at "localhost:5173".
You can now play the game locally with others in your network.


## Controls
| W | Walk forward \
| A | Walk left \
| S | Walk backwards \
| D | Walk right 


SnackMan only \
| space | jump \
| hold space | jump and boost your height 


## Contributors
This Project was done by group one of the "Software Technik Projekt" course at Hochschule RheinMain.
It was developed by:
> - Ann-Kathrin Barth
> - Atta Farsimadan
> - Mohamad Hajjar
> - Abdallah Jaber 
> - Simon Jäckel
> - Lisa Kügler
> - Larissa Oblong
> - David Thomann
> - Laura Wiatrek
> - Marvin Wernli

Under the lead of Product Owner Merle Erdmann.
