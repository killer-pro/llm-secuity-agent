# Security AI Platform - API Documentation

## Base URL

```
http://localhost:8080/api
```

## Authentication

Currently, the API does not require authentication. For production, implement API key or JWT-based authentication.

## Response Format

All responses are in JSON format:

```json
{
  "data": {},
  "error": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## AI Agent Endpoints

### Chat with Agent

Send a message to the AI agent for analysis.

**Endpoint**: `POST /agent/chat`

**Request Body**:
```json
{
  "message": "Scan network 192.168.1.0/24 for vulnerabilities",
  "sessionId": "session-123",
  "useTools": true,
  "maxTokens": 2000
}
```

**Response**:
```json
{
  "response": "Starting network scan...",
  "sessionId": "session-123",
  "taskId": 1,
  "isComplete": true,
  "executionTimeMs": 1234,
  "toolExecutions": [
    {
      "toolName": "NmapTool",
      "toolInput": "192.168.1.0/24 -sV",
      "toolOutput": "...",
      "executionTimeMs": 5000,
      "success": true
    }
  ]
}
```

**Status Codes**:
- `200 OK`: Request successful
- `400 Bad Request`: Invalid request
- `500 Internal Server Error`: Server error

### Get Agent Status

Check the health and status of the AI agent.

**Endpoint**: `GET /agent/status`

**Response**:
```json
{
  "response": "AI Agent is operational and ready to process requests.",
  "isComplete": true
}
```

### Health Check

**Endpoint**: `GET /agent/health`

**Response**: `200 OK - AI Agent is running`

## Scan Endpoints

### Initiate Scan

Start a new security scan.

**Endpoint**: `POST /scans/initiate`

**Request Body**:
```json
{
  "scanType": "FULL",
  "target": "192.168.1.10",
  "scanName": "Production Server Scan",
  "parameters": "-sV -sC -O",
  "performVulnerabilityAnalysis": true,
  "performMalwareCheck": false,
  "timeoutMinutes": 30,
  "sessionId": "session-123"
}
```

**Scan Types**:
- `QUICK`: Quick ping scan
- `FULL`: Comprehensive scan
- `SERVICE_DETECTION`: Service and version detection
- `VULNERABILITY`: Vulnerability assessment
- `FILE_ANALYSIS`: File malware analysis
- `CUSTOM`: Custom parameters

**Response**:
```json
{
  "id": 1,
  "scanType": "FULL",
  "status": "PENDING",
  "target": "192.168.1.10",
  "scanName": "Production Server Scan",
  "progress": 0,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### Get All Scans

Retrieve all scans.

**Endpoint**: `GET /scans/`

**Response**:
```json
[
  {
    "id": 1,
    "scanType": "FULL",
    "status": "COMPLETED",
    "target": "192.168.1.10",
    "scanName": "Production Server Scan",
    "progress": 100,
    "hostsDiscovered": 1,
    "vulnerabilitiesFound": 3,
    "criticalIssues": 1,
    "maxCvssScore": 8.5,
    "durationSeconds": 120,
    "createdAt": "2024-01-15T10:30:00Z",
    "completedAt": "2024-01-15T10:32:00Z"
  }
]
```

### Get Recent Scans

**Endpoint**: `GET /scans/recent`

Returns the 10 most recent scans.

### Get Scan by ID

**Endpoint**: `GET /scans/{id}`

**Parameters**:
- `id` (path): Scan ID

### Update Scan Progress

**Endpoint**: `PUT /scans/{id}/progress`

**Parameters**:
- `id` (path): Scan ID
- `progress` (query): Progress percentage (0-100)

**Response**: Updated ScanResult object

### Get Critical Scans

**Endpoint**: `GET /scans/critical`

Returns scans with critical issues.

### Get Scans with Vulnerabilities

**Endpoint**: `GET /scans/vulnerabilities`

Returns scans that found vulnerabilities.

### Delete Scan

**Endpoint**: `DELETE /scans/{id}`

**Parameters**:
- `id` (path): Scan ID

**Response**: `200 OK`

## Provider Endpoints

### Get All Providers

**Endpoint**: `GET /providers/`

**Response**:
```json
[
  {
    "providerName": "Gemini",
    "isAvailable": true,
    "isActive": true,
    "modelName": "gemini-1.5-pro",
    "statusMessage": "Active and responding",
    "responseTimeMs": 250,
    "isLocal": false,
    "supportedFeatures": "Chat, Tool calling, Streaming"
  },
  {
    "providerName": "Claude",
    "isAvailable": true,
    "isActive": false,
    "modelName": "claude-3-5-sonnet-20241022",
    "statusMessage": "Configured and available",
    "isLocal": false,
    "supportedFeatures": "Chat, Tool calling, Vision"
  }
]
```

### Get Active Provider

**Endpoint**: `GET /providers/active`

Returns the currently active LLM provider.

### Switch Provider

**Endpoint**: `POST /providers/switch`

**Parameters**:
- `providerName` (query): Name of provider to switch to

**Response**: Updated ProviderStatus

### Get Model Capabilities

**Endpoint**: `GET /providers/capabilities`

**Response**:
```json
"Advanced multimodal understanding, code generation, streaming support"
```

## CVE Endpoints

### Search CVEs

Search for CVEs by product name.

**Endpoint**: `GET /cve/search`

**Parameters**:
- `query` (query): Search term (e.g., "Apache HTTP Server")

**Response**:
```json
{
  "results": [
    {
      "cveId": "CVE-2024-1234",
      "severity": "HIGH",
      "cvssScore": 7.5,
      "description": "Description of vulnerability",
      "affectedProducts": ["Apache HTTP Server 2.4.41"]
    }
  ],
  "count": 1
}
```

### Get CVE Details

**Endpoint**: `GET /cve/{cveId}`

**Parameters**:
- `cveId` (path): CVE identifier (e.g., CVE-2024-1234)

**Response**:
```json
{
  "cveId": "CVE-2024-1234",
  "description": "Detailed description",
  "cvssScore": 7.5,
  "cvssVector": "CVSS:3.1/AV:N/AC:L/...",
  "severity": "HIGH",
  "publishedDate": "2024-01-15T00:00:00Z",
  "references": ["https://nvd.nist.gov/vuln/detail/CVE-2024-1234"],
  "mitigation": "Upgrade to version X.X.X"
}
```

### Search CVEs by Severity

**Endpoint**: `GET /cve/severity/{level}`

**Parameters**:
- `level` (path): Severity level (CRITICAL, HIGH, MEDIUM, LOW)

**Response**: Array of CVE objects matching severity level

## WebSocket Endpoints

Real-time communication for scan updates and streaming responses.

### Chat WebSocket

**Endpoint**: `ws://localhost:8080/ws/chat`

**Message Format**:
```json
{
  "type": "message",
  "content": "User input",
  "sessionId": "session-123"
}
```

### Scan Progress WebSocket

**Endpoint**: `ws://localhost:8080/ws/scan-progress`

**Message Format**:
```json
{
  "scanId": 1,
  "progress": 50,
  "status": "IN_PROGRESS",
  "message": "Scanning ports..."
}
```

## Error Handling

### Error Response Format

```json
{
  "error": {
    "code": "INVALID_TARGET",
    "message": "Target not in allowed ranges",
    "timestamp": "2024-01-15T10:30:00Z"
  }
}
```

### Common Error Codes

| Code | Status | Description |
|------|--------|-------------|
| `INVALID_TARGET` | 400 | Target IP not in allowed ranges |
| `INVALID_PORTS` | 400 | Invalid port specification |
| `TOOL_ERROR` | 500 | Tool execution failed |
| `AI_AGENT_ERROR` | 500 | AI agent processing error |
| `INVALID_CVE_ID` | 400 | Invalid CVE ID format |
| `VIRUSTOTAL_API_ERROR` | 500 | VirusTotal API error |

## Rate Limiting

- **Scan Endpoints**: 10 requests per minute per IP
- **CVE Search**: 30 requests per minute
- **Provider Switch**: 1 request per second

## Pagination

Endpoints that return lists support pagination:

**Parameters**:
- `page` (query): Page number (default: 0)
- `size` (query): Items per page (default: 20, max: 100)

**Response Headers**:
- `X-Total-Count`: Total number of items
- `X-Page-Number`: Current page
- `X-Page-Size`: Items per page

## cURL Examples

### Chat with Agent
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Scan 192.168.1.10",
    "sessionId": "session-1",
    "useTools": true
  }'
```

### Start Network Scan
```bash
curl -X POST http://localhost:8080/api/scans/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "scanType": "QUICK",
    "target": "192.168.1.0/24",
    "scanName": "Network Discovery"
  }'
```

### Search CVEs
```bash
curl "http://localhost:8080/api/cve/search?query=Apache"
```

## Version

Current API Version: `1.0.0`

## Support

For API issues:
- Review logs: `/app/logs/security-ai-platform.log`
- Check backend health: `GET /api/agent/health`
- File GitHub issue with error details
