package com.example.getman.data.remote.datasource

import com.example.getman.data.remote.model.UserEntity
import com.example.getman.utils.HibernateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session

class UserDao {

    suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.IO) {
        val session: Session = HibernateUtil.sessionFactory.openSession()
        session.beginTransaction()
        val id = session.persist(user)
        session.transaction.commit()
        session.close()
        print(id.toString())
    }
}