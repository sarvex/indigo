package indigo.facades.worker

import indigo.shared.events.GlobalEvent
import indigo.shared.assets.AssetName
import indigo.shared.animation.Animation
import indigo.shared.animation.AnimationAction
import indigo.shared.animation.AnimationKey
import indigo.shared.animation.Cycle
import indigo.shared.animation.CycleLabel
import indigo.shared.animation.Frame
import indigo.shared.datatypes.Material
import indigo.shared.datatypes.Texture
import indigo.shared.datatypes.Rectangle
import indigo.shared.datatypes.Point
import indigo.shared.datatypes.Vector2
import indigo.shared.datatypes.Radians
import indigo.shared.datatypes.Depth
import indigo.shared.datatypes.FontInfo
import indigo.shared.datatypes.FontSpriteSheet
import indigo.shared.datatypes.FontChar
import indigo.shared.datatypes.RGBA
import indigo.shared.datatypes.RGB
import indigo.shared.datatypes.Flip
import indigo.shared.datatypes.TextAlignment
import indigo.shared.datatypes.Effects
import indigo.shared.datatypes.Border
import indigo.shared.datatypes.Thickness
import indigo.shared.datatypes.Glow
import indigo.shared.datatypes.Overlay
import indigo.shared.datatypes.BindingKey
import indigo.shared.datatypes.FontKey
import indigo.shared.platform.SceneFrameData
import indigo.shared.platform.AssetMapping
import indigo.shared.platform.TextureRefAndOffset
import indigo.shared.time.GameTime
import indigo.shared.time.Millis
import indigo.shared.time.Seconds
import indigo.shared.scenegraph.SceneUpdateFragment
import indigo.shared.scenegraph.SceneAudio
import indigo.shared.scenegraph.SceneAudioSource
import indigo.shared.scenegraph.PlaybackPattern
import indigo.shared.scenegraph.SceneLayer
import indigo.shared.scenegraph.ScreenEffects
import indigo.shared.scenegraph.Light
import indigo.shared.scenegraph.PointLight
import indigo.shared.scenegraph.SpotLight
import indigo.shared.scenegraph.DirectionLight
import indigo.shared.scenegraph.CloneBlank
import indigo.shared.scenegraph.Graphic
import indigo.shared.scenegraph.Group
import indigo.shared.scenegraph.Sprite
import indigo.shared.scenegraph.Text
import indigo.shared.scenegraph.Transformer
import indigo.shared.scenegraph.Clone
import indigo.shared.scenegraph.CloneId
import indigo.shared.scenegraph.CloneBatch
import indigo.shared.scenegraph.CloneTransformData
import indigo.shared.collections.NonEmptyList
import indigo.shared.audio.Volume
import indigo.shared.audio.Track
import indigo.shared.datatypes.mutable.CheapMatrix4

import scala.scalajs.js
import scalajs.js.JSConverters._

object SceneFrameDataConversion {

  def toJS(sceneFrameData: SceneFrameData): js.Any =
    js.Dynamic.literal(
      gameTime = GameTimeConversion.toJS(sceneFrameData.gameTime),
      scene = SceneUpdateFragmentConversion.toJS(sceneFrameData.scene),
      assetMapping = sceneFrameData.assetMapping.mappings.map((k, v) => (k, TextureRefAndOffsetConversion.toJS(v))).toJSDictionary,
      screenWidth = sceneFrameData.screenWidth,
      screenHeight = sceneFrameData.screenHeight,
      orthographicProjectionMatrix = sceneFrameData.orthographicProjectionMatrix.mat.toJSArray
    )

  def fromJS(obj: js.Any): SceneFrameData =
    fromSceneFrameDataJS(obj.asInstanceOf[SceneFrameDataJS])

  def fromSceneFrameDataJS(res: SceneFrameDataJS): SceneFrameData =
    SceneFrameData(
      gameTime = GameTimeConversion.fromGameTimeJS(res.gameTime),
      scene = SceneUpdateFragmentConversion.fromSceneUpdateFragmentJS(res.scene),
      assetMapping = AssetMapping(res.assetMapping.toMap.map((k, v) => (k, TextureRefAndOffsetConversion.fromTextureRefAndOffsetJS(v)))),
      screenWidth = res.screenWidth,
      screenHeight = res.screenHeight,
      orthographicProjectionMatrix = CheapMatrix4(res.orthographicProjectionMatrix.toArray)
    )

