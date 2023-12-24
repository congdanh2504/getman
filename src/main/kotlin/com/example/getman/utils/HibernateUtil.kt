package com.example.getman.utils

import com.example.getman.data.remote.model.RequestEntity
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

object HibernateUtil {
    val sessionFactory: SessionFactory

    init {
        try {
            sessionFactory =
                Configuration().addAnnotatedClass(RequestEntity::class.java).configure().buildSessionFactory()
        } catch (e: Exception) {
            throw ExceptionInInitializerError(e)
        }
    }
}