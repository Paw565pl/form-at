# formAT

Welcome to formAT, the ultimate form builder and survey tool! Whether you're gathering feedback, conducting research, or collecting data, formAT has you covered.

**Live Demo** available at: *https://format-app.cc/*

## Features

- **User Authentication** - Secure account creation and user management
- **Advanced Form Builder** - Complex configuration with multiple question types
- **Media Support** - Upload and embed images in your forms
  **Real-Time Results** - View responses as they come in with live support
- **Rating Forms** - Collect feedback from respondents via ratings and comments
- **Responsive Design** - Works seamlessly on desktop and mobile devices
- **Observability** - Distributed metrics for monitoring and debugging

## Tech Stack

- **Frontend:** Next.js + Shadcn
- **Backend:** Spring Boot
- **Database:** MongoDB
- **File Storage:** RustFS
- **Authentication:** Keycloak
- **Observability:** OpenTelemetry
- **Containerization:** Docker

## Installation & Configuration

**Requirements**: Docker

1. Clone the repository:

```bash
git clone https://github.com/Paw565pl/form-at.git
cd form-at
```

2. Configure environment variables:

```bash
cp .env.example .env.local
# edit .env.local with your configuration
```

3. Start the application:

**Production:**

```bash
docker compose -f "docker-compose.prod.yaml" up
```

**Development:**

```bash
docker compose -f "docker-compose.dev.yaml" up
```
