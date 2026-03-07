# Linting Configuration

## Backend (Java)

**Tools:**
- **Checkstyle** - Code style enforcement
- **SpotBugs** - Static analysis for bug detection
- **PMD** - Code quality analysis

**Configuration:** `/api/pom.xml`

**Commands:**
```bash
cd api
mvn checkstyle:check    # Code style
mvn spotbugs:check      # Bug detection
mvn pmd:check           # Code quality
mvn verify              # Run all linters
```

## Frontend (TypeScript/JavaScript)

**Tools:**
- **ESLint 9** - Code quality and best practices
- **Prettier** - Code formatting

**Configuration:**
- ESLint: `/web/eslint.config.mjs`
- Prettier: `/web/.prettierrc`

**Commands:**
```bash
cd web
npm run lint           # Run ESLint
npm run lint:fix       # Auto-fix ESLint issues
npm run format         # Format with Prettier
npm run format:check   # Check Prettier compliance
```

## Pre-Commit Hooks

**Tools:**
- **Husky** - Git hooks manager
- **lint-staged** - Run linters on staged files only

**Configuration:**
- `.husky/pre-commit`
- `.lintstagedrc.js`

**Bypass:**
```bash
git commit --no-verify -m "message"
```

## CI/CD

**Workflow:** `.github/workflows/lint.yml`
- Runs on PRs to `main`, `dev`, `master`
- Runs on pushes to `main`, `dev`, `master`
- Parallel execution for API and Web
