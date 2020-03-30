###################################################
# Header files
###################################################

;----------------------------------------------------------------
; include Modern UI version 2
;----------------------------------------------------------------
!include 'MUI2.nsh'
!include 'Sections.nsh'
!include 'LogicLib.nsh'

###################################################
# Defines
###################################################

;------------------------------------
; product info
;------------------------------------

;!define INTERNAL_VERSION '9.1.0'
;!define INFILE_PATH 'cubridmanager'
;!define OUTFILE_PATH '.'

!define PRODUCT_NAME 'CUBRID Admin'
!define PRODUCT_VERSION '10.2.0'
!define PRODUCT_EXE_NAME 'cubridadmin'
!define INSTALLER_ARCH 'x64'

!define SHORTCUT_NAME '${PRODUCT_NAME}'
!define START_MENU_FOLDER 'CUBRID'
!define UNINSTALL_NAME 'Uninstall ${PRODUCT_NAME}'

;------------------------------------
; install files info
;------------------------------------

;!define RELEASE_FILE 'CUBRID_Manager_8.4.3_ReleaseNotes.html'

###################################################
# General install information
###################################################

Name '${PRODUCT_NAME} ${PRODUCT_VERSION}'
OutFile '${OUTFILE_PATH}\CUBRIDAdmin-${INTERNAL_VERSION}-windows-x64.exe'
BrandingText 'http://www.cubrid.org'

;---------------------------------------------------
; request application privileges for Windows Vista
;---------------------------------------------------

RequestExecutionLevel user

###################################################
# Interface settings
###################################################

;---------------------------------------------------
; installer icon
;---------------------------------------------------

!define MUI_ICON 'cubrid_installer.ico'

;---------------------------------------------------
; header image
;---------------------------------------------------

!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP 'installer_header.bmp'
!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH

;---------------------------------------------------
; installer welcome/finish page
;---------------------------------------------------

!define MUI_WELCOMEFINISHPAGE_BITMAP 'welcome_finish.bmp'
!define MUI_WELCOMEFINISHPAGE_BITMAP_NOSTRETCH

;---------------------------------------------------
; small desc area
;---------------------------------------------------

!define MUI_COMPONENTSPAGE_SMALLDESC

;---------------------------------------------------
; show warning when user wants to close installer
; or un-installer
;---------------------------------------------------

!define MUI_ABORTWARNING
!define MUI_UNABORTWARNING

;---------------------------------------------------
; don't automatically jump to finish page,
; allow user to check the install/un-install log.
;---------------------------------------------------

!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

;---------------------------------------------
; show all languages, despite user's codepage
;---------------------------------------------

!define MUI_LANGDLL_ALLLANGUAGES

;---------------------------------------------
; remember the installer language
;---------------------------------------------

;!define MUI_LANGDLL_REGISTRY_ROOT 'HKLM'
;!define MUI_LANGDLL_REGISTRY_KEY "Software\Modern UI Test"
;!define MUI_LANGDLL_REGISTRY_VALUENAME "Installer Language"

###################################################
# Page settings
###################################################

;-------------------------------
; welcome page settings
;-------------------------------

;!define MUI_WELCOMEPAGE_TITLE $(message_welcome)
!define MUI_WELCOMEPAGE_TITLE_3LINES
;!define MUI_WELCOMEPAGE_TEXT ''

;-------------------------------
; directory page settings
;-------------------------------

!define MUI_DIRECTORYPAGE_TEXT_TOP $(msgDirTextTop)

;-------------------------------
; finish page settings
;-------------------------------

;!define MUI_FINISHPAGE_SHOWREADME '$INSTDIR/${RELEASE_FILE}'
;!define MUI_FINISHPAGE_SHOWREADME_TEXT $(textShowReadme)

###################################################
# Install pages
###################################################

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE 'COPYING'
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY

!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
; languages page
;--------------------------------

!insertmacro MUI_LANGUAGE 'English'
!insertmacro MUI_LANGUAGE 'SimpChinese'
!insertmacro MUI_LANGUAGE 'Korean'

###################################################
# text files of different language
###################################################

!include 'install_text.en'
!include 'install_text.zh'
!include 'install_text.kr'

###################################################
# Install section
###################################################

