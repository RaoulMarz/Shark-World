package com.codingcrafters.screens

import java.util.Calendar

import com.badlogic.gdx.Gdx
import com.codingcrafters.gameobjects.{AnglerFish, BaseActor, BaseGame, BaseScreen, DialogBox, DrawAnimationUtility, EnumerationDialogType, EnumerationMoveDirection, SceneActions, SceneSegment, TimePositionTracker, TimePositionTrackerList}

import scala.util.Random
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.graphics.Color
import com.codingcrafters.gameobjects.EnumerationMoveDirection.EnumerationMoveDirection
import com.codingcrafters.gameobjects.prey.{SeaTurtle, Seal}
import com.codingcrafters.operational.{DebugHelper, GeometryUtils, Position, PositionTrack, RandomAssistFunctions, StringAssist}

import scala.collection.mutable.ListBuffer

class GamePlayWildWaters extends BaseScreen {
  Gdx.app.log("GamePlayWildWaters", "Construction, reset of variables")
  protected var mainCharacterImage: String = "/Mister_Shark_Game_Portrait_w380.png"
  protected var mainCharacterImageRight: String = "/Mister_Shark_Game_Portrait_Right_w380.png"
  protected var seagullChar = ""
  protected var sealChar = ""
  protected var backdropImage: String = "/ocean_below_huntersground2_w800.png"
  protected var mainAudioTrack: String = "/sharkwaters_danger_lurks.ogg"
  protected var soundMunch1 = "/sound_munch1.ogg"
  protected var soundMunch2 = "/sound_munch2.ogg"
  protected var background: BaseActor = null
  protected var music_Danger: Music = null
  protected var music_HeeHaw: Music = null
  protected var sharkProwler: BaseActor = null //new BaseActor(0, 0, mainStage)
  protected var sharkProwler2: BaseActor = null //new BaseActor(0, 0, mainStage)
  protected var dialogBoxAnim: DialogBox = null //new DialogBox(0, 0, uiStage)
  protected var alignWavesToCentre : Boolean = false
  protected var allowInGameMenu : Boolean = true
  protected var targetPositionTracker : TimePositionTrackerList = null
  //protected var targetFlipCounter = 1
  protected var directionDriftToggle : Int = 1
  protected var angleRotateRatio: Float = (Math.PI * 0.75).toFloat
  protected var targetDriftPeriods : List[Int] = null
  protected val radialDriftBand1_TicksSpan : (Int, Int) = (45, 78) //Inner circle -- more time change / instability
  protected val radialDriftBand2_TicksSpan : (Int, Int) = (69, 90)
  protected val radialDriftBand3_TicksSpan : (Int, Int) = (88, 105) // Outer circle -- most stable
  protected val innerBand1_ZoneRatio: Float = 0.32f
  protected val innerBand2_ZoneRatio: Float = 0.67f

  protected var waveBreakOffset: Float = 145.0f // 370.0f
  protected var waveBreakCurveOffset1: Float = 138.0f
  protected var waveBreakCurveOffset2: Float = 138.0f
  protected var waveLeftHorizontalOffset: Float = 580.0f
  protected var targetDriftSwitchTicks: (Int, Int) = null
  protected var targetCentrePosition: Position = Position(480.0f, appHeight - waveBreakOffset + 20.0f)

  def initGameAssets() = {
    backdropImage = "/ocean_below_huntersground2_w800.png"
    Gdx.app.log("GamePlayWildWaters", "initGameAssets() started")
    appWidth = Gdx.graphics.getWidth();
    appHeight = Gdx.graphics.getHeight();
    waveBreakOffset = 145.0f // 380.0f
    waveLeftHorizontalOffset = 380.0f
    targetCentrePosition = Position(480.0f, appHeight - waveBreakOffset + 20.0f)
    alignWavesToCentre = true
    allowInGameMenu = true
    directionDriftToggle = 1
    targetDriftPeriods = RandomAssistFunctions.createRandomSequence(16, 37, 2)

    Gdx.app.log("GamePlayWildWaters", s"initGameAssets(), targetDriftPeriods=${DebugHelper.printList("", targetDriftPeriods)}")
    addBagItem("menu_selected_item", "none")
    addNumberItem("target-span-distance", 165.0f)
    addNumberItem("target-vertical-max", 80.0f)
    addNumberItem("target-float-radius", 65.0f)
    targetPositionTracker = new TimePositionTrackerList(500)

    background = new BaseActor(0, 0, mainStage)
    background.loadTexture(assetGamePath + backdropImage)
    background.setSize(appWidth, appHeight)
    background.setOpacity(1.0f)
    BaseActor.setWorldBounds(background)
    Gdx.app.log("GamePlayWildWaters", s"initGameAssets() background=$background")

    if (music_Danger != null) {
      music_Danger.setLooping(true)
      music_Danger.setVolume(0.72f)
      addMusicPlayer("music-danger", music_Danger)
      Gdx.app.log("GamePlayWildWaters", s"initialize() background music = ${music_Danger}")
      music_Danger.play()
    }

    if (music_HeeHaw != null) {
      music_HeeHaw.setLooping(false)
      music_HeeHaw.setVolume(0.65f)
      addMusicPlayer("effect-donkey", music_HeeHaw)
      Gdx.app.log("GamePlayWildWaters", s"initialize() effects music = ${music_HeeHaw}")
    }
  }

