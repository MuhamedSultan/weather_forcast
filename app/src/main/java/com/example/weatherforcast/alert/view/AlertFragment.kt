package com.example.weatherforcast.alert.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforcast.R
import com.example.weatherforcast.alert.repo.AlertRepositoryImpl
import com.example.weatherforcast.alert.viewmodel.AlertViewModel
import com.example.weatherforcast.alert.viewmodel.AlertViewModelFactory
import com.example.weatherforcast.databinding.FragmentAlertBinding
import com.example.weatherforcast.db.LocalDataSourceImpl
import com.example.weatherforcast.db.WeatherDatabase
import com.example.weatherforcast.pojo.alerts.Alerts
import kotlinx.coroutines.launch
import java.util.Calendar

class AlertFragment : Fragment(), OnItemAlertClick {

    private lateinit var binding: FragmentAlertBinding
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Toast.makeText(requireContext(),
            if (isGranted) "Notifications permission granted"
            else "Notifications permission denied",
            Toast.LENGTH_SHORT).show()
    }

    private val REQUEST_OVERLAY_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        val dao = WeatherDatabase.getInstance(requireContext())?.weatherDao()!!
        val localDataSource = LocalDataSourceImpl(dao)
        val alertRepository = AlertRepositoryImpl(localDataSource)
        val factory = AlertViewModelFactory(alertRepository)
        alertViewModel = ViewModelProvider(this, factory)[AlertViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlertBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.show()

        checkNotificationPermission()
        binding.alertFab.setOnClickListener { showChoiceDialog() }
        observeAlerts()
        createNotificationChannel()


    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun observeAlerts() {
        alertViewModel.getAllAlerts()
        lifecycleScope.launch {
            alertViewModel.getAllAlerts.collect {
                setupAlertRecyclerView(it ?: emptyList())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Alerts"
            val description = "Notifications for weather alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("weatherId", name, importance).apply {
                this.description = description
            }
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showChoiceDialog() {
        val options = arrayOf("Set Alarm", "Send Notification")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Alert Type")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showDateTimePicker("alarm")
                    1 -> showDateTimePicker("notification")
                }
            }
            .show()
    }

    private fun showDateTimePicker(alertType: String) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val formattedDateTime = "${dayOfMonth}/${month + 1}/${year} ${hourOfDay}:${minute}"

                    when (alertType) {
                        "alarm" -> {
                            setAlarm(calendar)
                            alertViewModel.addAlert(Alerts(0,"alarm",formattedDateTime))
                        }
                        "notification" -> {sendNotification(calendar)
                            alertViewModel.addAlert(Alerts(0,"notification",formattedDateTime))

                        }
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(calendar: Calendar) {
        if (!Settings.canDrawOverlays(requireContext())) {
            startActivityForResult(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}")),
                REQUEST_OVERLAY_PERMISSION
            )
        }

        val alarmIntent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmTimeInMillis = calendar.timeInMillis

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarmTimeInMillis,
            pendingIntent
        )

        Toast.makeText(requireContext(), "Alarm set successfully", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun sendNotification(calendar: Calendar) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("notification_title", "Weather Alert")
            putExtra("notification_message", "Click to see weather state today")
        }

        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(requireContext(), "Notification scheduled at ${calendar.time}", Toast.LENGTH_LONG).show()
    }

    private fun setupAlertRecyclerView(alerts: List<Alerts>) {
        val alertAdapter = AlertAdapter(alerts, this)
        val alertManager = LinearLayoutManager(requireContext())
        binding.alertRv.apply {
            adapter = alertAdapter
            layoutManager = alertManager
        }
    }

    override fun deleteAlertItem(alert: Alerts) {
        lifecycleScope.launch {
            alertViewModel.deleteAlert(alert)

        }
    }
}
