package des.c5inco.pokedexer.data.parties

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import des.c5inco.pokedexer.model.Party
import des.c5inco.pokedexer.model.PartyMember
import des.c5inco.pokedexer.model.Pokemon
import kotlinx.coroutines.flow.Flow

data class PartyWithMembers(
    @Embedded val party: Party,
    @Relation(
        parentColumn = "id",
        entityColumn = "partyId"
    )
    val members: List<PartyMember>
)

@Dao
interface PartiesDao {
    @Query("SELECT * FROM parties")
    fun getAllPartiesFlow(): Flow<List<Party>>

    @Transaction
    @Query("SELECT * FROM parties")
    fun getAllPartiesWithMembersFlow(): Flow<List<PartyWithMembers>>

    @Transaction
    @Query("SELECT * FROM parties WHERE id = :partyId")
    fun getPartyWithMembersFlow(partyId: Int): Flow<PartyWithMembers?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: Party): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartyMember(partyMember: PartyMember)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartyMembers(partyMembers: List<PartyMember>)

    @Delete
    suspend fun deleteParty(party: Party)

    @Query("DELETE FROM party_members WHERE partyId = :partyId")
    suspend fun deletePartyMembers(partyId: Int)

    @Transaction
    suspend fun createParty(name: String, pokemonIds: List<Int>) {
        val partyId = insertParty(Party(name = name)).toInt()
        val members = pokemonIds.mapIndexed { index, pokemonId ->
            PartyMember(partyId, pokemonId, index)
        }
        insertPartyMembers(members)
    }

    @Transaction
    suspend fun updateParty(partyId: Int, name: String, pokemonIds: List<Int>) {
        insertParty(Party(id = partyId, name = name))
        deletePartyMembers(partyId)
        val members = pokemonIds.mapIndexed { index, pokemonId ->
            PartyMember(partyId, pokemonId, index)
        }
        insertPartyMembers(members)
    }
}