  object GameTimeConversion {

    def toJS(gameTime: GameTime): js.Any =
      js.Dynamic.literal(
        running = gameTime.running.value,
        delta = gameTime.delta.value,
        targetFPS = gameTime.targetFPS.value
      )

    def fromJS(obj: js.Any): GameTime =
      fromGameTimeJS(obj.asInstanceOf[GameTimeJS])

    def fromGameTimeJS(res: GameTimeJS): GameTime =
      GameTime(
        running = Seconds(res.running),
        delta = Seconds(res.delta),
        targetFPS = GameTime.FPS(res.targetFPS)
      )

  }

  object TextureRefAndOffsetConversion {

    def toJS(textureRefAndOffset: TextureRefAndOffset): js.Any =
      js.Dynamic.literal(
        atlasName = textureRefAndOffset.atlasName,
        atlasSize = Vector2Conversion.toJS(textureRefAndOffset.atlasSize),
        offset = PointConversion.toJS(textureRefAndOffset.offset)
      )

    def fromJS(obj: js.Any): TextureRefAndOffset =
      fromTextureRefAndOffsetJS(obj.asInstanceOf[TextureRefAndOffsetJS])

    def fromTextureRefAndOffsetJS(res: TextureRefAndOffsetJS): TextureRefAndOffset =
      TextureRefAndOffset(
        atlasName = res.atlasName,
        atlasSize = Vector2Conversion.fromVector2JS(res.atlasSize),
        offset = PointConversion.fromPointJS(res.offset)
      )

  }

}

object SceneUpdateFragmentConversion {

  def toJS(suf: SceneUpdateFragment): js.Any =
    js.Dynamic.literal(
      gameLayer = SceneLayerConversion.toJS(suf.gameLayer),
      lightingLayer = SceneLayerConversion.toJS(suf.lightingLayer),
      distortionLayer = SceneLayerConversion.toJS(suf.distortionLayer),
      uiLayer = SceneLayerConversion.toJS(suf.uiLayer),
      ambientLight = RGBAConversion.toJS(suf.ambientLight),
      lights = suf.lights.map(LightConversion.toJS).toJSArray,
      audio = SceneAudioConversion.toJS(suf.audio),
      screenEffects = ScreenEffectsConversion.toJS(suf.screenEffects),
      cloneBlanks = suf.cloneBlanks.map(CloneBlankConversion.toJS).toJSArray
    )

  def fromJS(obj: js.Any): SceneUpdateFragment =
    fromSceneUpdateFragmentJS(obj.asInstanceOf[SceneUpdateFragmentJS])

  def fromSceneUpdateFragmentJS(res: SceneUpdateFragmentJS): SceneUpdateFragment =
    SceneUpdateFragment(
      gameLayer = SceneLayerConversion.fromSceneLayerJS(res.gameLayer),
      lightingLayer = SceneLayerConversion.fromSceneLayerJS(res.lightingLayer),
      distortionLayer = SceneLayerConversion.fromSceneLayerJS(res.distortionLayer),
      uiLayer = SceneLayerConversion.fromSceneLayerJS(res.uiLayer),
      ambientLight = RGBAConversion.fromRGBAJS(res.ambientLight),
      lights = res.lights.toList.map(LightConversion.fromLightJS),
      audio = SceneAudioConversion.fromSceneAudioJS(res.audio),
      screenEffects = ScreenEffectsConversion.fromScreenEffectsJS(res.screenEffects),
      cloneBlanks = res.cloneBlanks.toList.map(CloneBlankConversion.fromCloneBlankJS)
    )

  object SceneLayerConversion {

