#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_mqqtsample_Keys_00024Companion_getClientMqtt(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "solaxxxxx");
}

JNIEXPORT jstring JNICALL
Java_com_example_mqqtsample_Keys_00024Companion_getPassMqtt(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "xxxxx");
}

