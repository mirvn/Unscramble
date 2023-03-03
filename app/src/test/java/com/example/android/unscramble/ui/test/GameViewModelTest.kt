package com.example.android.unscramble.ui.test

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.unscramble.data.MAX_NO_OF_WORDS
import com.example.android.unscramble.data.SCORE_INCREASE
import com.example.android.unscramble.data.getUnscrambledWord
import com.example.android.unscramble.ui.GameViewModel
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class GameViewModelTest {
    private val gameViewModel = GameViewModel()
    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }

    @Test // Success Path
    // naming of test class should be theClassBeingTested_triggerOfTheTest_resultOfTest
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = gameViewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        gameViewModel.updateUserGuess(correctPlayerWord)
        gameViewModel.checkUserGuess()

        currentGameUiState = gameViewModel.uiState.value
        Assert.assertFalse(currentGameUiState.isGuessedWordWrong)
        Assert.assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test // Error Path
    fun gameViewModel_IncorectGuess_ErrorFlagSet() {
        // given incorrect input
        val incorrectPlayerWord = "and"
        gameViewModel.updateUserGuess(incorrectPlayerWord)
        gameViewModel.checkUserGuess()

        val currentGameUiState = gameViewModel.uiState.value

        Assert.assertEquals(0, currentGameUiState.score)
        Assert.assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test // Boundary Case
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val gameUiState = gameViewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // Assert that current word is scrambled.
        assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)
        // Assert that current word count is set to 1.
        assertNotEquals(1, gameUiState.currentWordCount)
        // Assert that initially the score is 0.
        assertTrue(gameUiState.score == 0)
        // Assert that the wrong word guessed is false.
        assertFalse(gameUiState.isGuessedWordWrong)
        // Assert that game is not over.
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = gameViewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS) {
            println(it)
            expectedScore += SCORE_INCREASE
            gameViewModel.updateUserGuess(correctPlayerWord)
            gameViewModel.checkUserGuess()
            currentGameUiState = gameViewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            // Assert that after each correct answer, score is updated correctly.
            assertEquals(expectedScore, currentGameUiState.score)
        }
        // Assert that after all questions are answered, the current word count is up-to-date.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount+1)
        // Assert that after 10 questions are answered, the game is over.
        assertTrue(currentGameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_SkippedWord_UiStateUpdatedCorrectly() {
        val currentUiState = gameViewModel.uiState.value
        val scoreTest = 0
        val wordCount = 0

        gameViewModel.skipWord()

        assertEquals(scoreTest, currentUiState.score)
        assertEquals(wordCount, currentUiState.currentWordCount)
        assertFalse(currentUiState.isGuessedWordWrong)
    }

}