package io.github.casl0.mediastoreexplorer.ui.images

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.casl0.mediastoreexplorer.R
import io.github.casl0.mediastoreexplorer.data.repository.FakeMediaRepository
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImagesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    // region パーミッション未付与状態

    @Test
    fun パーミッション未付与のとき_パーミッション要求画面のメッセージが表示される() {
        val viewModel = ImagesViewModel(FakeMediaRepository())
        composeTestRule.setContent {
            ImagesScreen(viewModel = viewModel, initialPermissionsGranted = false)
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.permission_images_message))
            .assertIsDisplayed()
    }

    @Test
    fun パーミッション未付与のとき_権限付与ボタンが表示される() {
        val viewModel = ImagesViewModel(FakeMediaRepository())
        composeTestRule.setContent {
            ImagesScreen(viewModel = viewModel, initialPermissionsGranted = false)
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.grant_permission))
            .assertIsDisplayed()
    }

    // endregion
}
