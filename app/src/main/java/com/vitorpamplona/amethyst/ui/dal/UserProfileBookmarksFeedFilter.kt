package com.vitorpamplona.amethyst.ui.dal

import com.vitorpamplona.amethyst.model.Account
import com.vitorpamplona.amethyst.model.LocalCache
import com.vitorpamplona.amethyst.model.Note
import com.vitorpamplona.amethyst.model.User

object UserProfileBookmarksFeedFilter : FeedFilter<Note>() {
    lateinit var account: Account
    var user: User? = null

    fun loadUserProfile(accountLoggedIn: Account, userId: String) {
        account = accountLoggedIn
        user = LocalCache.users[userId]
    }

    override fun feed(): List<Note> {
        val notes = user?.latestBookmarkList?.taggedEvents()?.mapNotNull {
            LocalCache.checkGetOrCreateNote(it)
        }?.toSet() ?: emptySet()

        val addresses = user?.latestBookmarkList?.taggedAddresses()?.map {
            LocalCache.getOrCreateAddressableNote(it)
        }?.toSet() ?: emptySet()

        return (notes + addresses)
            .filter { account.isAcceptable(it) }
            .sortedBy { it.createdAt() }
            .reversed()
    }
}
