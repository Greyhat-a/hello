package com.netguard.app.di

import com.netguard.app.domain.monitor.NetworkEventLog
import com.netguard.app.domain.monitor.NetworkMonitor
import com.netguard.app.network.monitor.ConnectivityNetworkMonitor
import com.netguard.app.network.monitor.InMemoryNetworkEventLog
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindNetworkEventLog(impl: InMemoryNetworkEventLog): NetworkEventLog
}
