# NexShizuku ProGuard Rules

# Keep AIDL interfaces
-keep interface com.nexshizuku.ipc.** { *; }

# Keep parcelable classes
-keep class com.nexshizuku.ipc.** implements android.os.Parcelable { *; }

# Keep model classes
-keep class com.nexshizuku.core.model.** { *; }

# Keep design system
-keep class com.nexshizuku.designsystem.** { *; }

# Keep feature ViewModels
-keep class com.nexshizuku.feature.**ViewModel { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep serialization
-keep class kotlinx.serialization.** { *; }

# Keep Room (if used)
-keep class androidx.room.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep Material3
-keep class androidx.compose.material3.** { *; }

# Keep Navigation
-keep class androidx.navigation.** { *; }
