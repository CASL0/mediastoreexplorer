package io.github.casl0.mediastoreexplorer.data.repository

import android.Manifest
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import io.github.casl0.mediastoreexplorer.data.repository.datasource.AudioMediaDataSource
import io.github.casl0.mediastoreexplorer.data.repository.datasource.DownloadMediaDataSource
import io.github.casl0.mediastoreexplorer.data.repository.datasource.ImageMediaDataSource
import io.github.casl0.mediastoreexplorer.data.repository.datasource.VideoMediaDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaRepositoryImplTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val permissionRule: GrantPermissionRule =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
            )
        } else {
            GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    private val repository by lazy {
        val resolver = context.contentResolver
        MediaRepositoryImpl(
            imageDataSource = ImageMediaDataSource(resolver),
            videoDataSource = VideoMediaDataSource(resolver),
            audioDataSource = AudioMediaDataSource(resolver),
            downloadDataSource = DownloadMediaDataSource(resolver),
            ioDispatcher = Dispatchers.Unconfined,
        )
    }

    // region getImages

    @Test
    fun getImages_例外を投げずに呼び出せる() = runBlocking {
        val result = repository.getImages()
        assertNotNull(result)
    }

    @Test
    fun getImages_戻り値はリストである() = runBlocking {
        val result = repository.getImages()
        assert(result.size >= 0)
    }

    @Test
    fun getImages_アイテムが存在する場合_idが正の値である() = runBlocking {
        val result = repository.getImages()
        result.forEach { item -> assert(item.id > 0) { "id must be positive, but was ${item.id}" } }
    }

    // endregion

    // region getVideos

    @Test
    fun getVideos_例外を投げずに呼び出せる() = runBlocking {
        val result = repository.getVideos()
        assertNotNull(result)
    }

    @Test
    fun getVideos_戻り値はリストである() = runBlocking {
        val result = repository.getVideos()
        assert(result.size >= 0)
    }

    @Test
    fun getVideos_アイテムが存在する場合_idが正の値である() = runBlocking {
        val result = repository.getVideos()
        result.forEach { item -> assert(item.id > 0) { "id must be positive, but was ${item.id}" } }
    }

    // endregion

    // region getAudios

    @Test
    fun getAudios_例外を投げずに呼び出せる() = runBlocking {
        val result = repository.getAudios()
        assertNotNull(result)
    }

    @Test
    fun getAudios_戻り値はリストである() = runBlocking {
        val result = repository.getAudios()
        assert(result.size >= 0)
    }

    @Test
    fun getAudios_アイテムが存在する場合_idが正の値である() = runBlocking {
        val result = repository.getAudios()
        result.forEach { item -> assert(item.id > 0) { "id must be positive, but was ${item.id}" } }
    }

    // endregion

    // region getDownloads

    @Test
    fun getDownloads_例外を投げずに呼び出せる() = runBlocking {
        val result = repository.getDownloads()
        assertNotNull(result)
    }

    @Test
    fun getDownloads_戻り値はリストである() = runBlocking {
        val result = repository.getDownloads()
        assert(result.size >= 0)
    }

    @Test
    fun getDownloads_アイテムが存在する場合_idが正の値である() = runBlocking {
        val result = repository.getDownloads()
        result.forEach { item -> assert(item.id > 0) { "id must be positive, but was ${item.id}" } }
    }

    // endregion
}
