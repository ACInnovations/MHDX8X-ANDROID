# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

-dontwarn android.support.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-dontwarn okio.**

-keep class com.facebook.** {*;}
-dontwarn com.facebook.**

-keep class com.firebase.** { *; }
-dontwarn com.firebase.**

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.**{*;}

-keep class android.support.v7.widget.SearchView { *; }

-keep class com.paypal.** {*;}
-dontwarn com.paypal.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembernames class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}
-keepnames class androidx.navigation.fragment.NavHostFragment
-keepnames class androidx.fragment.app.FragmentContainerView
