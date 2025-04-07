package com.mrsomething.daneplay.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mrsomething.daneplay.data.entity.DanceDef
import com.mrsomething.daneplay.data.entity.MusicDanceMapping

data class MappingWithDance(
    @Embedded val mapping: MusicDanceMapping,
    @Relation(
        parentColumn = "dance_id",
        entityColumn = "dance_id"
    )
    val dance: DanceDef
)

