<p align="center">
  <h2 align="center"><a href="https://k0d01653e1a11a.user-app.krampoline.com/">☕ 픽업 셔틀</a></h2>
  <p align="center">카카오테크캠퍼스 1기 12조 픽업셔틀 팀입니다😇 </p>
</p>

## 저희 프로젝트를 소개합니다
<img src = "https://github.com/Step3-kakao-tech-campus/Team12_BE/assets/114290599/6999fc27-4d12-436c-8564-7e6693aa7528">

<img src = "https://github.com/Step3-kakao-tech-campus/Team12_BE/assets/114290599/1ff68c37-814b-43a8-91da-bc4cb87c6d0e">

<img src="https://github.com/Step3-kakao-tech-campus/Team12_BE/assets/114290599/8f494e42-601a-484c-b548-0fda714bc7be"/>

### 편리하게 음료를 픽업하고, 픽업 받을 수 있는 [픽업셔틀](https://k0d01653e1a11a.user-app.krampoline.com/)입니다. ###
### 배포 주소 (FE): https://k0d01653e1a11a.user-app.krampoline.com/ ###
### 배포 주소 (BE): https://k0d01653e1a11a.user-app.krampoline.com/api ###

## ❗ 프로젝트에서 고민한 점
### 사용자 로그인 과정에 대한 고민
```
사용자로부터 ID와 PW를 입력 받아서 로그인 하는 과정과 카카오 OAuth 로그인 중 어떤 것을 구현할지 고민을 하였습니다. 저희는 최대한 사용자가 회원가입할 때 편의성을 제공해주기 위해서 OAuth 로그인 과정을 선택하였습니다.
OAuth 처리할 때 redirect uri를 백엔드 서버 주소로 할 것인가 프론트엔드 서버 주소로 할 것인가에 대한 고민
KAKAO OAuth 로그인을 구현하기 위해서 카카오로부터 인가코드받고 카카오로부터 엑세스 토큰과 리프레시 토큰을 요청하는 부분을 프론트엔드와 백엔드가 각각 역할을 나누는 방식으로 구현할지 아니면 모든 과정을 백엔드에서 처리할지 고민하였습니다.
결국은 카카오로부터 인가코드를 받고 엑세스 토큰과 리프레시 토큰을 요청하는 모든 과정을 백엔드에서 처리하는 것으로 구현하였습니다. 즉, redirect uri를 백엔드 서버 주소로 설정하였었는데, 이 방법을 선택했던 이유는 최대한 프론트엔드 개발하는데 있어서 최대한 신경을 덜 쓸 수 있도록 편의성을 제공해주기 위해서였습니다.
```

### 인증 과정
```
저희 서비스는 인증 과정을 JWT로 구성하였습니다. JWT를 사용하게 된 이유는 쿠키는 보안에 너무 취약하고 세션으로 사용하게 된다면 사용자수가 많아지게 되면 서버에 과부하가 생길 수 있다고 판단하였기 때문에 인증 절차를 JWT로 사용하기로 결정하였습니다.
엑세스 토큰은 응답 Body를 통해 프론트엔드로 넘겨주고 리프레시 토큰은 DB 테이블을 만들어서 저장하는 식으로 구성하였습니다. 엑세스 토큰 기간과 리프레시 토큰 기간을 얼마로 둘 건지에 대해서도 고민을 했는데 엑세스 토큰은 30분 리프레시 토큰은 2주로 정했습니다.
```

