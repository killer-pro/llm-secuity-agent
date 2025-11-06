# Security AI Platform - Setup Guide

## System Requirements

### Minimum Requirements
- **CPU**: 4 cores
- **RAM**: 8GB
- **Disk**: 20GB free space
- **Network**: Internet connection for API calls

### Supported Platforms
- Linux (Ubuntu 20.04+, CentOS 8+)
- macOS (10.14+)
- Windows 10/11

## Installation Steps

### 1. Prerequisites Installation

#### Java 17+
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install openjdk-17-jdk

# Verify installation
java -version
```

#### Node.js 18+
```bash
# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install nodejs

# Verify installation
node --version
npm --version
```

#### Nmap
```bash
# Ubuntu/Debian
sudo apt-get install nmap

# macOS
brew install nmap

# Windows
# Download from https://nmap.org/download.html
```

#### Docker & Docker Compose
```bash
# Ubuntu/Debian
sudo apt-get install docker.io docker-compose

# Start Docker daemon
sudo systemctl start docker
sudo usermod -aG docker $USER

# Verify installation
docker --version
docker-compose --version
```

### 2. Clone Repository

```bash
git clone https://github.com/yourrepo/security_agents.git
cd security_agents
```

### 3. Environment Configuration

Create `.env` file in project root:

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```env
# Database
DB_PASSWORD=SecurePassword123!

# LLM Providers (choose one or more)
GEMINI_API_KEY=your_gemini_key_here
ANTHROPIC_API_KEY=your_claude_key_here
OPENAI_API_KEY=your_openai_key_here

# External Services
VIRUSTOTAL_API_KEY=your_virustotal_key
NVD_API_KEY=your_nvd_api_key

# Qdrant Vector Database
QDRANT_API_KEY=your_qdrant_key
```

### 4. Backend Setup

```bash
cd backend

# Build project
mvn clean package -DskipTests

# Test backend
java -jar target/security-ai-platform-1.0.0.jar

# Should start on http://localhost:8080
```

### 5. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Test frontend dev server
npm run dev

# Should start on http://localhost:5173
```

### 6. Docker Setup (Recommended)

```bash
# Start all services
docker-compose -f docker/docker-compose.yml up -d

# Wait for services to start
sleep 30

# Verify services
docker-compose -f docker/docker-compose.yml ps

# Check logs
docker-compose -f docker/docker-compose.yml logs -f backend
```

## API Key Acquisition

### Gemini API
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create new API key
3. Add to `.env` file

### Claude API
1. Visit [Anthropic Console](https://console.anthropic.com)
2. Create API key
3. Add to `.env` file

### OpenAI API
1. Go to [OpenAI Platform](https://platform.openai.com/account/api-keys)
2. Create new API key
3. Add to `.env` file

### VirusTotal API
1. Visit [VirusTotal](https://www.virustotal.com/gui/home/upload)
2. Create free account
3. Get API key from account settings
4. Add to `.env` file

### NVD API
1. Go to [NVD API Documentation](https://nvd.nist.gov/developers/request-an-api-key)
2. Request API key
3. Add to `.env` file

## Database Setup

### PostgreSQL (Docker)
Automatically configured with Docker Compose

### PostgreSQL (Manual)
```bash
# Create database
createdb security_db -U postgres

# Create user
createuser admin

# Set password
psql -c "ALTER USER admin WITH PASSWORD 'your_password';"

# Grant privileges
psql -d security_db -c "GRANT ALL PRIVILEGES ON DATABASE security_db TO admin;"
```

## Qdrant Setup

### Using Docker
Automatically configured with Docker Compose

### Manual Setup
```bash
# Pull Qdrant image
docker pull qdrant/qdrant:latest

# Run Qdrant
docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant:latest
```

## Ollama Setup (Optional)

For local LLM execution without cloud APIs:

```bash
# Pull Ollama image
docker pull ollama/ollama:latest

# Run Ollama
docker run -d -p 11434:11434 ollama/ollama:latest

# Pull a model (example: neural-chat)
docker exec ollama ollama pull neural-chat

# Update docker-compose.yml to use ollama provider
```

## Application Configuration

### Spring Boot Profiles

Configure which LLM provider to use:

```bash
# Gemini (default)
java -jar app.jar --spring.profiles.active=gemini

# Claude
java -jar app.jar --spring.profiles.active=claude

# OpenAI
java -jar app.jar --spring.profiles.active=openai

# Ollama (local)
java -jar app.jar --spring.profiles.active=ollama
```

### Nmap Configuration

Set Nmap path in `application.yml`:

```yaml
security:
  scan:
    nmap:
      path: /usr/bin/nmap
      # Or on Windows: C:\\Program Files (x86)\\Nmap\\nmap.exe
    allowed-ranges:
      - 192.168.0.0/16
      - 10.0.0.0/8
      - 172.16.0.0/12
```

## Verification

### Check Backend
```bash
curl http://localhost:8080/api/agent/health
# Expected: HTTP 200 OK
```

### Check Frontend
Open http://localhost:5173 in browser

### Check Database Connection
```bash
psql -h localhost -d security_db -U admin
# Should connect successfully
```

### Check Qdrant
```bash
curl http://localhost:6333/health
# Expected: HTTP 200 OK with health status
```

## Startup Script

Create `start.sh`:

```bash
#!/bin/bash

# Start Docker services
echo "Starting Docker services..."
docker-compose -f docker/docker-compose.yml up -d

# Wait for services
echo "Waiting for services to start..."
sleep 30

# Check health
echo "Checking service health..."
curl http://localhost:8080/api/agent/health

# Open frontend
echo "Opening application..."
open http://localhost:5173

echo "Setup complete! Application is running."
```

## Troubleshooting

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Database Connection Error
```bash
# Check PostgreSQL is running
docker-compose ps postgres

# Check logs
docker-compose logs postgres
```

### API Key Issues
- Verify keys in `.env` are correctly set
- Ensure keys have not expired
- Check API provider account for rate limiting

### Nmap Not Found
```bash
# Verify Nmap installation
which nmap

# Update path in application.yml if needed
```

## Production Deployment

### Security Checklist
- [ ] Change all default passwords
- [ ] Enable HTTPS/SSL
- [ ] Configure firewall rules
- [ ] Enable database backups
- [ ] Set up monitoring and logging
- [ ] Configure rate limiting
- [ ] Enable API authentication
- [ ] Review CORS settings

### Performance Optimization
- [ ] Enable database query caching
- [ ] Configure connection pooling
- [ ] Enable Qdrant caching
- [ ] Set up CDN for frontend assets
- [ ] Configure load balancer if needed

## Support

For issues:
1. Check logs: `docker-compose logs`
2. Review documentation
3. Open GitHub issue with:
   - Error message
   - Stack trace
   - Configuration details (without secrets)
   - Steps to reproduce
