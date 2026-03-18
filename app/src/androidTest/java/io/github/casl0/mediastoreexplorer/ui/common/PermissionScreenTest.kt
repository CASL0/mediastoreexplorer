package io.github.casl0.mediastoreexplorer.ui.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.casl0.mediastoreexplorer.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PermissionScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    // region メッセージ表示

    @Test
    fun 渡したメッセージが画面に表示される() {
        composeTestRule.setContent {
            PermissionRequiredScreen(message = "テストメッセージ", onRequestPermission = {})
        }

        composeTestRule.onNodeWithText("テストメッセージ").assertIsDisplayed()
    }

    // endregion

    // region 権限付与ボタン

    @Test
    fun 権限付与ボタンが表示される() {
        composeTestRule.setContent {
            PermissionRequiredScreen(message = "テストメッセージ", onRequestPermission = {})
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.grant_permission))
            .assertIsDisplayed()
    }

    @Test
    fun 権限付与ボタンをタップするとコールバックが呼ばれる() {
        var called = false
        composeTestRule.setContent {
            PermissionRequiredScreen(message = "テストメッセージ", onRequestPermission = { called = true })
        }

        composeTestRule.onNodeWithText(context.getString(R.string.grant_permission)).performClick()

        assertTrue(called)
    }

    // endregion
}
