/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.orhanobut.logger.Logger
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import com.orhanobut.logger.AndroidLogAdapter

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb() {
        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.d("createDb")

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // 테스트 코드이기 때문에, 데이터들이 DB에 저장됐다가 프로세스가 만료되면 사라져야하기때문에 in-memory DB를 사용함
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                .allowMainThreadQueries() // 기본적으로 메인 쓰레드에서 쿼리를 실행하면 오류가 난다. 하지만 테스트이기 때문에 allowMainThreadQueries()를 호출해서 해제해주자!
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        Logger.d("insertAndGetNight")

        //실제 테스팅
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAllNight() {
        val size = 10
        for (i in 0 until size) {
            Logger.d("Insert $i")
            val night = SleepNight()
            sleepDao.insert(night)
        }

        val allNight = sleepDao.getAllNight()
        Logger.d(allNight)
        assertEquals(allNight.size, 10)
    }
}