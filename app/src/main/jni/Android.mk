LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := Keys
LOCAL_SRC_FILES := Keys.c

include $(BUILD_SHARED_LIBRARY)