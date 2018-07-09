/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_fuwei_asr_SpeechTranscript_modular_service_JniShmService */

#ifndef _Included_com_fuwei_asr_SpeechTranscript_modular_service_JniShmService
#define _Included_com_fuwei_asr_SpeechTranscript_modular_service_JniShmService
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_fuwei_asr_SpeechTranscript_modular_service_JniShmService
 * Method:    JNI_shmInit
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_fuwei_asr_SpeechTranscript_modular_service_JniShmService_JNI_1shmInit
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_fuwei_asr_SpeechTranscript_modular_service_JniShmService
 * Method:    JNI_readSpeechRecordShm
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_fuwei_asr_SpeechTranscript_modular_service_JniShmService_JNI_1readSpeechRecordShm
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_fuwei_asr_SpeechTranscript_modular_service_JniShmService
 * Method:    JNI_writeTextRecordShm
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_fuwei_asr_SpeechTranscript_modular_service_JniShmService_JNI_1writeTextRecordShm
  (JNIEnv *, jobject, jint, jstring);

/*
 * Class:     com_fuwei_asr_SpeechTranscript_modular_service_JniShmService
 * Method:    JNI_shmTerm
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fuwei_asr_SpeechTranscript_modular_service_JniShmService_JNI_1shmTerm
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif