# Node.js Service Assessment Report

**Component:** node-service/
**Agent:** Runtime Version Upgrade (runtime-upgrade)
**Workspace:** drift-and-debt-node-modernization
**Job:** node-service-upgrade
**Date:** 2026-08-04

## Current State

| Attribute | Value |
|-----------|-------|
| Runtime | Node.js 12.x (EOL) |
| Framework | Express 4.16.4 |
| HTTP Client | request 2.88.0 (deprecated, unmaintained since 2020) |
| Lines of Code | ~50 (server.js) |
| Complexity | Low (single file, 2 routes) |

## Issues Identified

### Runtime & Dependencies
1. **Node.js 12.x (EOL)** — end of life, no security patches
2. **Express 4.16.4** — outdated, needs upgrade for Node 20 compatibility
3. **request 2.88.0** — deprecated and unmaintained since 2020, must be replaced

### Code Patterns
4. **Callback pyramid** — nested fs.readFile callbacks in /aggregate route
5. **Synchronous file I/O** — fs.appendFileSync on every request (blocks event loop)
6. **No async/await** — all callback-style code
7. **var declarations** — no const/let usage

### Security
8. **XSS vulnerability** — user input (userId) reflected in HTML without escaping
9. **Hardcoded API key** — `sk-live-fake1234567890abcdef` exposed in source
10. **No input validation** — route parameters used directly without sanitization

## Transformation Plan

### Phase 1: Node.js Version Upgrade (12 → 20)
- Update package.json engines field to Node 20
- Upgrade Express to latest 4.x (4.19+)
- Replace `request` library with `axios` or native `fetch` (available in Node 18+)
- Migrate all callbacks to async/await
- Replace `fs.appendFileSync` with `fs.promises.appendFile`
- Replace nested `fs.readFile` callbacks with `fs.promises.readFile`
- Convert `var` to `const`/`let`

### Phase 2: Security Fixes
- Sanitize user input before HTML rendering (fix XSS)
- Move API key to environment variable (process.env)
- Add basic input validation on route parameters

## Risk Assessment
- **Blast radius:** Low — single-file service, no downstream dependencies in this repo
- **Breaking changes:** Express 4.16 → 4.19 is backward compatible. Node 20 drops some legacy APIs but none used here.
- **Testing:** No existing tests. Manual verification of both routes required after transformation.