    def toJS(sceneLayer: SceneLayer): js.Any =
      js.Dynamic.literal(
        nodes = sceneLayer.nodes.map {
          case g: Graphic     => GraphicConversion.toJS(g)
          case g: Group       => GroupConversion.toJS(g)
          case s: Sprite      => SpriteConversion.toJS(s)
          case t: Text        => TextConversion.toJS(t)
          case c: Clone       => CloneConversion.toJS(c)
          case c: CloneBatch  => CloneBatchConversion.toJS(c)
          case t: Transformer => null
        }.toJSArray,
        tint = RGBAConversion.toJS(sceneLayer.tint),
        saturation = sceneLayer.saturation,
        magnification = sceneLayer.magnification.orUndefined
      )

    def fromJS(obj: js.Any): SceneLayer =
      fromSceneLayerJS(obj.asInstanceOf[SceneLayerJS])

    val nodeTypes: List[String] = List("graphic", "group", "sprite", "text", "clone", "clone batch")

    def fromSceneLayerJS(res: SceneLayerJS): SceneLayer =
      SceneLayer(
        nodes = res.nodes.toList.filterNot(nodeTypes.contains).map {
          case node: SceneGraphNodeJS if node._type == "graphic"     => GraphicConversion.fromJS(node)
          case node: SceneGraphNodeJS if node._type == "group"       => GroupConversion.fromJS(node)
          case node: SceneGraphNodeJS if node._type == "sprite"      => SpriteConversion.fromJS(node)
          case node: SceneGraphNodeJS if node._type == "text"        => TextConversion.fromJS(node)
          case node: SceneGraphNodeJS if node._type == "clone"       => CloneConversion.fromJS(node)
          case node: SceneGraphNodeJS if node._type == "clone batch" => CloneBatchConversion.fromJS(node)
        },
        tint = RGBAConversion.fromRGBAJS(res.tint),
        saturation = res.saturation,
        magnification = res.magnification.toOption
      )

  }

  object RGBConversion {

    def toJS(rgb: RGB): js.Any =
      js.Dynamic.literal(
        r = rgb.r,
        g = rgb.g,
        b = rgb.b
      )

    def fromJS(obj: js.Any): RGB =
      fromRGBJS(obj.asInstanceOf[RGBJS])

    def fromRGBJS(rgbJS: RGBJS): RGB =
      RGB(rgbJS.r, rgbJS.g, rgbJS.b)

  }

  object RGBAConversion {

    def toJS(rgba: RGBA): js.Any =
      js.Dynamic.literal(
        r = rgba.r,
        g = rgba.g,
        b = rgba.b,
        a = rgba.a
      )

    def fromJS(obj: js.Any): RGBA =
      fromRGBAJS(obj.asInstanceOf[RGBAJS])

    def fromRGBAJS(rgbaJS: RGBAJS): RGBA =
      RGBA(rgbaJS.r, rgbaJS.g, rgbaJS.b, rgbaJS.a)

  }

  object LightConversion {

    def toJS(light: Light): js.Any =
      light match {
        case PointLight(position, height, color, power, attenuation) =>
          js.Dynamic.literal(
            _type = "point",
            position = PointConversion.toJS(position),
            height = height,
            color = RGBConversion.toJS(color),
            power = power,
            attenuation = attenuation
          )

        case SpotLight(position, height, color, power, attenuation, angle, rotation, near, far) =>
          js.Dynamic.literal(
            _type = "spot",
            position = PointConversion.toJS(position),
            height = height,
            color = RGBConversion.toJS(color),
            power = power,
            attenuation = attenuation,
            angle = angle.value,
            rotation = rotation.value,
            near = near,
            far = far
          )

        case DirectionLight(height, color, power, rotation) =>
          js.Dynamic.literal(
            _type = "direction",
            height = height,
            power = power,
            color = RGBConversion.toJS(color),
            rotation = rotation.value
          )
      }

    def fromJS(obj: js.Any): Light =
      fromLightJS(obj.asInstanceOf[LightJS])

