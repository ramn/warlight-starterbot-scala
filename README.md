A Scala starter bot for the Warlight AI Challenge
=================================================

The challenge resides at http://theaigames.com/competitions/warlight-ai-challenge


Build
=====
Requires scala 2.10 and sbt 0.13.1.

In your console, run:

    sbt stage

Then an executable will be created at

    ./target/universal/stage/bin/scala-starter-bot

This could be used for testing the game locally, provided you have the conquest-engine.
The conquest-engine resides at https://github.com/theaigames/conquest-engine

The executable will communicate with stdin and stdout/stderr, as the game engine requires.


Run
===
Start the bot by executing:

    ./target/universal/stage/bin/scala-starter-bot

connecting its stdin and stdout to the game engine.