Section 'CUBRIDAdmin' SEC_CUBRIDManager
  Call WindowsArchCheck
  Call CUBRIDManagerExist

  SectionIn RO ; section read only

  SetOutPath '$INSTDIR'
  ;SetOverwrite try
  File /r '${INFILE_PATH}\configuration'
  File /r '${INFILE_PATH}\driver'
  File /r '${INFILE_PATH}\plugins'
  File /r '${INFILE_PATH}\features'
  File /r '${INFILE_PATH}\dropins'
  File /r '${INFILE_PATH}\jre'
  File /r '${INFILE_PATH}\p2'
  File '${INFILE_PATH}\${PRODUCT_EXE_NAME}.exe'
  File '${INFILE_PATH}\${PRODUCT_EXE_NAME}.ini'

  CreateDirectory $INSTDIR
  Call regCUBRIDManager

  WriteUninstaller '$INSTDIR\Uninstall.exe'
  Call regUninstallInfo
SectionEnd

;Section 'Release notes' SEC_RELEASE
  ;SetOutPath '$INSTDIR'
  ;SetOverwrite try
  ;File '${RELEASE_FILE}'
;SectionEnd

;Section 'Documentation' SEC_DOC
  ;SetOutPath '$INSTDIR'
  ;SetOverwrite try
  ;File '${DOC_FILE}'
;SectionEnd

SectionGroup /e $(descSec_Shortcuts) SEC_SHORTCUTS
  Section $(descSec_DesktopShortcut) SEC_DESKTOP_SHORTCUT
    ; Create desktop shortcut
    CreateShortCut '$DESKTOP\${SHORTCUT_NAME}.lnk' '$INSTDIR\${PRODUCT_EXE_NAME}.exe' ''
  SectionEnd

  Section $(descSec_StartmenuShortcut) SEC_STARTMENU_SHORTCUT
    ; Create start-menu shortcut
    CreateDirectory '$SMPROGRAMS\${START_MENU_FOLDER}'
    CreateShortCut '$SMPROGRAMS\${START_MENU_FOLDER}\${SHORTCUT_NAME}.lnk' '$INSTDIR\${PRODUCT_EXE_NAME}.exe' ''
    CreateShortCut '$SMPROGRAMS\${START_MENU_FOLDER}\${UNINSTALL_NAME}.lnk' '$INSTDIR\Uninstall.exe' ''
  SectionEnd

  Section /o $(descSec_QuicklaunchShortcut) SEC_QUICKLAUNCH_SHORTCUT
    ; Create quick launch shortcut
    CreateShortCut '$QUICKLAUNCH\${SHORTCUT_NAME}.lnk' '$INSTDIR\${PRODUCT_EXE_NAME}.exe' ''
  SectionEnd
SectionGroupEnd

#string replace macro define
!macro StrReplaceConstructor ORIGINAL_STRING TO_REPLACE REPLACE_BY
  Push "${ORIGINAL_STRING}"
  Push "${TO_REPLACE}"
  Push "${REPLACE_BY}"
  Call StrRep
  Pop $0
!macroend

!define StrReplace '!insertmacro "StrReplaceConstructor"'

;--------------------------------
; section descriptions
;--------------------------------

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC_CUBRIDManager} $(descSec_Driver)
  ;!insertmacro MUI_DESCRIPTION_TEXT ${SEC_RELEASE} $(desc_sec_release_notes)
  ;!insertmacro MUI_DESCRIPTION_TEXT ${SEC_DOC} $(desc_sec_doc)
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC_SHORTCUTS} $(descSec_Shortcuts)
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC_DESKTOP_SHORTCUT} $(descSec_DesktopShortcut)
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC_STARTMENU_SHORTCUT} $(descSec_StartmenuShortcut)
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC_QUICKLAUNCH_SHORTCUT} $(descSec_QuicklaunchShortcut)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

###################################################
# Install functions
###################################################

;---------------------------------------------------------------------
; At initialization, handle Win7&Vista UAC, and set install directory
;---------------------------------------------------------------------

