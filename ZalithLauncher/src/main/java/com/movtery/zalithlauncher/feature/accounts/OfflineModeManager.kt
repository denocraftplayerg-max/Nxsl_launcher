package com.movtery.zalithlauncher.feature.accounts

import com.movtery.zalithlauncher.feature.log.Logging
import net.kdt.pojavlaunch.value.MinecraftAccount

/**
 * Manages offline mode to prevent Microsoft login interference
 * Ensures offline gameplay is never interrupted by online authentication
 */
object OfflineModeManager {
    private const val TAG = "OfflineMode"
    
    /**
     * Check if account is offline-only
     */
    fun isOfflineAccount(account: MinecraftAccount?): Boolean {
        if (account == null) return false
        return account.accountType == "local" || account.username.isEmpty()
    }

    /**
     * Check if current session should be offline
     */
    fun isOfflineSession(): Boolean {
        val currentAccount = AccountsManager.currentAccount
        return isOfflineAccount(currentAccount)
    }

    /**
     * Suppress Microsoft authentication during offline mode
     */
    fun suppressMicrosoftAuth(block: () -> Unit) {
        if (isOfflineSession()) {
            Logging.i(TAG, "Microsoft auth suppressed - offline mode active")
            return
        }
        block()
    }

    /**
     * Check if online sync is allowed
     */
    fun isOnlineSyncAllowed(): Boolean {
        return !isOfflineSession()
    }

    /**
     * Validate offline account
     */
    fun validateOfflineAccount(username: String): Boolean {
        return username.isNotEmpty() && 
               username.length in 2..16 && 
               username.matches(Regex("[a-zA-Z0-9_]+"))
    }

    /**
     * Create offline session account
     */
    fun createOfflineAccount(username: String): MinecraftAccount {
        val account = MinecraftAccount()
        account.username = username
        account.accountType = "local"
        account.accessToken = "offline"
        account.clientToken = "offline"
        account.profileId = "00000000-0000-0000-0000-000000000000"
        account.msaRefreshToken = "0"
        
        Logging.i(TAG, "Created offline account: $username")
        return account
    }

    /**
     * Block any online dialogs during offline mode
     */
    fun blockOnlineDialogs(): Boolean {
        if (isOfflineSession()) {
            Logging.d(TAG, "Online dialogs blocked - offline mode active")
            return true
        }
        return false
    }

    /**
     * Log offline mode state
     */
    fun logOfflineModeState() {
        if (isOfflineSession()) {
            val account = AccountsManager.currentAccount
            Logging.i(TAG, "Offline mode: ACTIVE - User: ${account?.username ?: "unknown"}")
        } else {
            Logging.i(TAG, "Offline mode: INACTIVE - Online mode allowed")
        }
    }
}
