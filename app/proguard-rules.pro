# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Сохраняем имена классов, реализующих NavKey.
# Это необходимо для аналитики (событие screen_view), так как мы используем
# simpleName класса в качестве имени экрана (screen_name).
# Если это правило удалить, R8 обфусцирует имена классов (например, в "a", "b"),
# и в аналитике будет невозможно разобрать, какой экран открывал пользователь.
-keepnames class * implements androidx.navigation3.runtime.NavKey
