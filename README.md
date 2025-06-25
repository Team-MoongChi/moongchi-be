# 🧶 뭉치 - Backend

**"뭉치면 산다!"** <br>
**"뭉치"** 는 **1인 가구를 위한 위치 기반 공동구매 플랫폼**입니다.  
사용자들은 가까운 거리의 공구글을 탐색하고, 공구 참여부터 결제까지 모두 플랫폼 내에서 간편하게 처리할 수 있습니다.

## 📆 일정

### 25.05.16 ~ 25.07.01

## 🛠️ 기술 스택

| 구분              | 사용 기술                                                            |
| ----------------- | -------------------------------------------------------------------- |
| **Language**      | Java 17                                                              |
| **Framework**     | Spring Boot 3.3.1                                                    |
| **Build Tool**    | Gradle                                                               |
| **Database**      | MySQL (RDBMS)<br>MongoDB (채팅용 NoSQL)                              |
| **Cloud & Infra** | AWS (EC2, S3)                                                        |
| **API & Others**  | Redis (추천 캐싱)<br>OAuth2 (소셜 로그인)<br>WebSocket (실시간 채팅) |

## 📌 주요 기능

- **회원가입** / **소셜 로그인** (OAuth2)
- **공동구매**
  - 공동구매 글 CRUD
  - 위치 기반 탐색
  - 공동구매 참여 / 찜
  - 실시간 채팅 (그룹)
  - 리뷰 및 매너 점수
  - 추천 공동구매 (지역 기반 - MLops)
  - 1/N 결제 기능
- **쇼핑**
  - 쇼핑 상품을 통한 공동구매 열기/참여
  - 쇼핑 상품 찜
  - 추천 쇼핑 상품(사용자 기반 - MLops)
  - 쇼핑 검색 LLM (AI 대화를 통한 쇼핑 검색 - MLops)

## 👨‍💻 협업 방식

- **Issue, Pull Request** 기반 개발
- **커밋 메시지 컨벤션**: feat, fix, chore, refactor, docs 등

## 👥 팀원 소개

<div align=center>

| <img src="https://avatars.githubusercontent.com/u/152269299?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/180184232?v=4" width="150" height="150"/> |
| :-----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: |
|                      홍주이<br/>[@ju2hong](https://github.com/ju2hong)                      |                     장가은<br/>[@zkaakakg](https://github.com/zkaakakg)                     |

</div>

## 🔗 링크

- **배포 주소** : <br>
- **시연 영상** :
