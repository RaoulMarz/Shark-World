package com.codingcrafters.prime

import com.codingcrafters.gameobjects.BaseGame
import com.codingcrafters.screens.GameMenuScreen
import com.codingcrafters.screens.GameScreen

class HungrySharkGame extends BaseGame {
  def myGameScreen : GameMenuScreen = new GameMenuScreen()
  //def myTestGame : GameScreen = new GameScreen()

  override def create(): Unit = {
    super.create()
    setActiveScreen(myGameScreen)
    //setActiveScreen(myTestGame)
  }
}