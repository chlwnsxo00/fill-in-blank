# 모바일 프로그래밍 프로젝트 ( Blank )

OCR을 통해 이미지에서 text를 추출하고 추출된 text에서 빈칸을 생성해 문제를 생성합니다.

앱의 WireFrame은 아래의 Figma 링크를 통해 확인할 수 있습니다.
https://www.figma.com/file/AbqcqlDPJR29MdARoA0r67/Untitled?node-id=0%3A1&t=nWvCFyYcAgVCQkS4-1

<img width="100" alt="image" src="https://github.com/chlwnsxo00/fill-in-blank/assets/31373739/51f52fd7-474a-4489-b0b4-8b04b4b37993">

## 1. 각 Activiry 설명

### (가) SplashActivity 
Splash screen은 이미지나 로고, 현재 버전의 소프트웨어를 포함한 그래픽 요소를 보여주는 화면으로, 보통 게임이나 프로그램이 실행되고 있을 때 나오는 화면입니다.
해당 액티비티에서 Blank 앱의 로고를 1초간 보여주고 MainActivity로 이동합니다.

### (나) MainActivity
MainActivity는 문제의 유형을 나눌 수 있는 폴더를 생성하고 각 폴더는 Room을 통해 local db로 저장됩니다. 또한 RecyclerView를 통해 스크롤을 통해 생성된 폴더를 확인할 수 있으며, 각 폴더 item은 문제 생성, 삭제, 문제 풀기의 기능을 가지고 있습니다. 우측 상단의 "지문 n개>"의 text를 클릭하면 InnerIndexActivity로 이동합니다. 우측 하단의 "삭제" text를 클릭하면 해당 폴더가 삭제됩니다.

### (다) InnerIndexActivity
InnerIndexActivity는 폴더 안의 문제들을 확인할 수 있는 Activity입니다. 해당 Activity의 상단 메뉴바를 활용해 PlayOCRActivity로 이동할 수 있습니다.

### (라) PlayOCRActivity
PlayOCRActivity는 OCR을 통해 이미지에서 text를 추출하는 과정을 수행하는 Activity입니다. 상단 메뉴바를 활용해 갤러리에서 이미지 선택을 통해 OCR을 진행할 수 있습니다. OCR을 진행한 후 선택한 이미지와 OCR한 결과를 확인할 수 있으며, 하단의 "문제 만들기" 버튼을 클릭하면 TextHighLightActivity로 이동합니다.

### (마) TextHighLightActivity
TextHighLightActivity은 OCR로 이미지에서 획득한 text를 통해 문제를 생성하는 Activity입니다.

---

## 2. 앱 기능을 구현하기 위해 사용된 기능 설명

### (가) Room & RecyclerView
생성된 폴더와 폴더 속 문제들을 local db에 저장해 앱이 종료된 후 생성한 문제들이 저장될 수 있도록 Room을 사용했습니다. RecyclerView 구현을 통해 폴더를 스크롤을 통해 확인할 수 있도록 했습니다.

### (나) Coroutine
해당 앱에서 상단 메뉴바를 통해 폴더를 생성했을 때 즉각적으로 UI가 변경될 수 있도록 RunOnUIThread와 OCR API를 활용하기 위한 Network 작업을 할 때 비동기가 필요하기에 비동기 작업을 효율적으로 처리하기 위해 Coroutine을 사용했습니다

### (다) Retrofit
Retrofit은 앱 개발 시 서버통신에 사용되는 HTTP API를 자바, 코틀린의 인터페이스 형태로 변환해 안드로이드 개발 시 API를 쉽게 호출할 수 있도록 지원하는 라이브러리로 OCR API를 활용하기 위해 사용했습니다.

### (라) okHTTP3
OkHttp는 기본적으로 효율적인 HTTP 클라이언트이다. 쉽게 HTTP 기반의 request/response를 할 수 있도록 도와주는 오픈소스 라이브러리로 OCR API를 활용하기 위해 사용했습니다.

### (마) glide
OCR API를 활용하는 과정에서 OCR 무료 버전에서 1MB 이하만 가능하기 때문에 Glide 이용해 고해상도 사진을 1MB 이하가 되기 위해 해상도를 줄였습니다.


