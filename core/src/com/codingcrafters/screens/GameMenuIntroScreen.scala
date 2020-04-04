package com.codingcrafters.screens

import com.codingcrafters.constants.Constants
import com.badlogic.gdx.{Gdx}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.codingcrafters.gameobjects.{BaseActor, BaseScreen}

object GameMenuIntroScreen extends BaseScreen {
  Gdx.app.log("GameMenuIntroScreen", "game screen created")

  final val IntroPath = "intro/"
  final val SharkPortraitResource = "Mister_Shark_Game_Portrait.png"
  final val startButtonImageResource = "button_start.png"
  final val exitButtonImageResource = "button_exit.png"
  final val gameBackdropImageResource = "ocean_below_backdrop.png"

  val camera: OrthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  val debugRenderer: Box2DDebugRenderer = new Box2DDebugRenderer()
  val batch: SpriteBatch = new SpriteBatch()
  val backgroundTexture = new Texture(Gdx.files.internal(Constants.BACKGROUND_PATH))
  val world: World = new World(Constants.GRAVITY, true)
  var labelGeneralStyle : LabelStyle = new LabelStyle()
  var playButton : ButtonStyle = new ButtonStyle()
  var labelGameTitle : Label = null
  var buttonTexture1 : Texture = null
  var buttonTexture2 : Texture = null
  var textureShark : Texture = null

  def InitialiseUserInterface(): Unit = {
    Gdx.app.log("GameMenuIntroScreen", "InitialiseUserInterface() called")
    labelGeneralStyle.font = new BitmapFont()
    labelGeneralStyle.fontColor = Color.RED
    if (textureShark == null) {
      textureShark = new Texture(Gdx.files.internal(IntroPath + SharkPortraitResource))
    }
    if (buttonTexture1 == null) {
      buttonTexture1 = new Texture(Gdx.files.internal(IntroPath + startButtonImageResource))
    }
    if (buttonTexture2 == null) {
      buttonTexture2 = new Texture(Gdx.files.internal(IntroPath + exitButtonImageResource))
    }
  }

  def drawTitle(titleText : String, ix : Int, iy : Int): Unit = {
    if (titleText != null) {
      labelGameTitle = new Label(titleText, labelGeneralStyle)
      labelGameTitle.setColor(Color.CYAN)
      labelGameTitle.setPosition(iy, ix)
    }
  }

  def drawSharkPortrait(): Unit = {

    //textureShark.draw()
  }

  override def initialize(): Unit = {
    Gdx.app.log("GameMenuIntroScreen", "initialize() called")
    val ocean = new BaseActor(0, 0, mainStage)
    ocean.loadTexture(IntroPath + gameBackdropImageResource)
    ocean.setSize(1200, 1200)
    BaseActor.setWorldBounds(ocean)
  }

  override def update(dt: Float): Unit = {
    Gdx.gl.glClearColor(0.04f, 0.102f, 0.62f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS)

    camera.update()
    batch.begin()
    batch.setProjectionMatrix(camera.combined)

    batch.end()

    if (Constants.DEBUG_INFO_ENABLED) {
      debugRenderer.render(world, camera.combined)
    }
  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

  }

  override def resize(width: Int, height: Int): Unit = {
    camera.viewportWidth = width / Constants.VIEWPORT_SCALE
    camera.viewportHeight = height / Constants.VIEWPORT_SCALE
    camera.update()
  }

  override def show(): Unit = {
    Gdx.app.log("GameMenuIntroScreen", "show the screen")
    InitialiseUserInterface()
  }

  override def resume(): Unit = {
    Gdx.app.log("GameMenuIntroScreen", "resume the screen")
  }

  override def pause(): Unit = {
    Gdx.app.log("GameMenuIntroScreen", "pause the screen")
  }

  override def hide(): Unit = {
    Gdx.app.log("GameMenuIntroScreen", "hide the screen")
  }

  override def dispose(): Unit = {
    world.dispose()
    Gdx.app.log("GameMenuIntroScreen", "game screen disposed")
  }
}
