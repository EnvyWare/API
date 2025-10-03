# API 
[![Join our Discord](https://img.shields.io/discord/831966641586831431.svg?logo=discord&label=)](https://discord.envyware.co.uk) 
[![Developer Releases](https://maven.envyware.co.uk/api/badge/latest/releases/com/envyful/api/commons?color=40c14a&prefix=v&name=API)]([https://jitpack.io/#Pixelmon-Development/API](https://maven.envyware.co.uk/#/releases/com/envyful/api)) 
[![GitHub](https://img.shields.io/github/license/EnvyWare/API)](https://www.gnu.org/licenses/lgpl-3.0.html) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/5ebf0eeeb79a4d19b6932b04da2a13ec)](https://app.codacy.com/gh/EnvyWare/API/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

This repository will contain a multi-project Gradle setup for Minecraft APIs. (Spigot, Forge, Sponge, Velocity, Bungee etc.)

Join the discord for further assitance.

# How to use

You will need to add the following to your build.gradle in the repositories section:

```groovy
  maven {
       url "https://maven.envyware.co.uk/releases"
  }
```

Then in the dependencies section you'll need to add

```groovy
  shadow "com.envyful.api:commons:7.2.8"
```

My API is designed to be shaded into the JAR file so if you need more information on how to do that please find it [here](https://gradleup.com/shadow/)
You can also use it with the Forge IDE runs due to some recent changes.

The latest version of my API can be found in my [Discord server](https://discord.envyware.co.uk/) in the #api-changelog channel. This channel is where I post detailed information about what changed between each version
