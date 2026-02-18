# Realtime Server

채점 서버의 실시간 채점 현황을 제공하는 SSE(Server-Sent Events) 기반 실시간 서버입니다.

## 역할

이 서버는 채점 서버가 채점을 진행하는 동안 실시간으로 채점 현황을 클라이언트에 전달합니다.

### 주요 기능

- **실시간 채점 현황 모니터링**: 채점 서버가 어떤 문제를, 어떤 제출 데이터를 채점 중인지 실시간으로 확인 가능
- **진행률 추적**: 채점 진행률(%)을 실시간으로 확인 가능
- **상세 로그 제공**: 채점 중, 컴파일 중, 테스트케이스 로드 중 등 채점 서버의 모든 동작 로그를 실시간으로 수신
- **SSE 기반 단방향 통신**: 클라이언트는 초기 연결 수립 후 데이터를 일방적으로 수신만 하므로 SSE 형식으로 구성

## 기술 스택

- **Spring Boot 4.0.2** (WebFlux)
- **Kotlin 2.2.21**
- **Redis** (채점 서버와의 메시지 브로커)
- **Reactor** (리액티브 프로그래밍)

## 아키텍처

```
채점 서버 → Redis (judge:submission 토픽) → Realtime Server → 클라이언트 (SSE)
```

1. 채점 서버가 채점 진행 상황을 Redis의 `judge:submission` 토픽에 발행
2. Realtime Server가 Redis를 구독하여 메시지 수신
3. 수신한 메시지를 SSE 형식으로 연결된 클라이언트들에게 브로드캐스트

## 사용법

### 1. 환경 설정

`application.yaml` 파일에서 Redis 연결 정보를 설정합니다:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 2. 서버 실행

```bash
./gradlew bootRun
```

또는

```bash
./gradlew build
java -jar build/libs/realtime-0.0.1-SNAPSHOT.jar
```

서버는 기본적으로 **8083** 포트에서 실행됩니다.

### 3. 클라이언트 연결

SSE 엔드포인트에 연결하여 실시간 채점 현황을 수신합니다:

**엔드포인트**: `GET /api/v1/realtime/submission-status`

**예시 (JavaScript)**:

```javascript
const eventSource = new EventSource('http://localhost:8083/api/v1/realtime/submission-status');

eventSource.onmessage = (event) => {
    const data = JSON.parse(event.data);
    console.log('채점 현황:', data);
    // 예: { submissionId: 123, problemId: 456, progress: 75, status: "채점 중", logs: [...] }
};

eventSource.onerror = (error) => {
    console.error('SSE 연결 오류:', error);
    eventSource.close();
};
```

**예시 (curl)**:

```bash
curl -N http://localhost:8083/api/v1/realtime/submission-status
```

### 4. 데이터 형식

SSE를 통해 전달되는 데이터는 JSON 문자열 형식입니다. 채점 서버가 발행하는 메시지 형식에 따라 다음 정보들을 포함할 수 있습니다:

- `submissionId`: 제출 ID
- `problemId`: 문제 ID
- `progress`: 채점 진행률 (0-100)
- `status`: 현재 상태 (예: "채점 중", "컴파일 중", "테스트케이스 로드 중" 등)
- `logs`: 상세 로그 메시지 배열
- 기타 채점 서버에서 제공하는 추가 정보

## API 엔드포인트

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/v1/realtime/submission-status` | SSE 스트림으로 실시간 채점 현황 수신 |

## 요구사항

- Java 17 이상
- Redis 서버 (채점 서버와 동일한 Redis 인스턴스 사용)
- 채점 서버가 `judge:submission` 토픽에 메시지를 발행해야 함

## 프로젝트 구조

```
src/main/kotlin/com/dalmeng/realtime/
├── RealtimeApplication.kt              # 메인 애플리케이션
├── redis/
│   ├── RedisSubmissionSubscriber.kt    # Redis 구독자
│   └── SubmissionEventBus.kt           # 이벤트 버스
└── submission/
    └── controller/
        └── SubmissionStatusController.kt # SSE 컨트롤러
```

## 개발

### 빌드

```bash
./gradlew build
```

### 테스트

```bash
./gradlew test
```

## 라이선스

이 프로젝트는 내부 사용을 위한 프로젝트입니다.