    def fromLightJS(res: LightJS): Light =
      res._type match {
        case "point" =>
          PointLight(
            PointConversion.fromPointJS(res.position),
            res.height,
            RGBConversion.fromRGBJS(res.color),
            res.power,
            res.attenuation
          )

        case "spot" =>
          SpotLight(
            PointConversion.fromPointJS(res.position),
            res.height,
            RGBConversion.fromRGBJS(res.color),
            res.power,
            res.attenuation,
            Radians(res.angle),
            Radians(res.rotation),
            res.near,
            res.far
          )

        case "direction" =>
          DirectionLight(
            res.height,
            RGBConversion.fromRGBJS(res.color),
            res.power,
            Radians(res.rotation)
          )
      }

  }

  object SceneAudioConversion {

    def toJS(sceneAudio: SceneAudio): js.Any =
      js.Dynamic.literal(
        sourceA = SceneAudioSourceConversion.toJS(sceneAudio.sourceA),
        sourceB = SceneAudioSourceConversion.toJS(sceneAudio.sourceB),
        sourceC = SceneAudioSourceConversion.toJS(sceneAudio.sourceC)
      )

    def fromJS(obj: js.Any): SceneAudio =
      fromSceneAudioJS(obj.asInstanceOf[SceneAudioJS])

    def fromSceneAudioJS(res: SceneAudioJS): SceneAudio =
      SceneAudio(
        sourceA = SceneAudioSourceConversion.fromSceneAudioSourceJS(res.sourceA),
        sourceB = SceneAudioSourceConversion.fromSceneAudioSourceJS(res.sourceB),
        sourceC = SceneAudioSourceConversion.fromSceneAudioSourceJS(res.sourceC)
      )

    object SceneAudioSourceConversion {

      def toJS(source: SceneAudioSource): js.Any =
        source.playbackPattern match {
          case PlaybackPattern.Silent =>
            js.Dynamic.literal(
              _type = "silent",
              bindingKey = source.bindingKey.value,
              masterVolume = source.masterVolume.amount
            )

          case PlaybackPattern.SingleTrackLoop(track) =>
            js.Dynamic.literal(
              _type = "single",
              bindingKey = source.bindingKey.value,
              masterVolume = source.masterVolume.amount,
              assetName = track.assetName.value,
              volume = track.volume.amount
            )
        }

      def fromJS(obj: js.Any): SceneAudioSource =
        fromSceneAudioSourceJS(obj.asInstanceOf[SceneAudioSourceJS])

      def fromSceneAudioSourceJS(res: SceneAudioSourceJS): SceneAudioSource =
        SceneAudioSource(
          bindingKey = BindingKey(res.bindingKey),
          playbackPattern = res._type match {
            case "silent" => PlaybackPattern.Silent
            case "single" => PlaybackPattern.SingleTrackLoop(Track(AssetName(res.assetName), Volume(res.volume)))
          },
          masterVolume = Volume(res.masterVolume)
        )

    }

  }

  object ScreenEffectsConversion {

    def toJS(screenEffects: ScreenEffects): js.Any =
      js.Dynamic.literal(
        gameColorOverlay = RGBAConversion.toJS(screenEffects.gameColorOverlay),
        uiColorOverlay = RGBAConversion.toJS(screenEffects.uiColorOverlay)
      )

    def fromJS(obj: js.Any): ScreenEffects =
      fromScreenEffectsJS(obj.asInstanceOf[ScreenEffectsJS])

    def fromScreenEffectsJS(res: ScreenEffectsJS): ScreenEffects =
      ScreenEffects(
        gameColorOverlay = RGBAConversion.fromRGBAJS(res.gameColorOverlay),
        uiColorOverlay = RGBAConversion.fromRGBAJS(res.uiColorOverlay)
      )

  }

  object CloneBlankConversion {

    def toJS(cloneBlank: CloneBlank): js.Any =
      js.Dynamic.literal(
        id = cloneBlank.id.value,
        cloneable = cloneBlank.cloneable match {
          case s: Sprite  => SpriteConversion.toJS(s)
          case g: Graphic => GraphicConversion.toJS(g)
        }
      )

    def fromJS(obj: js.Any): CloneBlank =
      fromCloneBlankJS(obj.asInstanceOf[CloneBlankJS])

