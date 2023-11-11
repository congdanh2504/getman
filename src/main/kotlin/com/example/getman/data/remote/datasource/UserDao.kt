package com.example.getman.data.remote.datasource

import com.example.getman.data.remote.model.UserEntity
import com.example.getman.data.remote.model.toUser
import com.example.getman.domain.model.User
import com.example.getman.utils.HibernateUtil.sessionFactory
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session

class UserDao {

    suspend fun saveUser(user: UserEntity): Boolean = withContext(Dispatchers.IO) {
        val session: Session = sessionFactory.openSession()
        var isSuccess = false
        try {
            session.beginTransaction()
            session.persist(user)
            session.transaction.commit()
            isSuccess = true
        } catch (e: Exception) {
            session.transaction.rollback()
        } finally {
            session.close()
        }
        isSuccess
    }

    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        try {
            val session: Session = sessionFactory.openSession()
            val builder: CriteriaBuilder = session.criteriaBuilder
            val criteria: CriteriaQuery<UserEntity> = builder.createQuery(UserEntity::class.java)
            val root: Root<UserEntity> = criteria.from(UserEntity::class.java)

            criteria.select(root).where(builder.equal(root.get<String>("email"), email))
            val query = session.createQuery(criteria)
            val userEntity = query.uniqueResult()

            session.close()
            userEntity.toUser()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserByEmailAndPassword(email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            val session: Session = sessionFactory.openSession()
            val builder: CriteriaBuilder = session.criteriaBuilder
            val criteria: CriteriaQuery<UserEntity> = builder.createQuery(UserEntity::class.java)
            val root: Root<UserEntity> = criteria.from(UserEntity::class.java)

            criteria.select(root)
                .where(
                    builder.and(
                        builder.equal(root.get<String>("email"), email),
                        builder.equal(root.get<String>("password"), password)
                    )
                )
            val query = session.createQuery(criteria)
            val userEntity = query.uniqueResult()

            session.close()
            userEntity.toUser()
        } catch (e: Exception) {
            null
        }
    }

}