const path = require("path");
const { execSync } = require("child_process");

const webDir = path.resolve(__dirname, "web");

module.exports = {
  "**/*.java": (filenames) => {
    const javaFiles = filenames.filter((f) => f.startsWith("api/"));
    if (javaFiles.length === 0) return [];
    return [`mvn checkstyle:check --file api/pom.xml`];
  },
  "web/**/*.{js,jsx,ts,tsx}": async (filenames) => {
    if (filenames.length === 0) return [];
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
    if (filenames.length === 0) return [];
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