    def fromCloneBlankJS(res: CloneBlankJS): CloneBlank =
      CloneBlank(
        id = CloneId(res.id),
        cloneable = res.cloneable._type match {
          case "graphic" =>
            GraphicConversion.fromJS(res.cloneable)

          case "sprite" =>
            SpriteConversion.fromJS(res.cloneable)
        }
      )

  }

  object CloneConversion {

    def toJS(node: Clone): js.Any =
      js.Dynamic.literal(
        _type = "clone",
        id = node.id.value,
        depth = node.depth.zIndex,
        transform = CloneTransformDataConversion.toJS(node.transform)
      )

    def fromJS(obj: js.Any): Clone =
      fromCloneJS(obj.asInstanceOf[CloneJS])

    def fromCloneJS(res: CloneJS): Clone =
      Clone(
        id = CloneId(res.id),
        depth = Depth(res.depth),
        transform = CloneTransformDataConversion.fromCloneTransformDataJS(res.transform)
      )

  }

  object CloneBatchConversion {

    def toJS(node: CloneBatch): js.Any =
      js.Dynamic.literal(
        _type = "clone batch",
        id = node.id.value,
        depth = node.depth.zIndex,
        transform = CloneTransformDataConversion.toJS(node.transform),
        clones = node.clones.map(CloneTransformDataConversion.toJS).toJSArray,
        staticBatchKey = node.staticBatchKey.map(_.value).orUndefined
      )

    def fromJS(obj: js.Any): CloneBatch =
      fromCloneBatchJS(obj.asInstanceOf[CloneBatchJS])

    def fromCloneBatchJS(res: CloneBatchJS): CloneBatch =
      CloneBatch(
        id = CloneId(res.id),
        depth = Depth(res.depth),
        transform = CloneTransformDataConversion.fromCloneTransformDataJS(res.transform),
        clones = res.clones.toList.map(CloneTransformDataConversion.fromCloneTransformDataJS),
        staticBatchKey = res.staticBatchKey.toOption.map(BindingKey.apply)
      )

  }

  object CloneTransformDataConversion {

    def toJS(data: CloneTransformData): js.Any =
      js.Dynamic.literal(
        position = PointConversion.toJS(data.position),
        rotation = data.rotation.value,
        scale = Vector2Conversion.toJS(data.scale),
        alpha = data.alpha,
        flipHorizontal = data.flipHorizontal,
        flipVertical = data.flipVertical
      )

    def fromJS(obj: js.Any): CloneTransformData =
      fromCloneTransformDataJS(obj.asInstanceOf[CloneTransformDataJS])

    def fromCloneTransformDataJS(res: CloneTransformDataJS): CloneTransformData =
      CloneTransformData(
        position = PointConversion.fromPointJS(res.position),
        rotation = Radians(res.rotation),
        scale = Vector2Conversion.fromVector2JS(res.scale),
        alpha = res.alpha,
        flipHorizontal = res.flipHorizontal,
        flipVertical = res.flipVertical
      )

  }

  object SpriteConversion {

    def toJS(node: Sprite): js.Any =
      js.Dynamic.literal(
        _type = "sprite",
        bindingKey = node.bindingKey.value,
        animationKey = node.animationKey.value,
        animationActions = node.animationActions.map(AnimationActionConversion.toJS).toJSArray,
        effects = EffectsConversion.toJS(node.effects),
        position = PointConversion.toJS(node.position),
        rotation = node.rotation.value,
        scale = Vector2Conversion.toJS(node.scale),
        depth = node.depth.zIndex,
        ref = PointConversion.toJS(node.ref),
        flip = FlipConversion.toJS(node.flip)
      )

    def fromJS(obj: js.Any): Sprite =
      fromSpriteJS(obj.asInstanceOf[SpriteJS])

    def fromSpriteJS(res: SpriteJS): Sprite =
      Sprite(
        bindingKey = BindingKey(res.bindingKey),
        animationKey = AnimationKey(res.animationKey),
        animationActions = res.animationActions.toList.map(AnimationActionConversion.fromAnimationActionJS),
        eventHandler = (_: (Rectangle, GlobalEvent)) => Nil,
        effects = EffectsConversion.fromEffectsJS(res.effects),
        position = PointConversion.fromPointJS(res.position),
        rotation = Radians(res.rotation),
        scale = Vector2Conversion.fromVector2JS(res.scale),
        depth = Depth(res.depth),
        ref = PointConversion.fromPointJS(res.ref),
        flip = FlipConversion.fromFlipJS(res.flip)
      )

  }

