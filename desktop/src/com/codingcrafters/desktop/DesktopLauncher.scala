package com.codingcrafters.desktop

import com.badlogic.gdx.Game
import com.codingcrafters.prime.HungrySharkGame
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

object DesktopLauncher {

  def main(args: Array[String]): Unit = { // To start a LibGDX program, this method:
    // (1) creates an instance of the game
    // (2) creates a new application with game instance and window settings as argument
    val config: LwjglApplicationConfiguration = new LwjglApplicationConfiguration
    config.resizable = true
    config.useGL30 = false
    config.width = 1024
    config.height = 768

    val myGame = new HungrySharkGame()
    val launcher = new LwjglApplication(myGame, "Top of the food chain to you", config.width, config.height /*800, 600*/)
  }
}

