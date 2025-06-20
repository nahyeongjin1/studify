# Contributing Guide – Studify

## 🧾 커밋 메시지 작성 규칙 (Conventional Commits)

커밋 메시지는 아래 규칙을 따릅니다:
<type>(optional-scope): <message>

### 🎯 예시

- `feat: 홈 화면 레이아웃 구현`
- `fix(timer): 종료 시점 계산 오류 수정`
- `chore: gradle 버전 업그레이드`

### ✅ 허용되는 타입

| 타입         | 의미                         |
|------------|----------------------------|
| `feat`     | 새로운 기능 추가                  |
| `fix`      | 버그 수정                      |
| `chore`    | 빌드/도구 설정 등 비즈니스 로직과 무관한 변경 |
| `docs`     | 문서 수정                      |
| `style`    | 코드 포맷팅 (공백, 세미콜론 등)        |
| `refactor` | 리팩토링 (기능 변화 없음)            |
| `test`     | 테스트 코드 추가/수정               |
| `perf`     | 성능 개선                      |
| `build`    | 빌드 시스템 관련 변경               |

> ✅ 커밋 시 자동으로 위 형식이 아닌 경우 커밋이 거부됩니다.

---

## 🌱 브랜치 네이밍 규칙

| 브랜치 유형 | 예시                          | 설명           |
|--------|-----------------------------|--------------|
| 기능 추가  | `feature/timer-integration` | 새로운 기능 개발 시  |
| 버그 수정  | `fix/login-crash`           | 이슈 해결 시      |
| 환경/설정  | `chore/gradle-update`       | 설정, 의존성 변경 등 |
| 핫픽스    | `hotfix/fix-crash`          | 배포 중 긴급 수정   |

---

## ✅ 커밋 & PR 규칙 요약

- 커밋 메시지: Conventional Commits 형식 준수
- 브랜치: 유형별 prefix 사용
- PR 제목: 커밋 메시지와 동일한 규칙 권장