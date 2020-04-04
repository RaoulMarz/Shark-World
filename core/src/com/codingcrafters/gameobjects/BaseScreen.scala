package com.codingcrafters.gameobjects

import com.badlogic.gdx.audio.Music

import scala.collection.mutable.{ListBuffer, Map}
import com.badlogic.gdx.{Gdx, InputMultiplexer, InputProcessor, Screen}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.codingcrafters.operational.{EventRecord, PositionTrack}

/*abstract*/ class BaseScreen() extends Screen with InputProcessor {
  val assetFontsPath: String = "fonts"
  val assetUXPath: String = "ux"
  val assetGamePath: String = "game"
  val assetMusicPath: String = "music"
  val gameTitleText: String = "Top of the food chain to you (WIP)"
  var appWidth = 0
  var appHeight = 0
  var tickCounter = 1
  var menuMode = false
  var storeBag: Map[String, String] = Map.empty[String, String]
  var numberBag: Map[String, Float] = Map.empty[String, Float]
  var eventRecordMap: Map[String, EventRecord] = Map.empty[String, EventRecord]
  var positionTrackerMap: Map[String, PositionTrack] = Map.empty[String, PositionTrack]
  var musicPlayers: Map[String, Music] = Map.empty[String, Music]

  protected var scene: Scene = new Scene
  protected var mainStage: Stage = new Stage
  protected var uiStage: Stage = new Stage
  protected var uiTable: Table = new Table
  //protected var screenMusicPlayer = Gdx.audio.newMusic()
  protected var musicDirector: MusicPlayList = new MusicPlayList(assetMusicPath)
  protected var fxAnimations: StaticAnimationStore = new StaticAnimationStore
  uiTable.setFillParent(true)
  uiStage.addActor(uiTable)
  initialize()

  def initialize(): Unit = {

  }

  def update(dt: Float): Unit = {

  }

  def addBagItem(key: String, value : String, replace : Boolean = true) = {
    if ((key != null) && (storeBag.keySet.contains(key) == false) ) {
      storeBag += (key -> value)
    } else if (replace) {
      storeBag -= key
      storeBag += (key -> value)
    }
  }

  def getBagItem(key : String) = {
    var result : String = ""
    if ((key != null) && (storeBag.keySet.contains(key)) )
      result = storeBag(key)
    result
  }

  def getBagItemsMatching(matchFilter : String): ListBuffer[String] = {
    var result : ListBuffer[String] = null
    if (storeBag.isEmpty == false) {
      result = ListBuffer[String]()
      for (key <- storeBag.keySet) {
        if (key.contains(matchFilter))
          result += key
      }
    }
    return result
  }

  def clearBagItems() = {
    if (storeBag != null) {
      storeBag.clear()
    }
  }

  def addPositionTrack(key: String, value : PositionTrack, replace : Boolean = true): Unit = {
    if ((key != null) && (positionTrackerMap.keySet.contains(key) == false) ) {
      positionTrackerMap += (key -> value)
    } else if (replace) {
      positionTrackerMap -= key
      positionTrackerMap += (key -> value)
    }
  }

  def getPositionTrack(key : String): PositionTrack = {
    var result : PositionTrack = null
    if ((key != null) && (positionTrackerMap.keySet.contains(key)) )
      result = positionTrackerMap(key)
    result
  }

  def addNumberItem(key: String, value : Float, replace : Boolean = true) = {
    if ((key != null) && (storeBag.keySet.contains(key) == false) ) {
      numberBag += (key -> value)
    } else if (replace) {
      numberBag -= key
      numberBag += (key -> value)
    }
  }

  def getNumberItem(key : String) = {
    var result : Float = 0.0f
    if ((key != null) && (numberBag.keySet.contains(key)) )
      result = numberBag(key)
    result
  }

  def clearNumberItems() = {
    if (numberBag != null) {
      numberBag.clear()
    }
  }

  def addMusicPlayer(key: String, musPlayer: Music, replace : Boolean = true) = {
    if ((key != null) && (musicPlayers.keySet.contains(key) == false) ) {
      musicPlayers += (key -> musPlayer)
    } else if (replace) {
      musicPlayers -= key
      musicPlayers += (key -> musPlayer)
    }
  }

  def getMusicPlayer(key : String) : Music = {
    var result : Music = null
    if ((key != null) && (musicPlayers.keySet.contains(key)) )
      result = musicPlayers(key)
    result
  }

  def clearMusicPlayers() = {
    if (musicPlayers != null) {
      musicPlayers.clear()
    }
  }

  def addEventRecord(key : String, record: EventRecord, replace : Boolean = true): Unit = {
    if ((key != null) && (eventRecordMap.keySet.contains(key) == false) ) {
      eventRecordMap += (key -> record)
    } else if (replace) {
      eventRecordMap -= key
      eventRecordMap += (key -> record)
    }
  }

  def getEventRecord(key : String): EventRecord = {
    var result : EventRecord = null
    if ((key != null) && (eventRecordMap.keySet.contains(key)) )
      result = eventRecordMap(key)
    result
  }

  def clearEventRecords() = {
    if (eventRecordMap != null) {
      eventRecordMap.clear()
    }
  }

  // Gameloop:
  // (1) process input (discrete handled by listener; continuous in update)
  // (2) update game logic
  // (3) render the graphics
  override def render(dt: Float): Unit = { // act methods
    uiStage.act(dt)
    mainStage.act(dt)
    // defined by user
    update(dt)
    // clear the screen
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    // draw the graphics
    mainStage.draw()
    uiStage.draw()
  }

  // methods required by Screen interface
  override def resize(width: Int, height: Int): Unit = {
  }

  override def pause(): Unit = {
  }

  override def resume(): Unit = {
  }

  override def dispose(): Unit = {
  }

  /**
   * Called when this becomes the active screen in a Game.
   * Set up InputMultiplexer here, in case screen is reactivated at a later time.
   */
  override def show(): Unit = {
    val im = Gdx.input.getInputProcessor.asInstanceOf[InputMultiplexer]
    im.addProcessor(this)
    im.addProcessor(uiStage)
    im.addProcessor(mainStage)
  }

  /**
   * Called when this is no longer the active screen in a Game.
   * Screen class and Stages no longer process input.
   * Other InputProcessors must be removed manually.
   */
  override def hide(): Unit = {
    val im = Gdx.input.getInputProcessor.asInstanceOf[InputMultiplexer]
    im.removeProcessor(this)
    im.removeProcessor(uiStage)
    im.removeProcessor(mainStage)
  }

  // methods required by InputProcessor interface
  override def keyDown(keycode: Int) = false

  override def keyUp(keycode: Int) = false

  override def keyTyped(c: Char) = false

  override def mouseMoved(screenX: Int, screenY: Int) = false

  override def scrolled(amount: Int) = false

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
}