### 배포
```
크램폴린에 배포할 때 저희는 먼저 프론트엔드 서버와 백엔드 서버를 서로 다른 도메인을 사용하여 배포하려고 했습니다. 하지만 여기에서 많은 문제점들이 발생하였었는데, CORS, SameSite 정책으로 프론트엔드 서버와 백엔드 서버 간에 통신이 제대로 안되는 문제점이 있었고, 프론트엔드에서 “카카오 로그인”버튼을 클릭해도 로그인 처리를 백엔드에서 모두 처리하기 때문에 백엔드 Spring Security의 SecurityContext에서 인증 객체가 제대로 관리가 되지 않아서 아무리 로그인이 성공해도 인증 객체가 저장이 되지 않는다는 문제점이 발생하였습니다. 도메인이 다름으로써 프론트엔드 서버와 백엔드 서버 간의 통신할 때 여러 문제점들이 계속 발생이 되어 따라서 프론트엔드와 백엔드 서버를 각각 다른 도메인으로 두지 않고 하나의 쿠버네티스 네임스페이스에서 프론트엔드와 백엔드, MySQL, Nginx를 모두 동작시키는 식으로 해결하였습니다. 하나의 네임스페이스에서 동작을 시킴으로써 위에 적었던 모든 문제점들을 해결할 수 있었습니다.
```

### 이미지 업로드
```
저희는 이미지 업로드를 S3로 할 것인지 DB에 바이너리 데이터 형식으로 저장할 것인지에 대해서 고민을 하였었습니다. 학교에서 DB 과목을 배웠을 당시에는 DB에 이미지 저장은 바이너리 데이터 형식으로 저장하는 걸로 배웠었기 때문에 이미지 업로드 관련해서는 바이너리 데이터 형식으로 저장하려고 했었습니다. 하지만 조원들과의 상의를 하면서 인터넷에 자료를 찾아보니깐 DB에 비해서 Amazon S3에 저장해서 사용하는 것이 더 많은 장점이 있다는 것을 알게 되었습니다. 주로 성능, 확장성, DB 비용, 빠른 로딩 시간에서 S3가 DB에 비해서 이점을 얻을 수 있다는 것을 알았기에 DB에 바이너리를 저장하는 것이 아닌 S3에 이미지를 업로드 하는 식으로 새롭게 사용해보기로 결정하였습니다. DB에 바이너리 데이터 형식으로 저장하는 것보단 S3 형식으로 이미지를 업로드하기 위해서 스프링 부트에서 사전 세팅하는데 시간이 좀 걸렸지만 확실히 DB에 바이너리 형식으로 업로드 하는 것에 비해 S3에 업로드를 해보니 DB 과부하에 대한 걱정이 사라지게 되었고 속도 측면에서도 빨라지게 되어서 서비스를 운영하는 측면에 있어서 성능 최적화를 얻을 수 있게 되었습니다.
```
## 프로젝트 구조
### ERD
<img src="https://github.com/Step3-kakao-tech-campus/Team12_BE/assets/114290599/b0ae89f6-545f-4796-94df-b29556a928b0">

### 디렉터리 구조

```
📦src
 ┣ 📂docs
 ┃ ┗ 📂asciidoc
 ┣ 📂main
 ┃ ┣ 📂generated
 ┃ ┃ ┗ 📂pickup_shuttle
 ┃ ┃ ┃ ┗ 📂pickup
 ┃ ┃ ┃ ┃ ┗ 📂domain
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂pickup_shuttle
 ┃ ┃ ┃ ┗ 📂pickup
 ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┣ 📂beverage
 ┃ ┃ ┃ ┃ ┃ ┣ 📂board
 ┃ ┃ ┃ ┃ ┃ ┣ 📂match
 ┃ ┃ ┃ ┃ ┃ ┣ 📂oauth2
 ┃ ┃ ┃ ┃ ┃ ┣ 📂refreshToken
 ┃ ┃ ┃ ┃ ┃ ┣ 📂store
 ┃ ┃ ┃ ┃ ┃ ┗ 📂user
 ┃ ┃ ┃ ┃ ┣ 📂security
 ┃ ┃ ┃ ┃ ┣ 📂utils
 ┃ ┃ ┃ ┃ ┣ 📂_core
 ┃ ┃ ┃ ┃ ┃ ┣ 📂errors
 ┃ ┃ ┃ ┃ ┃ ┗ 📂utils
 ┃ ┗ 📂resources
 ┃ ┃ ┣ 📂db
 ┃ ┃ ┣ 📂static
 ┃ ┃ ┣ 📂templates
 ┗ 📂test
 ┃ ┗ 📂java
 ┃ ┃ ┗ 📂pickup_shuttle
 ┃ ┃ ┃ ┗ 📂pickup
 ┃ ┃ ┃ ┃ ┗ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┣ 📂board
 ┃ ┃ ┃ ┃ ┃ ┣ 📂user
```
## 🪪 Tech Stacks

