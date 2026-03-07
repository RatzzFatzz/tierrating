# Development Setup Guide

## IDE Configuration

### Visual Studio Code (Recommended)

Install the following extensions:

1. **ESLint** (`dbaeumer.vscode-eslint`)
   - Automatically lints JavaScript/TypeScript files
   - Shows errors and warnings inline

2. **Prettier - Code formatter** (`esbenp.prettier-vscode`)
   - Auto-formats code on save
   - Consistent code style across the team

3. **Java Extension Pack** (`vscjava.vscode-java-pack`)
   - Includes Language Support for Java™
   - Debugger for Java
   - Maven for Java
   - Test Runner for Java

4. **Checkstyle for Java** (`shengchen.vscode-checkstyle`)
   - Shows Checkstyle violations inline
   - Uses the same `google_checks.xml` as CI

#### VS Code Settings

Create or update `.vscode/settings.json`:

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": "explicit"
  },
  "[java]": {
    "editor.defaultFormatter": "redhat.java"
  },
  "java.format.settings.url": "google-style.xml",
  "checkstyle.configurationPath": "google_checks.xml",
  "typescript.tsdk": "web/node_modules/typescript/lib"
}
```

### IntelliJ IDEA (Recommended for Java)

1. **Enable Checkstyle Plugin**
   - Go to `Preferences` → `Plugins`
   - Search for "Checkstyle-IDEA"
   - Install and restart

2. **Configure Checkstyle**
   - Go to `Preferences` → `Tools` → `Checkstyle`
   - Add configuration file: `google_checks.xml`
   - Set as active

3. **Enable ESLint**
   - Go to `Preferences` → `Languages & Frameworks` → `JavaScript` → `Code Quality Tools` → `ESLint`
   - Enable: `Automatic ESLint configuration`

4. **Enable Prettier**
   - Go to `Preferences` → `Languages & Frameworks` → `JavaScript` → `Prettier`
   - Enable: `On save`, `On code reformat`

## Pre-Commit Hooks

The project uses **husky** and **lint-staged** to automatically run linters on staged files before each commit.

### What Gets Checked

**Backend (Java):**
- Checkstyle (code style)
- Runs on all `.java` files in `api/`

**Frontend (TypeScript/JavaScript):**
- ESLint (code quality)
- Prettier (formatting)
- Runs on all `.js`, `.jsx`, `.ts`, `.tsx` files in `web/`

**Other Files:**
- Prettier formatting for `.json`, `.css`, `.md` files in `web/`

### Bypassing Hooks (Not Recommended)

If you need to commit without running hooks (emergency only):

```bash
git commit --no-verify -m "Your message"
```

**⚠️ Warning:** CI will still run these checks, so your PR may fail.

## Running Linters Manually

### Backend

```bash
cd api

# Run all linters
mvn verify

# Run individually
mvn checkstyle:check    # Code style
mvn spotbugs:check      # Bug detection
mvn pmd:check           # Code quality
```

### Frontend

```bash
cd web

# Run all checks
npm run lint            # ESLint
npm run format:check    # Prettier

# Auto-fix issues
npm run lint:fix        # Fix ESLint issues
npm run format          # Format with Prettier
```

## Troubleshooting

### ESLint not working in VS Code

1. Check ESLint extension is installed
2. Reload VS Code window: `Cmd/Ctrl + Shift + P` → "Reload Window"
3. Check ESLint output: `View` → `Output` → Select "ESLint"

### Prettier not formatting on save

1. Check Prettier extension is installed
2. Verify `editor.formatOnSave` is `true` in settings
3. Check Prettier output for errors

### Java Checkstyle errors in IDE but not in CI

1. Ensure Checkstyle plugin uses `google_checks.xml`
2. Refresh Checkstyle configuration
3. Rebuild project: `Build` → `Rebuild Project`

### Pre-commit hooks not running

1. Verify husky is installed:
   ```bash
   npm run prepare
   ```
2. Check `.husky/pre-commit` is executable:
   ```bash
   chmod +x .husky/pre-commit
   ```

### "Cannot find module" errors

1. Install dependencies:
   ```bash
   # Root
   npm install
   
   # Frontend
   cd web && npm install
   ```

## Code Style Guidelines

### Java

- Follow Google Java Style Guide
- Use 2-space indentation
- Max line length: 100 characters
- Use Lombok annotations to reduce boilerplate

### TypeScript/JavaScript

- Use 2-space indentation
- Max line length: 100 characters
- Prefer `const` over `let`
- Use arrow functions for callbacks
- Prefer async/await over Promises

### React

- Use functional components with hooks
- Use `const` for components
- Prefer named exports
- Use TypeScript for prop types

## CI/CD Integration

All pull requests must pass linting checks:

- **Backend**: Checkstyle, SpotBugs, PMD
- **Frontend**: ESLint, Prettier

Check the GitHub Actions tab for detailed error messages if CI fails.

## Getting Help

- Check existing issues on GitHub
- Review CI error messages carefully
- Ask in team chat or create a new issue