  object GraphicConversion {

    def toJS(node: Graphic): js.Any =
      js.Dynamic.literal(
        _type = "graphic",
        material = MaterialConversion.toJS(node.material),
        crop = RectangleConversion.toJS(node.crop),
        effects = EffectsConversion.toJS(node.effects),
        position = PointConversion.toJS(node.position),
        rotation = node.rotation.value,
        scale = Vector2Conversion.toJS(node.scale),
        depth = node.depth.zIndex,
        ref = PointConversion.toJS(node.ref),
        flip = FlipConversion.toJS(node.flip)
      )

    def fromJS(obj: js.Any): Graphic =
      fromGraphicJS(obj.asInstanceOf[GraphicJS])

    def fromGraphicJS(res: GraphicJS): Graphic =
      Graphic(
        material = MaterialConversion.fromMaterialJS(res.material),
        crop = RectangleConversion.fromRectangleJS(res.crop),
        effects = EffectsConversion.fromEffectsJS(res.effects),
        position = PointConversion.fromPointJS(res.position),
        rotation = Radians(res.rotation),
        scale = Vector2Conversion.fromVector2JS(res.scale),
        depth = Depth(res.depth),
        ref = PointConversion.fromPointJS(res.ref),
        flip = FlipConversion.fromFlipJS(res.flip)
      )

  }

  object TextConversion {

    def toJS(node: Text): js.Any =
      js.Dynamic.literal(
        _type = "text",
        text = node.text,
        alignment = node.alignment match {
          case TextAlignment.Left   => "left"
          case TextAlignment.Right  => "right"
          case TextAlignment.Center => "center"
        },
        fontKey = node.fontKey.key,
        effects = EffectsConversion.toJS(node.effects),
        position = PointConversion.toJS(node.position),
        rotation = node.rotation.value,
        scale = Vector2Conversion.toJS(node.scale),
        depth = node.depth.zIndex,
        ref = PointConversion.toJS(node.ref),
        flip = FlipConversion.toJS(node.flip)
      )

    def fromJS(obj: js.Any): Text =
      fromTextJS(obj.asInstanceOf[TextJS])

    def fromTextJS(res: TextJS): Text =
      Text(
        text = res.text,
        alignment = res.alignment match {
          case "left"   => TextAlignment.Left
          case "right"  => TextAlignment.Right
          case "center" => TextAlignment.Center
          case _        => TextAlignment.Left
        },
        fontKey = FontKey(res.fontKey),
        eventHandler = (_: (Rectangle, GlobalEvent)) => Nil,
        effects = EffectsConversion.fromEffectsJS(res.effects),
        position = PointConversion.fromPointJS(res.position),
        rotation = Radians(res.rotation),
        scale = Vector2Conversion.fromVector2JS(res.scale),
        depth = Depth(res.depth),
        ref = PointConversion.fromPointJS(res.ref),
        flip = FlipConversion.fromFlipJS(res.flip)
      )

  }

  object GroupConversion {

    def toJS(node: Group): js.Any =
      js.Dynamic.literal(
        _type = "group",
        children = node.children.map {
          case g: Group   => GroupConversion.toJS(g)
          case s: Sprite  => SpriteConversion.toJS(s)
          case g: Graphic => GraphicConversion.toJS(g)
          case t: Text    => TextConversion.toJS(t)
        }.toJSArray,
        position = PointConversion.toJS(node.position),
        rotation = node.rotation.value,
        scale = Vector2Conversion.toJS(node.scale),
        depth = node.depth.zIndex,
        ref = PointConversion.toJS(node.ref),
        flip = FlipConversion.toJS(node.flip)
      )

    def fromJS(obj: js.Any): Group =
      fromGroupJS(obj.asInstanceOf[GroupJS])

