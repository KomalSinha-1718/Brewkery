package com.brewkery.app.data

import com.brewkery.app.data.remote.BrewkeryApi
import com.brewkery.app.domain.Menu
import com.brewkery.app.domain.MenuItem
import com.brewkery.app.domain.StoreMeta
import kotlin.coroutines.cancellation.CancellationException

class MenuRepository(private val api: BrewkeryApi) {

    @Volatile
    private var cachedMenu: Menu? = null

    /** Store config of the last successful menu load, or the brief's defaults. */
    val storeMeta: StoreMeta get() = cachedMenu?.meta ?: StoreMeta.Default

    suspend fun getMenu(): Result<Menu> = runCatchingCancellable {
        api.getMenu().toDomain().also { cachedMenu = it }
    }

    /**
     * The menu payload already contains every field of the item endpoint, so a tap serves the
     * cached item instantly. The item endpoint is only used as a fallback (e.g. cache is empty).
     */
    suspend fun getItem(id: Int): Result<MenuItem> = runCatchingCancellable {
        cachedMenu?.items?.firstOrNull { it.id == id } ?: api.getItem(id).toDomain()
    }
}

/** Like runCatching, but never swallows coroutine cancellation. */
internal suspend inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