### Backend

| 분류      | Stack                   |
|-----------|-------------------------|
| BACK-END | <img src="https://img.shields.io/badge/JAVA-007396?style=forthebage"/> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=forthebage&logo=springboot&logoColor=white"/> |
| DATABASE | <img src="https://img.shields.io/badge/MySQL-4479A1?style=forthebadge&logo=MySQL&logoColor=white"/>                                                         
|Version Control|<img src = "https://img.shields.io/badge/git-%23F05033.svg?style=forthebage&logo=git&logoColor=white"> <img src ="https://img.shields.io/badge/github-%23121011.svg?style=forthebage&logo=github&logoColor=white">
| INFRA    | <img src="https://img.shields.io/badge/AWS S3-569A31?style=forthebadge&logo=amazons3&logoColor=white"/> <img src="https://img.shields.io/badge/Krampoline-F7DF1E?style=forthebadge&logo=kakao&logoColor=white">


## 📒 참고 자료


- 💡 [기획 및 디자인](https://www.figma.com/file/UHfny7FM7ZtXo0cTsBcuKY/12%EC%A1%B0?type=design&node-id=376-1660&mode=design)

- 📜 [API 명세서](https://www.notion.so/API-0e2e4398bd8a4bc5914b42cd4b7141b8?pvs=4)

- 🔍‍️ [DTO명 컨벤션](https://bronzed-amount-986.notion.site/DTO-0770d09a84e642819245e1c4846ba314?pvs=4)

- 📌‍️ [DTO 컨벤션](https://bronzed-amount-986.notion.site/DTO-0770d09a84e642819245e1c4846ba314?pvs=4)

- ⚠️ [ErrorMessage 컨벤션](https://bronzed-amount-986.notion.site/d3f8c995552048e4bcdfe549291d68c2?pvs=4)

- 🚨 [상태코드 컨벤션](https://bronzed-amount-986.notion.site/46b20e72652d4f74b71accee9f73c04f?pvs=4)

- 🎞️‍️ [테스트 시나리오](https://bronzed-amount-986.notion.site/2bb8c9e7c2094d15a7e3c88ec09ea41f?pvs=4)

## 🙋 참여 인원

<table>
    <tr align="center">
        <td style="min-width: 150px;">
            <a href="https://github.com/LJH098">
              <img src="https://github.com/LJH098.png" width="100">
              <br />
              <b>이진혁 </br>(LJH098)</b>
            </a> 
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/dnjfqhd12345">
              <img src="https://github.com/dnjfqhd12345.png" width="100">
              <br />
              <b>이기준</br> (dnjfqhd12345)</b>
            </a>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/B-JuHyeon">
              <img src="https://github.com/B-JuHyeon.png" width="100">
              <br />
              <b>박주현 </br>(B-JuHyeon)</b>
            </a>
        </td>
    </tr>
    <tr align="center">
        </td>
                <td>
            Backend
        </td>
                <td>
            Backend
        </td>   <td>
            Backend
        </td>
    </tr>
</table>



# Team12_BE
12조
## 카카오 테크 캠퍼스 3단계 진행 보드

</br>

## 배포와 관련하여

```

최종 배포는 크램폴린으로 배포해야 합니다.

하지만 배포 환경의 불편함이 있는 경우를 고려하여 

임의의 배포를 위해 타 배포 환경을 자유롭게 이용해도 됩니다. (단, 금액적인 지원은 어렵습니다.)

아래는 추가적인 설정을 통해 (체험판, 혹은 프리 티어 등)무료로 클라우드 배포가 가능한 서비스입니다.

ex ) AWS(아마존), GCP(구글), Azure(마이크로소프트), Cloudtype 

```
## Notice

```
필요 산출물들은 수료 기준에 영향을 주는 것은 아니지만, 
주차 별 산출물을 기반으로 평가가 이루어 집니다.

주차 별 평가 점수는 추 후 최종 평가에 최종 합산 점수로 포함됩니다.
```

![레포지토리 운영-001 (1)](https://github.com/Step3-kakao-tech-campus/practice/assets/138656575/acb0dccd-0441-4200-999a-981865535d5f)
![image](https://github.com/Step3-kakao-tech-campus/practice/assets/138656575/b42cbc06-c5e7-4806-8477-63dfa8e807a0)

[git flowchart_FE.pdf](https://github.com/Step3-kakao-tech-campus/practice/files/12521045/git.flowchart_FE.pdf)


</br>

## 필요 산출물
<details>
<summary>Step3. Week-1</summary>
<div>
    
✅**1주차**
    
```
    - 5 Whys
    - 마켓 리서치
    - 페르소나 & 저니맵
    - 와이어 프레임
    - 칸반보드
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-2</summary>
<div>
    
✅**2주차**
    
```
    - ERD 설계서
    
    - API 명세서
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-3</summary>
<div>
    
✅**3주차**
    
```
    - 최종 기획안
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-4</summary>
<div>
    
✅**4주차**
    
```
    - 4주차 github
    
    - 4주차 노션
```
    
</div>
</details>

---
<details>
<summary>Step3. Week-5</summary>
<div>
    
✅**5주차**
    
```
    - 5주차 github
    
    - 5주차 노션
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-6</summary>
<div>
    
✅**6주차**
    
```
    - 6주차 github
    
    - 중간발표자료
    
    - 피어리뷰시트
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-7</summary>
<div>
    
✅**7주차**
    
```
    - 7주차 github
    
    - 7주차 노션
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-8</summary>
<div>
    
✅**8주차**
    
```
    - 중간고사
    
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-9</summary>
<div>
    
✅**9주차**
    
```
    - 9주차 github
    
    - 9주차 노션
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-10</summary>
<div>
    
✅**10주차**
    
```
    - 10주차 github
    
    - 테스트 시나리오 명세서
    
    - 테스트 결과 보고서
```
    
</div>
</details>

---

<details>
<summary>Step3. Week-11</summary>
<div>
    
✅**11주차**
    
```
    - 최종 기획안
    
    - 배포 인스턴스 링크
```
    
</div>
</details>

---

## **과제 상세 : 수강생들이 과제를 진행할 때, 유념해야할 것**

```
1. README.md 파일은 동료 개발자에게 프로젝트에 쉽게 랜딩하도록 돕는 중요한 소통 수단입니다.
해당 프로젝트에 대해 아무런 지식이 없는 동료들에게 설명하는 것처럼 쉽고, 간결하게 작성해주세요.

2. 좋은 개발자는 디자이너, 기획자, 마케터 등 여러 포지션에 있는 분들과 소통을 잘합니다.
UI 컴포넌트의 명칭과 이를 구현하는 능력은 필수적인 커뮤니케이션 스킬이자 필요사항이니 어떤 상황에서 해당 컴포넌트를 사용하면 좋을지 고민하며 코드를 작성해보세요.

```

</br>

## **코드리뷰 관련: review branch로 PR시, 아래 내용을 포함하여 코멘트 남겨주세요.**

**1. PR 제목과 내용을 아래와 같이 작성 해주세요.**

> PR 제목 : 부산대_0조_아이템명_0주차
> 

</br>

</div>

---