    def fromGroupJS(res: GroupJS): Group =
      Group(
        children = res.children.toList.map {
          case node: SceneGraphNodeJS if node._type == "group" =>
            GroupConversion.fromJS(node)

          case node: SceneGraphNodeJS if node._type == "graphic" =>
            GraphicConversion.fromJS(node)

          case node: SceneGraphNodeJS if node._type == "sprite" =>
            SpriteConversion.fromJS(node)

          case node: SceneGraphNodeJS if node._type == "text" =>
            TextConversion.fromJS(node)
        },
        position = PointConversion.fromPointJS(res.position),
        rotation = Radians(res.rotation),
        scale = Vector2Conversion.fromVector2JS(res.scale),
        depth = Depth(res.depth),
        ref = PointConversion.fromPointJS(res.ref),
        flip = FlipConversion.fromFlipJS(res.flip)
      )

  }

  object FlipConversion {

    def toJS(flip: Flip): js.Any =
      js.Dynamic.literal(
        horizontal = flip.horizontal,
        vertical = flip.vertical
      )

    def fromJS(obj: js.Any): Flip =
      fromFlipJS(obj.asInstanceOf[FlipJS])

    def fromFlipJS(res: FlipJS): Flip =
      Flip(res.horizontal, res.vertical)

  }

  object AnimationActionConversion {

    def toJS(action: AnimationAction): js.Any =
      action match {
        case AnimationAction.Play             => js.Dynamic.literal(_action = "play")
        case AnimationAction.ChangeCycle(l)   => js.Dynamic.literal(_action = "change", label = l.value)
        case AnimationAction.JumpToFirstFrame => js.Dynamic.literal(_action = "first")
        case AnimationAction.JumpToLastFrame  => js.Dynamic.literal(_action = "last")
        case AnimationAction.JumpToFrame(num) => js.Dynamic.literal(_action = "jump", to = num)
      }

    def fromJS(obj: js.Any): AnimationAction =
      fromAnimationActionJS(obj.asInstanceOf[AnimationActionJS])

    def fromAnimationActionJS(res: AnimationActionJS): AnimationAction =
      res._action match {
        case "play"   => AnimationAction.Play
        case "change" => AnimationAction.ChangeCycle(CycleLabel(res.label.get))
        case "first"  => AnimationAction.JumpToFirstFrame
        case "last"   => AnimationAction.JumpToLastFrame
        case "jump"   => AnimationAction.JumpToFrame(res.to.get)
        case _        => AnimationAction.Play
      }

  }

  object EffectsConversion {

    def toJS(effects: Effects): js.Any =
      js.Dynamic.literal(
        tint = RGBAConversion.toJS(effects.tint),
        overlay = effects.overlay match {
          case Overlay.Color(rgba) =>
            js.Dynamic.literal(_type = "color", color = RGBAConversion.toJS(rgba))

          case Overlay.LinearGradiant(fromPoint, fromColor, toPoint, toColor) =>
            js.Dynamic.literal(
              _type = "linear gradiant",
              fromPoint = PointConversion.toJS(fromPoint),
              fromColor = RGBAConversion.toJS(fromColor),
              toPoint = PointConversion.toJS(toPoint),
              toColor = RGBAConversion.toJS(toColor)
            )
        },
        border = js.Dynamic.literal(
          color = RGBAConversion.toJS(effects.border.color),
          innerThickness = effects.border.innerThickness.toInt,
          outerThickness = effects.border.outerThickness.toInt
        ),
        glow = js.Dynamic.literal(
          color = RGBAConversion.toJS(effects.glow.color),
          innerGlowAmount = effects.glow.innerGlowAmount,
          outerGlowAmount = effects.glow.outerGlowAmount
        ),
        alpha = effects.alpha
      )

    def fromJS(obj: js.Any): Effects =
      fromEffectsJS(obj.asInstanceOf[EffectsJS])

