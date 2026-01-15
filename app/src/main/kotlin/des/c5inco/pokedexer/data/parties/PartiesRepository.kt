package des.c5inco.pokedexer.data.parties

import des.c5inco.pokedexer.model.Party
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

class PartiesRepository @Inject constructor(
    private val partiesDao: PartiesDao
) {
    fun getAllPartiesFlow(): Flow<List<Party>> = partiesDao.getAllPartiesFlow()

    fun getAllPartiesWithMembersFlow(): Flow<List<PartyWithMembers>> = partiesDao.getAllPartiesWithMembersFlow()

    fun getPartyWithMembersFlow(partyId: Int): Flow<PartyWithMembers?> = partiesDao.getPartyWithMembersFlow(partyId)

    suspend fun createParty(name: String, pokemonIds: List<Int>) {
        partiesDao.createParty(name, pokemonIds)
    }

    suspend fun updateParty(partyId: Int, name: String, pokemonIds: List<Int>) {
        partiesDao.updateParty(partyId, name, pokemonIds)
    }

    suspend fun deleteParty(party: Party) {
        partiesDao.deleteParty(party)
    }
}
