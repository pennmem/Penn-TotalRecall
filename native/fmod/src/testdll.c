#include <windows.h> 
#include <stdio.h> 

#ifdef __cplusplus
#error plain old C
#endif

typedef int (__cdecl *StartPlaybackProc)(char*, long long, long long);
typedef long long (__cdecl *StopPlaybackProc) (void);

int main()
{
    HINSTANCE hinstLib; 
    StartPlaybackProc StartPlayback;
	StopPlaybackProc StopPlayback;
    BOOL fFreeResult = FALSE; 
 
    // Get a handle to the DLL module. 
    hinstLib = LoadLibrary(TEXT("penntotalrecall.dll")); 
 
    // If the handle is valid, try to get the function address. 
    if (hinstLib != NULL) { 
        StartPlayback = (StartPlaybackProc) GetProcAddress(hinstLib, "startPlayback");
		StopPlayback = (StopPlaybackProc) GetProcAddress(hinstLib, "stopPlayback");
 
        // If the function address is valid, call the function.
        if (NULL != StartPlayback && NULL != StopPlayback) {
            StartPlayback("..\\..\\..\\..\\dev\\audio_tests\\BeepTest.wav", 0, 88200);
			Sleep(3000);
			StopPlayback();
        }
		else {
			printf("Failure: LoadLibrary() succeeded, but unable to retrieve function/s\n");
		}

        // Free the DLL module. 
        fFreeResult = FreeLibrary(hinstLib);
		if (fFreeResult == FALSE) {
			printf("Unable to free library\n");
		}
    }
	else {
		printf("Failure: LoadLibrary() returned NULL\n");
	}
	system("pause");
}
