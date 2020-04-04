package com.codingcrafters.gameobjects.prey

import com.badlogic.gdx.scenes.scene2d.Stage
import com.codingcrafters.gameobjects.BaseActor

class SeaTurtle(override val xP: Float, override val yP: Float, override val s: Stage, val duration : Float = 0.0f) extends BaseActor(xP, yP, s) {
    private var assetGamePath = "game"
    //loadAnimationFromSheet(assetGamePath + "/seal_swim_tiles.png", 2, 2, 1.8f, true)
    loadAnimationFromSheetScaled(assetGamePath + "/sea_turtle_tiles.png", 2, 2, 0.9f, true, 0.475f, 0.475f, duration)

    override def act(dt: Float): Unit = {
        super.act(dt)
        if (isAnimationFinished) remove
    }
}