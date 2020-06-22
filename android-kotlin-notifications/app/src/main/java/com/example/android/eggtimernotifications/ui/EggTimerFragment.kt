/*
 * Copyright (C) 2019 Google Inc.
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

package com.example.android.eggtimernotifications.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.databinding.FragmentEggTimerBinding
import com.google.firebase.messaging.FirebaseMessaging

class EggTimerFragment : Fragment(){

    private val TOPIC = "breakfast"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentEggTimerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_egg_timer, container, false
        )

        val viewModel = ViewModelProviders.of(this).get(EggTimerViewModel::class.java)

        binding.eggTimerViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        // TODO: Step 1.7 call create channel
        createChannel(
            getString(R.string.egg_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )

        return binding.root
    }

    // Oreo(sdk26) 이상부터는 notification을 만드는데 있어서 NotificationChannel을 사용해야함
    // Nougat(sdk25) 에서는 체널이 존재하지 않으며, 따라서 버젼 대응을 요함
    // 둘 다 노티를 생성할 때는 NotificationCompat.Builder()로 생성함
    // Channel을 사용하면, snooze기능 등 노티에 대한 세부적인 설정이 가능
    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        // 오레오 이상일 때는 채널을 생성해서 넣어주는 것을 권장
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            //채널 세부 설정
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.breakfast_notification_channel_description)

            //노티 매니져 객체를 가져와서
            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )

            //채널을 설정해줌 (노티매니져의 유일성은 보장되기에 이러한 동작이 가능)
            //나중에 노티 빌더에서 channelId를 통해서 채널을 바인
            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create a channel
    }

    companion object {
        fun newInstance() = EggTimerFragment()
    }
}

