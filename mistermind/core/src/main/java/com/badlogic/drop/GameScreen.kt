package com.badlogic.drop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GameScreen(private val game: MastermindGame) : Screen {
    private val worldHeight = 1300f
    private val worldWidth = 650f
    private val viewport: Viewport = FitViewport(worldWidth, worldHeight)

    private val COLORS = listOf(
        Color(1f, 0f, 0f, 1f),     // Rojo
        Color(0f, 0f, 1f, 1f),     // Azul
        Color(0f, 1f, 0f, 1f),     // Verde
        Color(1f, 1f, 0f, 1f),     // Amarillo
        Color(1f, 0.5f, 0f, 1f),   // Naranja
        Color(0.5f, 0f, 0.5f, 1f)  // Morado
    )
    private val CODE_LENGTH = 2
    private val MAX_ATTEMPTS = 10
    private val CIRCLE_RADIUS = 25f

    private var secretCode = generateSecretCode()
    private val attempts = mutableListOf<List<Color>>()
    private val feedback = mutableListOf<Pair<Int, Int>>()
    private val shapeRenderer = ShapeRenderer()

    private var selectedColor: Color? = null
    private var currentAttempt = mutableListOf<Color>()
    private var gameOver = false
    private var gameWon = false

    private fun generateSecretCode(): List<Color> = List(CODE_LENGTH) { COLORS.random() }

    private fun checkAttempt(attempt: List<Color>): Pair<Int, Int> {
        var correctPosition = 0
        var correctColor = 0
        val secretTemp = secretCode.toMutableList()
        val attemptTemp = attempt.toMutableList()

        // Verificar posiciones correctas
        for (i in attempt.indices) {
            if (attempt[i] == secretCode[i]) {
                correctPosition++
                secretTemp[i] = Color.WHITE
                attemptTemp[i] = Color.BLACK
            }
        }

        // Verificar colores correctos en posiciones incorrectas
        for (i in attemptTemp.indices) {
            if (attemptTemp[i] != Color.BLACK) {
                for (j in secretTemp.indices) {
                    if (secretTemp[j] != Color.WHITE && attemptTemp[i] == secretTemp[j]) {
                        correctColor++
                        secretTemp[j] = Color.WHITE
                        break
                    }
                }
            }
        }

        return Pair(correctPosition, correctColor)
    }

    private fun handleInput() {
        if (!Gdx.input.justTouched() || gameOver || gameWon) return

        val touchPos = viewport.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        val touchX = touchPos.x
        val touchY = touchPos.y

        // Manejo de la paleta de colores
        for (i in COLORS.indices) {
            val x = 50f + i * 60f
            val y = 50f
            if (Vector2(touchX - x, touchY - y).len() < CIRCLE_RADIUS) {
                if (currentAttempt.size < CODE_LENGTH) {
                    currentAttempt.add(COLORS[i])
                }
                selectedColor = COLORS[i]
                return
            }
        }

        // Manejo del intento actual
        if (selectedColor != null) {
            for (i in 0 until CODE_LENGTH) {
                val x = 50f + i * 60f
                val y = 150f
                if (Vector2(
                        touchX - x,
                        touchY - y
                    ).len() < CIRCLE_RADIUS && currentAttempt.size < CODE_LENGTH
                ) {
                    currentAttempt.add(selectedColor!!)
                    return
                }
            }
        }

        // Manejo del botón de enviar
        if (currentAttempt.size == CODE_LENGTH &&
            touchX > 400f && touchX < 500f &&
            touchY > 130f && touchY < 170f
        ) {

            val result = checkAttempt(currentAttempt)
            attempts.add(currentAttempt.toList())
            feedback.add(result)
            currentAttempt.clear()

            if (result.first == CODE_LENGTH) {
                gameWon = true
            } else if (attempts.size >= MAX_ATTEMPTS) {
                gameOver = true
            }
        }
    }

    override fun render(delta: Float) {

        viewport.apply()
        game.batch.projectionMatrix = viewport.camera.combined
        shapeRenderer.projectionMatrix = viewport.camera.combined

        // Manejo de input
        handleInput()

        // Limpieza de pantalla
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Habilitar blending para antialiasing
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        if (gameOver || gameWon) {
            shapeRenderer.begin(ShapeType.Filled)
            for (i in secretCode.indices) {
                val x = 250f + i * 60f
                val y = 900f
                shapeRenderer.color = secretCode[i]
                shapeRenderer.circle(x, y, CIRCLE_RADIUS)
            }
            shapeRenderer.end()
        }

        shapeRenderer.begin(ShapeType.Filled)

        // Dibujar paleta de colores
        for (i in COLORS.indices) {
            val x = 50f + i * 60f
            val y = 50f
            shapeRenderer.color = COLORS[i]
            shapeRenderer.circle(x, y, CIRCLE_RADIUS)
        }

        // Dibujar intento actual
        for (i in 0 until CODE_LENGTH) {
            val x = 50f + i * 60f
            val y = 150f
            shapeRenderer.color = currentAttempt.getOrNull(i) ?: Color.GRAY
            shapeRenderer.circle(x, y, CIRCLE_RADIUS)
        }

        // Dibujar intentos anteriores
        for (i in attempts.indices) {
            val attempt = attempts[i]
            for (j in attempt.indices) {
                val x = 50f + j * 60f
                val y = 250f + i * 60f
                shapeRenderer.color = attempt[j]
                shapeRenderer.circle(x, y, CIRCLE_RADIUS)
            }
        }

        shapeRenderer.end()

        // Dibujar texto y feedback
        game.batch.begin()

        // Dibujar título
        if (!gameOver && !gameWon) {
            game.font.draw(game.batch, "MASTERMIND", 225f, 1000f)
        }

        // Dibujar feedback de intentos anteriores
        for (i in feedback.indices) {
            val result = feedback[i]
            game.font.draw(
                game.batch,
                "Posición: ${result.first}, Color: ${result.second}",
                300f, 250f + i * 60f
            )
        }

        // Dibujar botón de enviar
        if (currentAttempt.size == CODE_LENGTH && !gameOver && !gameWon) {
            game.font.draw(game.batch, "Enviar", 400f, 150f)
        }

        // Dibujar mensajes de estado del juego
        if (gameWon) {
            game.font.draw(game.batch, "¡Has ganado!", 225f, 1025f)
            game.font.draw(game.batch, "Código secreto:", 225f, 975f)

        } else if (gameOver) {
            game.font.draw(game.batch, "¡Juego terminado!", 225f, 1025f)
            game.font.draw(game.batch, "Código secreto:", 225f, 975f)
        }

        game.batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        game.batch.projectionMatrix = viewport.camera.combined
        shapeRenderer.projectionMatrix = viewport.camera.combined
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun show() {}

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
