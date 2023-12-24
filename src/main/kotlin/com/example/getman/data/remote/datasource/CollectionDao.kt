package com.example.getman.data.remote.datasource

import com.example.getman.domain.model.CollectionModel
import com.example.getman.ui.main.model.JsonCollection
import com.example.getman.ui.main.model.KeyValueTableModel
import com.example.getman.ui.main.model.RequestModel
import com.example.getman.utils.BodyEnum
import com.example.getman.utils.HibernateUtil
import com.example.getman.utils.RequestEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Transaction

class CollectionDao {

    suspend fun add(collectionModel: CollectionModel) = withContext(Dispatchers.IO) {
        try {
            val session = HibernateUtil.sessionFactory.openSession()
            val tx: Transaction = session.beginTransaction()
            val sql = "INSERT INTO Collection (collectionId, name, userId) VALUES (NULL, :name, :userId)"
            val query = session.createNativeQuery(sql)

            query.setParameter("name", collectionModel.name)
            query.setParameter("userId", collectionModel.userId)

            query.executeUpdate()
            tx.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getAll(userId: Int): List<CollectionModel> = withContext(Dispatchers.IO) {
        try {
            val session = HibernateUtil.sessionFactory.openSession()
            val tx: Transaction = session.beginTransaction()
            val sqlQuery = """
              SELECT CL.collectionId, CL.name, RQ.requestId, RQ.url, RQ.requestType, BD.body, BD.bodyType, BD.Form,
                GROUP_CONCAT(DISTINCT PR.paramKey,'=', PR.value) AS Params,  
                            GROUP_CONCAT(DISTINCT HD.headerKey,'=', HD.value) AS Headers FROM Collection as CL 
                LEFT JOIN CollectionRequest as CR on CR.collectionId = CL.collectionId
                LEFT JOIN Request as RQ on RQ.requestId = CR.requestId
                LEFT JOIN  
                                  (SELECT Body.bodyId, Body.body, Body.bodyType, GROUP_CONCAT(DISTINCT FD.dataKey,'=', FD.value) as Form 
                                     FROM Body  
                                     LEFT JOIN FormData as FD ON FD.bodyId = Body.bodyId 
                                     GROUP BY Body.bodyId, Body.body, Body.bodyType)  
                                   as BD ON BD.bodyId = RQ.bodyId 
                                LEFT JOIN Param as PR ON PR.requestId = RQ.requestId 
                                LEFT JOIN Header as HD ON HD.requestId = RQ.requestId
                WHERE userId = $userId
                GROUP BY CL.collectionId, CL.name, RQ.requestId, RQ.url, RQ.requestType, BD.body, BD.bodyType, BD.Form
            """.trimIndent()

            val query = session.createNativeQuery(sqlQuery)
            val result = query.resultList
            val idMap = mutableMapOf<Int, Boolean>()
            val collections = mutableListOf<CollectionModel>()
            for (row in result) {
                val rowArray = row as Array<*>

                val collectionId = rowArray[0] as Int
                val name = rowArray[1] as String
                val requestId = rowArray[2] as Int?
                val url = rowArray[3] as String?
                val requestType = (rowArray[4] as String?)?.let { RequestEnum.valueOf(it) }
                val body = rowArray[5] as String?
                val bodyType = BodyEnum.valueOf(rowArray[6] as String? ?: "NONE")
                val form = rowArray[7] as String?
                val params = rowArray[8] as String?
                val headers = rowArray[9] as String?

                val formList = form?.split(",")?.map { KeyValueTableModel(it.split("=")[0], it.split("=")[1]) }
                val paramsList = params?.split(",")?.map { KeyValueTableModel(it.split("=")[0], it.split("=")[1]) }
                val headersList = headers?.split(",")?.map { KeyValueTableModel(it.split("=")[0], it.split("=")[1]) }

                val requestModel = if (requestId != null) RequestModel(
                    requestId,
                    requestType!!,
                    url!!,
                    paramsList ?: emptyList(),
                    headersList ?: emptyList(),
                    bodyType,
                    formList ?: emptyList(),
                    body
                ) else null

                if (idMap[collectionId] == true) {
                    requestModel?.let {
                        collections.firstOrNull { it.id!! == collectionId }?.requests?.add(requestModel)
                    }
                } else {
                    collections.add(
                        CollectionModel(
                            id = collectionId,
                            userId = userId,
                            name = name,
                            requests = if (requestId == null) mutableListOf() else mutableListOf(requestModel!!)
                        )
                    )
                }
                idMap[collectionId] = true
            }

            tx.commit()
            return@withContext collections
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    suspend fun deleteCollection(collectionId: Int) =
        withContext(Dispatchers.IO) {
            try {
                val session = HibernateUtil.sessionFactory.openSession()
                val tx: Transaction = session.beginTransaction()
                val sql = "DELETE FROM Collection WHERE Collection.collectionId = :collectionId"
                val query = session.createNativeQuery(sql)

                query.setParameter("collectionId", collectionId)

                query.executeUpdate()

                tx.commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun deleteCollectionRequest(collectionId: Int, requestId: Int) = withContext(Dispatchers.IO) {
        try {
            val session = HibernateUtil.sessionFactory.openSession()
            val tx: Transaction = session.beginTransaction()
            val sql =
                "DELETE FROM CollectionRequest WHERE CollectionRequest.collectionId = :collectionId AND CollectionRequest.requestId = :requestId"
            val query = session.createNativeQuery(sql)

            query.setParameter("requestId", requestId)
            query.setParameter("collectionId", collectionId)

            query.executeUpdate()

            tx.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun add(jsonCollection: JsonCollection, userId: Int) = withContext(Dispatchers.IO) {
        try {
            print("--------------------------------------------")
            val session = HibernateUtil.sessionFactory.openSession()
            val tx: Transaction = session.beginTransaction()
            val sql = "INSERT INTO Collection (collectionId, name, userId) VALUES (NULL, :name, :userId)"
            val query = session.createNativeQuery(sql)

            query.setParameter("name", jsonCollection.name)
            query.setParameter("userId", userId)

            query.executeUpdate()

            val collectionId = session.createNativeQuery("SELECT LAST_INSERT_ID()").uniqueResult() as Long

            jsonCollection.requests.forEach { requestModel ->
                var bodyId: Long? = null
                if (requestModel.bodyType != BodyEnum.NONE) {
                    val sql2 = "INSERT INTO Body (bodyId, bodyType, body) VALUES (NULL, :bodyType, :body)"
                    val query2 = session.createNativeQuery(sql2)

                    query2.setParameter("bodyType", requestModel.bodyType.name)
                    query2.setParameter("body", requestModel.body)

                    query2.executeUpdate()

                    bodyId = session.createNativeQuery("SELECT LAST_INSERT_ID()").uniqueResult() as Long

                    requestModel.formData.forEach {
                        val insertFormData =
                            "INSERT INTO FormData (formDataId, bodyId, dataKey, value) VALUES (NULL, :bodyId, :dataKey, :value)"
                        val insertFormDataQuery = session.createNativeQuery(insertFormData)

                        insertFormDataQuery.setParameter("bodyId", bodyId)
                        insertFormDataQuery.setParameter("dataKey", it.getKeyString())
                        insertFormDataQuery.setParameter("value", it.getValueString())
                        insertFormDataQuery.executeUpdate()
                    }
                }
                val insertRequest =
                    "INSERT INTO Request (requestId, url, requestType, bodyId) VALUES (NULL, :url, :requestType, :bodyId);"
                val query2 = session.createNativeQuery(insertRequest)

                query2.setParameter("url", requestModel.url)
                query2.setParameter("requestType", requestModel.requestType.name)
                query2.setParameter("bodyId", bodyId)

                query2.executeUpdate()

                val requestId = session.createNativeQuery("SELECT LAST_INSERT_ID()").uniqueResult() as Long

                requestModel.params.forEach {
                    val insertParam =
                        "INSERT INTO Param (paramId, requestId, paramKey, value) VALUES (NULL, :requestId, :paramKey, :value)"
                    val insertParamDataQuery = session.createNativeQuery(insertParam)

                    insertParamDataQuery.setParameter("requestId", requestId)
                    insertParamDataQuery.setParameter("paramKey", it.getKeyString())
                    insertParamDataQuery.setParameter("value", it.getValueString())
                    insertParamDataQuery.executeUpdate()
                }

                requestModel.headers.forEach {
                    val insertHeader =
                        "INSERT INTO Header (headerId, requestId, headerKey, value) VALUES (NULL, :requestId, :headerKey, :value)"
                    val insertHeaderDataQuery = session.createNativeQuery(insertHeader)

                    insertHeaderDataQuery.setParameter("requestId", requestId)
                    insertHeaderDataQuery.setParameter("headerKey", it.getKeyString())
                    insertHeaderDataQuery.setParameter("value", it.getValueString())
                    insertHeaderDataQuery.executeUpdate()
                }

                val insertRequestCollection =
                    "INSERT INTO CollectionRequest (collectionId, requestId) VALUES (:collectionId, :requestId)"
                val insertCollectionDataQuery = session.createNativeQuery(insertRequestCollection)

                insertCollectionDataQuery.setParameter("collectionId", collectionId)
                insertCollectionDataQuery.setParameter("requestId", requestId)
                insertCollectionDataQuery.executeUpdate()
            }

            tx.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}