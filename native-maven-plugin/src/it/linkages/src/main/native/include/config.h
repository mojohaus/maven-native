#ifndef config_h
#define config_h


#ifdef  BUILD_DLL
#  include <windows.h>
#  define DLL_EXPORT  __declspec(dllexport)
#else
#  define DLL_EXPORT
#endif

#endif