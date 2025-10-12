package com.br.linecut.data.repository

import com.br.linecut.data.models.Company
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositório para gerenciar dados de empresas/lanchonetes
 */
class CompanyRepository {

    private val database = FirebaseDatabase.getInstance().getReference("empresas")
    private val storage = FirebaseStorage.getInstance()

    /**
     * Obtém todas as empresas do Firebase Realtime Database
     * @return Flow com lista de empresas
     */
    fun getCompanies(): Flow<List<Company>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val companies = mutableListOf<Company>()

                for (childSnapshot in snapshot.children) {
                    try {
                        val company = childSnapshot.getValue(Company::class.java)
                        if (company != null) {
                            // Adicionar o ID do snapshot ao objeto company
                            val companyWithId = company.copy(id = childSnapshot.key ?: company.id)
                            companies.add(companyWithId)
                        }
                    } catch (e: Exception) {
                        // Log detalhado do erro mas continua processando outras empresas
                        println("Erro ao deserializar empresa ${childSnapshot.key}: ${e.message}")
                        e.printStackTrace()
                    }
                }

                trySend(companies)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)

        awaitClose {
            database.removeEventListener(listener)
        }
    }

    /**
     * Obtém uma empresa específica por ID
     * @param id ID da empresa
     * @return Company ou null se não encontrada
     */
    suspend fun getCompanyById(id: String): Company? {
        return try {
            val snapshot = database.child(id).get().await()
            snapshot.getValue(Company::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}