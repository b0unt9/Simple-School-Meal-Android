# 심플 급식 식단표
> 간단한 수정으로 학교 급식 식단표 안드로이드 어플리케이션을 제작하실 수 있습니다

[![License: MIT](https://img.shields.io/badge/Lecense-MIT%202.0-blue.svg)](https://github.com/prigic/Simple-School-Meal-Android/blob/master/LICENSE)

## 수정 하는 방법
`FragmentMeal`에서 약 255번째 줄을 찾으면 아래 코드가 나옵니다.
```
override fun doInBackground(vararg params: Void?): Void? {
    if (permissionCheck == PackageManager.PERMISSION_GRANTED
        && !Utils.read("$mealLoadYear-$mealLoadMonth.data", "null").equals("null")
    ) {
        textMenu = Utils.read("$mealLoadYear-$mealLoadMonth.data", "파일 오류!")
        isText = true
        return null
    } else {
        menu = School(
            School.Type.HIGH,
            School.Region.SEOUL,
            "B100000456"
        ).getMonthlyMenu(
            mealLoadYear!!,
            mealLoadMonth!!
        )
        return null
    }
}
```
이중 아래 부분을 수정하시면 됩니다.
```
School.Type.HIGH,
School.Region.SEOUL,
"B100000456"
```

수정 방식은 아래에 있습니다.

### 수정방식

#### 학교 종류

 학교 종류는 `School.Type` 에서 선택할 수 있습니다.

- 병설유치원: `School.Type.KINDERGARTEN`
- 초등학교: `School.Type.ELEMENTARY`
- 중학교: `School.Type.MIDDLE`
- 고등학교: `School.Type.HIGH`

#### 관할 지역

관할 지역은 `School.Region` 에서 선택할 수 있습니다.

- 서울특별시: `School.Region.SEOUL`
- 인천광역시: `School.Region.INCHEON`
- 부산광역시: `School.Region.BUSAN`
- 광주광역시: `School.Region.GWANGJU`
- 대전광역시: `School.Region.DAEJEON`
- 대구광역시: `School.Region.DAEGU`
- 세종특별자치시: `School.Region.SEJONG`
- 울산광역시: `School.Region.ULSAN`
- 경기도: `School.Region.GYEONGGI`
- 강원도: `School.Region.KANGWON`
- 충청북도: `School.Region.CHUNGBUK`
- 충청남도: `School.Region.CHUNGNAM`
- 경상북도: `School.Region.GYEONGBUK`
- 경상남도: `School.Region.GYEONGNAM`
- 전라북도: `School.Region.JEONBUK`
- 전라남도: `School.Region.JEONNAM`
- 제주도: `School.Region.JEJU`

#### 학교 코드

학교의 고유 코드는 [여기](http://jubsoo2.bscu.ac.kr/src_gogocode/src_gogocode.asp)에서 학교명으로 검색할 수 있습니다.
 학교 코드는 `X000000000` 형식의 10자리 문자열입니다.

## 참고 라이브러리
[neis-api](https://github.com/agemor/neis-api)

## 원본 로고 출처
http://www.urbanbrush.net/downloads/%EC%8B%9D%ED%8C%90-%EC%9D%BC%EB%9F%AC%EC%8A%A4%ED%8A%B8-ai-%EB%AC%B4%EB%A3%8C%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C-free-plate-vector/

## License
MIT License
