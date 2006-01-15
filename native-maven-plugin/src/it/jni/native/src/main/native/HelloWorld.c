#include <stdio.h>
#include "HelloWorld.h"

JNIEXPORT void JNICALL Java_HelloWorld_sayHello
  (JNIEnv *env, jobject obj)
{
    printf("Hello Native World!");
}
