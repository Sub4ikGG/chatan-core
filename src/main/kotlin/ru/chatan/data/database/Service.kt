package ru.chatan.data.database

import org.jetbrains.exposed.sql.Database


abstract class Service {
    abstract val database: Database
}