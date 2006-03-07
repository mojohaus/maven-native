#include <stdio.h>
#include "HelloWorld.h"

JNIEXPORT jstring JNICALL Java_HelloWorld_sayHello( JNIEnv *env, jobject obj )
{
	jstring value;           /* the return value */

	char buf[40];            /* working buffer (really only need 20 ) */


	sprintf ( buf, "%s", "Hello Native World!" );

	value = (*env)->NewStringUTF( env, buf );

	return value;
}