Function .onInit
  ; UAS plug-in init
  UAC_Elevate:
    UAC::RunElevated
    StrCmp 1223 $0 UAC_ElevationAborted ; UAC dialog aborted by user?
    StrCmp 0 $0 0 UAC_Err ; Error?
    StrCmp 1 $1 0 UAC_Success ;Are we the real deal or just the wrapper?
    Quit

  UAC_Err:
    MessageBox mb_iconstop "Unable to elevate, error $0"
    Abort

  UAC_ElevationAborted:
    # elevation was aborted, run as normal?
    MessageBox mb_iconstop "This installer requires admin access, aborting!"
    Abort

  UAC_Success:
    StrCmp 1 $3 +4 ;Admin?
    StrCmp 3 $1 0 UAC_ElevationAborted ;Try again?
    MessageBox mb_iconstop "This installer requires admin access, try again"
    goto UAC_Elevate

  !insertmacro MUI_LANGDLL_DISPLAY ; multi-language select

  ReadEnvStr $R1 'CUBRID'

  StrCmp $R1 '' 0 lable_cubrid_env_set_x86
    Strcpy $R2 $WINDIR 2
    StrCpy $INSTDIR '$R2\CUBRID\cubridadmin'
    Return
  lable_cubrid_env_set_x86:
    ${StrReplace} $R1 "//" "\"
  	Strcpy $R3 $0
    StrCpy $INSTDIR '$R3\cubridadmin'
    Return
FunctionEnd

;---------------------------------------------------------------------
; Check if the os arch is x64
;---------------------------------------------------------------------

Function WindowsArchCheck
 GetVersion::WindowsPlatformArchitecture
  Pop $R0
  StrCmp '32' $R0 isX86 isX64
  isX86:
    MessageBox mb_iconstop $(msgWindowsArchError)
    Quit
  isX64:
FunctionEnd

;---------------------------------------------------------------------
; Check if the same version CUBRIDManager is already installed
; on this system
;---------------------------------------------------------------------

Function CUBRIDManagerExist
  SetRegView 64

  ; check CUBRIDManager version
  ReadRegStr $R0 HKLM 'Software\${PRODUCT_NAME}\${INTERNAL_VERSION}' 'Version'
  StrCmp $R0 '' label_continue
  StrCmp $R0 '${INTERNAL_VERSION}' 0 label_continue
    MessageBox MB_OKCANCEL|MB_ICONQUESTION $(msgAlreadyInstalled) IDOK label_continue IDCANCEL label_abort
      label_abort:
        Quit
    label_continue:
FunctionEnd

;-----------------------------------------------------
; Register CUBRIDManager into Windows Registry
;-----------------------------------------------------

Function regCUBRIDManager
  SetRegView 64

  StrCpy $R0 'Software\${PRODUCT_NAME}\${INTERNAL_VERSION}'

  WriteRegStr HKLM $R0 'ROOT_PATH' '$INSTDIR\${PRODUCT_NAME} ${PRODUCT_VERSION}'
  WriteRegStr HKLM $R0 'Version' '${INTERNAL_VERSION}'
  ;WriteRegDWORD HKLM $R1 'UsageCount' 0x1
FunctionEnd

Function regUninstallInfo
  StrCpy $R0 'Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME} ${PRODUCT_VERSION}'

  SetRegView 64

  WriteRegStr HKLM $R0 'DisplayName' '${PRODUCT_NAME} ${PRODUCT_VERSION}'
  WriteRegStr HKLM $R0 'UninstallString' '$INSTDIR\Uninstall.exe'

  WriteRegStr HKLM $R0 'Publisher' 'CUBRID'
  WriteRegStr HKLM $R0 'DisplayVersion' '${INTERNAL_VERSION}'
  WriteRegStr HKLM $R0 'HelpLink' 'http://www.cubrid.org/'

  WriteRegDWORD HKLM $R0 'NoModify' 0x1
  WriteRegDWORD HKLM $R0 'NoRepair' 0x1
FunctionEnd

Function .OnInstFailed
    UAC::Unload ;Must call unload!
FunctionEnd

Function .OnInstSuccess
    UAC::Unload ;Must call unload!
FunctionEnd

###################################################
# Uninstall sections
###################################################

