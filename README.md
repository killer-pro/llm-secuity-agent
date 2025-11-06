# Security AI Platform

A comprehensive desktop application for security testing and network analysis powered by AI assistants. This platform combines Nmap network scanning, malware detection, CVE vulnerability analysis, and conversational AI to provide intelligent security insights.

## 🚀 Features

### Core Capabilities
- **AI-Powered Chat Interface**: Conversational security analysis with multi-provider LLM support
- **Network Scanning**: Advanced Nmap integration for network reconnaissance
- **Malware Detection**: VirusTotal integration for file and URL threat analysis
- **CVE Intelligence**: Semantic search through CVE database with RAG (Retrieval-Augmented Generation)
- **Vulnerability Correlation**: Automatic matching of detected services with known CVEs
- **Real-time Updates**: WebSocket-based live scan progress tracking

### Multi-Provider LLM Support
- **Gemini**: Advanced multimodal understanding
- **Claude**: Long context window and complex reasoning
- **OpenAI**: Mature GPT-4 capabilities
- **Ollama**: Local, privacy-focused execution

### Dashboard & Visualization
- Real-time statistics and metrics
- Network topology visualization
- Vulnerability risk assessment
- Scan history and trending

## 📋 Tech Stack

### Backend
- **Framework**: Spring Boot 3.2+
- **Language**: Java 17+
- **AI/LLM**: LangChain4j with multi-provider support
- **Database**: PostgreSQL
- **Vector Store**: Qdrant for RAG
- **Build Tool**: Maven

### Frontend
- **Desktop**: Electron 28+
- **UI Framework**: React 18+ with TypeScript
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **Communication**: Axios + Socket.io
- **Build Tool**: Vite

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Vector DB**: Qdrant
- **Relational DB**: PostgreSQL
- **Optional Local LLM**: Ollama

## 🛠️ Installation

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Nmap (for network scanning)

### Quick Start with Docker

```bash
# Clone repository
git clone <repository-url>
cd security_agents

# Create .env file
cp .env.example .env

# Add your API keys
GEMINI_API_KEY=your_gemini_key
ANTHROPIC_API_KEY=your_claude_key
OPENAI_API_KEY=your_openai_key
VIRUSTOTAL_API_KEY=your_virustotal_key

# Start services
docker-compose -f docker/docker-compose.yml up -d

# Wait for services to be ready (30-60 seconds)
docker-compose logs -f backend
```

### Manual Installation

#### Backend Setup
```bash
cd backend

# Build project
mvn clean package

# Run application
java -jar target/security-ai-platform-1.0.0.jar

# Or run with specific profile
java -jar target/security-ai-platform-1.0.0.jar --spring.profiles.active=gemini
```

#### Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Development mode
npm run dev

# Desktop app (Electron)
npm run electron:dev

# Production build
npm run electron:build
```

## 🔌 API Configuration

### Environment Variables

Create a `.env` file in the project root:

```env
# Database
DB_PASSWORD=your_secure_password

# LLM Providers
GEMINI_API_KEY=your_gemini_key
ANTHROPIC_API_KEY=your_anthropic_key
OPENAI_API_KEY=your_openai_key

# External APIs
VIRUSTOTAL_API_KEY=your_virustotal_key
NVD_API_KEY=your_nvd_key

# Qdrant
QDRANT_API_KEY=your_qdrant_key
```

### Spring Boot Configuration

Active profiles:
- `gemini` (default)
- `claude`
- `openai`
- `ollama`

Switch provider via application.yml or command line:
```bash
java -jar app.jar --spring.profiles.active=claude
```

## 📱 Usage Examples

### Chat Interface Commands

```
// Scan a network
"Scan network 192.168.1.0/24 for open ports"

// Analyze specific host
"Check host 192.168.1.10 for vulnerabilities"

// File analysis
"Analyze file /path/to/file.exe for malware"

// CVE search
"Find CVEs for Apache HTTP Server 2.4.41"

// Generate report
"Create a security report for the last scan"
```

## 🏗️ Architecture

### Backend Architecture

```
backend/
├── src/main/java/com/security/
│   ├── config/           # Spring configurations (LLM, WebSocket, Security)
│   ├── controller/       # REST API endpoints
│   ├── service/          # Business logic
│   │   ├── ai/          # AI agent and LLM provider services
│   │   ├── security/    # Security tools (Nmap, VirusTotal, CVE search)
│   │   └── storage/     # Data persistence
│   ├── model/           # Entities, DTOs, Enums
│   ├── repository/      # JPA repositories
│   └── exception/       # Custom exceptions
└── pom.xml
```

### Frontend Architecture

```
frontend/
├── src/
│   ├── components/      # React components
│   │   ├── layout/     # Main layout, sidebar, header
│   │   ├── chat/       # Chat interface
│   │   ├── scan/       # Scan results
│   │   ├── cve/        # CVE search
│   │   ├── dashboard/  # Dashboard
│   │   └── settings/   # Settings
│   ├── services/       # API and utility services
│   ├── store/          # Zustand state management
│   ├── types/          # TypeScript interfaces
│   └── utils/          # Helper functions
├── electron/           # Electron main process
└── vite.config.ts      # Build configuration
```

## 🔒 Security Considerations

### Input Validation
- Strict parameter validation for Nmap commands
- IP address range whitelist (192.168.x.x, 10.x.x.x, 172.16.x.x)
- File size limits for analysis

### API Security
- CORS configuration for frontend
- Rate limiting on external APIs
- API key encryption in configuration

### Data Protection
- Database credentials secured via environment variables
- HTTPS for external API communication
- WebSocket secure connections recommended for production

## 🚀 Deployment

### Docker Deployment

```bash
# Build images
docker-compose -f docker/docker-compose.yml build

# Run services
docker-compose -f docker/docker-compose.yml up -d

# View logs
docker-compose -f docker/docker-compose.yml logs -f
```

### Production Hardening

1. **Database**: Configure PostgreSQL with strong passwords and backups
2. **Security**: Enable HTTPS, configure firewall rules
3. **Monitoring**: Set up logging and alerting
4. **Updates**: Keep dependencies and system packages updated

## 📊 Monitoring

### Health Checks

```bash
# Backend health
curl http://localhost:8080/api/agent/health

# Agent status
curl http://localhost:8080/api/agent/status

# Provider status
curl http://localhost:8080/api/providers/active
```

## 🐛 Troubleshooting

### Backend Won't Start
- Check Java version: `java -version`
- Verify PostgreSQL connection: `psql -h localhost -U admin`
- Check Qdrant connectivity: `curl http://localhost:6333/health`

### Frontend Build Issues
- Clear node_modules: `rm -rf node_modules && npm install`
- Clear Vite cache: `rm -rf node_modules/.vite`

### LLM Provider Not Responding
- Verify API keys in environment
- Check network connectivity to provider
- Review logs: `docker-compose logs backend`

## 📚 Documentation

- [API Documentation](docs/API.md)
- [Setup Guide](docs/SETUP.md)
- [Contributing Guidelines](docs/CONTRIBUTING.md)

## 📝 License

MIT License - See LICENSE file for details

## 🤝 Contributing

Contributions are welcome! Please read our contributing guidelines.

## 📧 Support

For issues and questions:
- GitHub Issues: [Report bugs](https://github.com/yourrepo/issues)
- Email: support@example.com

## ⚠️ Disclaimer

This tool is for authorized security testing only. Ensure you have proper authorization before scanning any systems. Unauthorized access to computer systems is illegal.
