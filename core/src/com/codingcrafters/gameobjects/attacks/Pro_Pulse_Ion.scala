package com.codingcrafters.gameobjects.attacks

import com.badlogic.gdx.scenes.scene2d.Stage
import com.codingcrafters.gameobjects.BaseActor

class Pro_Pulse_Ion(override val xP: Float, override val yP: Float, override val s: Stage) extends BaseActor(xP, yP, s) {
  private var assetGamePath = "game"
  loadAnimationFromSheet(assetGamePath + "/tiles_propulsion.png", 2, 5, 0.1f, false)

  override def act(dt: Float): Unit = {
    super.act(dt)
    if (isAnimationFinished) remove
  }
}