    def fromEffectsJS(res: EffectsJS): Effects = {
      val overlay =
        res.overlay._type match {
          case "color" =>
            Overlay.Color(RGBAConversion.fromRGBAJS(res.overlay.color.get))

          case "linear gradiant" =>
            Overlay.LinearGradiant(
              fromPoint = PointConversion.fromPointJS(res.overlay.fromPoint.get),
              fromColor = RGBAConversion.fromRGBAJS(res.overlay.fromColor.get),
              toPoint = PointConversion.fromPointJS(res.overlay.toPoint.get),
              toColor = RGBAConversion.fromRGBAJS(res.overlay.toColor.get)
            )

          case _ =>
            Overlay.Color.default

        }

      val border =
        Border(
          RGBAConversion.fromRGBAJS(res.glow.color),
          Thickness.fromInt(res.border.innerThickness),
          Thickness.fromInt(res.border.outerThickness)
        )

      val glow =
        Glow(
          RGBAConversion.fromRGBAJS(res.glow.color),
          res.glow.innerGlowAmount,
          res.glow.outerGlowAmount
        )

      Effects(
        tint = RGBAConversion.fromRGBAJS(res.tint),
        overlay = overlay,
        border = border,
        glow = glow,
        alpha = res.alpha
      )
    }

  }

}

object RectangleConversion {

  def toJS(rectangle: Rectangle): js.Any =
    js.Dynamic.literal(
      position = PointConversion.toJS(rectangle.position),
      size = PointConversion.toJS(rectangle.size)
    )

  def fromJS(obj: js.Any): Rectangle =
    fromRectangleJS(obj.asInstanceOf[RectangleJS])

  def fromRectangleJS(res: RectangleJS): Rectangle =
    Rectangle(PointConversion.fromJS(res.position), PointConversion.fromJS(res.size))

}

object PointConversion {

  def toJS(point: Point): js.Any =
    js.Dynamic.literal(
      x = point.x,
      y = point.y
    )

  def fromJS(obj: js.Any): Point =
    fromPointJS(obj.asInstanceOf[PointJS])

  def fromPointJS(res: PointJS): Point =
    Point(res.x, res.y)
}

object Vector2Conversion {

  def toJS(vector: Vector2): js.Any =
    js.Dynamic.literal(
      x = vector.x,
      y = vector.y
    )

  def fromJS(obj: js.Any): Vector2 =
    fromVector2JS(obj.asInstanceOf[Vector2JS])

  def fromVector2JS(res: Vector2JS): Vector2 =
    Vector2(res.x, res.y)

}

object MaterialConversion {

  def toJS(material: Material): js.Any =
    material match {
      case Material.Textured(diffuse, isLit) =>
        js.Dynamic.literal(
          _type = "textured",
          diffuse = diffuse.value,
          isLit = isLit
        )

      case Material.Lit(albedo, emissive, normal, specular, isLit) =>
        js.Dynamic.literal(
          _type = "lit",
          albedo = albedo.value,
          emissive = emissive.map(TextureConversion.toJS).orUndefined,
          normal = normal.map(TextureConversion.toJS).orUndefined,
          specular = specular.map(TextureConversion.toJS).orUndefined,
          isLit = isLit
        )
    }

  def fromJS(obj: js.Any): Material =
    fromMaterialJS(obj.asInstanceOf[MaterialJS])

  def fromMaterialJS(res: MaterialJS): Material =
    res._type match {
      case "textured" =>
        Material.Textured(AssetName(res.diffuse), res.isLit)

      case "lit" =>
        Material.Lit(
          albedo = AssetName(res.albedo),
          emissive = res.emissive.toOption.map(TextureConversion.fromTextureJS),
          normal = res.normal.toOption.map(TextureConversion.fromTextureJS),
          specular = res.specular.toOption.map(TextureConversion.fromTextureJS),
          isLit = res.isLit
        )
    }

  object TextureConversion {

    def toJS(texture: Texture): js.Any =
      js.Dynamic.literal(
        assetName = texture.assetName.value,
        amount = texture.amount
      )

    def fromJS(obj: js.Any): Texture =
      fromTextureJS(obj.asInstanceOf[TextureJS])

    def fromTextureJS(res: TextureJS): Texture =
      Texture(AssetName(res.assetName), res.amount)

  }

}
