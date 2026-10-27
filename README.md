# NexShizuku

A premium Android application for managing privileged services with a beautiful Obsidian Glass design.

## Features

- **Service Management** - Connect, monitor, and control privileged services
- **Terminal** - Execute commands with real-time output
- **Permissions** - Manage app access to the service
- **Logs** - View and filter system logs
- **Diagnostics** - System information and health checks
- **Settings** - Customize theme, behavior, and logging

## Design

**Obsidian Glass** - A premium dark-first design system featuring:
- Deep graphite surfaces with glass-morphism effects
- Electric blue accent colors
- Smooth animations respecting reduced motion preferences
- Material 3 components with custom theming

## Architecture

Modular multi-module architecture:
- `app` - Main application entry point
- `core:model` - Data models and state definitions
- `core:designsystem` - Colors, typography, shapes, components
- `core:common` - Shared utilities and extensions
- `core:ipc` - AIDL interfaces and Binder client
- `service` - Privileged service implementation
- `feature:*` - Feature modules (home, service, permissions, terminal, logs, settings)

## Building

### Prerequisites
- JDK 21
- Android SDK 35
- Gradle 8.7+

### Local Build
```bash
./gradlew assembleDebug
```

### CI/CD
GitHub Actions workflow builds on every push and PR.

## Requirements

- Android 7.0 (API 24) or higher
- For full functionality: Root access or Shizuku service

## License

MIT License - see LICENSE file for details.
