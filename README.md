# Networked Tic-Tac-Toe

## Overview

Networked Tic-Tac-Toe is a Java-based multiplayer game that uses socket networking for communication between a server and two clients. The game interface is built using JavaFX, providing a rich graphical user interface for players.

This was a project developed for the purpose of learning about networking and JavaFX UI in Java. 

The game follows the standard rules of Tic-Tac-Toe, where two players take turns placing their marks (X or O) on a 3x3 grid. The first player to get three of their marks in a row (horizontally, vertically, or diagonally) wins the game. If the grid is full and no player has won, the game ends in a draw.

## Features

- **Multiplayer Support**: Connect multiple clients to a single server.
- **Socket Networking**: Uses Java sockets for client-server communication.
- **JavaFX Interface**: Provides a user-friendly graphical interface.
- **Rematch Option**: Players can choose to play a rematch after a game ends.

## Prerequisites

- Java 21 or higher
- Maven
- JavaFX 21

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/Andre-lucs/NetworkedTicTacToe.git
cd NetworkedTicTacToe
```

### Build the Project

#### Using the maven wrapper

- Windows ```./mvnw.cmd clean install```
- Linux ```./mvnw clean install```

#### Using the maven installed on your machine

```sh
mvn clean install
```

### Run the Application

#### Using the maven wrapper

- Windows ```./mvnw.cmd javafx:run```
- Linux ```./mvnw javafx:run```

#### Using the maven installed on your machine

```sh
mvn javafx:run
```

### Start the Server

Click on the `Ceate server` button to start the server on your machine.

### Connect Clients

Click on the `Connect` button passing the server ip and port to connect clients to the server. The server must be running before clients can connect.

## Project Structure

- `src/main/java/org/andrelucs/networkedtictactoe`: Contains the main application and controller classes.
- `src/main/resources/org/andrelucs/networkedtictactoe`: Contains the FXML files for the JavaFX UI.

## Key Classes

### Server

Handles client connections and game logic.

#### Messages
- `ROLE`: Sends to the client if it will be the cross or the circle.
- `START`: Starts the match.
- `MOVE`: Sends the moves made.
- `END`: Finishes a match.
- `REMATCH`: Asks if the players want a rematch.
- `WARN`: Sends warnings to the clients.
- `EXIT`: When a player logout or the server is stopped.


### Client

Handles communication with the server.

#### Messages
- `MOVE`: Makes a movement request.
- `REMATCH`: Accepts a rematch.
- `EXIT`: Client exits.

### MainController

Manages the main game logic and UI interactions.

