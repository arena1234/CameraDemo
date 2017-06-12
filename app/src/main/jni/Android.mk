LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE        := libopengl_jni
LOCAL_CFLAGS        := -Werror

LOCAL_C_INCLUDES    :=  $(LOCAL_PATH)

LOCAL_SRC_FILES     :=  gl/gl_matrix.cpp       \
                        gl/gl_base.cpp         \
                        gl/gl_camera.cpp       \
                        ndk_cpp.cpp

LOCAL_LDLIBS        += -llog -lGLESv3 -lEGL -ljnigraphics -lm

include $(BUILD_SHARED_LIBRARY)