  def initProwlerAnimation() = {
    var mainCharacterImage = "/Mister_Shark_Game_Portrait_w380.png"
    var mainCharacterImageRight = "/Mister_Shark_Game_Portrait_Right_w380.png"
    sharkProwler = new BaseActor(0, 0, mainStage)
    sharkProwler2 = new BaseActor(0, 0, mainStage)
    if (sharkProwler != null) {
      sharkProwler.loadTexture(assetGamePath + mainCharacterImage)
      sharkProwler.scaleBy(0.4f, 0.4f)
      sharkProwler.setPosition(-sharkProwler.getWidth + 120.0f, appHeight * 0.825f)

      sharkProwler2.loadTexture(assetGamePath + mainCharacterImageRight)
      sharkProwler.scaleBy(0.4f, 0.4f)
      sharkProwler2.setVisible(false)
      sharkProwler2.setPosition(sharkProwler.getWidth - 120.0f, appHeight * 0.1225f)

      fxAnimations.addStaticAnimation("sharkprowler#Left", sharkProwler)
      fxAnimations.addStaticAnimation("sharkprowler#Right", sharkProwler2)
    } else {
      Gdx.app.log("GameMenuScreen", s"initProwlerAnimation() Error, sharkProwler=$sharkProwler")
    }
  }

  def generateChaoticMovementPath(pathTag : String, actor: BaseActor, numFrames : Int): Unit = {
    if ( (pathTag != null) && (actor != null) ) {

    }
  }

  def initSceneAnimation() = {
    //val dialogBox = new DialogBox(0, 0, uiStage)
    dialogBoxAnim = new DialogBox(0, 0, uiStage)
    if (dialogBoxAnim != null) {
      dialogBoxAnim.setDialogSize(600, 200)
      dialogBoxAnim.setBackgroundColor(new Color(0.6f, 0.6f, 0.8f, 1))
      dialogBoxAnim.setFontScale(0.75f)
      dialogBoxAnim.setVisible(false)
      uiTable.add(dialogBoxAnim).expandX.expandY.bottom
    } else {
      Gdx.app.log("GameMenuScreen", s"initSceneAnimation() Error, dialogBoxAnim=$dialogBoxAnim")
    }

    //Gdx.app.log("GamePlayWildWaters", s"adding scene = $scene to main stage")
    mainStage.addActor(scene)
    scene.addSegment(new SceneSegment(background, Actions.fadeIn(1)))
    scene.addSegment(new SceneSegment(sharkProwler, SceneActions.moveToScreenCenter(4.1f)))
    if (dialogBoxAnim != null) {
      scene.addSegment(new SceneSegment(dialogBoxAnim, Actions.show))
      scene.addSegment(new SceneSegment(dialogBoxAnim, SceneActions.setText("Are you ready to catapult your blood-thirsty rampage? Do play with your food ...")))
    }
    scene.addSegment(new SceneSegment(background, SceneActions.pause))
    scene.addSegment(new SceneSegment(sharkProwler2, SceneActions.moveToOutsideLeft(4.3f)))
    scene.addSegment(new SceneSegment(background, Actions.fadeOut(1)))
    scene.addSegment(new SceneSegment(background, Actions.fadeIn(1.5f)))
    scene.start()
  }

