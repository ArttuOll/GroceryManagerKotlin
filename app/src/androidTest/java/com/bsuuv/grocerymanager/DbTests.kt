package com.bsuuv.grocerymanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bsuuv.grocerymanager.data.db.FoodItemDatabase
import com.bsuuv.grocerymanager.data.db.dao.FoodItemDao
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.util.TimeFrame
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

/*
Run only with a clean device!
 */
@RunWith(AndroidJUnit4::class)
class DbTests {

    private lateinit var db: FoodItemDatabase
    private lateinit var dao: FoodItemDao
    private lateinit var foodItem: FoodItemEntity
    private lateinit var foodItem2: FoodItemEntity
    private lateinit var context: Context

    // Run normally asynchronous tasks synchronously
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FoodItemDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.foodItemDao
        foodItem = FoodItemEntity(
            1, "",
            "Kalja", "Karjala", "Raikasta",
            2, "Packets", TimeFrame.WEEK, 1, 0.0
        )
        foodItem2 = FoodItemEntity(
            0, "",
            "Makkara", "Atria", "Lihaisaa",
            3, "Bags", TimeFrame.WEEK, 1, 0.0
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetFoodItem() {
        runBlocking { dao.insert(foodItem) }
        val actual = runBlocking { dao.get(1) }
        Assert.assertEquals(foodItem, actual)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAll() {
        runBlocking { dao.insert(foodItem) }
        runBlocking { dao.insert(foodItem2) }
        dao.getAll().observeOnce {
            Assert.assertEquals(2, it.size)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRemove() {
        runBlocking { dao.insert(foodItem) }
        runBlocking { dao.insert(foodItem2) }
        runBlocking { dao.delete(foodItem) }

        dao.getAll().observeOnce {
            Assert.assertEquals(1, it.size)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertAndUpdate() {
        runBlocking { dao.insert(foodItem) }
        foodItem.countdownValue = 1.0
        runBlocking { dao.update(foodItem) }
        val actual = runBlocking { dao.get(1) }.countdownValue
        dao.getAll().observeOnce {
            Assert.assertEquals(1.0, actual, 0.1)
        }
    }

    private fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
        val observer = OneTimeObserver(executeOnChanged = onChangeHandler)
        observe(observer, observer)
    }

    /**
     * Observer that owns its own lifecycle. After handling the first `onChange()`-event, it's
     * marked as destroyed and removed from observers of the `LiveData`. Accepts a lambda as
     * parameter, which will be executed as part of the onChanged()-event.
     *
     * [Source](https://alediaferia.com/2018/12/17/testing-livedata-room-android/)
     */
    inner class OneTimeObserver<T>(private val executeOnChanged: (T) -> Unit) : Observer<T>,
        LifecycleOwner {
        private val lifecycle = LifecycleRegistry(this)

        init {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        override fun getLifecycle(): Lifecycle = lifecycle

        override fun onChanged(t: T) {
            executeOnChanged(t)
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
    }
}