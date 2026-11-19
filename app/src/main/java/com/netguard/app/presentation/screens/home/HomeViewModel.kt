package com.netguard.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import com.netguard.app.domain.model.ConnectionState
import com.netguard.app.domain.model.NetworkEvent
import com.netguard.app.domain.monitor.NetworkEventLog
import com.netguard.app.domain.monitor.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    networkEventLog: NetworkEventLog,
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = networkMonitor.connectionState
    val events: StateFlow<List<NetworkEvent>> = networkEventLog.events

    /** Called after the Location permission dialog resolves, whatever the answer was. */
    fun refreshConnectionInfo() = networkMonitor.refresh()
}
