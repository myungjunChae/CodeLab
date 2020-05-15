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

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {
    abstract val sleepDatabaseDao: SleepDatabaseDao

    //데이터 베이스를 제공하는 것이 목적, 인스턴스화의 의무가 없음
    companion object {
        //Volatile 이란
        //변수를 Main Memory에 저장하겠다라는 것을 명시하는 것
        //Volatile이 선언된 변수는 Read/Write 시, CPU cache가 아닌 Main Memory를 사용한다.
        //따라서 한 스레드에서 INSTANCE를 변경하더라도 모든 스레드에서 즉시 변경된 상태로 유지됨.
        //결국 thread-safe한 INSTANCE를 만들기 위한 어노테이션이다.
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase {
            //여러 스레드가 동시에 데이터베이스 인스턴스를 요청할 수 있으므로 하나 대신 두 개의 데이터베이스가 생성됩니다.
            // 이 샘플 앱에서는이 문제가 발생하지는 않지만보다 복잡한 앱에서는 가능합니다.
            // 데이터베이스를 가져 오기 위해 코드를 줄이면 synchronized 한 번에 하나의 실행 스레드 만이 코드 블록에 들어갈 수 있으므로 데이터베이스가 한 번만 초기화됩니다.
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}