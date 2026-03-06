const path = require("path");

module.exports = {
  "**/*.java": (filenames) => {
    const javaFiles = filenames.filter((f) => f.startsWith("api/"));
    if (javaFiles.length === 0) return [];
    return [`mvn checkstyle:check --file api/pom.xml`];
  },
  "web/**/*.{js,jsx,ts,tsx}": (filenames) => {
    const relativePaths = filenames.map((f) => path.relative("web", f));
    return [
      `cd web && npx eslint --fix ${relativePaths.join(" ")}`,
      `cd web && npx prettier --write ${relativePaths.join(" ")}`,
    ];
  },
  "web/**/*.{json,css,md}": (filenames) => {
    const relativePaths = filenames.map((f) => path.relative("web", f));
    return [`cd web && npx prettier --write ${relativePaths.join(" ")}`];
  },
};
