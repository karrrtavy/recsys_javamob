package data.database.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class DiaryEntryWithProduct {
    @Embedded
    public DiaryEntry entry;

    @Relation(
            parentColumn = "productId",
            entityColumn = "id"
    )
    public Product product;
}