  override def initialize(): Unit = {
    clearBagItems()
    clearMusicPlayers()
    var mainAudioTrack = "/sharkwaters_danger_lurks.ogg"
    music_Danger = Gdx.audio.newMusic(Gdx.files.internal(assetMusicPath + mainAudioTrack))
    addMusicPlayer("game-danger-track", music_Danger)
    addMusicPlayer("pinata-spit", music_HeeHaw)
    Gdx.app.log("GamePlayWildWaters", "initialize() started")
    initGameAssets()

    appWidth = Gdx.graphics.getWidth();
    appHeight = Gdx.graphics.getHeight();
    angleRotateRatio = (Math.PI * 0.75).toFloat
    addBagItem("shark-action", "passive")

    fxAnimations.clear()
    fxAnimations.addStaticAnimation("seaweed#bulbs", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("bubbleWall#1A", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("bubbleWall#2B", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("bubbleWall#3C", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("bubbleWall#4D", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waveMotion#1A", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waveMotion#2B", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waveMotion#3C", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waveMotion#4D", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("kelpplants#1A", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("kelpplants#2B", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("kelpplants#3C", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("reticule#Target", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("centre#Indicator", new BaseActor(0, 0, mainStage))
    //effectAnimations.addStaticAnimation("seagullFlyer", new BaseActor(0, 0, mainStage))

    val kelpLine = 45.0f
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("seaweed#bulbs"), assetGamePath, "/seaweed_bulbs_floor.png", 1.0f, 296.0f, 35.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("reticule#Target"), assetGamePath, "/attack_reticule.png", 1.0f, 296.0f, 35.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("centre#Indicator"), assetGamePath, "/centre_indicator_small.png", 0.9f, targetCentrePosition.x, targetCentrePosition.y)
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("bubbleWall#1A"), assetGamePath, "/bubble_screen#1.png", 0.32f, 200.0f, 150.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("bubbleWall#2B"), assetGamePath, "/bubble_screen#2.png", 0.35f, 500.0f, 250.0f)
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("bubbleWall#3C"), assetGamePath, "/bubble_screen#1.png", 0.35f, 500.0f, 250.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("seagullFlyer"), assetGamePath, "/straight_gull.png", 1.0f, 300.0f, 350.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("waveMotion#1A"), assetGamePath, "/style_wave#1.png", 0.41f, 10.0f, appHeight - 40.0f - (waveBreakOffset + 200.0f))
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("waveMotion#2B"), assetGamePath, "/style_wave#2.png", 0.425f, 0.0f, appHeight - 20.0f - (waveBreakOffset + 200.0f))
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("waveMotion#3C"), assetGamePath, "/style_wave#3.png", 0.495f, 0.0f, appHeight - 45.0f - (waveBreakOffset + 200.0f))
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("waveMotion#4D"), assetGamePath, "/style_wave#4.png", 0.465f, 0.0f, appHeight - 35.0f - (waveBreakOffset + 200.0f))
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("kelpplants#1A"), assetGamePath, "/borealis_strips#1.png", 0.78f, 33.0f, 10.0f + kelpLine)
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("kelpplants#2B"), assetGamePath, "/borealis_strips#2.png", 0.71f, 190.0f, 15.0f + kelpLine)
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("kelpplants#3C"), assetGamePath, "/borealis_strips#1.png", 0.68f, 376.0f, 8.0f + kelpLine)
    //DrawAnimationUtility.showBaseActor(effectAnimations.getAnimation("kelpplants#1A"), false)
    //DrawAnimationUtility.showBaseActor(effectAnimations.getAnimation("kelpplants#2B"), false)
    //DrawAnimationUtility.showBaseActor(effectAnimations.getAnimation("kelpplants#3C"), false)
    DrawAnimationUtility.showActor(fxAnimations.getAnimation("reticule#Target"), false)
    DrawAnimationUtility.showActor(fxAnimations.getAnimation("centre#Indicator"), false)

    DrawAnimationUtility.setActorPositionAndScale(fxAnimations.getAnimation("kelpplants#1A"), 33.0f, 30.0f, 0.4f, 0.38f)
    DrawAnimationUtility.setActorPositionAndScale(fxAnimations.getAnimation("kelpplants#2B"), 190.0f, 24.0f, 0.445f, 0.445f)
    DrawAnimationUtility.setActorPositionAndScale(fxAnimations.getAnimation("kelpplants#3C"), 376.0f, 14.0f, 0.485f, 0.585f)
    DrawAnimationUtility.setActorPositionAndScale(fxAnimations.getAnimation("reticule#Target"), 480.0f, appHeight - 120.0f, 0.6f, 0.6f)

    initProwlerAnimation()

    initSceneAnimation()
  }

  def changeThemeMusic(volume : Float, position : Float): Unit = {
    val musicDanger = getMusicPlayer("music-danger")
    if (musicDanger != null) {
      musicDanger.setVolume(volume)
      if (position >= 0.0f)
        musicDanger.setPosition(position)
    }
  }

  def showGameTitle() = {
    val dialogTitleBox = new DialogBox(120.0f, 75.0f, uiStage, "", EnumerationDialogType.DIALOG_TYPE_TITLE)
    Gdx.app.log("GamePlayWildWaters", s"showGameTitle(), dialogTitleBox = ${dialogTitleBox}")
    dialogTitleBox.setDialogSize(380, 145)
    dialogTitleBox.setBackgroundColor(new Color(0.4f, 0.46f, 0.82f, 1))
    dialogTitleBox.setFontScale(0.75f)
    dialogTitleBox.setPosition(20.0f, appHeight - 25.0f)
    dialogTitleBox.alignTopLeft()
    dialogTitleBox.setText(gameTitleText)
    dialogTitleBox.setVisible(true)

    /*
    Create game sub title -- bite on the ass -- here below the dialogbox title
     */
    uiTable.add(dialogTitleBox).expandX.expandY.top()
  }

  /* Draw the seal and other hunted sea-life that wanders across the surface */
  def drawLinearMovingActor(actor: BaseActor, tagID : String, ticker : Int, totalTicks : Int, verticalMaxDeviate : Float, fromLeft : Boolean): Unit = {
    if (ticker == 0) {
      //"seal-prey-xpos"
      if (fromLeft)
        addNumberItem(tagID, 40.0f)
      else
        addNumberItem(tagID, appWidth - 40.0f)
    }
    if (actor != null) {
      var xPos = actor.getX
      val yPos = actor.getY
    }
  }

  def adjustHorizontalMovement(actor : BaseActor, minSideMove : Float, maxSideMove : Float, reverse : Boolean = false) = {
    if (actor != null) {
      var xPos = actor.getX
      val yPos = actor.getY
      var adjustMove = RandomAssistFunctions.getFloatRandomRange(minSideMove, maxSideMove)
      if (adjustMove >= 30.0f)
        adjustMove = 12.0f
      if (reverse)
        adjustMove = -adjustMove
      xPos = xPos + adjustMove
      if (alignWavesToCentre) {
        xPos += waveLeftHorizontalOffset
      }
      if (xPos <= -30.0f)
        xPos = (math.random * 45.0f).toInt
        if (xPos >= (appWidth / 3.0f))
        xPos = (math.random * (65-15) + 15).toInt
        //xPos = Random.between(15, 65)
      actor.setPosition(xPos + adjustMove, yPos)
    }
  }

  def updateDriftSwitchValues(): Unit = {
    val lowSwitch : Int = radialDriftBand1_TicksSpan._1
    val highSwitch : Int = radialDriftBand1_TicksSpan._2
    if (targetDriftSwitchTicks == null) {
      val listDriftSwitchVals = RandomAssistFunctions.createRandomSequence(lowSwitch, highSwitch, 2)
      if (listDriftSwitchVals != null) {
        targetDriftSwitchTicks = (lowSwitch, highSwitch)
      }
    } else {
      val newSwitchValue = RandomAssistFunctions.getIntRandomNumber(highSwitch - lowSwitch)
      //targetDriftSwitchTicks._1 = targetDriftSwitchTicks.copy(_1 = listDriftSwitchVals(0))
      //targetDriftSwitchTicks._2 = targetDriftSwitchTicks.copy(_2 = listDriftSwitchVals(1))
    }
  }

  def getPositionFromStrategy(diffComps : (Float, Float), targetSpanDistance : Float) : Position = {
    /* Random 1 approach */
    /*
    var randomDove = RandomAssistFunctions.getFloatRandomRange(1.2f, 3.9f)
    if (Math.abs(diffComps._1) >= targetSpanDistance) {
      //Flip the movement direction in order to move towards the idea target centre again
      targetFlipCounter += 1
      if (diffComps._1 < 0)
        randomDove = -randomDove
    }
    var res = Position(randomDove, (1.0f / diffComps._2) * 30.0f )
     */
    var randomDove = RandomAssistFunctions.getFloatRandomRange(1.2f, 3.9f)
    val targetFloatRadius: Float = getNumberItem("target-float-radius")
    var radialAngle : Float = (tickCounter / 360.0f) * angleRotateRatio
    if (radialAngle >= (2 * Math.PI)) {
      val angleWholeRevs : Long  = (radialAngle / (Math.PI * 2).toFloat).toLong
      radialAngle = ((radialAngle / (Math.PI * 2).toFloat) - angleWholeRevs) * (2 * Math.PI.toFloat)
    }
    val coinToss1 = Random.nextBoolean()
    val coinToss2 = Random.nextBoolean()
    var xP: Float = targetFloatRadius * Math.sin(radialAngle).toFloat
    var yP: Float = targetFloatRadius * Math.cos(radialAngle).toFloat
    if (coinToss1)
      xP += randomDove
    if (coinToss2)
      yP += randomDove
    //Gdx.app.log("GamePlayWildWaters", s"getPositionFromStrategy(), tick=$tickCounter, angle=$radialAngle, x=$xP, y=$yP")
    var res = Position(xP, yP)
    res
  }

  def calculateRandomDrift(fromPosition : Position, currentPosition : Position) : Position = {
    var res : Position = Position(0.0f , 0.0f)
    val targetSpanDistance: Float = getNumberItem("target-span-distance")
    val diffComps = GeometryUtils.getComponentDifference(currentPosition, fromPosition)
    val seqFrame = getNumberItem("target-drift-sequence").toInt
    val seqCount = getNumberItem("target-drift-sequence-count").toInt
    /*
    On every completion of a sequence the drift direction flips and the UP and DOWN keys, "Swaps" around
     */
    //if (tickCounter % 20 == 0)
    //  Gdx.app.log("GamePlayWildWaters", s"calculateRandomDrift(), $tickCounter, diffComps = $diffComps")
    /* Change the sign of the drift value -> randomDove) depending on euclidian distance */
    //res = new Position(randomDove, (1.0f / diffComps._2) * 30.0f )
    res = getPositionFromStrategy(diffComps,targetSpanDistance)
    if (targetPositionTracker != null) {
      /*
      val targetPosition = new TimePositionTracker()
      targetPosition.createFromCurrentTime(res.x, res.y)
      targetPositionTracker.add(targetPosition)
      targetPositionTracker.removeEntriesOnLimit(200)
      */
    }
    res
  }

  def inputKeyAdjustDriftRadius(direction : Int): Unit = {
    var targetFloatRadius: Float = getNumberItem("target-float-radius")
    if (direction > 0) {
      targetFloatRadius -= 0.4f
    } else {
      targetFloatRadius += 0.4f
    }
    if (targetFloatRadius >= 120.0f)
      targetFloatRadius = 120.0f
    if (targetFloatRadius <= 1.0f)
      targetFloatRadius = 1.0f
    addNumberItem("target-float-radius", targetFloatRadius)
    Gdx.app.log("GamePlayWildWaters", s"inputKeyAdjustDriftRadius(), targetFloatRadius = $targetFloatRadius")
  }

  def displayReticule(counter : Int, show : Boolean) = {
    DrawAnimationUtility.showActor(fxAnimations.getAnimation("reticule#Target"), show)
    if ( (fxAnimations != null) && (show) ) {
      val Actor1 = fxAnimations.getAnimation("reticule#Target")
      if (Actor1 != null) {
        var xPos = Actor1.getX
        val yPos = Actor1.getY
        val WaveCentre : Position = new Position(appWidth / 2.0f, 310.0f)
        val offsetDrift : Position = calculateRandomDrift(WaveCentre, new Position(xPos, yPos))
        //Gdx.app.log("GamePlayWildWaters", s"displayReticule(), offsetDrift = $offsetDrift")
        Actor1.setPosition(targetCentrePosition.x + offsetDrift.x, targetCentrePosition.y + offsetDrift.y)
        //Actor1.setPosition(xPos + offsetDrift.x, yPos + offsetDrift.y)
      }
    }
  }

  def animateWaves(counter : Int) = {
    if (fxAnimations != null) {
      var reverseDirection1 = false
      val Actor1 = fxAnimations.getAnimation("waveMotion#1A")
      val Actor2 = fxAnimations.getAnimation("waveMotion#2B")
      val Actor3 = fxAnimations.getAnimation("waveMotion#3C")
      val Actor4 = fxAnimations.getAnimation("waveMotion#4D")
      val rotCounter = counter % 1001
      if ( (rotCounter >= 233) && (rotCounter <= (233 + 502)) ) {
        reverseDirection1 = true
      }
      if ((counter >= 10) && (counter % 3 == 1)) {
        adjustHorizontalMovement(Actor1, 3.2f, 11.5f, reverseDirection1)
        adjustHorizontalMovement(Actor3, 4.8f, 14.5f, reverseDirection1)
      }
      if ((counter >= 10) && (counter % 3 == 2)) {
        adjustHorizontalMovement(Actor2, 5.9f, 13.2f, !reverseDirection1)
        adjustHorizontalMovement(Actor4, 4.2f, 17.3f, !reverseDirection1)
      }
    }
  }

  def removeDialogComponent() = {
    if (uiTable != null) {
      uiTable.clearChildren()
    }
  }

  def drawFloatingBubbles(counter : Int) = {

    if (fxAnimations != null) {
      if ( (counter >= 10) && (counter % 2 == 1) ) {
        //Gdx.app.log("GamePlayWildWaters", s"drawFloatingBubbles(), counter = ${counter}")
        val Actor1 = fxAnimations.getAnimation("bubbleWall#1A")
        val Actor2 = fxAnimations.getAnimation("bubbleWall#2B")
        val Actor3 = fxAnimations.getAnimation("bubbleWall#3C")
        if (Actor1 != null)
          Actor1.setPosition(140.0f + (0.825f * (counter % 650)) , appHeight - 280.0f - (0.75f * (counter % 600)) )
        if (Actor2 != null)
          Actor2.setPosition(45.0f + (1.025f * (counter % 540)) , appHeight - 295.0f - (1.15f * (counter % 720)) )
        if (Actor3 != null)
          Actor3.setPosition(20.0f + (1.15f * (counter % 760)) , appHeight - 260.0f - (0.835f * (counter % 900)) )
      }
    } else {
      Gdx.app.log("GamePlayWildWaters", "drawFloatingBubbles(), effectAnimations == null")
    }

  }

  override def update(dt: Float): Unit = {
    if (mainStage != null) {
      val actorsList = mainStage.getActors()
      tickCounter += 1
      if (tickCounter == 420) {
        animateAnglerFish()
      }
      if (tickCounter == 500) {
        changeThemeMusic(0.52f, -5.0f)
      }
      if (tickCounter == 620)
        showDecorativeSeal()
      if (tickCounter == 720)
        showDecorativeTurtle()
      if (tickCounter >= 850) {
        displayReticule(tickCounter, true)
      }
      val inputKeyBagItems: ListBuffer[String] = getBagItemsMatching("key#")
      if (inputKeyBagItems != null) {
        for (inputKey <- inputKeyBagItems) {
          inputKey match {
            case "key#UP" => { } //Up
            case "key#DOWN" => { } //Down
            case "key#59" => { } //
            case "key#60" => { }
            case _ => { }
          }
        }
      }
      val sharkAction = getBagItem("shark-action")
      sharkAction match {
        case "passive" => { }
        case "prowl" => { }
        case _ => { }
      }
      try {
        //if (tickCounter % 100 == 1)
        //  Gdx.app.log("GamePlayWildWaters", s"update() called, tickCounter = $tickCounter")

        if ( (scene != null) && (scene.isSceneFinished) ) {
          addBagItem("shark-action", "prowl")

        }
        drawFloatingBubbles(tickCounter)
        animateWaves(tickCounter)
      } catch {
        case ex: Exception => {
          Gdx.app.log("GamePlayWildWaters", s"update(), scene Exception = ${ex.getMessage}")
        }
      }
    }
  }

  def animateAnglerFish() = {
    val devyFish = new AnglerFish(250, appHeight - 480, mainStage)
    //devyFish.setAnimationPaused(false)
  }

  def showDecorativeSeal(): Unit = {
    val decoSeal = new Seal(390, appHeight - waveBreakOffset + 30.0f, mainStage, 8.0f)
  }

  def showDecorativeTurtle(): Unit = {
    val decoTurtle = new SeaTurtle(735, appHeight - waveBreakOffset - 40.0f, mainStage, 8.0f)
  }

  def returnMainMenu() = {
    BaseGame.setActiveScreen(new GameMenuScreen())
  }

  def moveSharkPlayerLeft(): Unit = {
    val currentPosition : Position = null
    addPositionTrack("shark-left", new PositionTrack(Calendar.getInstance(), currentPosition, tickCounter))
  }

  def moveSharkPlayerRight(): Unit = {
    val currentPosition : Position = null
    addPositionTrack("shark-right", new PositionTrack(Calendar.getInstance(), currentPosition, tickCounter))
  }

  def moveSharkPlayer(keyCode: Int) = {
    keyCode match {
      case Keys.LEFT => { moveSharkPlayerLeft() }
      case Keys.RIGHT => { moveSharkPlayerRight() }
      case _ => {}
    }
  }

  def animateMovingReticule(opacity : Float, horizontalMark : Float) = {

  }

  def driftTargetingTowardsDirection(direction: EnumerationMoveDirection) = {
    val previousTargetDrift : Float = getNumberItem("target_drift")
    val driftValue =  RandomAssistFunctions.getFloatRandomRange(0.75f, 3.5f)
    direction match {
      case EnumerationMoveDirection.MOVE_DIRECTION_LEFT => {
        //Move towards zero on the x-axis
        //val driftValue =  RandomAssistFunctions.getFloatRandomRange(0.75f, 3.5f)
        inputKeyAdjustDriftRadius(-1 * directionDriftToggle)
      }
      case EnumerationMoveDirection.MOVE_DIRECTION_RIGHT => {
        inputKeyAdjustDriftRadius(1 * directionDriftToggle)
      }
      case _ => {}
    }
  }

  override def keyUp(keycode: Int): Boolean = {
    //Cancel the previous active status of the passed keyCode
    false
  }

  def addKeyCodeStore(keyDesc : String, action : String): Unit = {
    Gdx.app.log("GamePlayWildWaters", s"addKeyCodeStore(), keyDesc=$keyDesc")
    addBagItem(keyDesc, "down")
  }

  override def keyDown(keyCode: Int): Boolean = {
    Gdx.app.log("GamePlayWildWaters", s"keyDown(), keyCode = $keyCode , scene = $scene")
    if ((scene != null) && (keyCode == Keys.SPACE || keyCode == Keys.TAB) /*&& continueKey.isVisible*/) {
      Gdx.app.log("GamePlayWildWaters", "loading next segment")
      scene.loadNextSegment()
      removeDialogComponent()
      val sharkProwler = fxAnimations.getAnimation("sharkprowler#Left")
      val sharkProwler2 = fxAnimations.getAnimation("sharkprowler#Right")
      if (sharkProwler != null)
        sharkProwler.setVisible(false)
      if (sharkProwler2 != null)
        sharkProwler2.setVisible(true)
    }
    if (scene != null && keyCode == Keys.ESCAPE) {
      if (allowInGameMenu) {

      }
    }
    if ((scene != null) && (keyCode == Keys.UP || keyCode == Keys.DOWN || keyCode == Keys.LEFT || keyCode == Keys.RIGHT) /*&& continueKey.isVisible*/) {
      Gdx.app.log("GamePlayWildWaters", "using arrow keys")

        if (keyCode == Keys.RIGHT || keyCode == Keys.LEFT) {
          moveSharkPlayer(keyCode)
          addKeyCodeStore("key#" + StringAssist.KeyToString(keyCode), "down")
          //addBagItem("key#" + StringAssist.KeyToString(keyCode), "down")
        }
        if (keyCode == Keys.DOWN || keyCode == Keys.UP) {
          addKeyCodeStore("key#" + StringAssist.KeyToString(keyCode), "down")

          var directionMove = EnumerationMoveDirection.MOVE_DIRECTION_LEFT
          if (keyCode == Keys.DOWN)
            directionMove = EnumerationMoveDirection.MOVE_DIRECTION_RIGHT
          driftTargetingTowardsDirection(directionMove)
        }

    }
    if ((scene != null) && (keyCode == Keys.ENTER) ) {
      //Perform some action based on what shark-action state the game is in

    }
    false
  }
}
