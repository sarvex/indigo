package snake.scenes

import indigo.*
import snake.init.{GameAssets, StaticAssets, ViewConfig}
import snake.model.GameMap
import snake.model.GameModel
import indigoextras.geometry.Vertex
import indigoextras.geometry.BoundingBox

object GameView:

  def update(viewConfig: ViewConfig, model: GameModel, walls: Group, staticAssets: StaticAssets): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
        .addLayer(
          Layer(
            BindingKey("game"),
            gameLayer(
              viewConfig,
              model,
              staticAssets,
              walls
            )
          )
        )
    )

  def gameLayer(
      viewConfig: ViewConfig,
      currentState: GameModel,
      staticAssets: StaticAssets,
      walls: Group
  ): Batch[SceneNode] =
    walls ::
      drawApple(viewConfig, currentState.gameMap, staticAssets) ++
      drawSnake(viewConfig, currentState, staticAssets.snake) ++
      drawScore(viewConfig, currentState.score)

  def drawApple(viewConfig: ViewConfig, gameMap: GameMap, staticAssets: StaticAssets): Batch[Graphic[_]] =
    Batch.fromList(gameMap.findApples).map { a =>
      staticAssets.apple
        .moveTo(gridPointToPoint(a.gridPoint, gameMap.gridSize, viewConfig.gridSquareSize))
    }

  def drawSnake(viewConfig: ViewConfig, currentState: GameModel, snakeAsset: Graphic[_]): Batch[Graphic[_]] =
    Batch.fromList(currentState.snake.givePath).map { pt =>
      snakeAsset.moveTo(gridPointToPoint(pt, currentState.gameMap.gridSize, viewConfig.gridSquareSize))
    }

  def drawScore(viewConfig: ViewConfig, score: Int): Batch[SceneNode] =
    Batch(
      Text(
        score.toString,
        (viewConfig.viewport.width / viewConfig.magnificationLevel) - 3,
        (viewConfig.viewport.height / viewConfig.magnificationLevel) - viewConfig.footerHeight + 21,
        1,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignRight
    )

  def gridPointToPoint(gridPoint: Vertex, gridSize: BoundingBox, gridSquareSize: Int): Point =
    Point((gridPoint.x * gridSquareSize).toInt, (((gridSize.height - 1) - gridPoint.y) * gridSquareSize).toInt)
