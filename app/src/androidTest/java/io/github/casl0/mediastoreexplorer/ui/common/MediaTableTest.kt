package io.github.casl0.mediastoreexplorer.ui.common

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.casl0.mediastoreexplorer.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaTableTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val testColumns = listOf(TableColumn<String>(header = "Name", width = 120.dp) { it })

    // region ローディング状態

    @Test
    fun isLoading_が_true_のとき_プログレスインジケーターが表示される() {
        composeTestRule.setContent {
            MediaTable(
                items = emptyList<String>(),
                columns = testColumns,
                isLoading = true,
                error = null,
                key = { it },
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    // endregion

    // region エラー状態

    @Test
    fun error_が非null_のとき_エラーメッセージが表示される() {
        val errorMessage = "接続エラー"
        composeTestRule.setContent {
            MediaTable(
                items = emptyList<String>(),
                columns = testColumns,
                isLoading = false,
                error = errorMessage,
                key = { it },
            )
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.error_message, errorMessage))
            .assertIsDisplayed()
    }

    // endregion

    // region 空リスト状態

    @Test
    fun items_が空_のとき_空メッセージが表示される() {
        composeTestRule.setContent {
            MediaTable(
                items = emptyList(),
                columns = testColumns,
                isLoading = false,
                error = null,
                key = { it },
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.no_items)).assertIsDisplayed()
    }

    // endregion

    // region データ表示状態

    @Test
    fun items_が存在するとき_カラムヘッダーが表示される() {
        composeTestRule.setContent {
            MediaTable(
                items = listOf("item1"),
                columns = testColumns,
                isLoading = false,
                error = null,
                key = { it },
            )
        }

        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
    }

    @Test
    fun items_が存在するとき_各アイテムの値が表示される() {
        composeTestRule.setContent {
            MediaTable(
                items = listOf("item1", "item2"),
                columns = testColumns,
                isLoading = false,
                error = null,
                key = { it },
            )
        }

        composeTestRule.onNodeWithText("item1").assertIsDisplayed()
        composeTestRule.onNodeWithText("item2").assertIsDisplayed()
    }

    @Test
    fun 複数カラムのヘッダーがすべて表示される() {
        val multiColumns =
            listOf(
                TableColumn<String>(header = "Col_A", width = 100.dp) { it },
                TableColumn<String>(header = "Col_B", width = 100.dp) { it.uppercase() },
            )
        composeTestRule.setContent {
            MediaTable(
                items = listOf("hello"),
                columns = multiColumns,
                isLoading = false,
                error = null,
                key = { it },
            )
        }

        composeTestRule.onNodeWithText("Col_A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Col_B").assertIsDisplayed()
    }

    // endregion
}