Section 'Uninstall'
  Call un.DetectProcessRunning

  ;Delete installed files
  Delete '$INSTDIR\${PRODUCT_EXE_NAME}.exe'
  Delete '$INSTDIR\${PRODUCT_EXE_NAME}.ini'
  Delete '$INSTDIR\launcher.exe'
  Delete '$INSTDIR\.eclipseproduct'

  RMDir /r '$INSTDIR\configuration'
  RMDir /r '$INSTDIR\driver'
  RMDir /r '$INSTDIR\plugins'
  RMDir /r '$INSTDIR\features'
  RMDir /r '$INSTDIR\dropins'
  RMDir /r '$INSTDIR\p2'
  RMDir /r '$INSTDIR\logs'
  RMDir /r '$INSTDIR\jre'

  Delete '$INSTDIR\Uninstall.exe'

  RMDir '$INSTDIR'

  SetRegView 64

  ;Delete reg value
  DeleteRegKey HKLM 'Software\${PRODUCT_NAME}\${INTERNAL_VERSION}'
  DeleteRegKey /ifempty HKLM 'Software\${PRODUCT_NAME}'
  DeleteRegKey HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME} ${PRODUCT_VERSION}'

  ;Delete shortcuts
  Delete '$DESKTOP\${SHORTCUT_NAME}.lnk'
  Delete '$QUICKLAUNCH\${SHORTCUT_NAME}.lnk'
  Delete '$SMPROGRAMS\${START_MENU_FOLDER}\${UNINSTALL_NAME}.lnk'
  Delete '$SMPROGRAMS\${START_MENU_FOLDER}\${SHORTCUT_NAME}.lnk'
  RmDir  '$SMPROGRAMS\${START_MENU_FOLDER}'
SectionEnd

###################################################
# Un-install functions
###################################################

Function un.onInit
  ; UAS plug-in init
  UAC_Elevate:
    UAC::RunElevated
    StrCmp 1223 $0 UAC_ElevationAborted ; UAC dialog aborted by user?
    StrCmp 0 $0 0 UAC_Err ; Error?
    StrCmp 1 $1 0 UAC_Success ;Are we the real deal or just the wrapper?
    Quit

  UAC_Err:
    MessageBox mb_iconstop "Unable to elevate, error $0"
    Abort

  UAC_ElevationAborted:
    # elevation was aborted, run as normal?
    MessageBox mb_iconstop "This installer requires admin access, aborting!"
    Abort

  UAC_Success:
    StrCmp 1 $3 +4 ;Admin?
    StrCmp 3 $1 0 UAC_ElevationAborted ;Try again?
    MessageBox mb_iconstop "This installer requires admin access, try again"
    goto UAC_Elevate

    !insertmacro MUI_UNGETLANGUAGE
FunctionEnd

Function un.onUninstFailed
    UAC::Unload ;Must call unload!
FunctionEnd

Function un.onUninstSuccess
    UAC::Unload ;Must call unload!
FunctionEnd

;-----------------------------------------------------
; Check if CUBRIDManager is running before uninstall
;-----------------------------------------------------

Function un.DetectProcessRunning
  nsExec::ExecToStack 'tasklist /NH /FO TABLE /FI "IMAGENAME eq ${PRODUCT_EXE_NAME}.exe"'
  Pop $0
  Pop $1
  StrLen $3 ${PRODUCT_EXE_NAME}
  IntOp $3 $3 + 2
  StrCpy $2 $1 $3
  StrCmp $2 "$\r$\n${PRODUCT_EXE_NAME}" running not_running
  running:
    MessageBox mb_iconstop $(msgStillRunning)
    Quit
  not_running:
FunctionEnd

;-----------------------------------------------------
; String replace function
;-----------------------------------------------------

Function StrRep
  Exch $R4 ; $R4 = Replacement String
  Exch
  Exch $R3 ; $R3 = String to replace (needle)
  Exch 2
  Exch $R1 ; $R1 = String to do replacement in (haystack)
  Push $R2 ; Replaced haystack
  Push $R5 ; Len (needle)
  Push $R6 ; len (haystack)
  Push $R7 ; Scratch reg
  StrCpy $R2 ""
  StrLen $R5 $R3
  StrLen $R6 $R1
loop:
  StrCpy $R7 $R1 $R5
  StrCmp $R7 $R3 found
  StrCpy $R7 $R1 1 ; - optimization can be removed if U know len needle=1
  StrCpy $R2 "$R2$R7"
  StrCpy $R1 $R1 $R6 1
  StrCmp $R1 "" done loop
found:
  StrCpy $R2 "$R2$R4"
  StrCpy $R1 $R1 $R6 $R5
  StrCmp $R1 "" done loop
done:
  StrCpy $R3 $R2
  Pop $R7
  Pop $R6
  Pop $R5
  Pop $R2
  Pop $R1
  Pop $R4
  Exch $R3
FunctionEnd
