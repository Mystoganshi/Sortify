# Keep ML Kit / TFLite classes safe in release builds (you can tighten later)
-keep class com.google.mlkit.** { *; }
-keep class org.tensorflow.** { *; }
