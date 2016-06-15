#include <jni.h>
#include <string.h>
#include "HelloWorldC.h"

JNIEXPORT jstring Java_com_example_harshalbenake_ndkhelloworld_MainActivity_getMessage(JNIEnv *env, jobject thisObj) {
   return (*env)->NewStringUTF(env, "Hello World from native code running successfully");
}