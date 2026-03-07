const path = require("path");
const { execSync } = require("child_process");

const webDir = path.resolve(__dirname, "web");

module.exports = {
  "**/*.java": (filenames) => {
    const javaFiles = filenames.filter((f) => f.startsWith("api/"));
    if (javaFiles.length === 0) return [];
    return [`mvn checkstyle:check --file api/pom.xml`];
  },
  // lint-staged v14+ uses execa without a shell, so `cd web && npx ...`
  // does not work (cd is a shell built-in, not an executable). Instead we
  // invoke the commands ourselves with the correct cwd so that ESLint can
  // locate eslint.config.mjs and Prettier can resolve its config.
  "web/**/*.{js,jsx,ts,tsx}": async (filenames) => {
    const relativePaths = filenames
      .map((f) => path.relative(webDir, f))
      .join(" ");
    execSync(`npx eslint --fix ${relativePaths}`, {
      cwd: webDir,
      stdio: "inherit",
    });
    execSync(`npx prettier --write ${relativePaths}`, {
      cwd: webDir,
      stdio: "inherit",
    });
    return [];
  },
  "web/**/*.{json,css,md}": async (filenames) => {
    const relativePaths = filenames
      .map((f) => path.relative(webDir, f))
      .join(" ");
    execSync(`npx prettier --write ${relativePaths}`, {
      cwd: webDir,
      stdio: "inherit",
    });
    return [];
  },
};
