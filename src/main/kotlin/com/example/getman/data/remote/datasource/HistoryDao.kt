package com.example.getman.data.remote.datasource

import com.example.getman.ui.main.model.HistoryRequest
import com.example.getman.ui.main.model.KeyValueTableModel
import com.example.getman.ui.main.model.RequestModel
import com.example.getman.utils.BodyEnum
import com.example.getman.utils.HibernateUtil.sessionFactory
import com.example.getman.utils.RequestEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session
import org.hibernate.Transaction
import java.sql.Timestamp


@Suppress("DEPRECATION")
class HistoryDao {

    suspend fun getHistoryRequests(userId: Int): List<HistoryRequest> = withContext(Dispatchers.IO) {
        val session: Session = sessionFactory.openSession()
        try {
            session.beginTransaction()

            val sqlQuery = """
               SELECT RH.requestId, RH.url, RH.requestType, BD.body, BD.bodyType, BD.Form, 
                HQ.time,
                GROUP_CONCAT(DISTINCT PR.paramKey,'=', PR.value) AS Params,  
                GROUP_CONCAT(DISTINCT HD.headerKey,'=', HD.value) AS Headers
                FROM Request as RH  
                LEFT JOIN  
                    (SELECT Body.bodyId, Body.body, Body.bodyType, GROUP_CONCAT(DISTINCT FD.dataKey,'=', FD.value) as Form 
                    FROM Body  
                    LEFT JOIN FormData as FD ON FD.bodyId = Body.bodyId 
                    GROUP BY Body.bodyId, Body.body, Body.bodyType)  
                   as BD ON BD.bodyId = RH.bodyId 
                LEFT JOIN Param as PR ON PR.requestId = RH.requestId 
                LEFT JOIN Header as HD ON HD.requestId = RH.requestId 
                LEFT JOIN HistoryRequest as HQ ON HQ.requestId = RH.requestId
                WHERE HQ.userId = $userId
                GROUP BY RH.requestId, RH.url, RH.requestType, BD.body, BD.bodyType, BD.Form, HQ.time
                ORDER BY HQ.time DESC
            """.trimIndent()

            val query = session.createNativeQuery(sqlQuery)
            val result = query.resultList

            session.transaction.commit()

            val historyRequests = mutableListOf<HistoryRequest>()

            for (row in result) {
                val rowArray = row as Array<*>

                val id = rowArray[0] as Int
                val url = rowArray[1] as String
                val requestType = RequestEnum.valueOf(rowArray[2] as String)
                val body = rowArray[3] as String?
                val bodyType = BodyEnum.valueOf(rowArray[4] as String? ?: "NONE")
                val form = rowArray[5] as String?
                val params = rowArray[7] as String?
                val headers = rowArray[8] as String?
                val time = rowArray[6] as Timestamp

                // Convert form, params, and headers to List<KeyValueTableModel>
                val formList = form?.split(",")?.map { KeyValueTableModel(it.split("=")[0], it.split("=")[1]) }
                val paramsList = params?.split(",")?.map { KeyValueTableModel(it.split("=")[0], it.split("=")[1]) }
                val headersList = headers?.split(",")?.map { KeyValueTableModel(it.split("=")[0], it.split("=")[1]) }

                // Create RequestModel
                val requestModel = RequestModel(
                    id,
                    requestType,
                    url,
                    paramsList ?: emptyList(),
                    headersList ?: emptyList(),
                    bodyType,
                    formList ?: emptyList(),
                    body
                )

                historyRequests.add(HistoryRequest(time, requestModel))
            }
            return@withContext historyRequests
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    suspend fun add(requestModel: RequestModel, userId: Int, isAddingToCollection: Boolean = false, collectionId: Int? = null) = withContext(Dispatchers.IO) {
        try {
            val session = sessionFactory.openSession()
            val tx: Transaction = session.beginTransaction()
            var bodyId: Long? = null
            if (requestModel.bodyType != BodyEnum.NONE) {
                val sql = "INSERT INTO Body (bodyId, bodyType, body) VALUES (NULL, :bodyType, :body)"
                val query = session.createNativeQuery(sql)

                query.setParameter("bodyType", requestModel.bodyType.name)
                query.setParameter("body", requestModel.body)

                query.executeUpdate()

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

            val insertHistory =
                "INSERT INTO HistoryRequest (requestId, userId, time) VALUES (:requestId, :userId, CURRENT_TIMESTAMP)"
            val insertHistoryDataQuery = session.createNativeQuery(insertHistory)

            insertHistoryDataQuery.setParameter("requestId", requestId)
            insertHistoryDataQuery.setParameter("userId", userId)
            insertHistoryDataQuery.executeUpdate()

            if (isAddingToCollection) {
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