# Grid Optimizer
A web based solution for calculation district heating networks. You can use it [online](https://grid-optimizer.herokuapp.com/). 

For more information visit our [wiki](https://github.com/fhac-ewi/grid-optimizer/wiki).

## Installation
**No installation required** if you want to use our [online](https://grid-optimizer.herokuapp.com/) version. For self hosted solutions or contributing visit usage section.


## Usage
### Online
You can use this project [online](https://grid-optimizer.herokuapp.com/) without any development experience. 

### Self hosted
1. Download our latest release from [here](https://github.com/fhac-ewi/grid-optimizer/releases).
2. Make sure you have Java 1.8 or higher installed on your maschine.
3. Open a Terminal Window. (on Windows PowerShell is a solid option, or you can go with [Git Bash](https://git-scm.com/downloads))
4. Run `cd <absolute-folder-path>` to enter the folder downloaded in step 1.
5. Run `java -jar server.jar` to start the server. Hopefully it spins up on http://localhost:80 after a few seconds.
6. To stop the program hit `CTRL+C`.


### For Developers
Glad you are here. We might have a perfect [Issue](https://github.com/fhac-ewi/grid-optimizer/issues) for you. Feel free to contribute!
0. Make sure you know about [React](https://reactjs.org/) or [Kotlin](https://kotlinlang.org/). Follow their instructions how to set up your machine for general development with these languages. 
1. Clone this repository. You might [fork](https://docs.github.com/en/github/getting-started-with-github/fork-a-repo) this repository first. 
2. Open a Terminal in the downloaded folder.
3. Run `./gradlew build` to build this project. The React frontend needs Yarn (I guess) to build.
4. Now everything should be working for development. 
   1. API Server: Run `gradlew run` to run the api in development mode on http://localhost:8080
   2. Frontend: Run `yarn start` in subfolder `src-frontend` (?) to run the frontend on http://localhost:8081
   3. Hot Reload: Run `gradlew installDist` to enable hot reload for the api server. The Frontend should already be fine. 
5. Submit your work as pull request!

## Contributing
Feel free to do so. We might have many open [Issues](https://github.com/fhac-ewi/grid-optimizer/issues).

## Credits


## License
See [here](./LICENSE).

I selected MIT. Is that good? How can I change it? 
