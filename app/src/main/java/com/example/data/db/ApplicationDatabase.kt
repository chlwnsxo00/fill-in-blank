import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.db.dao.InnerIndexDao
import com.example.data.db.dao.MainIndexDao
import com.example.data.db.entity.InnerIndexEntity
import com.example.data.db.entity.MainIndexEntity

@Database(entities = [MainIndexEntity::class, InnerIndexEntity::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun mainIndexDao(): MainIndexDao
    abstract fun innerIndexDao(): InnerIndexDao

    companion object {
        @Volatile
        private var instance: ApplicationDatabase? = null

        fun getInstance(context: Context): ApplicationDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, ApplicationDatabase::class.java, "ApplicationDB.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
