package com.example.getman.utils

import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

object HibernateUtil {
    val sessionFactory: SessionFactory

    init {
        try {
            sessionFactory = Configuration().configure().buildSessionFactory()
        } catch (e: Exception) {
            throw ExceptionInInitializerError(e)
        }
    }
}